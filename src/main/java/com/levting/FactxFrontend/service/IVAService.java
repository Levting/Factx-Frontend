package com.levting.FactxFrontend.service;

import com.levting.FactxFrontend.model.IVAModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class IVAService {

    private final WebClient webClient;

    @Autowired
    public IVAService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<IVAModel> obtenerIVAs() {
        return webClient.get()
                .uri("/iva")
                .retrieve()
                .bodyToFlux(IVAModel.class);
    }
}
