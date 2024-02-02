package com.api.gestion.pojo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NamedQuery(name = "Factura.getFacturas", query = "SELECT f FROM Factura f ORDER BY  f.id DESC")
@NamedQuery(name = "Factura.getFacturaByUsername", query = "SELECT f FROM Factura f WHERE f.createdBy=: username ORDER BY f.id DESC ")

@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "email")
    private String email;

    @Column(name = "numeroContacto")
    private String numeroContacto;

    @Column(name = "metodoPago")
    private String metodoPago;

    @Column(name = "total")
    private Integer total;
    //columnDefinition --> nos sirve para indicar como se creara la columna sin depender de la anotacion de jpa
    @Column(name = "productoDetalles", columnDefinition = "json")
    private String productoDetalles;

    @Column(name = "createdBy")
    private String createdBy;

}
