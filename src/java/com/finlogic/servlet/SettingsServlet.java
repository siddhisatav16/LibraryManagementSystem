package com.finlogic.servlet;

import com.finlogic.dao.SettingsDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class SettingsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equalsIgnoreCase((String) session.getAttribute("role"))) {
            response.sendRedirect("login.html");
            return;
        }

        double rate = SettingsDAO.getPenaltyRate();
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html><html lang='en'><head>");
        out.println("<meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Settings - LibraryOS</title>");
        out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css'>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head><body>");
        out.println("<div class='blob-container'><div class='blob blob-1'></div><div class='blob blob-3'></div></div>");
        
        out.println("<nav class='top-navbar'><div class='nav-container container'>");
        out.println("<a href='dashboard.html' class='nav-logo'><i class='fas fa-layer-group'></i> LibraryOS</a>");
        out.println("<div class='nav-links'>");
        out.println("<a href='dashboard.html' class='nav-link'><i class='fas fa-home'></i> Dashboard</a>");
        out.println("<a href='bookadd.html' class='nav-link'><i class='fas fa-book'></i> Books</a>");
        out.println("<a href='memberadd.html' class='nav-link'><i class='fas fa-users'></i> Members</a>");
        out.println("<a href='issuedbooklist.html' class='nav-link'><i class='fas fa-exchange-alt'></i> Issued Books</a>");
        out.println("<a href='settings' class='nav-link active'><i class='fas fa-cog'></i> Settings</a>");
        out.println("<a href='LogoutServlet' class='lib-btn lib-btn-secondary' style='height: 40px; padding: 0 1rem; font-size: 0.9rem; border-radius: 12px; margin-left: 1rem;'><i class='fas fa-sign-out-alt'></i> Logout</a>");
        out.println("</div></div></nav>");
        
        out.println("<div class='container' style='max-width: 500px; margin-top: 4rem; margin-bottom: 4rem;'>");
        out.println("<div class='lib-card' style='padding: 3rem 2rem;'>");
        out.println("<div style='text-align: center; margin-bottom: 2rem;'>");
        out.println("<div class='stat-orb orb-orange' style='margin: 0 auto 1.5rem auto;'><i class='fas fa-cog'></i></div>");
        out.println("<h1 class='text-section'>Global Settings</h1>");
        out.println("<p class='text-muted'>Manage library system rules</p>");
        out.println("</div>");

        String msg = request.getParameter("msg");
        if ("saved".equals(msg)) {
            out.println("<div style='background: rgba(16, 185, 129, 0.1); color: #10B981; padding: 1rem; border-radius: 12px; margin-bottom: 1.5rem; text-align: center; font-weight: 700;'><i class='fas fa-check-circle'></i> Settings saved successfully!</div>");
        }

        out.println("<form action='settings' method='post'>");
        out.println("<div class='form-group'>");
        out.println("<label for='penaltyRate'>Daily Penalty Rate (₹)</label>");
        out.println("<input type='number' step='0.1' min='0' id='penaltyRate' name='penaltyRate' class='lib-input' value='" + rate + "' required>");
        out.println("</div>");
        out.println("<div style='display: flex; gap: 1rem; margin-top: 2rem;'>");
        out.println("<button type='submit' class='lib-btn lib-btn-primary' style='flex: 1;'>Save Settings</button>");
        out.println("<a href='dashboard.html' class='lib-btn lib-btn-secondary' style='flex: 1;'>Cancel</a>");
        out.println("</div></form>");
        out.println("</div></div>");
        out.println("<script src='script.js'></script></body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equalsIgnoreCase((String) session.getAttribute("role"))) {
            response.sendRedirect("login.html");
            return;
        }

        try {
            double rate = Double.parseDouble(request.getParameter("penaltyRate"));
            SettingsDAO.setPenaltyRate(rate);
            response.sendRedirect("settings?msg=saved");
        } catch (Exception e) {
            response.sendRedirect("settings?error=invalid");
        }
    }
}
