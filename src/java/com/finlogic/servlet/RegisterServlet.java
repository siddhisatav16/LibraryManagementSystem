package com.finlogic.servlet;

import com.finlogic.dao.MemberDAO;
import com.finlogic.model.Member;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for member registration.
 */
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String name     = request.getParameter("name");
        String email    = request.getParameter("email");
        String password = request.getParameter("password");
        String phoneStr = request.getParameter("phone");
        long phone = 0;

        if (name == null || email == null || password == null || phoneStr == null || name.isBlank() || email.isBlank() || password.isBlank() || phoneStr.isBlank()) {
            response.sendRedirect("register.html?error=empty_fields");
            return;
        }

        try {
            phone = Long.parseLong(phoneStr.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect("register.html?error=invalid_phone");
            return;
        }

        Member m = new Member(name, email, phone, password);
        int result = MemberDAO.addMember(m);

        if (result > 0) {
            response.sendRedirect("login.html?success=registered");
        } else {
            response.sendRedirect("register.html?error=failed");
        }
    }
}
