package com.farmatodo.checkout.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador simple para verificar la disponibilidad del servicio.
 * 
 * Expone el endpoint GET /ping, que responde con "pong" cuando
 * la aplicación está en funcionamiento.
 * 
 * Este endpoint se usa como healthcheck tanto en entornos locales
 * como en despliegues (Docker, Cloud Run, etc.).
 */
@RestController
public class PingController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
