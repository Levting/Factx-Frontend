package com.levting.FactxFrontend.controller;

import com.levting.FactxFrontend.model.RoleModel;
import com.levting.FactxFrontend.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Controller
@RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping({"/listar", "/"})
    public String listarRoles(Model model) {
        Flux<RoleModel> roles = roleService.obtenerRoles();
        model.addAttribute("roles", roles);
        return "listar_roles";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrearRol(Model model) {
        model.addAttribute("rol", new RoleModel());
        return "crear_rol";
    }

    @PostMapping("")
    public String guardarRol(RoleModel rol) {
        roleService.guardarRol(rol);
        return "redirect:/roles/listar";
    }
}

