package com.finlogic.servlet;

import com.finlogic.dao.BookDAO;
import com.finlogic.dao.IssueBookDAO;
import com.finlogic.dao.MemberDAO;
import com.finlogic.model.Book;
import com.finlogic.model.IssueBook;
import com.finlogic.model.Member;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class IssuedBookInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect("issuedbooklist.html");
            return;
        }

        try {
            int id = Integer.parseInt(idParam.trim());
            IssueBook ib = IssueBookDAO.getIssueBookById(id);

            out.println("<!DOCTYPE html><html><head><title>Issue Info</title><link rel='stylesheet' href='css/style.css'></head><body>");
            out.println("<div class='container' style='margin-top:4rem;'><div class='lib-card'>");

            if (ib != null) {
                Book b = BookDAO.getBookById(ib.getBookId());
                Member m = MemberDAO.getMemberById(ib.getMemberId());

                out.println("<h2>Issue Information</h2>");
                out.println("<p><strong>Issue ID:</strong> " + ib.getId() + "</p>");
                out.println("<p><strong>Book:</strong> " + (b != null ? b.getTitle() : "ID " + ib.getBookId()) + "</p>");
                out.println("<p><strong>Member:</strong> " + (m != null ? m.getName() : "ID " + ib.getMemberId()) + "</p>");
                out.println("<p><strong>Issue Date:</strong> " + ib.getIssueDate() + "</p>");
                out.println("<p><strong>Return Date:</strong> " + ib.getReturnDate() + "</p>");
                out.println("<p><strong>Status:</strong> " + (ib.isReturned() ? "Returned" : "Active") + "</p>");
            } else {
                out.println("<p>Issue record not found.</p>");
            }

            out.println("<a href='issuedbooklist.html' class='lib-btn lib-btn-secondary'>Back to List</a>");
            out.println("</div></div></body></html>");

        } catch (NumberFormatException e) {
            response.sendRedirect("issuedbooklist.html");
        }
    }
}
