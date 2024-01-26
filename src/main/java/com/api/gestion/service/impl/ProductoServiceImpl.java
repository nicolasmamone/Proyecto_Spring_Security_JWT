package com.api.gestion.service.impl;

import com.api.gestion.constantes.FacturaConstantes;
import com.api.gestion.dao.ProductoDAO;
import com.api.gestion.pojo.Categoria;
import com.api.gestion.pojo.Producto;
import com.api.gestion.security.jwt.JwtFilter;
import com.api.gestion.service.ProductoService;
import com.api.gestion.utils.FacturaUtils;
import com.api.gestion.wrapper.ProductoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoDAO productoDAO;

    @Autowired
    private JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNuevoProducto(Map<String, String> requestMap) {
        try{
            if (jwtFilter.isAdmin()){
                if (validateProductoMap(requestMap, false)){
                    productoDAO.save(getProductoFromMap(requestMap, false));
                    return FacturaUtils.getResponseEntity("Producto Agregado con exito", HttpStatus.OK);
                    }
                return FacturaUtils.getResponseEntity(FacturaConstantes.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }else{
                    return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
                }
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductoWrapper>> getAllProductos() {
        try{
            return new ResponseEntity<>(productoDAO.getAllProductos(), HttpStatus.OK);
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProducto(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){ // Si es admin
                if (validateProductoMap(requestMap, true)){ // Si estamos actualizando
                    Optional<Producto> productoOptional = productoDAO.findById(Integer.parseInt(requestMap.get("id")));
                    if (!productoOptional.isEmpty()){ // Si se encuentra el producto
                        Producto producto = getProductoFromMap(requestMap, true);
                        producto.setStatus(productoOptional.get().getStatus());
                        productoDAO.save(producto);
                        return FacturaUtils.getResponseEntity("Producto actualizado con Exito", HttpStatus.OK);
                    }else{
                        return FacturaUtils.getResponseEntity("Ese producto no existe", HttpStatus.NOT_FOUND);
                    }
                }else{
                    return FacturaUtils.getResponseEntity(FacturaConstantes.INVALID_DATA, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }else{
                return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProducto(Integer id) {
        try {
            if (jwtFilter.isAdmin()){ // Si es admin
                Optional<Producto> productoOptional = productoDAO.findById(id);
                if (!productoOptional.isEmpty()){
                    productoDAO.deleteById(id);
                    return FacturaUtils.getResponseEntity("Producto eliminado con Exito", HttpStatus.OK);
                }else{
                    return FacturaUtils.getResponseEntity(FacturaConstantes.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
            }else{
                return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){ // Si es admin
                Optional<Producto> productoOptional = productoDAO.findById(Integer.parseInt(requestMap.get("id")));
                if (!productoOptional.isEmpty()){
                    productoDAO.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    return FacturaUtils.getResponseEntity("STATUS del Producto actualizado con Exito", HttpStatus.OK);
                }else{
                    return FacturaUtils.getResponseEntity("El Producto no existe", HttpStatus.NOT_FOUND);
                }
            }else{
                return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Producto getProductoFromMap(Map<String, String> requestMap, boolean isAdd){
        Categoria categoria = new Categoria();
        categoria.setId(Integer.parseInt(requestMap.get("categoriaId")));
        Producto producto = new Producto();
        if (isAdd){ //Si el producto ya esta creado o guardado, le seteamos el id
            producto.setId(Integer.parseInt(requestMap.get("id")));
        }else{
            producto.setStatus("true");
        }

        //trabajando con Maps en vez de DTO
        producto.setCategoria(categoria);
        producto.setNombre(requestMap.get("nombre"));
        producto.setDescripcion(requestMap.get("descripcion"));
        producto.setPrecio(Integer.parseInt(requestMap.get("precio")));
        return producto;
    }

    private boolean validateProductoMap(Map<String, String> requestMap, boolean validateId){
        if (requestMap.containsKey("nombre")){
            if(requestMap.containsKey("id") && validateId){
                return  true;
            }
            if (!validateId){
                return true;
            }
        }
        return false;
    }
}
