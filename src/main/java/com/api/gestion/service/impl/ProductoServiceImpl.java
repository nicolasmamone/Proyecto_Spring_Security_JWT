package com.api.gestion.service.impl;

import com.api.gestion.constantes.FacturaConstantes;
import com.api.gestion.dao.ProductoDAO;
import com.api.gestion.pojo.Categoria;
import com.api.gestion.pojo.Producto;
import com.api.gestion.security.jwt.JwtFilter;
import com.api.gestion.service.ProductoService;
import com.api.gestion.utils.FacturaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

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
