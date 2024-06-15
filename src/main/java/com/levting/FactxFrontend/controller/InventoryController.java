package com.levting.FactxFrontend.controller;

import com.levting.FactxFrontend.model.CategoryModel;
import com.levting.FactxFrontend.model.ProductModel;
import com.levting.FactxFrontend.model.UserModel;
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
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/inventario")
public class InventoryController {

    // Este controlador se encarga de manejar las peticiones relacionadas con el
    // inventario, productos, categorías, iva, y empresas.

    // Inyección de dependencias
    private final ProductService productService;
    private final CategoryService categoryService;
    private final IVAService ivaService;
    private final CompanyService companyService;

    /**
     * Controlador de los productos
     */

    @Autowired
    public InventoryController(
            ProductService productService, CategoryService categoryService,
            IVAService ivaService, CompanyService companyService) {

        this.productService = productService;
        this.categoryService = categoryService;
        this.ivaService = ivaService;
        this.companyService = companyService;
    }

    /**
     * Método para mostrar la vista de los productos
     *
     * @param model
     * @param exchange
     * @return
     */
    @GetMapping({ "/productos", "" })
    public Mono<String> mostrarVistaInventario(Model model, ServerWebExchange exchange) {

        UserModel usuario = (UserModel) model.getAttribute("usuario");

        // Si el usuario se encuentra en la sesion se le permite acceder a la vista, si
        // no se le redirige al login
        if (usuario != null) {
            return exchange.getSession()
                    .flatMap(session -> {
                        // Añadir los mensajes de éxito o error al modelo
                        model.addAttribute("successMessage", session.getAttribute("successMessage"));
                        model.addAttribute("errorMessage", session.getAttribute("errorMessage"));

                        // Limpiar los mensajes de la sesión
                        session.getAttributes().remove("successMessage");
                        session.getAttributes().remove("errorMessage");

                        // Obtener los productos y añadirlos al modelo
                        return productService.obtenerProductos().collectList();
                    })
                    .doOnNext(productos -> {
                        if (productos.isEmpty()) {
                            System.out.println("No hay Productos!");
                        }
                        model.addAttribute("productos", productos);
                    })
                    .thenReturn("inventario/productos");
        } else {
            return Mono.just("redirect:/inicio_sesion");
        }
    }

    /**
     * Método para mostrar el formulario de creación de productos
     *
     * @param model
     * @return
     */
    @GetMapping("/productos/crear")
    public Mono<String> mostrarFormularioCrearProductos(Model model) {
        // Añadir un nuevo producto al modelo
        model.addAttribute("producto", new ProductModel());

        // Obtener las categorias y añadirlas al modelo
        return categoryService.obtenerCategorias()
                .collectList()
                .map(categorias -> {
                    model.addAttribute("categorias", categorias);
                    return "inventario/crear_producto";
                });
    }

    /**
     * Método para guardar un producto
     *
     * @param producto
     * @param descripcion
     * @param idCategoria
     * @param precio
     * @param cantidad
     * @param icono
     * @return
     */
    @PostMapping(value = "/productos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> guardarProducto(
            ServerWebExchange exchange,

            @RequestPart("producto") String producto,
            @RequestPart("descripcion") String descripcion,
            @RequestPart("categoria.id_categoria") String idCategoria,
            @RequestPart("precio") String precio,
            @RequestPart("cantidad") String cantidad,
            @RequestPart("icono") FilePart icono) {

        // Convertir los valores a los tipos correctos
        int id_categoria = Integer.parseInt(idCategoria);
        double precio_double = Double.parseDouble(precio);
        int cantidad_int = Integer.parseInt(cantidad);

        // Crear un nuevo producto
        ProductModel productModel = new ProductModel();
        productModel.setProducto(producto);
        productModel.setDescripcion(descripcion);
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.setId_categoria(id_categoria);
        productModel.setCategoria(categoryModel);
        productModel.setPrecio(precio_double);
        productModel.setCantidad(cantidad_int);

        // Guardar el producto
        return productService.guardarProducto(productModel, icono)
                .doOnSuccess(success -> exchange.getSession()
                        .doOnNext(session -> session.getAttributes()
                                .put("successMessage", "Producto Añadido con Éxito!"))
                        .subscribe())
                .doOnError(error -> exchange.getSession()
                        .doOnNext(session -> session.getAttributes()
                                .put("errorMessage", "Error al Añadir el Producto!"))
                        .subscribe())
                .then(Mono.just("redirect:/inventario/productos"));
    }

    /**
     * Método para mostrar el formulario de edición de productos
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/productos/editar/{id}")
    public Mono<String> mostrarFormularioEditarProducto(@PathVariable Integer id, Model model) {
        return Mono.zip(
                productService.obtenerProducto(id),
                categoryService.obtenerCategorias().collectList()).doOnNext(tuple -> {
                    model.addAttribute("producto", tuple.getT1());
                    model.addAttribute("categorias", tuple.getT2());
                }).thenReturn("inventario/editar_producto");
    }

    /**
     * Método para editar un producto
     *
     * @param id
     * @param producto
     * @param descripcion
     * @param idCategoria
     * @param precio
     * @param cantidad
     * @param icono
     * @return
     */
    @PostMapping(value = "/productos/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> editarProducto(
            @PathVariable Integer id, ServerWebExchange serverWebExchange,
            @RequestPart("producto") String producto,
            @RequestPart("descripcion") String descripcion,
            @RequestPart("categoria.id_categoria") String idCategoria,
            @RequestPart("precio") String precio,
            @RequestPart("cantidad") String cantidad,
            @RequestPart("icono") FilePart icono) {

        return productService.obtenerProducto(id)
                .flatMap(existingProduct -> {
                    // Convertir los valores a los tipos correctos
                    int id_categoria = Integer.parseInt(idCategoria);
                    double precio_double = Double.parseDouble(precio);
                    int cantidad_int = Integer.parseInt(cantidad);

                    // Actualizar el producto
                    existingProduct.setProducto(producto);
                    existingProduct.setDescripcion(descripcion);
                    CategoryModel categoryModel = new CategoryModel();
                    categoryModel.setId_categoria(id_categoria);
                    existingProduct.setCategoria(categoryModel);
                    existingProduct.setPrecio(precio_double);
                    existingProduct.setCantidad(cantidad_int);

                    // Guardar el producto actualizado
                    return productService.guardarProducto(existingProduct, icono)
                            .doOnSuccess(success -> serverWebExchange.getSession()
                                    .doOnNext(session -> session.getAttributes()
                                            .put("successMessage", "Producto Actualizado con Éxito!"))
                                    .subscribe())
                            .doOnError(error -> serverWebExchange.getSession()
                                    .doOnNext(session -> session.getAttributes()
                                            .put("errorMessage", "Error al Actualizar el Producto!"))
                                    .subscribe());
                })
                .thenReturn("redirect:/inventario/productos");

    }

    /**
     * Método para eliminar un producto
     *
     * @param id
     * @return
     */
    @GetMapping("/productos/{id}")
    public Mono<String> eliminarProducto(@PathVariable Integer id, ServerWebExchange exchange) {
        return productService.eliminarProducto(id)
                .doOnSuccess(success -> exchange.getSession()
                        .doOnNext(session -> session.getAttributes()
                                .put("successMessage", "Producto Eliminado con Éxito!"))
                        .subscribe())
                .doOnError(error -> exchange.getSession()
                        .doOnNext(session -> session.getAttributes()
                                .put("errorMessage", "Error al Eliminar el Producto!"))
                        .subscribe())
                .thenReturn("redirect:/inventario/productos");
    }

    // -------------------- CATEGORIAS --------------------

    @GetMapping("/categorias")
    public Mono<String> mostrarVistaCategorias(Model model, ServerWebExchange exchange) {

        UserModel usuario = (UserModel) model.getAttribute("usuario");

        // Si el usuario se encuentra en la sesion se le permite acceder a la vista, si
        // no se le redirige al login
        if (usuario != null) {
            return exchange.getSession()
                    .flatMap(session -> {
                        // Añadir los mensajes de éxito o error al modelo
                        model.addAttribute("successMessage", session.getAttribute("successMessage"));
                        model.addAttribute("errorMessage", session.getAttribute("errorMessage"));

                        // Limpiar los mensajes de la sesión
                        session.getAttributes().remove("successMessage");
                        session.getAttributes().remove("errorMessage");

                        // Obtener los productos y añadirlos al modelo
                        return categoryService.obtenerCategorias().collectList();
                    })
                    .doOnNext(categorias -> {
                        if (categorias.isEmpty()) {
                            System.out.println("No hay Productos!");
                        }
                        model.addAttribute("categorias", categorias);
                    })
                    .thenReturn("inventario/categorias");
        } else {
            return Mono.just("redirect:/inicio_sesion");
        }
    }

    /**
     * Método para mostrar el formulario de creación de categorias
     * 
     * @param model
     * @return
     */
    @GetMapping("/categorias/crear")
    public Mono<String> mostrarFormularioCrearCategoria(Model model) {
        // Añadir una nueva categoria al modelo
        model.addAttribute("categoria", new CategoryModel());

        // Obtener las categorias y añadirlas al modelo
        return Mono.zip(
                ivaService.obtenerIVAs().collectList(),
                companyService.obtenerEmpresas().collectList()).doOnNext(tuple -> {
                    model.addAttribute("ivas", tuple.getT1());
                    model.addAttribute("empresas", tuple.getT2());
                }).thenReturn("inventario/crear_categoria");
    }

    /**
     * Método para guardar una categoria
     * 
     * @param categoryModel
     * @param exchange
     * @return
     */
    @PostMapping("/categorias")
    public Mono<String> guardarCategoria(@ModelAttribute("categoria") CategoryModel categoryModel,
            ServerWebExchange exchange) {
        return categoryService.guardarCategoria(categoryModel)
                .doOnSuccess(success -> exchange.getSession()
                        .doOnNext(session -> session.getAttributes()
                                .put("successMessage", "Categoria Añadida con Éxito!"))
                        .subscribe())
                .doOnError(error -> exchange.getSession()
                        .doOnNext(session -> session.getAttributes()
                                .put("errorMessage", "Error al Añadir la Categoria!"))
                        .subscribe())
                .thenReturn("redirect:/inventario/categorias");

    }

    /**
     * Método para mostrar el formulario de edición de categorias
     * 
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/categorias/editar/{id}")
    public Mono<String> mostrarFormularioEditarCategoria(@PathVariable Integer id, Model model) {
        return Mono.zip(
                categoryService.obtenerCategoria(id),
                ivaService.obtenerIVAs().collectList(),
                companyService.obtenerEmpresas().collectList()).doOnNext(tuple -> {
                    model.addAttribute("categoria", tuple.getT1());
                    model.addAttribute("ivas", tuple.getT2());
                    model.addAttribute("empresas", tuple.getT3());
                }).thenReturn("inventario/editar_categoria");

    }

    /**
     * Método para editar una categoria
     * 
     * @param id
     * @param categoryModel
     * @param exchange
     * @return
     */
    @PostMapping("/categorias/{id}")
    public Mono<String> editarUsuario(@PathVariable Integer id,
            @ModelAttribute("categoria") CategoryModel categoryModel, ServerWebExchange exchange) {

        return categoryService.obtenerCategoria(id)
                .flatMap(existingCategory -> {
                    // Actualizar la categoria
                    existingCategory.setCategoria(categoryModel.getCategoria());
                    existingCategory.setIva(categoryModel.getIva());
                    existingCategory.setEmpresa(categoryModel.getEmpresa());

                    // Guardar la categoria actualizada
                    return categoryService.guardarCategoria(existingCategory)
                            .doOnSuccess(success -> exchange.getSession()
                                    .doOnNext(session -> session.getAttributes()
                                            .put("successMessage", "Categoria Actualizada con Éxito!"))
                                    .subscribe())
                            .doOnError(error -> exchange.getSession()
                                    .doOnNext(session -> session.getAttributes()
                                            .put("errorMessage", "Error al Actualizar la Categoria!"))
                                    .subscribe());
                })
                .thenReturn("redirect:/inventario/categorias");

    }

    /**
     * Método para eliminar una categoria
     * 
     * @param id
     * @param exchange
     * @return
     */
    @GetMapping("/categorias/{id}")
    public Mono<String> eliminarCategoria(@PathVariable Integer id, ServerWebExchange exchange) {
        return categoryService.eliminarCategoria(id)
                .doOnSuccess(success -> exchange.getSession()
                        .doOnNext(session -> session.getAttributes()
                                .put("successMessage", "Categoria Eliminada con Éxito!"))
                        .subscribe())
                .doOnError(error -> exchange.getSession()
                        .doOnNext(session -> session.getAttributes()
                                .put("errorMessage", "Error al Eliminar la Categoria!"))
                        .subscribe())
                .thenReturn("redirect:/inventario/categorias");

    }



}
