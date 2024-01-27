package com.api.gestion.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ProductoWrapper {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer precio;
    private String status;
    private Integer categoriaId;
    private String nombreCategoria;

    public ProductoWrapper(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public ProductoWrapper(Integer id, String nombre, String descripcion, Integer precio) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }
}
