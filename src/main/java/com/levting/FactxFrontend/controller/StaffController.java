package com.levting.FactxFrontend.controller;

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
@RequestMapping("/personal")
public class StaffController {

    // El controlador del personal, gestionara el usuario y el rol, posiblemente tambien la empresa.

    private final UserService userService;
    private final RoleService roleService;
    private final CompanyService companyService;

    @Autowired
    public StaffController(UserService userService, RoleService roleService, CompanyService companyService) {
        this.userService = userService;
        this.roleService = roleService;
        this.companyService = companyService;
    }

    /**
     * Controlador del Usuario
     */

    @GetMapping({"/usuarios", ""})
    public Mono<String> mostrarVistaUsuarios(Model model) {
        return userService.obtenerUsuarios()
                .collectList()
                .doOnNext(usuarios -> model.addAttribute("usuarios", usuarios))
                .thenReturn("personal/usuarios");
    }

    @PostMapping("/usuarios")
    public Mono<String> guardarUsuario(@ModelAttribute("usuarioNuevo") UserModel userModel) {
        System.out.println("usuarioNuevo: " + userModel);
        return userService.guardarUsuario(userModel)
                .doOnError(error -> System.out.println("Error al crear el usuario!"))
                .doOnSuccess(user -> System.out.println("Usuario creado con éxito!" + user))
                .thenReturn("redirect:/personal/usuarios");
    }

    @PostMapping("/usuarios/{id}")
    public Mono<String> editarUsuario(@PathVariable Integer id, @Validated @ModelAttribute("usuarioNuevo") UserModel userModel) {
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
                .thenReturn("redirect:/personal/usuarios");
    }

    @GetMapping("/usuarios/{id}")
    public Mono<String> eliminarUsuario(@PathVariable Integer id) {
        return userService.eliminarUsuario(id)
                .doOnError(error -> System.err.println("Error al Eliminar: " + error.getMessage()))
                .doOnSuccess(aVoid -> System.out.println("Usuario Eliminado con Éxito!"))
                .thenReturn("redirect:/personal/usuarios");
    }

    // FORMULARIOS

    @GetMapping("/usuarios/crear")
    public Mono<String> mostrarFormularioCrearUsuario(Model model) {
        model.addAttribute("usuarioNuevo", new UserModel());
        return Mono.zip(
                roleService.obtenerRoles().collectList(),
                companyService.obtenerEmpresas().collectList()
        ).doOnNext(tuple -> {
            model.addAttribute("roles", tuple.getT1());
            model.addAttribute("empresas", tuple.getT2());
        }).thenReturn("personal/crear_usuario");
    }

    @GetMapping("/usuarios/editar/{id}")
    public Mono<String> mostrarFormularioEditarUsuario(@PathVariable Integer id, Model model) {
        return Mono.zip(
                userService.obtenerUsuario(id),
                roleService.obtenerRoles().collectList(),
                companyService.obtenerEmpresas().collectList()
        ).doOnNext(tuple -> {
            model.addAttribute("usuario", tuple.getT1());
            model.addAttribute("roles", tuple.getT2());
            model.addAttribute("empresas", tuple.getT3());
        }).thenReturn("personal/editar_usuario");
    }


    /**
     * Controlador para el Rol
     */

    @GetMapping("/roles")
    public Mono<String> mostrarRoles(Model model) {
        return roleService.obtenerRoles()
                .collectList()
                .doOnNext(roles -> model.addAttribute("roles", roles))
                .thenReturn("personal/roles");
    }

    @PostMapping("/roles")
    public Mono<String> guardarRol(@ModelAttribute("rol") RoleModel roleModel) {
        System.out.println("Rol: " + roleModel);
        return roleService.guardarRol(roleModel)
                .doOnError(error -> System.err.println("Error al guardar el rol: " + error.getMessage()))
                .doOnSuccess(savedRole -> System.out.println("Rol creado con Éxito! " + savedRole))
                .then(Mono.just("redirect:/personal/roles"));
    }

    @PostMapping("/roles/{id}")
    public Mono<String> editarRol(@PathVariable Integer id, @ModelAttribute("rol") RoleModel roleModel) {
        return roleService.obtenerRol(id)
                .flatMap(existingRole -> {
                    existingRole.setRol(roleModel.getRol());
                    return roleService.actualizarRol(existingRole);
                })
                .doOnError(error -> System.err.println("Error al editar el rol: " + error.getMessage()))
                .doOnSuccess(updatedRole -> System.out.println("Rol editado con éxito!" + updatedRole))
                .then(Mono.just("redirect:/personal/roles"));
    }

    @GetMapping("/roles/{id}")
    public Mono<String> eliminarRol(@PathVariable Integer id) {
        return roleService.eliminarRol(id)
                .doOnError(error -> System.err.println("Error al eliminar el rol: " + error.getMessage()))
                .doOnSuccess(aVoid -> System.out.println("Rol Eliminado con Éxito!" + aVoid))
                .then(Mono.just("redirect:/personal/roles"));
    }

    @GetMapping("/roles/crear")
    public Mono<String> mostrarFormularioCrearRol(Model model) {
        model.addAttribute("rol", new RoleModel());
        return Mono.just("personal/crear_rol");
    }

    @GetMapping("/roles/editar/{id}")
    public Mono<String> mostrarFormularioEditarRol(@PathVariable Integer id, Model model) {
        model.addAttribute("rol", roleService.obtenerRol(id));
        return Mono.just("personal/editar_rol");
    }

    /**
     * Controlador de Empresa
     */

    @GetMapping("/empresas")
    public Mono<String> mostrarEmpresas(Model model) {
        return companyService.obtenerEmpresas()
                .collectList()
                .doOnNext(empresas -> model.addAttribute("empresas", empresas))
                .thenReturn("personal/empresas");
    }

}
