package com.api.gestion.dao;

import com.api.gestion.pojo.Producto;
import com.api.gestion.wrapper.ProductoWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoDAO extends JpaRepository<Producto, Integer> {

    List<ProductoWrapper> getAllProductos();
}
