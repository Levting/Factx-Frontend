package com.levting.FactxFrontend.service;

import com.levting.FactxFrontend.model.CustomerModel;
import com.levting.FactxFrontend.model.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

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

    public Mono<ProductModel> obtenerProducto(Integer id_producto) {
        return webClient.get()
                .uri("/producto/{id}", id_producto)
                .retrieve()
                .bodyToMono(ProductModel.class);
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

    public Mono<Void> actualizarProducto(ProductModel productModel, FilePart icono) {
        return webClient.post()
                .uri("/producto")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("producto", productModel)
                        .with("icono", icono))
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Void> eliminarProducto(Integer id_producto) {
        return webClient.delete()
                .uri("/producto/{id}", id_producto)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Flux<ProductModel> obtenerProductoNombreOcurrente(String nombre) {
        return webClient.get()
                .uri("/producto/query?nombre={nombre}", nombre)
                .retrieve()
                .bodyToFlux(ProductModel.class);
    }
}




