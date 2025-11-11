package com.example.gestionvehiculos.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Utilidad para generar hashes BCrypt de contraseñas
 * Ejecutar como aplicación Java independiente
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        // Generar hash para "password123"
        String plainPassword = "password123";
        String hashedPassword = passwordEncoder.encode(plainPassword);
        
        System.out.println("=".repeat(80));
        System.out.println("GENERADOR DE HASHES BCRYPT");
        System.out.println("=".repeat(80));
        System.out.println();
        System.out.println("Contraseña original: " + plainPassword);
        System.out.println("Hash BCrypt generado:");
        System.out.println(hashedPassword);
        System.out.println();
        System.out.println("Copia este hash para usar en el SQL:");
        System.out.println("passwd = '" + hashedPassword + "'");
        System.out.println();
        
        // Verificar que el hash funciona
        boolean matches = passwordEncoder.matches(plainPassword, hashedPassword);
        System.out.println("Verificación: " + (matches ? "✅ HASH VÁLIDO" : "❌ ERROR"));
        System.out.println("=".repeat(80));
    }
}
