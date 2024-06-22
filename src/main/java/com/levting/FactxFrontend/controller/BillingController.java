package com.levting.FactxFrontend.controller;

import com.levting.FactxFrontend.model.*;
import com.levting.FactxFrontend.service.BillingService;
import com.levting.FactxFrontend.service.CustomerService;
import com.levting.FactxFrontend.service.ProductService;
import com.levting.FactxFrontend.service.WayPayService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/facturacion")
public class BillingController {

    // Este controlador se encarga de manejar las peticiones relacionadas con la
    // facturación de la aplicación

    // Servicios
    private final BillingService billingService;
    private final CustomerService customerService;
    private final ProductService productService;
    private final WayPayService wayPayService;

    /**
     * Constructor de la clase BillingController (Controlador de la Factura)
     *
     * @param billingService
     * @param customerService
     * @param productService
     * @param wayPayService
     */
    @Autowired
    public BillingController(
            BillingService billingService, CustomerService customerService,
            ProductService productService, WayPayService wayPayService) {
        this.billingService = billingService;
        this.customerService = customerService;
        this.productService = productService;
        this.wayPayService = wayPayService;
    }

    /**
     * Mostrar la página de facturación
     *
     * @param model
     * @return
     */
    @GetMapping
    public Mono<String> mostrarPaginaFacturacion(Model model, ServerWebExchange serverWebExchange) {
        // Obtener el usuario del modelo (si esta logueado, si no regresa a iniciar
        // sesión)
        UserModel usuario = (UserModel) model.getAttribute("usuario");
        if (usuario != null) {

            return serverWebExchange.getSession()
                    .flatMap(session -> {
                        // Añadir los mensajes de éxito o error al modelo
                        model.addAttribute("successMessage", session.getAttribute("successMessage"));
                        model.addAttribute("errorMessage", session.getAttribute("errorMessage"));

                        // Limpiar los mensajes de la sesión
                        session.getAttributes().remove("successMessage");
                        session.getAttributes().remove("errorMessage");

                        // Obtener las facturas y añadirlas al modelo
                        return billingService.obtenerFacturas().collectList();
                    }).doOnNext(facturas -> {
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

    /**
     * Mostrar el formulario para crear una factura
     *
     * @param model
     * @return
     */
    @GetMapping("/facturas/crear")
    public Mono<String> mostrarFormularioCrearFactura(Model model) {
        UserModel usuario = (UserModel) model.getAttribute("usuario");
        if (usuario != null) {
            // Añadir un nuevo objeto Factura al modelo
            model.addAttribute("factura", new BillingModel());

            // Añadir el usuario al modelo
            model.addAttribute("usuario", usuario);

            // Obtener los clientes y añadirlos al modelo
            return customerService.obtenerClientes().collectList()
                    .doOnNext(clientes -> model.addAttribute("clientes", clientes))
                    .thenReturn("facturacion/crear_factura");
        } else {
            return Mono.just("redirect:/inicio_sesion");
        }

    }

    /**
     * Abrir una factura
     *
     * @param billingModel
     * @param model
     * @return
     */
    @PostMapping("/factura/abrir")
    public Mono<String> abrirFactura(@ModelAttribute("factura") BillingModel billingModel, Model model) {

        UserModel usuario = (UserModel) model.getAttribute("usuario");
        if (usuario == null) {
            return Mono.just("redirect:/inicio_sesion");
        }

        // Obtener el id del usuario y el id del cliente del modelo factura al enviar la
        // petición post
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

    /**
     * Añadir un detalle a la factura
     *
     * @param detalleFactura
     * @return
     */
    @PostMapping("/factura/detalle")
    @ResponseBody
    public Mono<Map<String, Object>> añadirDetalleFactura(@RequestBody Map<String, String> detalleFactura) {

        // Obtener los datos del detalle de la factura
        Integer facturaID = Integer.parseInt(detalleFactura.get("id_factura"));
        Integer productoID = Integer.parseInt(detalleFactura.get("id_producto"));
        Integer cantidad = Integer.parseInt(detalleFactura.get("cantidad"));

        // Añadir el detalle a la factura llamando al servicio de facturación
        return billingService.anadirDetalles(facturaID, productoID, cantidad)
                .map(detalle -> {
                    // Crear un mapa de respuesta, en caso de éxito
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("detalle", detalle); // Añadir el detalle a la respuesta para obtenerla en AJAX
                    return response;
                })
                .onErrorResume(error -> {
                    // Crear un mapa de respuesta, en caso de error
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Error al añadir el detalle a la factura: " + error.getMessage());
                    return Mono.just(response);
                });
    }

    /**
     * Cerrar Factura
     *
     * @param billingModel
     * @param model
     * @return
     */
    @PostMapping("/factura/cerrar")
    public Mono<String> cerrarFactura(
            @ModelAttribute("factura") BillingModel billingModel,
            Model model, ServerWebExchange serverWebExchange) {

        // Obtener el id de la factura y el id de la forma de pago del modelo factura al
        // enviar la petición post
        int idFactura = billingModel.getIdFactura();
        int idFormaPago = billingModel.getFormaPago().getId_forma_pago();

        // Mostrar por consola la factura que se va a cerrar y su forma de pago
        System.out.println("Factura Cerrada: ID Factura = " + idFactura + ", ID Forma de Pago = " + idFormaPago);

        // Cerrar la factura y redicreccionar a la vista de factura
        return billingService.cerrarFactura(idFactura, idFormaPago)
                // Añadir los mensajes de éxito a la vista
                .doOnSuccess(facturaCerrada -> serverWebExchange.getSession()
                        .doOnNext(session -> session.getAttributes()
                                .put("successMessage", "Factura Cerrada!"))
                        .subscribe())

                // Añadir los mensajes de error a la vista
                .doOnError(error -> {
                    serverWebExchange.getSession()
                            .doOnNext(session -> session.getAttributes()
                                    .put("errorMessage", "Error al cerrar la factura"))
                            .subscribe();
                }).thenReturn("redirect:/facturacion");
        /*
         * .doOnSuccess(success -> System.out.println("Factura Cerrada!"))
         * .doOnError(error -> System.out.println("Error al cerrar la factura: " +
         * error.getMessage()))
         * .thenReturn("redirect:/facturacion");
         */
    }

    /**
     * Mostrar el PDF de la factura
     *
     * @param id_factura
     * @return
     */

    @Value("${factura.pdf.directory:/uploads/documents/pdf}")
    private String pdfDirectory;

    @GetMapping("/factura/pdf/{id}")
    public Mono<ResponseEntity<Object>> mostrarPDF(@PathVariable("id") Integer id_factura) {
        return billingService.obtenerFactura(id_factura)
                .flatMap(factura -> {
                    String claveAcceso = factura.getClave_acceso();
                    // String pdfUrl = "http://localhost:8080/uploads/documents/pdf/" + claveAcceso
                    // + ".pdf";
                    String pdfUrl = "http://34.74.207.191:8081/uploads/documents/pdf/" + claveAcceso + ".pdf";

                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(URI.create(pdfUrl));

                    return Mono.just(new ResponseEntity<>(headers, HttpStatus.FOUND));
                })
                .switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    /**
     * Buscar clientes por nombre, usado para mostrar los clientes en el buscador
     *
     * @param query
     * @return
     */
    @GetMapping("/clientes/buscar")
    @ResponseBody
    public Flux<CustomerModel> buscarClientes(@RequestParam("query") String query) {
        return customerService.obtenerClienteNombreOcurrente(query);
    }

    /**
     * Buscar productos por nombre, usado para mostrar los productos en el buscador
     *
     * @param query
     * @return
     */
    @GetMapping("/productos/buscar")
    @ResponseBody
    public Flux<ProductModel> buscarProductos(@RequestParam("query") String query) {
        return productService.obtenerProductoNombreOcurrente(query);
    }

    /**
     * Obtener un producto por su id, usado para contruir el producto en el detalle
     * para mostar el icono.
     *
     * @param id_producto
     * @return
     */
    @GetMapping("/productos/{id}")
    @ResponseBody
    public Mono<ProductModel> obtenerProducto(@PathVariable("id") Integer id_producto) {
        return productService.obtenerProducto(id_producto);
    }

}
