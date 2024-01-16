package com.api.gestion.pojo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

//JDBC
@NamedQuery(name = "User.findByEmail",query = "select u from User u where u.email=:email") // donde el mail del usuario sea igual al email q le pasemos por parametro
@NamedQuery(name = "User.getAllUsers",query = "select new com.api.gestion.wrapper.UserWrapper(u.id, u.nombre, u.email, u.numeroDeContacto, u.status) from User u where u.role='user'")
@NamedQuery(name = "User.getAllAdmins",query = "select u.email from User u where u.role='admin'")
@NamedQuery(name = "User.updateStatus",query = "update User u set u.status=:status where u.id=:id") // update el status del user con el status que le pasemos por parametro donde el id del user sea igual al id que le pasemos por parametro
// --------------




@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;


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
