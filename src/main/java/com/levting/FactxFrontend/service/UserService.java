package com.levting.FactxFrontend.service;

import com.levting.FactxFrontend.model.UserModel;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class UserService {

    private final WebClient webClient;

    @Autowired
    public UserService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<UserModel> obtenerUsuarios() {
        return webClient.get()
                .uri("/usuario")
                .retrieve()
                .bodyToFlux(UserModel.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    System.err.println("Error al obtener usuarios: " + ex.getMessage());
                    return Flux.empty(); // Retornar un Flujo vacío
                });
    }

    public Mono<UserModel> obtenerUsuario(Integer id_usuario) {
        return webClient.get()
                .uri("/usuario/{id}", id_usuario)
                .retrieve()
                .bodyToMono(UserModel.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    System.err.println("Error al obtener usuario: " + ex.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<UserModel> guardarUsuario(UserModel userModel){
        return webClient.post()
                .uri("/usuario")
                .body(BodyInserters.fromValue(userModel))
                .retrieve()
                .bodyToMono(UserModel.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    System.err.println("Error al guardar un Usuario: " + ex.getMessage());
                    return Mono.empty();
                });

    }
}
