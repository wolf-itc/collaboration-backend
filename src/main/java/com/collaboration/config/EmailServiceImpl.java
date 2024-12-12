/**
 *  EmailServiceImpl
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmailServiceImpl {

  @Value("${admin.email}")
  private String adminEMail;

  private JavaMailSender mailSender;
  
  public EmailServiceImpl(final JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public JavaMailSender getMailSender() {
    return mailSender;
  }

  public String getAdminEMail() {
    return adminEMail;
  }

  public void sendSimpleMessage( String to, String subject, String text) {
    log.trace("> sendSimpleMessage to={} subject={} text={}", to, subject, text);
    
    try {
      MimeMessage message = mailSender.createMimeMessage();
      message.setSubject(subject);
      MimeMessageHelper helper;
      helper = new MimeMessageHelper(message,true);
      helper.setFrom(adminEMail);
      helper.setTo(to);
      helper.setText(text,true);
      mailSender.send(message);
      log.trace("< sendSimpleMessage: eMail has been sent");
    } catch (MessagingException ex) {
      log.error("< sendSimpleMessage: ERROR! {}", ex.getMessage(), ex);
    }
  }
}