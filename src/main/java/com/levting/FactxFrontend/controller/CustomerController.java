package com.levting.FactxFrontend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;

import com.levting.FactxFrontend.model.CustomerModel;
import com.levting.FactxFrontend.model.UserModel;
import com.levting.FactxFrontend.service.CompanyService;
import com.levting.FactxFrontend.service.CustomerService;
import com.levting.FactxFrontend.service.CustomerTypeService;

import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/clientes")
public class CustomerController {

    // Inyeccion de los servicios
    private final CustomerService customerService;
    private final CompanyService companyService;
    private final CustomerTypeService customerTypeService;

    // Constructor
    @Autowired
    public CustomerController(
            CustomerService customerService,
            CompanyService companyService,
            CustomerTypeService customerTypeService) {

        this.customerService = customerService;
        this.companyService = companyService;
        this.customerTypeService = customerTypeService;
    }

    /**
     * Método para mostrar la página de clientes
     *
     * @param model
     * @return
     */
    @GetMapping({ "/clientes", "" })
    public Mono<String> mostrarPaginaClientes(Model model, ServerWebExchange exchange) {
        // Obtener el usuario de la sesión
        UserModel usuario = (UserModel) model.getAttribute("usuario");
        if (usuario != null) {
            // Obtener los clientes y añadirlos al modelo
            return customerService.obtenerClientes()
                    .collectList()
                    .doOnNext(clientes -> {
                        if (clientes.isEmpty()) {
                            System.out.println("No existen Clientes!");
                        } else {
                            model.addAttribute("clientes", clientes);
                        }
                    }).thenReturn("clientes/clientes");
        } else {
            return Mono.just("redirect:/inicio_sesion");
        }
    }

    /**
     * Método para mostrar la página del formulario de creación de un cliente
     *
     * @param model
     * @return
     */
    @GetMapping("/clientes/crear")
    public Mono<String> mostrarFormularioCrearCliente(Model model) {
        // Añadir un nuevo objeto Cliente al modelo
        model.addAttribute("cliente", new CustomerModel());

        // Añadir el cliente al modelo junto con la empresa y el tipo de cliente
        return Mono.zip(
                companyService.obtenerEmpresas().collectList(),
                customerTypeService.obtenerTiposCliente().collectList())
                .doOnNext(tuple -> {
                    model.addAttribute("empresas", tuple.getT1());
                    model.addAttribute("tipos_cliente", tuple.getT2());
                })
                .thenReturn("clientes/crear_cliente");
    }

    /**
     * Método para guardar el cliente
     *
     * @param customerModel
     * @return
     */
    @PostMapping("/clientes")
    public Mono<String> guardarCliente(
            @ModelAttribute("cliente") CustomerModel customerModel,
            ServerWebExchange exchange) {

        // Guardar el cliente
        return customerService.guardarCliente(customerModel)
                .flatMap(savedCustomer -> exchange.getSession()
                        .doOnNext(session -> {
                            session.getAttributes().put("successMessage", "Cliente creado con Éxito!");
                        })
                        .then(Mono.just("redirect:/clientes/clientes")))

                .onErrorResume(error -> exchange.getSession()
                        .doOnNext(session -> {
                            System.out.println("Error al guardar el cliente: " + error.getMessage());
                            session.getAttributes().put("errorMessage", "Error al guardar el cliente");
                        })
                        .then(Mono.just("redirect:/clientes/clientes")));

    }

    /**
     * Método para mostrar la página de edición de un cliente
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/clientes/editar/{id}")
    public Mono<String> mostrarFormularioEditarCliente(@PathVariable Integer id, Model model) {
        return Mono.zip(
                customerService.obtenerCliente(id),
                companyService.obtenerEmpresas().collectList(),
                customerTypeService.obtenerTiposCliente().collectList())
                .doOnNext(tuple -> {
                    model.addAttribute("cliente", tuple.getT1());
                    model.addAttribute("empresas", tuple.getT2());
                    model.addAttribute("tipos_cliente", tuple.getT3());
                }).thenReturn("clientes/editar_cliente");
    }

    /**
     * Método para editar un cliente
     *
     * @param id
     * @param customerModel
     * @param serverWebExchange
     * @return
     */
    @PostMapping("/clientes/{id}")
    public Mono<String> editarCliente(
            @PathVariable Integer id,
            @ModelAttribute("cliente") CustomerModel customerModel,
            ServerWebExchange serverWebExchange) {

        return customerService.obtenerCliente(id)
                .flatMap(existingCustomer -> {
                    existingCustomer.setNombre(customerModel.getNombre());
                    existingCustomer.setApellido(customerModel.getApellido());
                    existingCustomer.setCorreo(customerModel.getCorreo());
                    existingCustomer.setTelefono(customerModel.getTelefono());
                    existingCustomer.setDireccion(customerModel.getDireccion());
                    existingCustomer.setEmpresa(customerModel.getEmpresa());
                    existingCustomer.setTipo_cliente(customerModel.getTipo_cliente());

                    // Guardar el cliente actualizado
                    return customerService.guardarCliente(existingCustomer);
                })
                .flatMap(updatedCustomer -> serverWebExchange.getSession()
                        .doOnNext(session -> session.getAttributes()
                                .put("successMessage", "Cliente Editado con Éxito!"))
                        .then(Mono.just("redirect:/clientes/clientes")))

                .onErrorResume(error -> serverWebExchange.getSession()
                        .doOnNext(session -> session.getAttributes()
                                .put("errorMessage", "Error al editar el cliente: " + error.getMessage()))
                        .then(Mono.just("redirect:/clientes/clientes")));
    }

    /**
     * Método para eliminar un cliente
     *
     * @param id
     * @return
     */
    @GetMapping("/clientes/{id}")
    public Mono<String> eliminarCliente(@PathVariable Integer id) {
        return customerService.eliminarCliente(id)
                .doOnError(error -> System.err.println("Error al Eliminar: " + error.getMessage()))
                .doOnSuccess(aVoid -> System.out.println("Cliente Eliminado con Éxito!"))
                .then(Mono.just("redirect:/clientes/clientes"));
    }

}
