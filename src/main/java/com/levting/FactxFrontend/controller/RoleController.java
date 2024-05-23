package com.levting.FactxFrontend.controller;

import com.levting.FactxFrontend.model.RoleModel;
import com.levting.FactxFrontend.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("")
    public Mono<String> listarRoles(Model model) {
        return roleService.obtenerRoles()
                .collectList()
                .map(roles -> {
                    model.addAttribute("roles", roles);
                    return "listar_roles";
                });
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrearRol(Model model) {
        RoleModel roleModel = new RoleModel();
        model.addAttribute("rol", roleModel);
        return "crear_rol";
    }

    @PostMapping("")
    public Mono<String> guardarRol(@ModelAttribute("rol") RoleModel roleModel) {
        return roleService.guardarRol(roleModel)
                .doOnError(error -> System.err.println("Error al guardar el rol: " + error.getMessage()))
                .then(Mono.just("redirect:/roles"));
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarRol(@PathVariable Integer id, Model model) {
        model.addAttribute("rol", roleService.obtenerRol(id));
        return "editar_rol";
    }

    @PostMapping("/{id}")
    public Mono<String> editarRol(@PathVariable Integer id, @ModelAttribute("rol") RoleModel roleModel, Model model) {
        return roleService.obtenerRol(id)
                .flatMap(existingRole -> {
                    existingRole.setRol(roleModel.getRol());
                    return roleService.actualizarRol(existingRole);
                })
                .doOnError(error -> System.err.println("Error al editar el rol: " + error.getMessage()))
                .then(Mono.just("redirect:/roles"));
    }

    @GetMapping("/{id}")
    public Mono<String> eliminarRol(@PathVariable Integer id) {
        return roleService.eliminarRol(id)
                .doOnError(error -> System.err.println("Error al eliminar el rol: " + error.getMessage()))
                .then(Mono.just("redirect:/roles"));
    }

}


