package com.example.demo.Service;

import com.example.demo.DTO.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private PasswordEncoder passwordEncoder;
@Autowired
    public void register(RegisterRequest request){
        String hash = passwordEncoder.encode(request.getPassword());
    }
}
