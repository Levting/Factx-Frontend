package com.levting.FactxFrontend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.levting.FactxFrontend.model.CustomerTypeModel;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerTypeService {

    // Inyectar el WebClient
    private final WebClient webClient;

    // Constructor
    @Autowired
    public CustomerTypeService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Método para obtener los tipos de clientes
     */
    public Flux<CustomerTypeModel> obtenerTiposCliente() {
        return webClient.get()
                .uri("/tipoCliente")
                .retrieve()
                .bodyToFlux(CustomerTypeModel.class);
    }

    /**
     * Método para obtener un tipo de cliente
     */
    public Mono<CustomerTypeModel> obtenerTipoCliente(Integer id_tipo_cliente) {
        return webClient.get()
                .uri("/tipoCliente/{id}", id_tipo_cliente)
                .retrieve()
                .bodyToMono(CustomerTypeModel.class);
    }

    /**
     * Método para guardar un tipo de cliente
     */
    public Mono<CustomerTypeModel> guardarTipoCliente(CustomerTypeModel customerTypeModel) {
        return webClient.post()
                .uri("/tipoCliente")
                .bodyValue(customerTypeModel)
                .retrieve()
                .bodyToMono(CustomerTypeModel.class);
    }

    /**
     * Método para actualizar un tipo de cliente
     */
    public Mono<CustomerTypeModel> actualizarTipoCliente(CustomerTypeModel customerTypeModel) {
        return webClient.post()
                .uri("/tipoCliente")
                .bodyValue(customerTypeModel)
                .retrieve()
                .bodyToMono(CustomerTypeModel.class);
    }

    /**
     * Método para eliminar un tipo de cliente
     */
    public Mono<Void> eliminarTipoCliente(Integer id_tipo_cliente) {
        return webClient.delete()
                .uri("/tipoCliente/{id}", id_tipo_cliente)
                .retrieve()
                .bodyToMono(Void.class);
    }

}
