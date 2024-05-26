package com.levting.FactxFrontend.service;

import com.levting.FactxFrontend.model.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class ProductService {

    private final WebClient webClient;

    @Autowired
    public ProductService(WebClient webClient){
        this.webClient = webClient;
    }

    public Flux<ProductModel> obtenerProductos(){
        return webClient.get()
                .uri("/producto")
                .retrieve()
                .bodyToFlux(ProductModel.class);
    }
}
