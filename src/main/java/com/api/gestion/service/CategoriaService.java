package com.api.gestion.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

public interface CategoriaService {

    ResponseEntity<String> addNuevaCategoria(Map<String, String> requestMap);
}
