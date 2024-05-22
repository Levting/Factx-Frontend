package com.levting.FactxFrontend.service;

import com.levting.FactxFrontend.model.RoleModel;
import com.levting.FactxFrontend.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.management.relation.Role;


@Service
public class RoleService {

    private final WebClient webClient;

    @Autowired
    public RoleService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<RoleModel> obtenerRoles() {
        return webClient.get()
                .uri("/rol")
                .retrieve()
                .bodyToFlux(RoleModel.class);
    }

    public Mono<RoleModel> obtenerRol(Integer id_rol) {
        return webClient.get()
                .uri("/usuario/{id}", id_rol)
                .retrieve()
                .bodyToMono(RoleModel.class);

    }

    public Mono<RoleModel> guardarRol(RoleModel roleModel) {
        System.out.println("Enviando rol: " + roleModel); // Logging antes de enviar
        return webClient.post()
                .uri("/rol")
                .bodyValue(roleModel)
                .retrieve()
                .bodyToMono(RoleModel.class)
                .doOnNext(savedRole -> System.out.println("Respuesta recibida: " + savedRole)); // Logging de la respuesta

    }

    public Mono<RoleModel> actualizarRol(RoleModel roleModel){
        System.out.println("Enviando rol: " + roleModel);
        return webClient.post()
                .uri("/rol")
                .bodyValue(roleModel)
                .retrieve()
                .bodyToMono(RoleModel.class)
                .doOnNext(savedRole -> System.out.println("Respuesta Recibida: " + savedRole)); // Logging de la respuesta
    }
}
