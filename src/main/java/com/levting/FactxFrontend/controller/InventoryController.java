package com.levting.FactxFrontend.controller;

import com.levting.FactxFrontend.model.CategoryModel;
import com.levting.FactxFrontend.model.ProductModel;
import com.levting.FactxFrontend.service.CategoryService;
import com.levting.FactxFrontend.service.CompanyService;
import com.levting.FactxFrontend.service.IVAService;
import com.levting.FactxFrontend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@Controller
@RequestMapping("/inventario")
public class InventoryController {

    // Este controlador gestionara a los productos, categorías e IVAs.

    private final ProductService productService;
    private final CategoryService categoryService;
    private final IVAService ivaService;
    private final CompanyService companyService;

    /**
     * Controlador de los productos
     */

    @Autowired
    public InventoryController(ProductService productService, CategoryService categoryService, IVAService ivaService, CompanyService companyService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.ivaService = ivaService;
        this.companyService = companyService;
    }

    @GetMapping({"/productos", ""})
    public Mono<String> mostrarVistaInventario(Model model) {
        return productService.obtenerProductos()
                .collectList()
                .doOnNext(productos -> {
                    if (productos.isEmpty()) {
                        System.out.println("No hay Productos!");
                    }
                    model.addAttribute("productos", productos);
                }).thenReturn("inventario/productos");
    }

    @GetMapping("/productos/crear")
    public Mono<String> mostrarFormularioCrearProductos(Model model) {
        model.addAttribute("producto", new ProductModel());
        return categoryService.obtenerCategorias()
                .collectList()
                .map(categorias -> {
                    model.addAttribute("categorias", categorias);
                    return "inventario/crear_producto";
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


    @GetMapping("/productos/editar/{id}")
    public Mono<String> mostrarFormularioEditarProducto(@PathVariable Integer id, Model model) {
        return Mono.zip(
                productService.obtenerProducto(id),
                categoryService.obtenerCategorias().collectList()
        ).doOnNext(tuple -> {
            model.addAttribute("producto", tuple.getT1());
            model.addAttribute("categorias", tuple.getT2());
        }).thenReturn("inventario/editar_producto");
    }

    @PostMapping(value = "/productos/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> editarProducto(@PathVariable Integer id,
                                       @RequestPart("producto") String producto,
                                       @RequestPart("descripcion") String descripcion,
                                       @RequestPart("categoria.id_categoria") String idCategoria,
                                       @RequestPart("precio") String precio,
                                       @RequestPart("cantidad") String cantidad,
                                       @RequestPart("icono") FilePart icono) {

        return productService.obtenerProducto(id)
                .flatMap(existingProduct -> {

                    int id_categoria = Integer.parseInt(idCategoria);
                    double precio_double = Double.parseDouble(precio);
                    int cantidad_int = Integer.parseInt(cantidad);

                    existingProduct.setProducto(producto);
                    existingProduct.setDescripcion(descripcion);
                    CategoryModel categoryModel = new CategoryModel();
                    categoryModel.setId_categoria(id_categoria);
                    existingProduct.setCategoria(categoryModel);
                    existingProduct.setPrecio(precio_double);
                    existingProduct.setCantidad(cantidad_int);

                    return productService.guardarProducto(existingProduct, icono);
                })
                .doOnError(error -> System.err.println("Error al Editar: " + error.getMessage()))
                .doOnSuccess(success -> System.out.println("Producto Editado con Éxito!"))
                .thenReturn("redirect:/inventario/productos");
    }

    @GetMapping("/productos/{id}")
    public Mono<String> eliminarProducto(@PathVariable Integer id) {
        return productService.eliminarProducto(id)
                .doOnError(error -> System.err.println("Error al Eliminar: " + error.getMessage()))
                .doOnSuccess(aVoid -> System.out.println("Producto Eliminado con Éxito!"))
                .thenReturn("redirect:/inventario/productos");
    }

    /**
     * Controlador para las Categorías
     */

    @GetMapping("/categorias")
    public Mono<String> obtenerCategorias(Model model) {
        return categoryService.obtenerCategorias()
                .collectList()
                .doOnNext(categorias -> model.addAttribute("categorias", categorias))
                .thenReturn("inventario/categorias");

    }

    @PostMapping("/categorias")
    public Mono<String> guardarCategoria(@ModelAttribute("categoria") CategoryModel categoryModel) {
        return categoryService.guardarCategoria(categoryModel)
                .doOnError(error -> System.out.println("Error al Guardar una Categoria!" + error.getMessage()))
                .doOnSuccess(sucess -> System.out.println("Guardado Exitoso!"))
                .thenReturn("redirect:/inventario/categorias");
    }

    @PostMapping("/categorias/{id}")
    public Mono<String> editarUsuario(@PathVariable Integer id, @ModelAttribute("categoria") CategoryModel categoryModel) {
        return categoryService.obtenerCategoria(id)
                .flatMap(categoriaExistente -> {
                    categoriaExistente.setCategoria(categoryModel.getCategoria());
                    categoriaExistente.setIva(categoryModel.getIva());
                    categoriaExistente.setEmpresa(categoryModel.getEmpresa());
                    return categoryService.guardarCategoria(categoriaExistente);
                })
                .doOnError(error -> System.err.println("Error al editar: " + error.getMessage()))
                .doOnSuccess(success -> System.out.println("Edicion Exitosa!"))
                .thenReturn("redirect:/inventario/categorias");

    }

    @GetMapping("/categorias/{id}")
    public Mono<String> eliminarCategoria(@PathVariable Integer id) {
        return categoryService.eliminarCategoria(id)
                .doOnError(error -> System.err.println("Error al Eliminar" + error.getMessage()))
                .doOnSuccess(success -> System.out.println("Eliminacion Exitosa!"))
                .thenReturn("redirect:/inventario/categorias");
    }

    // FORMULARIOS
    @GetMapping("/categorias/crear")
    public Mono<String> mostrarFormularioCrearCategoria(Model model) {
        model.addAttribute("categoria", new CategoryModel());
        return Mono.zip(
                ivaService.obtenerIVAs().collectList(),
                companyService.obtenerEmpresas().collectList()
        ).doOnNext(tuple -> {
            model.addAttribute("ivas", tuple.getT1());
            model.addAttribute("empresas", tuple.getT2());
        }).thenReturn("inventario/crear_categoria");
    }

    @GetMapping("/categorias/editar/{id}")
    public Mono<String> mostrarFormularioEditarCategoria(@PathVariable Integer id, Model model) {
        return Mono.zip(
                categoryService.obtenerCategoria(id),
                ivaService.obtenerIVAs().collectList(),
                companyService.obtenerEmpresas().collectList()
        ).doOnNext(tuple -> {
            model.addAttribute("categoria", tuple.getT1());
            model.addAttribute("ivas", tuple.getT2());
            model.addAttribute("empresas", tuple.getT3());
        }).thenReturn("inventario/editar_categoria");


    }
}



