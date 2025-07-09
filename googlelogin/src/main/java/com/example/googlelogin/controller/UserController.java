package com.example.googlelogin.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController // sadece controller olunca html döndürmeni bekliyor. rest controller olunca json bekliyor.
@RequestMapping("api/user")
public class UserController {
    public UserController(){
        System.out.println("usercontroller");
    }

    @GetMapping("/me")
    public Map<String, Object> currentUser(@AuthenticationPrincipal OAuth2User principal) {

        return Map.of(
                "name", principal.getAttribute("name"),
                "email", principal.getAttribute("email")
        );
    }

    @GetMapping("/you")
    public String foo(@AuthenticationPrincipal OAuth2User principal){
        return "foo";
    }




}
