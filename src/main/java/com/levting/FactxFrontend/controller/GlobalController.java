package com.levting.FactxFrontend.controller;

import com.levting.FactxFrontend.model.UserModel;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalController {

    /**
     * Controlador para mostrar el usuario (nombre y apellido) en todas las vistas, ya que es un fragmento navbar.
     *
     * @param model
     * @param exchange
     */
    @ModelAttribute
    public Mono<Void> addUserToModel(Model model, ServerWebExchange exchange) {
        return exchange.getSession()
                .flatMap(session -> {
                    UserModel usuario = (UserModel) session.getAttributes().get("usuario");
                    if (usuario != null) {
                        model.addAttribute("usuario", usuario);
                    }
                    return Mono.empty();
                });
    }

}
