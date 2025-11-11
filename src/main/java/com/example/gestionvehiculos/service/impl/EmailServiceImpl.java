package com.example.gestionvehiculos.service.impl;

import com.example.gestionvehiculos.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    @Async
    public void enviarNotificacionRevision(String to, String subject, String bodyHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(bodyHtml, true);
            helper.setFrom("no-reply@gestion-vehiculos.local");
            mailSender.send(message);
            logger.info("Email enviado a {} con asunto={}", to, subject);
        } catch (MessagingException ex) {
            logger.error("Error al enviar email a {}: {}", to, ex.getMessage());
        }
    }
}
