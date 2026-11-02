package com.finlogic.servlet;

import com.finlogic.dao.BookDAO;
import com.finlogic.model.Book;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ViewBookByIdServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect("booklist.html");
            return;
        }

        try {
            int id = Integer.parseInt(idParam.trim());
            Book b = BookDAO.getBookById(id);

            out.println("<!DOCTYPE html><html><head><title>Book Details</title><link rel='stylesheet' href='css/style.css'></head><body>");
            out.println("<div class='container' style='margin-top:4rem;'><div class='lib-card'>");
            
            if (b != null) {
                out.println("<h2>Book Details</h2>");
                out.println("<p><strong>ID:</strong> " + b.getId() + "</p>");
                out.println("<p><strong>Title:</strong> " + b.getTitle() + "</p>");
                out.println("<p><strong>Author:</strong> " + b.getAuthor() + "</p>");
                out.println("<p><strong>Category:</strong> " + b.getCategory() + "</p>");
                out.println("<p><strong>Quantity:</strong> " + b.getQuantity() + "</p>");
            } else {
                out.println("<p>Book not found.</p>");
            }
            
            out.println("<a href='booklist.html' class='lib-btn lib-btn-secondary'>Back to List</a>");
            out.println("</div></div></body></html>");

        } catch (NumberFormatException e) {
            response.sendRedirect("booklist.html");
        }
    }
}
