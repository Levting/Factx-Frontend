package com.levting.FactxFrontend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductModel {
    private Integer id_producto;
    private CategoryModel categoria;
    private String icono;
    private String producto;
    private String descripcion;
    private Double precio;
    private Integer cantidad;

}
