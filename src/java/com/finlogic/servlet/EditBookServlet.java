package com.finlogic.servlet;

import com.finlogic.dao.BookDAO;
import com.finlogic.model.Book;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class EditBookServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect("booklist.html?error=missing_id");
            return;
        }

        int id;
        try { id = Integer.parseInt(idParam.trim()); }
        catch (NumberFormatException e) { response.sendRedirect("booklist.html?error=invalid_id"); return; }

        Book b = BookDAO.getBookById(id);
        if (b == null) { response.sendRedirect("booklist.html?error=book_not_found"); return; }

        out.println("<!DOCTYPE html><html lang='en'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Edit Book - LibraryOS</title>");
        out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css'>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head><body>");

        out.println("<div class='blob-container'>");
        out.println("<div class='blob blob-1'></div>");
        out.println("<div class='blob blob-3'></div>");
        out.println("</div>");

        printNavbar(out, "books");

        out.println("<div class='container max-w-800 mt-4 mb-4'>");
        out.println("<div class='lib-card'>");
        out.println("<div class='text-center mb-2'>");
        out.println("<div class='stat-orb m-auto' style='background: linear-gradient(to bottom right, #60A5FA, #3B82F6);'>");
        out.println("<i class='fas fa-edit'></i>");
        out.println("</div>");
        out.println("<h1 class='text-section'>Edit Book</h1>");
        out.println("<p class='text-muted'>Update book details for <strong>" + esc(b.getTitle()) + "</strong></p>");
        out.println("</div>");

        out.println("<form action='update' method='post'>");
        out.println("<input type='hidden' name='id' value='" + b.getId() + "'>");

        out.println("<div class='form-group'>");
        out.println("<label for='title'>Book Title</label>");
        out.println("<input type='text' id='title' name='title' class='lib-input' value='" + esc(b.getTitle()) + "' required>");
        out.println("</div>");

        out.println("<div class='form-group'>");
        out.println("<label for='auth'>Author Name</label>");
        out.println("<input type='text' id='auth' name='auth' class='lib-input' value='" + esc(b.getAuthor()) + "' required>");
        out.println("</div>");

        out.println("<div class='form-group'>");
        out.println("<label for='cate'>Category</label>");
        out.println("<input type='text' id='cate' name='cate' class='lib-input' value='" + esc(b.getCategory()) + "' required>");
        out.println("</div>");

        out.println("<div class='form-group'>");
        out.println("<label for='qunt'>Quantity</label>");
        out.println("<input type='number' id='qunt' name='qunt' class='lib-input' value='" + b.getQuantity() + "' min='0' required>");
        out.println("</div>");

        out.println("<div class='flex gap-1 mt-2'>");
        out.println("<button type='submit' class='lib-btn lib-btn-primary flex-1'>Update Book</button>");
        out.println("<a href='booklist.html' class='lib-btn lib-btn-secondary flex-1'>Cancel</a>");
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
        out.println("<a href='LogoutServlet' class='lib-btn lib-btn-secondary nav-logout'><i class='fas fa-sign-out-alt'></i> Logout</a>");
        out.println("</div></div></nav>");
    }

    private String navItem(String href, String icon, String label, boolean active) {
        return "<a href='" + href + "' class='nav-link" + (active ? " active" : "") +
               "'><i class='fas " + icon + "'></i> " + label + "</a>";
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}