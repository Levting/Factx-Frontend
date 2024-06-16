package com.levting.FactxFrontend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;

import com.levting.FactxFrontend.model.CompanyModel;
import com.levting.FactxFrontend.model.UserModel;
import com.levting.FactxFrontend.service.CompanyService;

import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/empresas")
public class CompanyController {

    // Controlador de la empresa

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping({ "/empresas", "" })
    public Mono<String> mostrarVistaEmpresas(Model model, ServerWebExchange serverWebExchange) {
        // Obtener el usuario de la sesión
        UserModel usuario = (UserModel) model.getAttribute("usuario");
        if (usuario != null) {
            // Obtener los clientes y añadirlos al modelo
            return serverWebExchange.getSession()
                    .doOnNext(session -> {
                        // Añadir los mensajes de éxito o error al modelo
                        model.addAttribute("successMessage", session.getAttribute("successMessage"));
                        model.addAttribute("errorMessage", session.getAttribute("errorMessage"));

                        // Eliminar los mensajes de éxito o error de la sesión
                        session.getAttributes().remove("successMessage");
                        session.getAttributes().remove("errorMessage");
                    })
                    .then(companyService.obtenerEmpresas()
                            .collectList()
                            .doOnNext(empresas -> {
                                if (empresas.isEmpty()) {
                                    System.out.println("No existen Clientes!");
                                } else {
                                    model.addAttribute("empresas", empresas);
                                }
                            }).thenReturn("empresas/empresas"));
        } else {
            return Mono.just("redirect:/inicio_sesion");
        }
    }

    /**
     * Mostrar el formulario para crear una empresa
     * 
     * @param model
     * @param serverWebExchange
     * @return
     */
    @GetMapping("/empresas/crear")
    public Mono<String> mostrarFormularioCrearEmpresa(Model model) {
        // Añadir un nueva empresa al modelo
        model.addAttribute("empresa", new CompanyModel());

        // Añaddir la empresa al modelo
        return Mono.just("empresas/crear_empresa");
    }

    // Metodo para guardar a una empresa

    // Metodo para mostar la pagina de edicion de una empresa
    @GetMapping("/empresas/editar/{id}")
    public Mono<String> mostrarFormularioEditarEmpresa(@PathVariable Integer id, Model model) {
        // Obtener el usuario de la sesión
        UserModel usuario = (UserModel) model.getAttribute("usuario");
        if (usuario != null) {
            model.addAttribute("empresa", companyService.obtenerEmpresa(id));
            return Mono.just("empresas/editar_empresa");
        } else {
            return Mono.just("redirect:/inicio_sesion");
        }
    }

    // Metodo para eliminar una empresa
    @GetMapping("/empresas/eliminar/{id}")
    public Mono<String> eliminarEmpresa(@PathVariable Integer id, ServerWebExchange serverWebExchange) {
        // Eliminar la empresa
        return companyService.eliminarEmpresa(id)
                .doOnError(error -> {
                    System.err.println("Error al Eliminar: " + error.getMessage());
                    serverWebExchange.getSession().doOnNext(session -> {
                        session.getAttributes().put("errorMessage", "Error al Eliminar la Empresa!");
                    }).subscribe();
                })
                .doOnSuccess(aVoid -> {
                    System.out.println("Empresa Eliminada con Éxito!");
                    serverWebExchange.getSession().doOnNext(session -> {
                        session.getAttributes().put("successMessage", "Empresa Eliminada con Éxito!");
                    }).subscribe();
                }).thenReturn("redirect:/empresas");
    }

}
