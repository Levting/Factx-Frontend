package com.levting.FactxFrontend.service;

import com.levting.FactxFrontend.model.CompanyModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CompanyService {

    private final WebClient webClient;

    @Autowired
    public CompanyService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<CompanyModel> obtenerEmpresas() {
        return webClient.get()
                .uri("/empresa")
                .retrieve()
                .bodyToFlux(CompanyModel.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    System.err.println("Error al obtener empresas: " + ex.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<CompanyModel> obtenerEmpresa(Integer id_empresa) {
        return webClient.get()
                .uri("/empresa/{id}", id_empresa)
                .retrieve()
                .bodyToMono(CompanyModel.class);
    }

    public Mono<Void> guardarEmpresa(CompanyModel companyModel, FilePart logo, FilePart firma_electronica) {
        return webClient.post()
                .uri("/empresa")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("empresa", companyModel)
                        .with("logo", logo)
                        .with("firma_electronica", firma_electronica))
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Void> eliminarEmpresa(Integer id_empresa) {
        return webClient.delete()
                .uri("/empresa/{id}", id_empresa)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    System.err.println("Error al eliminar la empresa: " + ex.getMessage());
                    return Mono.empty();
                });
    }
}
