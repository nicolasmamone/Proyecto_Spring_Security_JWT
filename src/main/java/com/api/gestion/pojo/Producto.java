package com.api.gestion.pojo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NamedQuery(name = "Producto.getAllProductos", query = "SELECT new com.api.gestion.wrapper.ProductoWrapper(p.id, p.nombre, p.descripcion, p.precio, p.status, p.categoria.id, p.categoria.nombre) FROM Producto p")
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre")
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY) //me va a listar categoria solo cuando se lo pida
    //Si no con EAGER en cada peticion me lo va a adjuntar
    @JoinColumn(name = "categoria_fk", nullable = false) // o categoria_id
    private Categoria categoria;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "precio")
    private Integer precio;

    @Column(name = "status")
    private String status;
}
