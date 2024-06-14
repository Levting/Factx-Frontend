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

    /**
     * Método para obtener los clientes
     * 
     * @return
     */
    public Flux<CustomerModel> obtenerClientes() {
        return webClient.get()
                .uri("/cliente")
                .retrieve()
                .bodyToFlux(CustomerModel.class);

    }

    /**
     * Método para obtener un cliente
     * 
     * @param id_cliente
     * @return
     */
    public Mono<CustomerModel> obtenerCliente(Integer id_cliente) {
        return webClient.get()
                .uri("/cliente/{id}", id_cliente)
                .retrieve()
                .bodyToMono(CustomerModel.class);
    }

    /**
     * Método para obtener un cliente escribiendo su nombre
     * 
     * @param nombre
     * @return
     */
    public Flux<CustomerModel> obtenerClienteNombreOcurrente(String nombre) {
        return webClient.get()
                .uri("/cliente/query?nombre={nombre}", nombre)
                .retrieve()
                .bodyToFlux(CustomerModel.class);
    }

    /**
     * Método para guardar un cliente
     * 
     * @param customerModel
     * @return
     */
    public Mono<CustomerModel> guardarCliente(CustomerModel customerModel) {
        return webClient.post()
                .uri("/cliente")
                .bodyValue(customerModel)
                .retrieve()
                .bodyToMono(CustomerModel.class);
    }

    /**
     * Método para eliminar un cliente
     * 
     * @param id_cliente
     * @return
     */
    public Mono<Void> eliminarCliente(Integer id_cliente) {
        return webClient.delete()
                .uri("/cliente/{id}", id_cliente)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
