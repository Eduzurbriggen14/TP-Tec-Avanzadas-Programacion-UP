package com.example.gestionvehiculos.service.impl;

import com.example.gestionvehiculos.dto.turno.TurnoResponseDTO;
import com.example.gestionvehiculos.dto.turno.TurnoSolicitudDTO;
import com.example.gestionvehiculos.dto.turno.TurnoSlotDTO;
import com.example.gestionvehiculos.entity.Cliente;
import com.example.gestionvehiculos.entity.Turno;
import com.example.gestionvehiculos.entity.Vehiculo;
import com.example.gestionvehiculos.enums.EstadoTurno;
import com.example.gestionvehiculos.exception.ResourceNotFoundException;
import com.example.gestionvehiculos.repository.ClienteRepository;
import com.example.gestionvehiculos.repository.TurnoRepository;
import com.example.gestionvehiculos.repository.VehiculoRepository;
import com.example.gestionvehiculos.service.TurnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.example.gestionvehiculos.dto.turno.TurnoSlotStatusDTO;

@Service
@RequiredArgsConstructor
public class TurnoServiceImpl implements TurnoService {

    private final TurnoRepository turnoRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;

    @Override
    public TurnoResponseDTO solicitarTurno(TurnoSolicitudDTO solicitudDTO, String userName) {
        Long clienteId = solicitudDTO.getClienteId();
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));

        Vehiculo vehiculo = vehiculoRepository.findById(solicitudDTO.getVehiculoId())
            .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado"));

        Turno turno = Turno.builder()
            .cliente(cliente)
            .vehiculo(vehiculo)
            .fechaTurno(solicitudDTO.getFechaTurno())
            .horaTurno(solicitudDTO.getHoraTurno())
            .estadoTurno(EstadoTurno.PENDIENTE)
            .fechaSolicitud(LocalDate.now())
            .observaciones(solicitudDTO.getObservaciones())
            .build();

        turno = turnoRepository.save(turno);
        return convertirAResponseDTO(turno);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> obtenerTurnosDisponibles() {
        List<EstadoTurno> estadosDisponibles = Arrays.asList(
            EstadoTurno.PENDIENTE,
            EstadoTurno.CONFIRMADO
        );

        return turnoRepository.findTurnosDisponibles(LocalDate.now(), estadosDisponibles)
            .stream()
            .map(this::convertirAResponseDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoSlotDTO> obtenerSlotsDisponibles(LocalDateTime inicioDT, LocalDateTime finDT, int duracionMinutos) {
        // Las validaciones de inicio/fin/duración son manejadas por el controlador
        // (se pasan valores válidos usando horarios fijos), por lo que se omiten aquí.

        List<EstadoTurno> ocupados = Arrays.asList(
            EstadoTurno.PENDIENTE,
            EstadoTurno.CONFIRMADO
        );

        java.time.LocalDate fecha = inicioDT.toLocalDate();

        List<Turno> turnosExistentes = turnoRepository.findByFechaTurnoAndEstadoTurnoIn(fecha, ocupados);

        // Construir intervalos ocupados alineados a la hora
        List<LocalTime[]> ocupadosIntervalos = new ArrayList<>();
        for (Turno t : turnosExistentes) {
            LocalTime rawStart = t.getHoraTurno();
            LocalTime start = rawStart.withMinute(0).withSecond(0);
            LocalTime end = start.plusMinutes(duracionMinutos);
            ocupadosIntervalos.add(new LocalTime[]{start, end});
        }

        // Generar candidatos alineados a la hora
        List<TurnoSlotDTO> disponibles = new ArrayList<>();

        LocalTime cursor = inicioDT.toLocalTime();
        if (cursor.getMinute() != 0 || cursor.getSecond() != 0) {
            cursor = cursor.plusHours(1).withMinute(0).withSecond(0);
        } else {
            cursor = cursor.withSecond(0);
        }

        LocalTime finAligned = finDT.toLocalTime().withSecond(0);

        while (!cursor.plusMinutes(duracionMinutos).isAfter(finAligned)) {
            LocalTime candidatoInicio = cursor;
            LocalTime candidatoFinExclusive = cursor.plusMinutes(duracionMinutos);
            boolean overlap = false;
            for (LocalTime[] intervalo : ocupadosIntervalos) {
                LocalTime s = intervalo[0];
                LocalTime e = intervalo[1];
                if (candidatoInicio.isBefore(e) && candidatoFinExclusive.isAfter(s)) {
                    overlap = true;
                    break;
                }
            }

            if (!overlap) {
                LocalTime finInclusivo = candidatoFinExclusive.minusMinutes(1);
                disponibles.add(TurnoSlotDTO.builder()
                    .inicio(candidatoInicio)
                    .fin(finInclusivo)
                    .build());
            }

            cursor = cursor.plusMinutes(duracionMinutos);
        }

        return disponibles;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoSlotStatusDTO> obtenerSlotsConEstado(LocalDateTime inicioDT, LocalDateTime finDT, int duracionMinutos) {
        // Las validaciones de inicio/fin/duración son manejadas por el controlador
        // (se pasan valores válidos usando horarios fijos), por lo que se omiten aquí.

        List<EstadoTurno> ocupados = Arrays.asList(
            EstadoTurno.PENDIENTE,
            EstadoTurno.CONFIRMADO
        );

        java.time.LocalDate fecha = inicioDT.toLocalDate();

        // Opción A: petición única a BD para obtener horas de inicio ocupadas
        List<LocalTime> horasOcupadas = turnoRepository.findHorasOcupadasByFechaAndEstadoIn(fecha, ocupados);

        // Generar candidatos alineados a la hora y marcar disponibilidad
        List<TurnoSlotStatusDTO> resultados = new ArrayList<>();

        LocalTime cursor = inicioDT.toLocalTime();
        if (cursor.getMinute() != 0 || cursor.getSecond() != 0) {
            cursor = cursor.plusHours(1).withMinute(0).withSecond(0);
        } else {
            cursor = cursor.withSecond(0);
        }

        LocalTime finAligned = finDT.toLocalTime().withSecond(0);

        while (!cursor.plusMinutes(duracionMinutos).isAfter(finAligned)) {
            LocalTime candidatoInicio = cursor;
            LocalTime candidatoFinExclusive = cursor.plusMinutes(duracionMinutos);
            boolean ocupado = horasOcupadas.stream()
                    .anyMatch(h -> h.withSecond(0).withNano(0).equals(candidatoInicio.withSecond(0).withNano(0)));

            LocalTime finInclusivo = candidatoFinExclusive.minusMinutes(1);
            resultados.add(TurnoSlotStatusDTO.builder()
                .inicio(candidatoInicio)
                .fin(finInclusivo)
                .disponible(!ocupado)
                .build());

            cursor = cursor.plusMinutes(duracionMinutos);
        }

        return resultados;
    }

    @Override
    @Transactional
    public TurnoResponseDTO confirmarTurno(Long turnoId) {
        Turno turno = turnoRepository.findById(turnoId)
            .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado con ID: " + turnoId));

        turno.confirmar();
        turno = turnoRepository.save(turno);
        return convertirAResponseDTO(turno);
    }

    @Override
    @Transactional
    public TurnoResponseDTO cancelarTurno(Long turnoId) {
        Turno turno = turnoRepository.findById(turnoId)
            .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado con ID: " + turnoId));

        turno.cancelar();
        turno = turnoRepository.save(turno);
        return convertirAResponseDTO(turno);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> obtenerTodos() {
        return turnoRepository.findAll()
            .stream()
            .map(this::convertirAResponseDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TurnoResponseDTO obtenerPorId(Long id) {
        Turno turno = turnoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado con ID: " + id));
        return convertirAResponseDTO(turno);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> obtenerPorPatente(String patente) {
        Vehiculo vehiculo = vehiculoRepository.findByPatente(patente)
            .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con patente: " + patente));

        return turnoRepository.findByVehiculo(vehiculo)
            .stream()
            .map(this::convertirAResponseDTO)
            .collect(Collectors.toList());
    }

    private TurnoResponseDTO convertirAResponseDTO(Turno turno) {
        return TurnoResponseDTO.builder()
            .id(turno.getId())
            .vehiculoId(turno.getVehiculo().getId())
            .patente(turno.getVehiculo().getPatente())
            .marcaModelo(turno.getVehiculo().getMarca() + " " + turno.getVehiculo().getModelo())
            .clienteId(turno.getCliente().getId())
            .clienteNombre(turno.getCliente().getNombre() + " " + turno.getCliente().getApellido())
            .fechaTurno(turno.getFechaTurno())
            .horaTurno(turno.getHoraTurno())
            .estadoTurno(turno.getEstadoTurno())
            .fechaSolicitud(turno.getFechaSolicitud())
            .observaciones(turno.getObservaciones())
            .build();
    }
}
