package com.levting.FactxFrontend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
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

    /**
     * Mostrar la vista de las empresas
     * 
     * @param model
     * @param serverWebExchange
     * @return
     */
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

    /**
     * Guardar una empresa
     * 
     * @param serverWebExchange
     * @param ruc
     * @param razon_social
     * @param nombre_comercial
     * @param telefono
     * @param direccion
     * @param logo
     * @param tipo_contribuyente
     * @param lleva_contabilidad
     * @param firma_electronica
     * @param contrasena_firma_electronica
     * @param desarrollo
     * @return
     */
    @PostMapping(value = "/empresas", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> guardarEmpresa(
            ServerWebExchange serverWebExchange,

            @RequestPart("ruc") String ruc,
            @RequestPart("razon_social") String razon_social,
            @RequestPart("nombre_comercial") String nombre_comercial,
            @RequestPart("telefono") String telefono,
            @RequestPart("direccion") String direccion,
            @RequestPart("logo") FilePart logo,
            @RequestPart("tipo_contribuyente") String tipo_contribuyente,
            @RequestPart("lleva_contabilidad") String lleva_contabilidad,
            @RequestPart("firma_electronica") FilePart firma_electronica,
            @RequestPart("contrasena_firma_electronica") String contrasena_firma_electronica,
            @RequestPart("desarrollo") String desarrollo) {

        // Contruccion del objeto empresa sin el logo y la firma electronica
        CompanyModel empresa = new CompanyModel();
        empresa.setRuc(ruc);
        empresa.setRazon_social(razon_social);
        empresa.setNombre_comercial(nombre_comercial);
        empresa.setDireccion(direccion);
        empresa.setTelefono(telefono);
        empresa.setTipo_contribuyente(tipo_contribuyente);
        empresa.setLleva_contabilidad(Boolean.parseBoolean(lleva_contabilidad));
        empresa.setContrasena_firma_electronica(contrasena_firma_electronica);
        empresa.setDesarrollo(Boolean.parseBoolean(desarrollo));

        return companyService.guardarEmpresa(empresa, logo, firma_electronica)
                .doOnSuccess(success -> serverWebExchange.getSession()
                        .doOnNext(
                                session -> session.getAttributes().put("successMessage", "Empresa Guardada con Éxito!"))
                        .subscribe())

                .doOnError(error -> serverWebExchange.getSession()
                        .doOnNext(
                                session -> session.getAttributes().put("errorMessage", "Error al Guardar la Empresa!"))
                        .subscribe())

                .thenReturn("redirect:/empresas");

    }

    /**
     * Mostrar el formulario para editar una empresa
     * 
     * @param id
     * @param model
     * @return
     */
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

    /**
     * Editar una empresa
     * 
     * @param id
     * @param serverWebExchange
     * @param ruc
     * @param razon_social
     * @param nombre_comercial
     * @param telefono
     * @param direccion
     * @param logo
     * @param tipo_contribuyente
     * @param lleva_contabilidad
     * @param firma_electronica
     * @param contrasena_firma_electronica
     * @param desarrollo
     * @return
     */
    @PostMapping(value = "/empresas/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> editarEmpresa(
            @PathVariable Integer id, ServerWebExchange serverWebExchange,

            @RequestPart("ruc") String ruc,
            @RequestPart("razon_social") String razon_social,
            @RequestPart("nombre_comercial") String nombre_comercial,
            @RequestPart("telefono") String telefono,
            @RequestPart("direccion") String direccion,
            @RequestPart("logo") FilePart logo,
            @RequestPart("tipo_contribuyente") String tipo_contribuyente,
            @RequestPart("lleva_contabilidad") String lleva_contabilidad,
            @RequestPart("firma_electronica") FilePart firma_electronica,
            @RequestPart("contrasena_firma_electronica") String contrasena_firma_electronica,
            @RequestPart("desarrollo") String desarrollo) {

        return companyService.obtenerEmpresa(id)
                .flatMap(existingCompany -> {
                    // Actualizar la empresa
                    existingCompany.setRuc(ruc);
                    existingCompany.setRazon_social(razon_social);
                    existingCompany.setNombre_comercial(nombre_comercial);
                    existingCompany.setDireccion(direccion);
                    existingCompany.setTelefono(telefono);
                    existingCompany.setTipo_contribuyente(tipo_contribuyente);
                    existingCompany.setLleva_contabilidad(Boolean.parseBoolean(lleva_contabilidad));
                    existingCompany.setContrasena_firma_electronica(contrasena_firma_electronica);
                    existingCompany.setDesarrollo(Boolean.parseBoolean(desarrollo));

                    return companyService.guardarEmpresa(existingCompany, logo,
                            firma_electronica)
                            .doOnSuccess(success -> serverWebExchange.getSession()
                                    .doOnNext(
                                            session -> session.getAttributes().put("successMessage",
                                                    "Empresa Actualizada con Éxito!"))
                                    .subscribe())

                            .doOnError(error -> serverWebExchange.getSession()
                                    .doOnNext(
                                            session -> session.getAttributes().put("errorMessage",
                                                    "Error al Actualizar la Empresa!"))
                                    .subscribe());

                }).thenReturn("redirect:/empresas");

    }

    /**
     * Eliminar una empresa
     * 
     * @param id
     * @param serverWebExchange
     * @return
     */
    @GetMapping("/empresas/{id}")
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
