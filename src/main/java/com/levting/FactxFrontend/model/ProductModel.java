package com.levting.FactxFrontend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductModel {
    private Integer id_producto;
    private CategoryModel categoria;
    private FilePart icono;
    private String producto;
    private String descripcion;
    private Float precio;
    private Integer cantidad;

}
