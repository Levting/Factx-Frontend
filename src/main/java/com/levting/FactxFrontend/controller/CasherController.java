package com.levting.FactxFrontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ServerWebExchange;

import com.levting.FactxFrontend.model.UserModel;

import reactor.core.publisher.Mono;

@Controller
public class CasherController {

    // Método para comprobar si el usuario está en la sesión
    private Mono<String> checkSession(ServerWebExchange exchange, Model model) {
        return exchange.getSession().flatMap(session -> {
            UserModel usuario = (UserModel) session.getAttributes().get("usuario");
            if (usuario != null) {
                model.addAttribute("usuario", usuario);
                return Mono.empty();
            } else {
                return Mono.just("redirect:/inicio_sesion");
            }
        });
    }

    @GetMapping("/cajeros")
    public Mono<String> mostrarPaginaCajeros(ServerWebExchange exchange, Model model) {
        // Comprueba si el usuario está en la sesión
        return checkSession(exchange, model).switchIfEmpty(Mono.just("cajeros/cajeros"));
    }

}
