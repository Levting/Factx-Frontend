package com.levting.FactxFrontend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryModel {
    private Integer id_categoria;
    private IVAModel iva;
    private CompanyModel empresa;
    private String categoria;
}
