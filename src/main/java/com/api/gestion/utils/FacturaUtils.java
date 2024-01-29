package com.api.gestion.utils;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FacturaUtils {

    private FacturaUtils(){

    };

    public static ResponseEntity<String> getResponseEntity(String message, HttpStatus httpStatus){
        return new ResponseEntity<String>("Mensaje: " + message, httpStatus);
    }

    public static String getUUId(){

        Date date = new Date();
        long time = date.getTime();
        return "FACTURA - " + time;
    }

    //para guardar los datos en un indice
    public static JSONArray getJsonArrayFromString(String data) throws JSONException {
        JSONArray jsonArray = new JSONArray(data);
        return jsonArray;
    }

    public static Map<String, Object> getMapFromJson(String data){
        if (!Strings.isNullOrEmpty(data)){
            // GSON --> Es una libreria que nos va a permitir serializar y desserializar entre objetos en java y JSON
            // SERIALIZACION --> convertir de objetos java a json
            // DESERIALIZACION --> convertir de json a objeto java
            return new Gson().fromJson(data, new TypeToken<Map<String, Object>>(){//deserealizando un json. de data al tipo q le indicamos.

            }.getType());
        }
        return new HashMap<>();
    }

}

