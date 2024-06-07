package com.levting.FactxFrontend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingModel {
    private Integer idFactura;
    private String clave_acceso;
    private String estado;
    private String fecha;
    private Integer numero_factura;
    private Double subtotal;
    private Double total;
    private Double total_iva;
    private BillDetailModel detalle;
    private CustomerModel cliente;
    private DocumentModel documento;
    private WayPayModel formaPago;
    private UserModel usuario;
}
