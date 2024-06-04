package com.levting.FactxFrontend.controller;

import com.levting.FactxFrontend.model.BillingModel;
import com.levting.FactxFrontend.model.CustomerModel;
import com.levting.FactxFrontend.model.UserModel;
import com.levting.FactxFrontend.service.BillingService;
import com.levting.FactxFrontend.service.CustomerService;
import com.levting.FactxFrontend.service.ProductService;
import com.levting.FactxFrontend.service.WayPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/facturacion")
public class BillingController {

    private final BillingService billingService;
    private final CustomerService customerService;
    private final ProductService productService;
    private final WayPayService wayPayService;


    @Autowired
    public BillingController(BillingService billingService, CustomerService customerService, ProductService productService, WayPayService wayPayService) {
        this.billingService = billingService;
        this.customerService = customerService;
        this.productService = productService;
        this.wayPayService = wayPayService;
    }

    @GetMapping
    public Mono<String> mostrarPaginaFacturacion(Model model) {
        UserModel usuario = (UserModel) model.getAttribute("usuario");
        if (usuario != null) {
            return billingService.obtenerFacturas()
                    .collectList()
                    .doOnNext(facturas -> {
                        if (facturas.isEmpty()) {
                            System.out.println("No existen Facturas!");
                        } else {
                            model.addAttribute("facturas", facturas);
                        }
                    }).thenReturn("facturacion/facturas");
        } else {
            return Mono.just("redirect:/inicio_sesion");
        }
    }

    @GetMapping("/facturas/crear")
    public Mono<String> mostrarFormularioCrearFactura(Model model) {
        model.addAttribute("factura", new BillingModel());
        return customerService.obtenerClientes().collectList()
                .doOnNext(clientes -> model.addAttribute("clientes", clientes))
                .thenReturn("facturacion/crear_factura");
    }

    @GetMapping("/clientes/buscar")
    @ResponseBody
    public Flux<CustomerModel> buscarClientes(@RequestParam("query") String query) {
        return customerService.obtenerClienteNombreOcurrente(query);
    }

    @PostMapping("/factura/abrir")
    public void abrirFactura(@ModelAttribute("factura") BillingModel billingModel, Model model) {
        // Obtener el usuario del modelo
        UserModel usuario = (UserModel) model.getAttribute("usuario");
        if (usuario != null) {
            // Realizar cualquier procesamiento necesario con el usuario
            System.out.println("Usuario: " + usuario.getNombre());
            // Obtener el ID del cliente
            System.out.println("ID del cliente: " + billingModel.getCliente().getNombre());
        } else {
            // Manejar el caso si el usuario no está presente
            System.out.println("Usuario no encontrado.");
        }
    }

}
