package com.api.gestion.service.impl;

import com.api.gestion.constantes.FacturaConstantes;
import com.api.gestion.dao.CategoriaDAO;
import com.api.gestion.pojo.Categoria;
import com.api.gestion.security.jwt.JwtFilter;
import com.api.gestion.service.CategoriaService;
import com.api.gestion.utils.FacturaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    CategoriaDAO categoriaDAO;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNuevaCategoria(Map<String, String> requestMap) {
        try{
            if (jwtFilter.isAdmin()){
                //Metodo para validar la categoria
                if (validateCategoriaMap(requestMap, false)){ //cuando no contiene el id
                    categoriaDAO.save(getCategoriaFromMap(requestMap, false));
                    return FacturaUtils.getResponseEntity("Categoria Agregada con exito",HttpStatus.OK);
                }

            }else{
                FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateCategoriaMap(Map<String, String> requestMap, boolean validateId){
        if (requestMap.containsKey("nombre")){
            /*
                Para cuando queremos Actualizar
                { "id" : "1" ,
                  "nombre" : "name" }
            */
            if (requestMap.containsKey("id") && validateId){ return true; }
            /*
            Cuando queremos guardar no le pasamos id
            Por eso en el requestMap, no va haber id
                { "nombre" : "name" }
            */
            if (!validateId){ return true; }
        }
        return false;
    }

    private Categoria getCategoriaFromMap(Map<String, String> requestMap, Boolean isAdd){
        Categoria categoria = new Categoria();
        if(isAdd){
            categoria.setId(Integer.parseInt(requestMap.get("id")));
        }
        categoria.setNombre(requestMap.get("nombre"));
        return categoria;
    }
}
