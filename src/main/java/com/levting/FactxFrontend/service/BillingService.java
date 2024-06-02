package com.levting.FactxFrontend.service;

import com.levting.FactxFrontend.model.BillingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class BillingService {
    private final WebClient webClient;

    @Autowired
    public BillingService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<BillingModel> obtenerFacturas() {
        return webClient.get()
                .uri("/factura")
                .retrieve()
                .bodyToFlux(BillingModel.class);
    }
}
