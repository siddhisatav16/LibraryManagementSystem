package com.finlogic.servlet;

import com.finlogic.dao.BookDAO;
import com.finlogic.model.Book;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UpdateBookServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam   = request.getParameter("id");
        String title     = request.getParameter("title");
        String auth      = request.getParameter("auth");
        String cate      = request.getParameter("cate");
        String quntParam = request.getParameter("qunt");

        if (idParam == null || title == null || auth == null || cate == null || quntParam == null) {
            response.sendRedirect("booklist.html?error=missing_fields");
            return;
        }

        try {
            int id       = Integer.parseInt(idParam.trim());
            int quantity = Integer.parseInt(quntParam.trim());
            Book book = new Book(id, title.trim(), auth.trim(), cate.trim(), quantity);
            BookDAO.updateBook(book);
            response.sendRedirect("booklist.html?success=updated");
        } catch (NumberFormatException e) {
            response.sendRedirect("booklist.html?error=invalid_input");
        }
    }
}