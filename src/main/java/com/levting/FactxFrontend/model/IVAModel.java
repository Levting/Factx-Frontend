package com.levting.FactxFrontend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class IVAModel {
    private Integer id_iva;
    private String iva_nombre;
    private Double iva;
}
