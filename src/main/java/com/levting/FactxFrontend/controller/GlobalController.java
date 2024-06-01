package com.levting.FactxFrontend.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.ServerWebExchange;

@ControllerAdvice
public class GlobalController {

    /**
     * Controlador para mostrar el usuario (nombre y apellido) en todas las vistas, ya que es un fragmento navbar.
     *
     * @param model
     * @param exchange
     */
    @ModelAttribute
    public void addUserToModel(Model model, ServerWebExchange exchange) {
        exchange.getSession().map(session -> session.getAttributes().get("usuario"))
                .doOnNext(usuario -> model.addAttribute("usuario", usuario))
                .subscribe();
    }

}
