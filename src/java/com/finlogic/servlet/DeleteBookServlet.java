package com.finlogic.servlet;

import com.finlogic.dao.BookDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DeleteBookServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bookIdParam = request.getParameter("bookId");

        if (bookIdParam == null || bookIdParam.isBlank()) {
            response.sendRedirect("removebook.html?error=missing_id");
            return;
        }

        try {
            int bookId = Integer.parseInt(bookIdParam.trim());
            int status = BookDAO.deleteBook(bookId);

            if (status > 0) {
                response.sendRedirect("booklist.html?success=deleted");
            } else {
                response.sendRedirect("removebook.html?error=not_found");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("removebook.html?error=invalid_id");
        }
    }
}
