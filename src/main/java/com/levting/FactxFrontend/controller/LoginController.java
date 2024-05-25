package com.levting.FactxFrontend.controller;

import com.levting.FactxFrontend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
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
    public Mono<String> iniciarSesion(ServerWebExchange exchange) {
        return exchange.getFormData().flatMap(data -> {
            String usuario = data.getFirst("usuario");
            String contrasena = data.getFirst("contrasena");

            return userService.iniciarSesion(usuario, contrasena)
                    .map(user -> {
                        String roleId = user.getRol().getId_rol().toString();
                        System.out.println("Usuario: " + user.getNombre());
                        System.out.println("Rol: " + roleId);
                        if (roleId.equals("1")) {
                            return "redirect:/administradores";
                        } else if (roleId.equals("2")) {
                            return "redirect:/cajeros";
                        } else {
                            return "redirect:/";
                        }
                    })
                    .defaultIfEmpty("inicio_sesion")
                    .doOnError(error -> System.out.println("Error al Iniciar Sesión!"))
                    .doOnSuccess(success -> System.out.println("Inicio de Sesión Exitoso!"));
        });
    }

//    @RequestMapping(value = "/inicio_sesion", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public Mono<String> iniciarSesion(ServerWebExchange exchange) {
//        return exchange.getFormData().doOnNext(data -> {
//            String usuario = data.getFirst("usuario");
//            String contrasena = data.getFirst("contrasena");
//            System.out.println("Usuario: " + usuario);
//            System.out.println("Contraseña: " + contrasena);
//        }).thenReturn("inicio_sesion");
//    }
}

//    @PostMapping("/inicio_sesion")
//    public Mono<String> iniciarSesion(@RequestParam("usuario") String usuario, @RequestParam("contrasena") String contrasena) {
//        return userService.iniciarSesion(usuario, contrasena)
//                .map(user -> {
//                    String roleId = user.getRol().getId_rol().toString();
//                    if (roleId.equals("1")) {
//                        return "redirect:/administradores";
//                    } else if (roleId.equals("2")) {
//                        return "redirect:/cajeros";
//                    } else {
//                        return "redirect:/";
//                    }
//                })
//                .defaultIfEmpty("inicio_sesion")
//                .doOnError(error -> System.out.println("Error al Iniciar Sesión!"))
//                .doOnSuccess(success -> System.out.println("Inicio de Sesión Exitoso!"));
//    }



