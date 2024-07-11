package com.example.movie.forgetpassword.repository;

import com.example.movie.auth.entity.User;
import com.example.movie.forgetpassword.entity.ForgetPassowrd;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgetPasswordRepo extends JpaRepository<ForgetPassowrd,Integer> {

    @Query("select fp from ForgetPassowrd  fp where fp.otp=?1 and fp.user=?2")
    Optional<ForgetPassowrd>findByOtpAndAndUser(Integer otp, User user);

    @Query("SELECT fp FROM ForgetPassowrd fp WHERE fp.user = :user")
    Optional<ForgetPassowrd> findByUser( User user);

    @Transactional
    @Modifying
    @Query("delete from ForgetPassowrd fp where fp.user=?1")
    void deleteByUser(User user);
}
