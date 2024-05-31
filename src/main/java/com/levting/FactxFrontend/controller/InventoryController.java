package com.levting.FactxFrontend.controller;

import com.levting.FactxFrontend.model.CategoryModel;
import com.levting.FactxFrontend.model.ProductModel;
import com.levting.FactxFrontend.service.CategoryService;
import com.levting.FactxFrontend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;


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

    @PostMapping(value = "/productos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> guardarProducto(@RequestPart("producto") String producto,
                                        @RequestPart("descripcion") String descripcion,
                                        @RequestPart("categoria.id_categoria") String idCategoria,
                                        @RequestPart("precio") String precio,
                                        @RequestPart("cantidad") String cantidad,
                                        @RequestPart("icono") FilePart icono) {

        int id_categoria = Integer.parseInt(idCategoria);
        double precio_double = Double.parseDouble(precio);
        int cantidad_int = Integer.parseInt(cantidad);

        ProductModel productModel = new ProductModel();
        productModel.setProducto(producto);
        productModel.setDescripcion(descripcion);
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.setId_categoria(id_categoria);
        productModel.setCategoria(categoryModel);
        productModel.setPrecio(precio_double);
        productModel.setCantidad(cantidad_int);

        return productService.guardarProducto(productModel, icono)
                .doOnSuccess(success -> System.out.println("Producto Añadido con Éxito!"))
                .doOnError(error -> System.out.println("Error al Añadir el Producto"))
                .thenReturn("redirect:/inventario/productos");
    }


}

