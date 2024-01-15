package com.api.gestion.pojo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

//JDBC
@NamedQuery(name = "User.findByEmail",query = "select u from User u where u.email=:email")
@NamedQuery(name = "User.getAllUsers",query = "select new com.api.gestion.wrapper.UserWrapper(u.id, u.nombre, u.email, u.numeroDeContacto, u.status) from User u where u.role='user'")
// --------------




@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "users")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "numeroDeContacto")
    private String numeroDeContacto;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private String status;

    @Column(name = "role")
    private String role;


}
