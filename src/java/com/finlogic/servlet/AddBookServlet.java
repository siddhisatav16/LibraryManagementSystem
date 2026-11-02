package com.finlogic.servlet;

import com.finlogic.dao.BookDAO;
import com.finlogic.model.Book;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AddBookServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String title = request.getParameter("title");
        String auth  = request.getParameter("auth");
        String cate  = request.getParameter("cate");
        String quntParam = request.getParameter("qunt");

        // Basic validation
        if (title == null || title.isBlank() ||
            auth  == null || auth.isBlank()  ||
            cate  == null || cate.isBlank()  ||
            quntParam == null || quntParam.isBlank()) {
            response.sendRedirect("bookadd.html?error=missing_fields");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quntParam.trim());
            if (quantity < 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            response.sendRedirect("bookadd.html?error=invalid_quantity");
            return;
        }

        Book book = new Book(title.trim(), auth.trim(), cate.trim(), quantity);
        int result = BookDAO.insertBook(book);

        if (result > 0) {
            response.sendRedirect("booklist.html?success=added");
        } else {
            response.sendRedirect("bookadd.html?error=db_error");
        }
    }
}