package com.sunilskyros.payanam.controller;

import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.features.signin.SignInModel;
import com.sunilskyros.payanam.features.signup.SignUpModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class AuthController {
    private final SignInModel signInModel = new SignInModel();
    private final SignUpModel signUpModel = new SignUpModel();


    @PostMapping("/login")
    public String login(@RequestParam("") String userId, 
                           @RequestParam String password, HttpSession session) {
        // USE THE PREVIOUSLY CODED BUSINESS LOGIC MODEL!
        Passenger passenger = signInModel.authenticate(userId,password);
        if (passenger != null) {
            session.setAttribute("user", passenger);
            String redirectUrl = "/dashboard.html";
            if (passenger.getRole() == Passenger.Role.ADMIN) {
                redirectUrl = "/admin.html";
            } else if (passenger.getRole() == Passenger.Role.TICKETCOLLECTOR) {
                redirectUrl = "/collector.html";
            }
            
            return "redirect:" + redirectUrl;
        } else {
            return "redirect:/index.html?error=true";
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, 
                           @RequestParam String phone, 
                           @RequestParam String password) {
        Passenger p = new Passenger();
        p.setName(username);
        p.setPhoneNumber(phone);
        p.setPassword(com.sunilskyros.payanam.util.PasswordUtil.hash(password));
        p.setRole(Passenger.Role.PASSENGER);
        
        // USE THE PREVIOUSLY CODED BUSINESS LOGIC MODEL!
        Passenger result = signUpModel.registerPassenger(p);
        if (result != null) {
            return "redirect:/index.html?registered=true";
        } else {
            return "redirect:/index.html?reg_error=true";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/index.html?logged_out=true";
    }

    @GetMapping("/api/auth/me")
    @ResponseBody
    public ResponseEntity<Passenger> getMe(HttpSession session) {
        Passenger user = (Passenger) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(user);
    }
}
