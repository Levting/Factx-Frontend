package com.levting.FactxFrontend.controller;

import com.levting.FactxFrontend.model.BillingModel;
import com.levting.FactxFrontend.model.ProductModel;
import com.levting.FactxFrontend.service.BillingService;
import com.levting.FactxFrontend.service.CustomerService;
import com.levting.FactxFrontend.service.ProductService;
import com.levting.FactxFrontend.service.WayPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
        return billingService.obtenerFacturas()
                .collectList()
                .doOnNext(facturas -> {
                    if (facturas.isEmpty()) {
                        System.out.println("No existen Facturas!");
                    } else {
                        model.addAttribute("facturas", facturas);
                    }
                }).thenReturn("facturacion/facturas");
    }

    @GetMapping("/facturas/crear")
    public Mono<String> mostrarFormularioCrearFactura(Model model) {
        model.addAttribute("factura", new BillingModel());
        return Mono.zip(
                customerService.obtenerClientes().collectList(),
                productService.obtenerProductos().collectList(),
                wayPayService.obtenerFormasPago().collectList()
        ).doOnNext(tuple -> {
            model.addAttribute("clientes", tuple.getT1());
            model.addAttribute("productos", tuple.getT2());
            model.addAttribute("formas_pago", tuple.getT3());
        }).thenReturn("facturacion/crear_factura");
    }
}
