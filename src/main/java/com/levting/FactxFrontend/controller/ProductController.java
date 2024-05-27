package com.levting.FactxFrontend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.levting.FactxFrontend.model.ProductModel;
import com.levting.FactxFrontend.service.CategoryService;
import com.levting.FactxFrontend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;


@Controller
@RequestMapping("/productos")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/crear")
    public Mono<String> mostrarFormularioCrearProductos(Model model) {
        model.addAttribute("producto", new ProductModel());
        return categoryService.obtenerCategorias()
                .collectList()
                .map(categorias -> {
                    model.addAttribute("categorias", categorias);
                    System.out.println("Categorias: " + categorias);
                    return "productos/crear_producto";
                });
    }

    @RequestMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void guardarProducto(@ModelAttribute("producto") ProductModel productModel) {
        System.out.println("ICONO: " + productModel.getIcono() + "\n\n");
    }
}
