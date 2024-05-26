package com.levting.FactxFrontend.controller;

import com.levting.FactxFrontend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/inicio_sesion")
    public String mostrarFormularioInicioSesion() {
        return "inicio_sesion";
    }

    @RequestMapping(value = "/inicio_sesion", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Mono<String> iniciarSesion(ServerWebExchange exchange, Model model) {
        return exchange.getFormData().flatMap(data -> {
            String usuario = data.getFirst("usuario");
            String contrasena = data.getFirst("contrasena");

            return userService.iniciarSesion(usuario, contrasena)
                    .doOnNext(user -> {
                        exchange.getSession().doOnNext(session -> {
                            session.getAttributes().put("usuario", user);
                            System.out.println("Usuario almacenado en la sesión: " + user.getNombre() + " " + user.getApellido());
                            System.out.println("Inicio de Sesión Exitoso!");
                        }).subscribe();
                    })
                    .map(user -> {
                        String roleId = user.getRol().getId_rol().toString();
                        if (roleId.equals("1")) {
                            return "redirect:/administradores";
                        } else if (roleId.equals("2")) {
                            return "redirect:/cajeros";
                        } else {
                            return "redirect:/";
                        }
                    })
                    .switchIfEmpty(Mono.defer(() -> {
                        System.out.println("Las credenciales no coinciden");
                        return Mono.just("inicio_sesion");
                    }))
                    .doOnError(error -> System.out.println("Error al Iniciar Sesión!"));
        });
    }
}




