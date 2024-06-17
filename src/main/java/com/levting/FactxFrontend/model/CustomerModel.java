package com.levting.FactxFrontend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerModel {
    private Integer id_cliente;
    private String cedula;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String direccion;
    private CompanyModel empresa;
    private CustomerTypeModel tipo_cliente;
}
