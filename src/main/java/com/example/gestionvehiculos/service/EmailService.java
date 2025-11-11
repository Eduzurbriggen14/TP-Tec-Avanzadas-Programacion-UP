package com.example.gestionvehiculos.service;

public interface EmailService {
    void enviarNotificacionRevision(String to, String subject, String bodyHtml);
}
