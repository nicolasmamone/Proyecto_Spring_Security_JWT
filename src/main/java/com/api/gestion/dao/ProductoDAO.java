package com.api.gestion.dao;

import com.api.gestion.pojo.Producto;
import com.api.gestion.wrapper.ProductoWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductoDAO extends JpaRepository<Producto, Integer> {

    List<ProductoWrapper> getAllProductos();

    @Transactional
    @Modifying
    Integer updateStatus(@Param("status") String status, @Param("id") Integer id);

    List<ProductoWrapper> getProductoByCategoria(@Param("id") Integer id);

    ProductoWrapper getProductoById(@Param("id") Integer id);
}
