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

    // Inyección de dependencias del servicio UserService
    @Autowired
    private UserService userService;

    // Manejo de las solicitudes GET a las rutas "/inicio_sesion" y "/"
    @GetMapping({"/inicio_sesion", "/"})
    public String mostrarFormularioInicioSesion() {
        // Retorna el nombre de la vista de inicio de sesión
        return "inicio_sesion";
    }

    // Manejo de las solicitudes POST a la ruta "/inicio_sesion"
    @RequestMapping(value = "/inicio_sesion", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Mono<String> iniciarSesion(ServerWebExchange exchange, Model model) {
        // Obtiene los datos del formulario
        return exchange.getFormData().flatMap(data -> {
            // Obtiene el usuario y la contraseña del formulario
            String usuario = data.getFirst("usuario");
            String contrasena = data.getFirst("contrasena");

            // Intenta iniciar sesión con el usuario y la contraseña
            return userService.iniciarSesion(usuario, contrasena)
                    .flatMap(user ->
                            // Si el inicio de sesión es exitoso, guarda el usuario en la sesión y en el modelo
                            exchange.getSession().flatMap(session -> {
                                session.getAttributes().put("usuario", user);
                                model.addAttribute("usuario", user);
                                // Redirige al usuario a la página correspondiente en función de su rol
                                String roleId = user.getRol().getId_rol().toString();
                                if (roleId.equals("1")) {
                                    return Mono.just("redirect:/administradores");
                                } else if (roleId.equals("2")) {
                                    return Mono.just("redirect:/cajeros");
                                } else {
                                    return Mono.just("redirect:/");
                                }
                            })
                    )
                    .switchIfEmpty(Mono.defer(() -> {
                        // Si el inicio de sesión no es exitoso, establece un mensaje de error en la sesión y redirige al usuario a la página de inicio de sesión
                        return exchange.getSession().doOnNext(session -> {
                            session.getAttributes().put("message", "Las credenciales no coinciden");
                        }).thenReturn("inicio_sesion");
                    }));
        });
    }

    @GetMapping("/cerrar_sesion")
    public Mono<String> cerrarSesion(ServerWebExchange exchange) {
        return exchange.getSession().flatMap(session -> {
            session.invalidate();
            return Mono.just("redirect:/inicio_sesion");
        });
    }
}



