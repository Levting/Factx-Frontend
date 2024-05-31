package com.levting.FactxFrontend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levting.FactxFrontend.model.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


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

    public Mono<Void> guardarProducto(ProductModel productModel, FilePart icono) {
        return webClient.post()
                .uri("/producto")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("producto", productModel)
                        .with("icono", icono))
                .retrieve()
                .bodyToMono(Void.class);
    }


}




