/**
 *  EmailServiceImpl
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Component
public class EmailServiceImpl {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${admin.email}")
    private String adminEMail;

    public void sendSimpleMessage( String to, String subject, String text) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.setSubject(subject);
            MimeMessageHelper helper;
            helper = new MimeMessageHelper(message,true);
            helper.setFrom(adminEMail);
            helper.setTo(to);
            helper.setText(text,true);
            mailSender.send(message);
            log.info("eMail has been sent.");
        } catch (MessagingException ex) {
            log.error( ex.getMessage());
        }
    }
}