package com.levting.FactxFrontend.service;

import com.levting.FactxFrontend.model.CategoryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

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
}
