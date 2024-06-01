package com.levting.FactxFrontend.service;

import com.levting.FactxFrontend.model.CategoryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CategoryService {

    private final WebClient webClient;

    @Autowired
    public CategoryService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<CategoryModel> obtenerCategorias() {
        return webClient.get()
                .uri("/categoria")
                .retrieve()
                .bodyToFlux(CategoryModel.class);
    }

    public Mono<CategoryModel> obtenerCategoria(Integer id_categoria) {
        return webClient.get()
                .uri("/categoria")
                .retrieve()
                .bodyToMono(CategoryModel.class);
    }

    public Mono<CategoryModel> guardarCategoria(CategoryModel categoryModel) {
        return webClient.post()
                .uri("/categoria")
                .bodyValue(categoryModel)
                .retrieve()
                .bodyToMono(CategoryModel.class);
    }

    public Mono<Void> eliminarCategoria(Integer id_categoria) {
        return webClient.delete()
                .uri("/categoria/{id}", id_categoria)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
