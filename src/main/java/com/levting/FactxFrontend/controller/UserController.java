package com.levting.FactxFrontend.controller;


import com.levting.FactxFrontend.model.CompanyModel;
import com.levting.FactxFrontend.model.RoleModel;
import com.levting.FactxFrontend.model.UserModel;
import com.levting.FactxFrontend.service.RoleService;
import com.levting.FactxFrontend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Controller
@RequestMapping("/usuarios")
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("")
    public Mono<String> mostrarUsuarios(Model model) {
        return userService.obtenerUsuarios()
                .collectList()
                .map(usuarios -> {
                    model.addAttribute("usuarios", usuarios);
                    return "listar_usuarios";
                });
    }

    @GetMapping("/crear")
    public Mono<String> mostrarFormularioCrearUsuario(Model model) {
        model.addAttribute("usuario", new UserModel());
        return roleService.obtenerRoles()
                .collectList()
                .doOnNext(roles -> {
                    model.addAttribute("roles", roles);
                })
                .then(Mono.just("crear_usuario"));
    }

    @PostMapping("")
    public Mono<String> guardarUsuario(@ModelAttribute("usuario") UserModel userModel) {
        System.out.println("Usuario a Crear: " + userModel);
        return userService.guardarUsuario(userModel)
                .doOnError(error -> {
                    System.out.println("Error al guardar un usuario: " + error.getMessage());
                })
                .doOnSuccess(savedUser -> {
                    System.out.println("Usuario Creado con Éxito!\n" + savedUser);
                })
                .then(Mono.just("redirect:/usuarios"));

    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarRol(@PathVariable Integer id_usuario, Model model) {
        model.addAttribute("usuario", userService.obtenerUsuario(id_usuario));
        return "editar_usuario";
    }

    @PostMapping("/{id}")
    public Mono<String> editarUsuario(@PathVariable Integer id_usuario, @ModelAttribute("usuario") UserModel userModel) {
        return userService.obtenerUsuario(id_usuario)
                .flatMap(existingUser -> {
                    existingUser.setNombre(userModel.getNombre());
                    existingUser.setApellido(userModel.getApellido());
                    existingUser.setUsuario(userModel.getUsuario());
                    existingUser.setContrasena(userModel.getContrasena());
                    existingUser.setEmpresa(userModel.getEmpresa());
                    existingUser.setRol(userModel.getRol());

                    return userService.actualizarUsuario(existingUser);
                })
                .doOnError(error -> System.out.println("Error al Editar: " + error.getMessage()))
                .doOnSuccess(updateUser -> System.out.println("Rol Editado con Éxito!"))
                .then(Mono.just("redirect:/usuarios"));
    }

    @GetMapping("/{id}")
    public Mono<String> eliminarUsuario(@PathVariable Integer id_usuario) {
        return userService.eliminarUsuario(id_usuario)
                .doOnError(error -> System.err.println("Error al Eliminar: " + error.getMessage()))
                .doOnSuccess(aVoid -> System.out.println("Usuario Eliminado con Éxito!"))
                .then(Mono.just("redirect/usuarios"));
    }
}
