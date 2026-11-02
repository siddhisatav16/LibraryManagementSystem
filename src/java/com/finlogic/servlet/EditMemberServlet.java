package com.finlogic.servlet;

import com.finlogic.dao.MemberDAO;
import com.finlogic.model.Member;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class EditMemberServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) { response.sendRedirect("memberlist.html"); return; }

        int id;
        try { id = Integer.parseInt(idParam.trim()); }
        catch (NumberFormatException e) { response.sendRedirect("memberlist.html?error=invalid_id"); return; }

        Member m = MemberDAO.getMemberById(id);
        if (m == null) { response.sendRedirect("memberlist.html?error=member_not_found"); return; }

        out.println("<!DOCTYPE html><html lang='en'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Edit Member - LibraryOS</title>");
        out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css'>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head><body>");

        out.println("<div class='blob-container'>");
        out.println("<div class='blob blob-1'></div>");
        out.println("<div class='blob blob-3'></div>");
        out.println("</div>");

        printNavbar(out, "members");

        out.println("<div class='container' style='max-width: 800px; margin-top: 4rem; margin-bottom: 4rem;'>");
        out.println("<div class='lib-card'>");
        out.println("<div style='text-align: center; margin-bottom: 2rem;'>");
        out.println("<div class='stat-orb' style='margin: 0 auto 1.5rem auto; background: linear-gradient(to bottom right, #34D399, #10B981);'>");
        out.println("<i class='fas fa-user-edit'></i>");
        out.println("</div>");
        out.println("<h1 class='text-section'>Edit Member</h1>");
        out.println("<p class='text-muted'>Update details for <strong>" + esc(m.getName()) + "</strong></p>");
        out.println("</div>");

        out.println("<form action='updatemember' method='post'>");
        out.println("<input type='hidden' name='id' value='" + m.getId() + "'>");

        out.println("<div class='form-group'>");
        out.println("<label for='name'>Full Name</label>");
        out.println("<input type='text' id='name' name='name' class='lib-input' value='" + esc(m.getName()) + "' required>");
        out.println("</div>");

        out.println("<div class='form-group'>");
        out.println("<label for='email'>Email Address</label>");
        out.println("<input type='email' id='email' name='email' class='lib-input' value='" + esc(m.getEmail()) + "' required>");
        out.println("</div>");

        out.println("<div class='form-group'>");
        out.println("<label for='phone'>Phone Number</label>");
        out.println("<input type='tel' id='phone' name='phone' class='lib-input' value='" + m.getPhone() + "' required>");
        out.println("</div>");

        out.println("<div style='display: flex; gap: 1rem; margin-top: 2rem;'>");
        out.println("<button type='submit' class='lib-btn lib-btn-primary' style='flex: 1;'>Update Member</button>");
        out.println("<a href='memberlist.html' class='lib-btn lib-btn-secondary' style='flex: 1;'>Cancel</a>");
        out.println("</div>");
        out.println("</form></div></div>");

        out.println("</body></html>");
    }

    private void printNavbar(PrintWriter out, String active) {
        out.println("<nav class='top-navbar'>");
        out.println("<div class='nav-container container'>");
        out.println("<a href='dashboard.html' class='nav-logo'><i class='fas fa-layer-group'></i> LibraryOS</a>");
        out.println("<div class='nav-links'>");
        out.println(navItem("dashboard.html",         "fa-home",         "Dashboard",   "dashboard".equals(active)));
        out.println(navItem("bookadd.html",        "fa-book",         "Books",       "books".equals(active)));
        out.println(navItem("memberadd.html",      "fa-users",        "Members",     "members".equals(active)));
        out.println(navItem("issuedbooklist.html",          "fa-exchange-alt", "Issued Books", "issue".equals(active)));
        out.println("<a href='login.html' class='lib-btn lib-btn-secondary' style='height: 40px; padding: 0 1rem; font-size: 0.9rem; border-radius: 12px; margin-left: 1rem;'><i class='fas fa-sign-out-alt'></i> Logout</a>");
        out.println("</div></div></nav>");
    }

    private String navItem(String href, String icon, String label, boolean active) {
        return "<a href='" + href + "' class='nav-link" + (active ? " active" : "") +
               "'><i class='fas " + icon + "'></i> " + label + "</a>";
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
                .replace("\"","&quot;").replace("'","&#39;");
    }
}