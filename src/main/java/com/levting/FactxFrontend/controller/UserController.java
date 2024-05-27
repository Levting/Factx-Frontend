package com.levting.FactxFrontend.controller;


import com.levting.FactxFrontend.model.CompanyModel;
import com.levting.FactxFrontend.model.RoleModel;
import com.levting.FactxFrontend.model.UserModel;
import com.levting.FactxFrontend.service.CompanyService;
import com.levting.FactxFrontend.service.RoleService;
import com.levting.FactxFrontend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@Controller
@RequestMapping("/usuarios")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final CompanyService companyService;

    @Autowired
    public UserController(UserService userService, RoleService roleService, CompanyService companyService) {
        this.userService = userService;
        this.roleService = roleService;
        this.companyService = companyService;
    }

    @GetMapping("")
    public Mono<String> mostrarUsuarios(Model model) {
        return userService.obtenerUsuarios()
                .collectList()
                .doOnNext(usuarios -> model.addAttribute("usuarios", usuarios))
                .thenReturn("listar_usuarios");
    }

    @GetMapping("/crear")
    public Mono<String> mostrarFormularioCrearUsuario(Model model) {
        model.addAttribute("usuario", new UserModel());
        return Mono.zip(
                roleService.obtenerRoles().collectList(),
                companyService.obtenerEmpresas().collectList()
        ).doOnNext(tuple -> {
            model.addAttribute("roles", tuple.getT1());
            model.addAttribute("empresas", tuple.getT2());
        }).thenReturn("crear_usuario");
    }

    @PostMapping("")
    public Mono<String> guardarUsuario(@ModelAttribute("usuario") UserModel userModel) {

        Mono<CompanyModel> empresa = companyService.obtenerEmpresa(userModel.getEmpresa().getId_empresa());
        Mono<RoleModel> rol = roleService.obtenerRol(userModel.getRol().getId_rol());

        return Mono.zip(empresa, rol)
                .flatMap(tuple -> {
                    userModel.setEmpresa(tuple.getT1());
                    userModel.setRol(tuple.getT2());
                    return userService.guardarUsuario(userModel);
                })
                .doOnSuccess(user -> System.out.println("Usuario creado con éxito!"))
                .doOnError(error -> System.out.println("Error al crear el usuario!"))
                .thenReturn("redirect:/usuarios");
    }

    @GetMapping("/editar/{id}")
    public Mono<String> mostrarFormularioEditarRol(@PathVariable Integer id, Model model) {
        return Mono.zip(
                userService.obtenerUsuario(id),
                roleService.obtenerRoles().collectList(),
                companyService.obtenerEmpresas().collectList()
        ).doOnNext(tuple -> {
            model.addAttribute("usuario", tuple.getT1());
            model.addAttribute("roles", tuple.getT2());
            model.addAttribute("empresas", tuple.getT3());
        }).thenReturn("editar_usuario");
    }

    @PostMapping("/{id}")
    public Mono<String> editarUsuario(@PathVariable Integer id, @Validated @ModelAttribute("usuario") UserModel userModel) {
        return userService.obtenerUsuario(id)
                .flatMap(existingUser -> {
                    existingUser.setNombre(userModel.getNombre());
                    existingUser.setApellido(userModel.getApellido());
                    existingUser.setUsuario(userModel.getUsuario());
                    existingUser.setContrasena(userModel.getContrasena());
                    existingUser.setEmpresa(userModel.getEmpresa());
                    existingUser.setRol(userModel.getRol());
                    return userService.actualizarUsuario(existingUser);
                })
                .doOnError(error -> System.err.println("Error al Editar: " + error.getMessage()))
                .doOnSuccess(updateUser -> System.out.println("Usuario Editado con Éxito!"))
                .thenReturn("redirect:/usuarios");
    }

    @GetMapping("/{id}")
    public Mono<String> eliminarUsuario(@PathVariable Integer id) {
        return userService.eliminarUsuario(id)
                .doOnError(error -> System.err.println("Error al Eliminar: " + error.getMessage()))
                .doOnSuccess(aVoid -> System.out.println("Usuario Eliminado con Éxito!"))
                .thenReturn("redirect:/usuarios");
    }
}
