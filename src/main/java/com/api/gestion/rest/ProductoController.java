package com.api.gestion.rest;

import com.api.gestion.constantes.FacturaConstantes;
import com.api.gestion.service.ProductoService;
import com.api.gestion.utils.FacturaUtils;
import com.api.gestion.wrapper.ProductoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping("/add")
    public ResponseEntity<String> agregarNuevoProducto(@RequestBody Map<String, String> requestMap){
        try {
            return productoService.addNuevoProducto(requestMap);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/get")
    public ResponseEntity<List<ProductoWrapper>> listarProductos(){
        try{
            return productoService.getAllProductos();
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
