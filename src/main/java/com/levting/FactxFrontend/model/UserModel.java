package com.levting.FactxFrontend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    private Integer id_usuario;
    private CompanyModel empresa;
    private RoleModel rol;
    private String usuario;
    private String contrasena;
    private String nombre;
    private String apellido;
}