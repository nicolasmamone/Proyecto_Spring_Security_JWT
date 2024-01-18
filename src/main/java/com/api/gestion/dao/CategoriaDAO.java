package com.api.gestion.dao;

import com.api.gestion.pojo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaDAO extends JpaRepository<Categoria, Integer> {

    List<Categoria> getAllCategorias(); // Se puede usar tambien findAll de jpa
}
