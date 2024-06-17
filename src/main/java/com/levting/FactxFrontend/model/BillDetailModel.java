package com.levting.FactxFrontend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillDetailModel {
    private Integer id_detalle_factura;
    private Integer cantidad;
    private Double precio_unitario;
    private Double subtotal_producto;
    private Double total_iva;
    private BillingModel factura;
    private ProductModel producto;
}
