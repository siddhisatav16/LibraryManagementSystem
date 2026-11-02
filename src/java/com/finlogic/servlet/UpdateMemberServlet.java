package com.finlogic.servlet;

import com.finlogic.dao.MemberDAO;
import com.finlogic.model.Member;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UpdateMemberServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam    = request.getParameter("id");
        String name       = request.getParameter("name");
        String email      = request.getParameter("email");
        String phoneParam = request.getParameter("phone");

        if (idParam == null || name == null || email == null || phoneParam == null) {
            response.sendRedirect("memberlist.html?error=missing_fields");
            return;
        }

        try {
            int  id    = Integer.parseInt(idParam.trim());
            long phone = Long.parseLong(phoneParam.trim().replaceAll("[^0-9]", ""));
            Member member = new Member(id, name.trim(), email.trim(), phone);
            MemberDAO.updateMember(member);
            response.sendRedirect("memberlist.html?success=updated");
        } catch (NumberFormatException e) {
            response.sendRedirect("memberlist.html?error=invalid_input");
        }
    }
}