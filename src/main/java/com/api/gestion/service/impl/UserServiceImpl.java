package com.api.gestion.service.impl;

import com.api.gestion.constantes.FacturaConstantes;
import com.api.gestion.dao.UserDAO;
import com.api.gestion.pojo.User;
import com.api.gestion.security.CustomerDetailsService;
import com.api.gestion.security.jwt.JwtFilter;
import com.api.gestion.security.jwt.JwtUtil;
import com.api.gestion.service.UserService;
import com.api.gestion.utils.EmailsUtils;
import com.api.gestion.utils.FacturaUtils;
import com.api.gestion.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private AuthenticationManager authenticationManager; // Quienes pueden acceder

    @Autowired
    private CustomerDetailsService customerDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private EmailsUtils emailsUtils;

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

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Dentro del login");
        try{
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );
            if (authentication.isAuthenticated()){ // si su estado es true entonces esta activo
                if (customerDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\""
                            + jwtUtil.generateToken(customerDetailsService.getUserDetail().getEmail(), customerDetailsService.getUserDetail().getRole()) + "\"}", // generamos el token
                            HttpStatus.OK);
                }else{
                    return new ResponseEntity<String>("{\"mensaje\":\" "+"Espere la aprobacion del administrador"+"\"}", HttpStatus.BAD_REQUEST);
                }
            }
        }catch(Exception exception){
            log.error(" {}",exception);
        }
        return new ResponseEntity<String>("{\"mensaje\":\" "+"Credenciales incorrectas"+"\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUsers() {
        try{
            if(jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDAO.getAllUsers(), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return  new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                Optional<User> optionalUser = userDAO.findById(Integer.parseInt(requestMap.get("id")));
                if (!optionalUser.isEmpty()){
                    userDAO.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    //enviamos el correo
                    enviarCorreoToAdmins(requestMap.get("status"), optionalUser.get().getEmail(), userDAO.getAllAdmins());

                    return FacturaUtils.getResponseEntity("Status del usuario actualizado", HttpStatus.OK);
                }else {
                    return FacturaUtils.getResponseEntity("Este Usuario no existe", HttpStatus.NOT_FOUND);
                }
            }else {
                return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    public void enviarCorreoToAdmins(String status, String user, List<String> allAdmins){
        allAdmins.remove(jwtFilter.getCurrentUser());
        if (status != null && status.equalsIgnoreCase("true")){

            emailsUtils.sendSimpleMessage(
                    //el admin q va a enviar los correos
                    jwtFilter.getCurrentUser() ,
                    "Cuenta Aprobada",
                    "USUARIO: " + user + "\n es aprobado por \n ADMIN: " + jwtFilter.getCurrentUser(),
                    allAdmins
            );
        }else{
            emailsUtils.sendSimpleMessage(
                    //el admin q va a enviar los correos
                    jwtFilter.getCurrentUser() ,
                    "Cuenta Desaprobada",
                    "USUARIO: " + user + "\n es desaprobado por \n ADMIN: " + jwtFilter.getCurrentUser(),
                    allAdmins
            );
        }
    }


    @Override
    public ResponseEntity<String> checkToken() {
        return FacturaUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User user = userDAO.findByEmail(jwtFilter.getCurrentUser());//el usuario que inicio sesion
            if (!user.equals(null)){
                if (user.getPassword().equals(requestMap.get("oldPassword"))){
                    user.setPassword(requestMap.get("newPassword"));
                    userDAO.save(user);
                    return FacturaUtils.getResponseEntity("Contrasenia actualizada con exito", HttpStatus.OK);
                }
                return FacturaUtils.getResponseEntity("Contrasenia incorrecta", HttpStatus.BAD_REQUEST);
            }
            return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
