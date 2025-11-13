package com.example.gestionvehiculos.service.impl;

import com.example.gestionvehiculos.dto.ItemChequeoDTO;
import com.example.gestionvehiculos.dto.revision.RevisionCreateDTO;
import com.example.gestionvehiculos.dto.revision.RevisionResponseDTO;
import com.example.gestionvehiculos.entity.ItemChequeo;
import com.example.gestionvehiculos.entity.RevisionVehiculo;
import com.example.gestionvehiculos.entity.Turno;
import com.example.gestionvehiculos.entity.Usuario;
import com.example.gestionvehiculos.entity.Vehiculo;
import com.example.gestionvehiculos.enums.EstadoTurno;
import com.example.gestionvehiculos.exception.ResourceNotFoundException;
import com.example.gestionvehiculos.repository.RevisionVehiculoRepository;
import com.example.gestionvehiculos.repository.TurnoRepository;
import com.example.gestionvehiculos.repository.UsuarioRepository;
import com.example.gestionvehiculos.repository.VehiculoRepository;
import com.example.gestionvehiculos.service.RevisionVehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.HashMap;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import com.example.gestionvehiculos.entity.ResultadoRevision;
import com.example.gestionvehiculos.entity.ItemChequeoTemplate;
import com.example.gestionvehiculos.service.EmailService;
import com.example.gestionvehiculos.repository.ItemChequeoTemplateRepository;
import com.example.gestionvehiculos.repository.ResultadoRevisionRepository;
import com.example.gestionvehiculos.enums.EstadoRevision;

@Service
@RequiredArgsConstructor
@Transactional
public class RevisionVehiculoServiceImpl implements RevisionVehiculoService {

    private final RevisionVehiculoRepository revisionRepository;
    private final UsuarioRepository usuarioRepository;
    private final VehiculoRepository vehiculoRepository;
        private final TurnoRepository turnoRepository;
        private final EmailService emailService;
        private final ItemChequeoTemplateRepository templateRepository;
        private final ResultadoRevisionRepository resultadoRevisionRepository;

    @Override
    public RevisionResponseDTO crear(RevisionCreateDTO revisionDTO, String userName) {
        // Obtener usuario revisor
        Usuario usuario = usuarioRepository.findByUserName(userName)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        // Buscar vehículo por patente
        Vehiculo vehiculo = vehiculoRepository.findByPatente(revisionDTO.getPatente())
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con patente: " + revisionDTO.getPatente()));

        // Obtener turno si fue especificado
        Turno turno = null;
        if (revisionDTO.getTurnoId() != null) {
            turno = turnoRepository.findById(revisionDTO.getTurnoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado"));
        }
        RevisionVehiculo revision = RevisionVehiculo.builder()
                .usuario(usuario)
                .vehiculo(vehiculo)
                .turno(turno)
                .fechaRevision(LocalDate.now())
                .resumen(revisionDTO.getResumen())
                .observaciones(revisionDTO.getObservaciones())
                .build();

                // Agregar items de chequeo
                List<ItemChequeo> items;
                List<ItemChequeoDTO> dtoItems = revisionDTO.getItemsChequeo();

                // Obtener plantilla canonical de items
                List<ItemChequeoTemplate> templates = templateRepository.findAllByOrderByOrdenAsc();

                // Mapear DTOs por nombre (normalizando) para sobreescribir defaults
                Map<String, ItemChequeoDTO> dtoMap = new HashMap<>();
                if (dtoItems != null) {
                        for (ItemChequeoDTO d : dtoItems) {
                                if (d != null && d.getNombreItem() != null) {
                                        dtoMap.put(d.getNombreItem().trim().toUpperCase(), d);
                                }
                        }
                }

                items = new java.util.ArrayList<>();
                // Para cada item del template, crear el ItemChequeo usando valores del DTO si existen,
                // o defaults en caso contrario
                for (ItemChequeoTemplate t : templates) {
                        String key = t.getNombreItem().trim().toUpperCase();
                        ItemChequeoDTO provided = dtoMap.get(key);
                        Integer puntuacion = provided != null && provided.getPuntuacion() != null ? provided.getPuntuacion() : 10;
                        String observ = provided != null && provided.getObservaciones() != null && !provided.getObservaciones().trim().isEmpty()
                                        ? provided.getObservaciones()
                                        : "No se requeria revision";

                        items.add(ItemChequeo.builder()
                                        .revision(revision)
                                        .nombreItem(t.getNombreItem())
                                        .puntuacion(puntuacion)
                                        .observaciones(observ)
                                        .build());
                }

                revision.setItemsChequeo(items);

        // Calcular puntaje total
        revision.calcularPuntajeTotal();
        
        // Determinar estado (SEGURO o RECHEQUEAR)
        revision.determinarEstadoResultado();
        
        // Construir y asociar el ResultadoRevision (se persistirá por cascade)
        ResultadoRevision resultado = ResultadoRevision.builder()
                .resultado(buildResultadoTexto(revision))
                .estadoFinalAuto(revision.getEstadoResultado())
                .revision(revision)
                .build();
        revision.setResultadoRevision(resultado);
        
        // Validar observaciones obligatorias si puntaje < 40
        if (!revision.validarObservaciones()) {
            throw new IllegalArgumentException("Cuando el puntaje es menor a 40, las observaciones son obligatorias");
        }

        // Guardar revisión
        RevisionVehiculo guardada = revisionRepository.save(revision);
        
                // Fallback: si por alguna razón el resultado no fue persistido por cascade,
                // persistirlo explícitamente y volver a asociarlo.
                if (guardada.getResultadoRevision() == null || guardada.getResultadoRevision().getId() == null) {
                        ResultadoRevision savedRes = resultadoRevisionRepository.save(resultado);
                        guardada.setResultadoRevision(savedRes);
                        guardada = revisionRepository.save(guardada);
                }
        
        // Actualizar estado del turno si existe
        if (turno != null) {
            turno.setEstadoTurno(EstadoTurno.REALIZADO);
            turnoRepository.save(turno);
        }

                // Notificar por email al dueño si la revisión requiere re-chequeo
                if (guardada.getEstadoResultado() == EstadoRevision.RECHEQUEAR) {
                        String correo = guardada.getVehiculo().getCliente().getCorreo();
                        if (correo != null && !correo.trim().isEmpty()) {
                                String asunto = "Revisión vehicular - Rechequeo obligatorio (patente " + guardada.getVehiculo().getPatente() + ")";
                                StringBuilder cuerpo = new StringBuilder();
                                cuerpo.append("Hola,\n\n");
                                cuerpo.append("Se ha realizado una revisión del vehículo con patente ")
                                          .append(guardada.getVehiculo().getPatente())
                                          .append(" y requiere re-chequeo.\n\n");
                                          int maxPuntaje = guardada.getPuntajeMaximo();
                                          int porcentaje = guardada.getPuntajePorcentual();
                                          cuerpo.append("Puntaje total: ").append(guardada.getPuntajeTotal())
                                                  .append("/").append(maxPuntaje)
                                                  .append(" (").append(porcentaje).append("%)\n\n");
                                cuerpo.append("Observaciones del inspector: \n").append(guardada.getObservaciones() != null ? guardada.getObservaciones() : "(sin observaciones)")
                                          .append("\n\nItems con puntuación baja:\n");
                                guardada.getItemsChequeo().forEach(item -> {
                                        cuerpo.append(" - ").append(item.getNombreItem()).append(": ").append(item.getPuntuacion()).append("/10\n");
                                });
                                cuerpo.append("\nPor favor, reserve un turno para el re-chequeo lo antes posible.\n\nSaludo");

                                emailService.enviarNotificacionRevision(correo, asunto, cuerpo.toString());
                        }
                }

        return convertirAResponseDTO(guardada);
    }

    @Override
    public void eliminar(Long id) {
        if (!revisionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Revisión no encontrada con ID: " + id);
        }
        revisionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public RevisionResponseDTO obtenerPorId(Long id) {
        RevisionVehiculo revision = revisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Revisión no encontrada con ID: " + id));
        return convertirAResponseDTO(revision);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevisionResponseDTO> obtenerTodos() {
        return revisionRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevisionResponseDTO> obtenerPorPatente(String patente) {
        Vehiculo vehiculo = vehiculoRepository.findByPatente(patente)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con patente: " + patente));
        
        return revisionRepository.findByVehiculoOrderByFechaRevisionDesc(vehiculo).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevisionResponseDTO> obtenerPorRangoFechas(LocalDate inicio, LocalDate fin) {
        return revisionRepository.findByFechaRevisionBetween(inicio, fin).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    private RevisionResponseDTO convertirAResponseDTO(RevisionVehiculo revision) {
        List<ItemChequeoDTO> itemsDTO = revision.getItemsChequeo().stream()
                .map(item -> ItemChequeoDTO.builder()
                        .nombreItem(item.getNombreItem())
                        .puntuacion(item.getPuntuacion())
                        .observaciones(item.getObservaciones())
                        .build())
                .collect(Collectors.toList());

        return RevisionResponseDTO.builder()
                .id(revision.getId())
                .usuarioRevisor(revision.getUsuario().getUserName())
                .patente(revision.getVehiculo().getPatente())
                .turnoId(revision.getTurno() != null ? revision.getTurno().getId() : null)
                .fechaRevision(revision.getFechaRevision())
                .resumen(revision.getResumen())
                .itemsChequeo(itemsDTO)
                .puntajeTotal(revision.getPuntajeTotal())
                .estadoResultado(revision.getEstadoResultado())
                .observaciones(revision.getObservaciones())
                                .resultadoRevisionId(revision.getResultadoRevision() != null ? revision.getResultadoRevision().getId() : null)
                                .resultadoRevisionTexto(revision.getResultadoRevision() != null ? revision.getResultadoRevision().getResultado() : null)
                                .estadoFinalAuto(revision.getResultadoRevision() != null ? revision.getResultadoRevision().getEstadoFinalAuto() : null)
                .build();
    }

        private String buildResultadoTexto(RevisionVehiculo revision) {
                int max = revision.getPuntajeMaximo();
                int porc = revision.getPuntajePorcentual();
                String base = String.format("Puntaje %d/%d (%d%%). Estado: %s. ",
                                revision.getPuntajeTotal() != null ? revision.getPuntajeTotal() : 0,
                                max,
                                porc,
                                revision.getEstadoResultado() != null ? revision.getEstadoResultado().name() : "N/A");
                // Añadir una línea resumen según estado
                switch (revision.getEstadoResultado()) {
                        case SEGURO:
                                return base + "Revisión aprobada: vehículo en condiciones seguras.";
                        case CONDICIONADO:
                                return base + "Condicionado: tiene observaciones menores o recomendaciones.";
                        case RECHEQUEAR:
                        default:
                                return base + "Requiere re-chequeo: por favor revisar los items reportados.";
                }
        }
}
