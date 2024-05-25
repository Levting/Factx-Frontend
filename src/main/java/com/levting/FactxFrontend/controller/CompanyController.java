package com.levting.FactxFrontend.controller;

import com.levting.FactxFrontend.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("empresas")
public class CompanyController {
    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("")
    public Mono<String> mostrarEmpresas(Model model) {
        return companyService.obtenerEmpresas()
                .collectList()
                .map(empresas -> {
                    model.addAttribute("empresas", empresas);
                    return "listar_empresas";
                });
    }
}
