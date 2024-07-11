package com.example.movie.forgetpassword.service;

import com.example.movie.forgetpassword.dto.MailBody;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class  EmailService {
    private final JavaMailSender javaMailSender;

    public void sendSimpleMessage(MailBody mailBody)  {
        SimpleMailMessage message=new SimpleMailMessage();

        message.setTo(mailBody.to());
        message.setFrom("sehanmadushanka0714@gmail.com");
        message.setSubject(mailBody.subject());
        message.setText(mailBody.text());




        javaMailSender.send(message);

    }
}
