package com.levting.FactxFrontend.service;

import com.levting.FactxFrontend.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
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
                .bodyToFlux(UserModel.class);
    }

    public Mono<UserModel> obtenerUsuario(Integer id_usuario) {
        return webClient.get()
                .uri("/usuario/{id}", id_usuario)
                .retrieve()
                .bodyToMono(UserModel.class);
    }

    public Mono<UserModel> guardarUsuario(UserModel userModel) {
        return webClient.post()
                .uri("/usuario")
                .bodyValue(userModel)
                .retrieve()
                .bodyToMono(UserModel.class);
    }

    public Mono<UserModel> actualizarUsuario(UserModel userModel) {
        return webClient.post()
                .uri("/usuario")
                .bodyValue(userModel)
                .retrieve()
                .bodyToMono(UserModel.class);
    }

    public Mono<Void> eliminarUsuario(Integer id_usuario) {
        return webClient.delete()
                .uri("/usuario/{id}", id_usuario)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<UserModel> iniciarSesion(String usuario, String contrasena) {
        return webClient.get()
                .uri("/usuario/query?usuario={usuario}&contrasena={contrasena}", usuario, contrasena)
                .retrieve()
                .bodyToFlux(UserModel.class)
                .next()
                .switchIfEmpty(Mono.empty());
    }
}
