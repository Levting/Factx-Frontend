package com.levting.FactxFrontend.service;

import com.levting.FactxFrontend.model.IVAModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class IVAService {

    private final WebClient webClient;

    @Autowired
    public IVAService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Método que obtiene los IVAs de la API
     * 
     * @return
     */
    public Flux<IVAModel> obtenerIVAs() {
        return webClient.get()
                .uri("/iva")
                .retrieve()
                .bodyToFlux(IVAModel.class);
    }

    /**
     * Método que obtiene un IVA de la API
     * 
     * @param id
     * @return
     */
    public Mono<IVAModel> obtenerIVA(Integer id_iva) {
        return webClient.get()
                .uri("/iva/{id}", id_iva)
                .retrieve()
                .bodyToMono(IVAModel.class);
    }

    /**
     * Método que guarda un IVA en la API
     * 
     * @param ivaModel
     * @return
     */
    public Mono<IVAModel> guardarIVA(IVAModel ivaModel) {
        return webClient.post()
                .uri("/iva")
                .bodyValue(ivaModel)
                .retrieve()
                .bodyToMono(IVAModel.class);
    }

    /**
     * Método que actualiza un IVA en la API
     * 
     * @param ivaModel
     * @return
     */
    public Mono<IVAModel> actualizarIVA(IVAModel ivaModel) {
        return webClient.put()
                .uri("/iva")
                .bodyValue(ivaModel)
                .retrieve()
                .bodyToMono(IVAModel.class);
    }

    /**
     * Método que elimina un IVA de la API
     * 
     * @param id_iva
     * @return
     */
    public Mono<Void> eliminarIVA(Integer id_iva) {
        return webClient.delete()
                .uri("/iva/{id}", id_iva)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
