package com.finlogic.servlet;

import com.finlogic.dao.MemberDAO;
import com.finlogic.model.Member;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email    = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            response.sendRedirect("login.html?error=empty_fields");
            return;
        }

        Member m = MemberDAO.authenticate(email, password);

        if (m != null) {
            HttpSession session = request.getSession();
            session.setAttribute("memberId", m.getId());
            session.setAttribute("memberName", m.getName());
            session.setAttribute("role", m.getRole());

            if ("admin".equalsIgnoreCase(m.getRole())) {
                response.sendRedirect("dashboard.html"); // Admin dashboard
            } else {
                response.sendRedirect("userdashboard.html"); // User dashboard
            }
        } else {
            response.sendRedirect("login.html?error=invalid_credentials");
        }
    }
}
