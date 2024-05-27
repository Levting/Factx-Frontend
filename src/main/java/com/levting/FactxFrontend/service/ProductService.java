package com.levting.FactxFrontend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.levting.FactxFrontend.model.ProductModel;
import jakarta.servlet.http.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;


    @Autowired
    public ProductService(WebClient webClient, ObjectMapper objectMapper){
        this.webClient = webClient;
        this.objectMapper = objectMapper;

    }

    public Flux<ProductModel> obtenerProductos(){
        return webClient.get()
                .uri("/producto")
                .retrieve()
                .bodyToFlux(ProductModel.class);
    }

    public Mono<ProductModel> guardarProducto(ProductModel productModel, FilePart icono){

    }
}
