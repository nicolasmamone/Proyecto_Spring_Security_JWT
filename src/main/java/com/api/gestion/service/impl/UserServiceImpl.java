package com.api.gestion.service.impl;

import com.api.gestion.constantes.FacturaConstantes;
import com.api.gestion.dao.UserDAO;
import com.api.gestion.pojo.User;
import com.api.gestion.service.UserService;
import com.api.gestion.util.FacturaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;





    // Método para poder guardar un usuario
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Registro interno de un usuario {}", requestMap);
        try{
            if (validateSignUpMap(requestMap)){
                User user = userDAO.findByEmail(requestMap.get("email"));
                if (Objects.isNull(user)){
                    userDAO.save(getUserFromMap(requestMap));
                    return FacturaUtils.getResponseEntity("Usuario registrado correctamente", HttpStatus.CREATED);
                }else{ //Sino es null entonces ya existe
                    return FacturaUtils.getResponseEntity("El usuario con ese email ya existe", HttpStatus.BAD_REQUEST);
                }
            }else{ //Sino pasa la validacion
                return FacturaUtils.getResponseEntity(FacturaConstantes.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch(Exception exception) {
            exception.printStackTrace();
        }//Si algo salio mal
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    //Metodo para validar el registro de un usuario
    private boolean validateSignUpMap(Map<String, String> requestMap){
        if (requestMap.containsKey("nombre") &&
            requestMap.containsKey("numeroDeContacto") &&
            requestMap.containsKey("email") &&
            requestMap.containsKey("password") ){

            return true;
        }
        return false;
    }

    // Método para poder registrar un usuario
    private User getUserFromMap(Map<String, String> requestMap){
        User user = new User();
        user.setNombre(requestMap.get("nombre"));
        user.setNumeroDeContacto(requestMap.get("numeroDeContacto"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");

        return user;
    }
}
