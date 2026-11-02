package com.finlogic.servlet;

import com.finlogic.dao.BookDAO;
import com.finlogic.model.Book;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ViewBookServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        List<Book> list = BookDAO.getAllBooks();

        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < list.size(); i++) {
            Book b = list.get(i);
            json.append("{");
            json.append("\"id\":").append(b.getId()).append(",");
            json.append("\"title\":\"").append(esc(b.getTitle())).append("\",");
            json.append("\"author\":\"").append(esc(b.getAuthor())).append("\",");
            json.append("\"category\":\"").append(esc(b.getCategory())).append("\",");
            json.append("\"quantity\":").append(b.getQuantity());
            json.append("}");
            if (i < list.size() - 1) json.append(",");
        }
        json.append("]");

        out.print(json.toString());
        out.flush();
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}