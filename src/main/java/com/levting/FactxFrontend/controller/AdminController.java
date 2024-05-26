package com.levting.FactxFrontend.controller;

import com.levting.FactxFrontend.model.UserModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
public class AdminController {

    @GetMapping("/administradores")
    public Mono<String> mostrarPaginaAdministradores(ServerWebExchange exchange, Model model) {
        return exchange.getSession().map(session -> {
            UserModel usuario = (UserModel) session.getAttributes().get("usuario");
            if (usuario != null) {
                model.addAttribute("usuario", usuario);
                return "administradores/administradores";
            } else {
                return "redirect:/inicio_sesion";
            }
        });
    }
}
