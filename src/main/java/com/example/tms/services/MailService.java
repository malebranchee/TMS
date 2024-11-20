package com.example.tms.services;

import jakarta.mail.internet.MimeMessage;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {


    private final String fromEmail = "gurinpavel312@gmail.com";

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, String subject, String body) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
