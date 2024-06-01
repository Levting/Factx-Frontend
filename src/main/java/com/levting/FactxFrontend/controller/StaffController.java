package com.levting.FactxFrontend.controller;

import com.levting.FactxFrontend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/personal")
public class StaffController {

    private final UserService userService;

    @Autowired
    public StaffController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("")
    public Mono<String> mostrarVistaUsuarios(Model model){
        return userService.obtenerUsuarios()
                .collectList()
                .doOnNext(usuarios -> {
                    if (usuarios.isEmpty()){
                        model.addAttribute("error", "No hay Usuarios!");
                    }
                    model.addAttribute("usuarios", usuarios);
                }).thenReturn("personal/usuarios");
    }
}
