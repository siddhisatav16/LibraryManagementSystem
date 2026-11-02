package com.finlogic.servlet;

import com.finlogic.dao.MemberDAO;
import com.finlogic.model.Member;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ViewMemberByIdServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect("memberlist.html");
            return;
        }

        try {
            int id = Integer.parseInt(idParam.trim());
            Member m = MemberDAO.getMemberById(id);

            out.println("<!DOCTYPE html><html><head><title>Member Details</title><link rel='stylesheet' href='css/style.css'></head><body>");
            out.println("<div class='container' style='margin-top:4rem;'><div class='lib-card'>");
            
            if (m != null) {
                out.println("<h2>Member Details</h2>");
                out.println("<p><strong>ID:</strong> " + m.getId() + "</p>");
                out.println("<p><strong>Name:</strong> " + m.getName() + "</p>");
                out.println("<p><strong>Email:</strong> " + m.getEmail() + "</p>");
                out.println("<p><strong>Phone:</strong> " + m.getPhone() + "</p>");
                out.println("<p><strong>Role:</strong> " + m.getRole() + "</p>");
            } else {
                out.println("<p>Member not found.</p>");
            }
            
            out.println("<a href='memberlist.html' class='lib-btn lib-btn-secondary'>Back to List</a>");
            out.println("</div></div></body></html>");

        } catch (NumberFormatException e) {
            response.sendRedirect("memberlist.html");
        }
    }
}
