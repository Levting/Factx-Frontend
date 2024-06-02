package com.levting.FactxFrontend.service;

import com.levting.FactxFrontend.model.WayPayModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class WayPayService {

    private final WebClient webClient;

    @Autowired
    public WayPayService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<WayPayModel> obtenerFormasPago() {
        return webClient.get()
                .uri("/formaPago")
                .retrieve()
                .bodyToFlux(WayPayModel.class);
    }

}
