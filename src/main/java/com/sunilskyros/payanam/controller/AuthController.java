package com.sunilskyros.payanam.controller;

import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.features.signin.SignInModel;
import com.sunilskyros.payanam.features.signup.SignUpModel;
import com.sunilskyros.payanam.util.PasswordUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class AuthController {
    private final SignInModel signInModel;
    private final SignUpModel signUpModel;

    @Autowired
    public AuthController(SignInModel signInModel, SignUpModel signUpModel) {
        this.signInModel = signInModel;
        this.signUpModel = signUpModel;
    }


    @PostMapping("/login")
    public String login(@RequestParam("") String userId, 
                        @RequestParam String password, 
                        @RequestParam(value = "rememberMe", required = false) String rememberMe,
                        HttpSession session,
                        HttpServletResponse response) {

        Passenger passenger = signInModel.authenticate(userId, password);
        if (passenger != null) {
            session.setAttribute("user", passenger);
            
            // Set secure HttpOnly cookie valid for 24 hours if 'rememberMe' checkbox is checked
            if ("on".equals(rememberMe) || "true".equals(rememberMe)) {
                Cookie userCookie = new Cookie("payanam_user", passenger.getPhoneNumber());
                userCookie.setMaxAge(24 * 60 * 60); // 24 hours (86400 seconds)
                userCookie.setPath("/");
                userCookie.setHttpOnly(true); // Neutralizes client-side XSS token hijacking
                response.addCookie(userCookie);
            }
            
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
        p.setPassword(PasswordUtil.hash(password));
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
    public String logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();
        
        // Delete remember-me cookie upon explicit logout to clear credentials
        Cookie userCookie = new Cookie("payanam_user", "");
        userCookie.setMaxAge(0); // Immediately delete
        userCookie.setPath("/");
        userCookie.setHttpOnly(true);
        response.addCookie(userCookie);
        
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