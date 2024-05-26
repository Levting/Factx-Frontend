package com.levting.FactxFrontend.controller;

import com.levting.FactxFrontend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;


@Controller
@RequestMapping("/inventario")
public class InventoryController {

    private final ProductService productService;

    @Autowired
    public InventoryController(ProductService productService) {
        this.productService = productService;

    }

    @GetMapping("")
    public Mono<String> mostrarVistaInventario(Model model) {
        return productService.obtenerProductos()
                .collectList()
                .doOnNext(productos -> {
                    if (productos.isEmpty()) {
                        System.out.println("No hay Productos!");
                    }
                    System.out.println(productos);
                    model.addAttribute("productos", productos);
                }).thenReturn("administradores/inventario");
    }
}
