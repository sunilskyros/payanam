package com.sunilskyros.payanam.config;

import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.repository.PassengerRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Servlet filter that intercepts all incoming web requests.
 * If the current HTTP session is unauthenticated but a valid "payanam_user"
 * cookie exists, the filter automatically queries the database and populates
 * the session to perform a seamless auto-login.
 */
@Component
public class CookieAuthFilter implements Filter {

    private final PassengerRepository passengerRepository;

    @Autowired
    public CookieAuthFilter(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);

        // If user is not logged in inside the session, look for the remember-me cookie
        if (session == null || session.getAttribute("user") == null) {
            Cookie[] cookies = httpRequest.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("payanam_user".equals(cookie.getName())) {
                        String phone = cookie.getValue();
                        if (phone != null && !phone.trim().isEmpty()) {
                            // Find user in database by primary key (phone number)
                            Passenger passenger = passengerRepository.findById(phone).orElse(null);
                            if (passenger != null && passenger.getStatus() == Passenger.Status.ACTIVE) {
                                // Re-create session and establish authentication state
                                HttpSession newSession = httpRequest.getSession(true);
                                newSession.setAttribute("user", passenger);
                            }
                        }
                        break;
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }
}
