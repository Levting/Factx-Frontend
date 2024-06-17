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
    public BillingController(
            BillingService billingService, CustomerService customerService,
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
        // Añadir un nuevo objeto Factura al modelo
        model.addAttribute("factura", new BillingModel());

        // Añadir el usuario al modelo
        UserModel usuario = (UserModel) model.getAttribute("usuario");
        model.addAttribute("usuario", usuario);

        // Obtener los clientes y añadirlos al modelo
        return customerService.obtenerClientes().collectList()
                .doOnNext(clientes -> model.addAttribute("clientes", clientes))
                .thenReturn("facturacion/crear_factura");
    }

    @PostMapping("/factura/abrir")
    public Mono<String> abrirFactura(@ModelAttribute("factura") BillingModel billingModel, Model model) {

        Integer id_usuario = billingModel.getUsuario().getId_usuario();
        Integer id_cliente = billingModel.getCliente().getId_cliente();

        // Añadir el estado de la factura abiera para mostrar el formulario de detalles
        model.addAttribute("facturaAbierta", true);

        // Abrir la factura y añadir la factura al modelo
        return billingService.abrirFactura(id_usuario, id_cliente)
                .flatMap(facturaAbierta -> {
                    // Crear un nuevo modelo de detalle de factura
                    BillDetailModel billDetailModel = new BillDetailModel();
                    billDetailModel.setFactura(facturaAbierta);

                    // Añadir el detalle de la factura al modelo
                    model.addAttribute("detalle", billDetailModel);

                    // Añadir la factura al modelo
                    model.addAttribute("factura", facturaAbierta);

                    // Obtener el cliente y añadirlo al modelo
                    return customerService.obtenerCliente(id_cliente)
                            // Añadir el cliente al modelo despues de actualizar la factura (mostrar en el
                            // buscador desabilitado)
                            .doOnNext(cliente -> model.addAttribute("cliente", cliente))
                            // Obtener los productos y añadirlos al modelo
                            .then(productService.obtenerProductos().collectList()
                                    .doOnNext(productos -> model.addAttribute("productos", productos)))
                            // Añadir las formas de pago al modelo
                            .then(wayPayService.obtenerFormasPago().collectList()
                                    .doOnNext(formas_pago -> model.addAttribute("formas_pago", formas_pago)));
                }).thenReturn("facturacion/crear_factura");
    }

    @PostMapping("/factura/detalle")
    @ResponseBody
    public Mono<Map<String, Object>> añadirDetalleFactura(@RequestBody Map<String, String> detalleFactura) {

        Integer facturaID = Integer.parseInt(detalleFactura.get("id_factura"));
        Integer productoID = Integer.parseInt(detalleFactura.get("id_producto"));
        Integer cantidad = Integer.parseInt(detalleFactura.get("cantidad"));

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

    @PostMapping("/factura/cerrar")
    public Mono<String> cerrarFactura(
            @ModelAttribute("factura") BillingModel billingModel,
            Model model) {
        int idFactura = billingModel.getIdFactura();
        int idFormaPago = billingModel.getFormaPago().getId_forma_pago();
        System.out.println("Factura Cerrada: ID Factura = " + idFactura + ", ID Forma de Pago = " + idFormaPago);

        // Cerrar la factura y redicreccionar a la vista de factura
        return billingService.cerrarFactura(idFactura, idFormaPago)
                .doOnSuccess(success -> System.out.println("Factura Cerrada!"))
                .doOnError(error -> System.out.println("Error al cerrar la factura: " + error.getMessage()))
                .thenReturn("redirect:/facturacion");
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

    @GetMapping("/productos/{id}")
    @ResponseBody
    public Mono<ProductModel> obtenerProducto(@PathVariable("id") Integer id_producto) {
        return productService.obtenerProducto(id_producto);
    }

}
