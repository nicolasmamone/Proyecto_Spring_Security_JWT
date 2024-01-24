package com.api.gestion.service;


import com.api.gestion.wrapper.ProductoWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface ProductoService {

    ResponseEntity<String> addNuevoProducto(Map<String, String> requestMap);

    ResponseEntity<List<ProductoWrapper>> getAllProductos();

    ResponseEntity<String> updateProducto(Map<String, String> requestMap);
}
