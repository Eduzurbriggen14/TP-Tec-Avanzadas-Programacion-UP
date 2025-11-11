package com.example.gestionvehiculos.service;

import io.jsonwebtoken.security.Keys;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        String secureKey = Base64.getEncoder().encodeToString(Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256).getEncoded());
        System.out.println("Tu nueva clave segura y en formato correcto es: " + secureKey);
    }
}
