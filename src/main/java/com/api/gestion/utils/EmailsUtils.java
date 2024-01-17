package com.api.gestion.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailsUtils {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleMessage(String to, String subject, String text, List<String> list){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nicolasmamone02@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        if (list != null && list.size() > 0){
            message.setCc(getCcArray(list));
        }
        javaMailSender.send(message);
    }

    private String[] getCcArray(List<String> cclist){ //devuelve un arreglo apartir de una lista
        String[] cc = new String[cclist.size()];
        for (int i = 0; i < cclist.size(); i++) {
            cc[i] = cclist.get(i);
        }
        return cc;
    }

    public void forgotPassword(String to, String subject, String password) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("nicolasmamone02@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);

        String htmlMessage = " <p> <b>Sus detalles de inicio de sesion para el sistema de facturas </b> <br> <b>Email: </b>" +
                to + "<br> <b>Password: </b>" +
                password + "</p> ";
        message.setContent(htmlMessage, "text/html");
        javaMailSender.send(message);

    }
}
