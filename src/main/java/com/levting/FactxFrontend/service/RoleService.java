package com.levting.FactxFrontend.service;

import com.levting.FactxFrontend.model.RoleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


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
                .uri("/rol/{id}", id_rol)
                .retrieve()
                .bodyToMono(RoleModel.class);
    }

    public Mono<RoleModel> guardarRol(RoleModel roleModel) {
        return webClient.post()
                .uri("/rol")
                .bodyValue(roleModel)
                .retrieve()
                .bodyToMono(RoleModel.class);
    }

    public Mono<RoleModel> actualizarRol(RoleModel roleModel) {
        return webClient.post()
                .uri("/rol")
                .bodyValue(roleModel)
                .retrieve()
                .bodyToMono(RoleModel.class);
    }

    public Mono<Void> eliminarRol(Integer id_rol) {
        return webClient.delete()
                .uri("/rol/{id}", id_rol)
                .retrieve()
                .bodyToMono(Void.class);
    }

}
