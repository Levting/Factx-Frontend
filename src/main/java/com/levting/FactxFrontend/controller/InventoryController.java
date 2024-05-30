package com.levting.FactxFrontend.controller;

import com.levting.FactxFrontend.model.ProductModel;
import com.levting.FactxFrontend.service.CategoryService;
import com.levting.FactxFrontend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;


@Controller
@RequestMapping("/inventario")
public class InventoryController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @Autowired
    public InventoryController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/productos")
    public Mono<String> mostrarVistaInventario(Model model) {
        return productService.obtenerProductos()
                .collectList()
                .doOnNext(productos -> {
                    if (productos.isEmpty()) {
                        System.out.println("No hay Productos!");
                    }
                    System.out.println(productos);
                    model.addAttribute("productos", productos);
                }).thenReturn("productos/productos");
    }

    @GetMapping("/productos/crear")
    public Mono<String> mostrarFormularioCrearProductos(Model model) {
        model.addAttribute("producto", new ProductModel());
        return categoryService.obtenerCategorias()
                .collectList()
                .map(categorias -> {
                    model.addAttribute("categorias", categorias);
                    return "productos/crear_producto";
                });
    }

    @PostMapping("/productos")
    public Mono<Void> guardarProducto(@ModelAttribute("producto") ProductModel productModel) {
        System.out.println("Producto: " + productModel.toString());

        // Extrae el icono del producto
        FilePart icono = productModel.getIcono();

        // Crea un nuevo objeto que contenga todas las propiedades del producto excepto el icono
        ProductModel productWithoutIcon = new ProductModel();
        productWithoutIcon.setProducto(productModel.getProducto());
        productWithoutIcon.setDescripcion(productModel.getDescripcion());
        // Continúa con el resto de las propiedades...

        // Llama al método del servicio para guardar el producto e icono
        // return productService.guardarProducto(productWithoutIcon, icono);
        return Mono.empty();
    }

}
