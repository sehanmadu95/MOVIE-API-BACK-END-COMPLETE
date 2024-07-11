package com.example.movie.controller;

import com.example.movie.auth.entity.User;
import com.example.movie.auth.repository.UserRepository;
import com.example.movie.exception.InvalidOtp;
import com.example.movie.forgetpassword.dto.MailBody;
import com.example.movie.forgetpassword.entity.ForgetPassowrd;
import com.example.movie.forgetpassword.repository.ForgetPasswordRepo;
import com.example.movie.forgetpassword.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/forget")
@RequiredArgsConstructor
public class ForgetPasswordController {

    private final UserRepository userRepository;
    private  final EmailService emailService;
    private final ForgetPasswordRepo forgetPasswordRepo;

    //send mail for email verification
    @PostMapping("/verify/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email){
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found!!"));

        int otp=otpGenerator();
        MailBody mailBody=MailBody.builder()
                .to(email)
                .subject("OTP VERIFICATION")
                .text("This is the OTP for your verification (Valid only 5min) : "+otp)
                .build();

        ForgetPassowrd forgetPassowrd= ForgetPassowrd.builder()
                .otp(otp)
                .user(user)
                .expirationTime(new Date(System.currentTimeMillis()+70*1000))
                .build();


        emailService.sendSimpleMessage(mailBody);


        Optional<ForgetPassowrd> fp=forgetPasswordRepo.findByUser(user);



        if(fp.isPresent()){
          forgetPasswordRepo.deleteByUser(user);
        }

        forgetPasswordRepo.save(forgetPassowrd);

        return ResponseEntity.ok("OTP SENDING...");
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp,@PathVariable String email){
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found!!"));

        ForgetPassowrd forgetPassoword=forgetPasswordRepo.findByOtpAndAndUser(otp,user)
                .orElseThrow(()->new InvalidOtp("Invalid Otp for email: "+email));



        if(forgetPassoword.getExpirationTime().before(Date.from(Instant.now()))){
            forgetPasswordRepo.delete(forgetPassoword);
            return new ResponseEntity("OTP has been expired...", HttpStatus.EXPECTATION_FAILED);
        }
        else {
            forgetPasswordRepo.delete(forgetPassoword);
            return ResponseEntity.ok("OTP Verfied...");

        }



    }

    private  Integer otpGenerator(){
        Random random=new Random();

        return random.nextInt(100_000,999_999);
    }


}
