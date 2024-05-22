package com.levting.FactxFrontend.controller;


import com.levting.FactxFrontend.model.UserModel;
import com.levting.FactxFrontend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;



@Controller
@RequestMapping("/usuarios")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String mostrarUsuarios(Model model) {
        Flux<UserModel> usuarios = userService.obtenerUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "usuarios"; // Devuelve el nombre de la plantilla HTML
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrearUsuario() {
        return "crear_usuario"; // Devuelve el nombre de la plantilla HTML del formulario
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute UserModel userModel){
        userService.guardarUsuario(userModel).subscribe(); // Guardar el Usuario
        return "redirect:/usuarios/listar"; // Redirigir a la lista de usuarios después de guardar
    }

}
// Mono<UserModel> usuarioMono = userService.obtenerUsuarioPorId("123");

