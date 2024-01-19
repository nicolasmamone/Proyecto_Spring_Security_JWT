package com.api.gestion.service.impl;

import com.api.gestion.constantes.FacturaConstantes;
import com.api.gestion.dao.CategoriaDAO;
import com.api.gestion.pojo.Categoria;
import com.api.gestion.security.jwt.JwtFilter;
import com.api.gestion.service.CategoriaService;
import com.api.gestion.utils.FacturaUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    public ResponseEntity<List<Categoria>> getAllCategorias(String valueFilter) {
        try {
            if (!Strings.isNullOrEmpty(valueFilter) && valueFilter.equalsIgnoreCase("true")){
                log.info("Usando el metodo getAllCategorias de Categoria ****************");
                return new ResponseEntity<List<Categoria>>(categoriaDAO.getAllCategorias(),HttpStatus.OK);
            }
                log.info("Usando el metodo findAll() de JpaRepository ****************");
                return new ResponseEntity<List<Categoria>>(categoriaDAO.findAll(),HttpStatus.OK);
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<List<Categoria>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
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
