package com.levting.FactxFrontend.service;

import com.levting.FactxFrontend.model.CustomerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {
    private final WebClient webClient;

    @Autowired
    public CustomerService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<CustomerModel> obtenerClientes() {
        return webClient.get()
                .uri("/cliente")
                .retrieve()
                .bodyToFlux(CustomerModel.class);

    }

    public Mono<CustomerModel> obtenerCliente(Integer id_cliente) {
        return webClient.get()
                .uri("/cliente/{id}", id_cliente)
                .retrieve()
                .bodyToMono(CustomerModel.class);
    }

    public Flux<CustomerModel> obtenerClienteNombreOcurrente(String nombre) {
        return webClient.get()
                .uri("/cliente/query?nombre={nombre}", nombre)
                .retrieve()
                .bodyToFlux(CustomerModel.class);
    }
}
