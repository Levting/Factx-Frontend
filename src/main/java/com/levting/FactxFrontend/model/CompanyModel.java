package com.levting.FactxFrontend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyModel {
    private Integer id_empresa;
    private String ruc;
    private String razon_social;
    private String nombre_comercial;
    private String direccion;
    private String telefono;
    private String logo;
    private String tipo_contribuyente;
    private Boolean lleva_contabilidad;
    private String firma_electronica;
    private String contrasena_firma_electronica;
    private Boolean desarrollo;

}
