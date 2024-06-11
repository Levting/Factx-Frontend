package com.levting.FactxFrontend.controller;

import com.levting.FactxFrontend.model.*;
import com.levting.FactxFrontend.service.BillingService;
import com.levting.FactxFrontend.service.CustomerService;
import com.levting.FactxFrontend.service.ProductService;
import com.levting.FactxFrontend.service.WayPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
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

    @PostMapping("/factura/abrir")
    public Mono<String> facturaAbierta(@ModelAttribute("factura") BillingModel billingModel, Model model) {
        Integer id_usuario = billingModel.getUsuario().getId_usuario();
        Integer id_cliente = billingModel.getCliente().getId_cliente();

        model.addAttribute("facturaAbierta", true);

        return billingService.abrirFactura(id_usuario, id_cliente)
                .flatMap(facturaAbierta -> {
                    BillDetailModel billDetailModel = new BillDetailModel();
                    billDetailModel.setFactura(facturaAbierta);
                    model.addAttribute("factura", facturaAbierta);
                    model.addAttribute("detalle", billDetailModel);

                    // Obtener el cliente y añadirlo al modelo
                    return customerService.obtenerCliente(id_cliente)
                            .doOnNext(clienteModel -> model.addAttribute("cliente", clienteModel))
                            .then(productService.obtenerProductos().collectList()
                                    .doOnNext(productos -> model.addAttribute("productos", productos)))
                            .then(wayPayService.obtenerFormasPago().collectList()
                                    .doOnNext(formas_pago -> model.addAttribute("formas_pago", formas_pago)));
                }).thenReturn("facturacion/crear_factura");
    }

    @GetMapping("/productos/buscar")
    @ResponseBody
    public Flux<ProductModel> buscarProductos(@RequestParam("query") String query) {
        return productService.obtenerProductoNombreOcurrente(query);
    }

    @GetMapping("/productos/{id}")
    @ResponseBody
    public Mono<ProductModel> obtenerProducto(@PathVariable("id") Integer id_producto) {
        return productService.obtenerProducto(id_producto);
    }

    @PostMapping("/factura/detalle")
    @ResponseBody
    public Mono<Map<String, Object>> añadirDetalleFactura(@RequestBody Map<String, String> detalleFactura) {

        Integer facturaID = Integer.parseInt(detalleFactura.get("id_factura"));
        Integer productoID = Integer.parseInt(detalleFactura.get("id_producto"));
        Integer cantidad = Integer.parseInt(detalleFactura.get("cantidad"));

        System.out.println("Factura ID: " + facturaID);
        System.out.println("Producto ID: " + productoID);
        System.out.println("Cantidad: " + cantidad);

        return billingService.anadirDetalles(facturaID, productoID, cantidad)
                .map(detalle -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("detalle", detalle);
                    return response;
                })
                .onErrorResume(error -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Error al añadir el detalle a la factura: " + error.getMessage());
                    return Mono.just(response);
                });
    }
}
