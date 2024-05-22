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

    public Mono<RoleModel> guardarRol(RoleModel rolModel) {
        return webClient.post()
                .uri("/rol")
                .bodyValue(rolModel)
                .retrieve()
                .bodyToMono(RoleModel.class);
    }
}
