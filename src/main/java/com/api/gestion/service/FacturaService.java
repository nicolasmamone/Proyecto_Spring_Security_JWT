package com.api.gestion.service;

import com.api.gestion.pojo.Factura;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface FacturaService {

    ResponseEntity<String> generatedReport(Map<String, Object> requestMap);

    ResponseEntity<List<Factura>> getFacturas();
}
