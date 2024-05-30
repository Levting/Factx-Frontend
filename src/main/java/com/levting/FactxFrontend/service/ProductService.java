package com.levting.FactxFrontend.service;

import com.levting.FactxFrontend.model.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.image.DataBuffer;

@Service
public class ProductService {

    private final WebClient webClient;


    @Autowired
    public ProductService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<ProductModel> obtenerProductos() {
        return webClient.get()
                .uri("/producto")
                .retrieve()
                .bodyToFlux(ProductModel.class);
    }

    public Mono<ProductModel> guardarProducto(ProductModel productModel, FilePart icono) {
        // Crea un MultipartBodyBuilder y agrega el producto y el icono
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("producto", productModel, MediaType.APPLICATION_JSON);
        builder.part("icono", icono);

        // Guarda el producto en la base de datos
        return webClient.post()
                .uri("/producto")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(ProductModel.class);
    }
}



