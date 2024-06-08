package com.levting.FactxFrontend.controller;

import com.levting.FactxFrontend.model.*;
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

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/facturacion")
public class BillingController {

    private final BillingService billingService;
    private final CustomerService customerService;
    private final ProductService productService;
    private final WayPayService wayPayService;

    @Autowired
    public BillingController(BillingService billingService, CustomerService customerService,
            ProductService productService, WayPayService wayPayService) {
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

    // FORMULARIOS
    @GetMapping("/facturas/crear")
    public Mono<String> mostrarFormularioCrearFactura(Model model) {
        model.addAttribute("factura", new BillingModel());

        UserModel usuario = (UserModel) model.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        return customerService.obtenerClientes().collectList()
                .doOnNext(clientes -> model.addAttribute("clientes", clientes))
                .thenReturn("facturacion/crear_factura");
    }

    @GetMapping("/clientes/buscar")
    @ResponseBody
    public Flux<CustomerModel> buscarClientes(@RequestParam("query") String query) {
        return customerService.obtenerClienteNombreOcurrente(query);
    }

    @GetMapping("/productos/buscar")
    @ResponseBody
    public Flux<ProductModel> buscarProductos(@RequestParam("query") String query) {
        return productService.obtenerProductoNombreOcurrente(query);
    }

    @PostMapping("/factura/abrir")
    @ResponseBody
    public Mono<Map<String, Object>> abrirFactura(@ModelAttribute("factura") BillingModel billingModel, Model model) {
        UserModel usuario = (UserModel) model.getAttribute("usuario");
        if (usuario != null && billingModel.getCliente() != null) {
            int id_usuario = usuario.getId_usuario();
            int id_cliente = billingModel.getCliente().getId_cliente();

            return billingService.abrirFactura(id_usuario, id_cliente)
                    .map(factura -> {

                        // Crear el modelo del detalle y guardarlo en el modelo de la vista
                        BillDetailModel billDetailModel = new BillDetailModel();
                        billDetailModel.setFactura(factura); // Usar la factura retornada por el servicio

                        // Devolver el ID de la factura y el detalle de la factura como respuesta
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "Factura abierta correctamente");
                        response.put("factura", billingModel);
                        response.put("detalleFactura", billDetailModel);

                        return response;
                    })
                    .onErrorResume(error -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("message", "Error al abrir la factura: " + error.getMessage());
                        return Mono.just(response);
                    });
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Usuario o Cliente no encontrado.");
            return Mono.just(response);
        }
    }
}
