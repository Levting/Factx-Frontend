package com.levting.FactxFrontend.service;

import com.levting.FactxFrontend.model.BillDetailModel;
import com.levting.FactxFrontend.model.BillingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    public Mono<BillingModel> abrirFactura(Integer id_usuario, Integer id_cliente) {
        return webClient.post()
                .uri("/factura/abrir")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromFormData("usuario", String.valueOf(id_usuario))
                        .with("cliente", String.valueOf(id_cliente)))
                .retrieve()
                .bodyToMono(BillingModel.class);
    }

    public Mono<BillingModel> obtenerFactura(Integer id_factura) {
        return webClient.get()
                .uri("/factura/{id}", id_factura)
                .retrieve()
                .bodyToMono(BillingModel.class);
    }

    public Mono<BillDetailModel> anadirDetalles(Integer id_factura, Integer id_producto, Integer cantidad_producto) {
        return webClient.post()
                .uri("/detalleFactura")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromFormData("factura", String.valueOf(id_factura))
                        .with("producto", String.valueOf(id_producto))
                        .with("cantidad", String.valueOf(cantidad_producto)))
                .retrieve()
                .bodyToMono(BillDetailModel.class);
    }

    public Mono<BillingModel> cerrarFactura(Integer id_factura, Integer id_forma_pago) {
        return webClient.post()
                .uri("/factura/cerrar")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromFormData("factura", String.valueOf(id_factura))
                        .with("pago", String.valueOf(id_forma_pago)))
                .retrieve()
                .bodyToMono(BillingModel.class);
    }
}
