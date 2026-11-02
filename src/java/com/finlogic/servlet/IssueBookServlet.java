package com.finlogic.servlet;

import com.finlogic.dao.BookDAO;
import com.finlogic.dao.IssueBookDAO;
import com.finlogic.model.Book;
import com.finlogic.model.IssueBook;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;

public class IssueBookServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String bidParam = request.getParameter("bid");
        String midParam = request.getParameter("mid");
        String issueParam  = request.getParameter("issue");
        String returnParam = request.getParameter("return");

        // Validate presence
        if (bidParam == null || midParam == null || issueParam == null || returnParam == null ||
            bidParam.isBlank() || midParam.isBlank() || issueParam.isBlank() || returnParam.isBlank()) {
            sendError(response, "All fields are required.", "userissuebook.html");
            return;
        }

        int bookId, memberId;
        Date issueDate, returnDate;
        try {
            String cleanBid = bidParam.trim();
            if (cleanBid.toUpperCase().startsWith("BK")) {
                cleanBid = cleanBid.substring(2);
            }
            bookId = Integer.parseInt(cleanBid);
            
            jakarta.servlet.http.HttpSession session = request.getSession(false);
            if (session != null && "user".equals(session.getAttribute("role"))) {
                memberId = (Integer) session.getAttribute("memberId");
            } else {
                memberId = Integer.parseInt(midParam.trim());
            }

            issueDate  = Date.valueOf(issueParam.trim());
            returnDate = Date.valueOf(returnParam.trim());
        } catch (Exception e) {
            sendError(response, "Invalid input values.", "userissuebook.html");
            return;
        }

        if (!returnDate.after(issueDate)) {
            sendError(response, "Return date must be after the issue date.", "userissuebook.html");
            return;
        }

        // Check book exists and has stock
        Book book = BookDAO.getBookById(bookId);
        if (book == null) {
            sendError(response, "Book ID " + bookId + " not found.", "userissuebook.html");
            return;
        }
        if (book.getQuantity() < 1) {
            sendError(response, "Book &quot;" + book.getTitle() + "&quot; is out of stock.", "userissuebook.html");
            return;
        }

        // Insert issue record
        IssueBook ib = new IssueBook();
        ib.setBookId(bookId);
        ib.setMemberId(memberId);
        ib.setIssueDate(issueDate);
        ib.setReturnDate(returnDate);
        ib.setReturned(false);
        ib.setPenaltyAmount(0.0);
        ib.setPenaltyPaid(false);
        ib.setActualReturnDate(null);

        int result = IssueBookDAO.issue(ib);

        if (result > 0) {
            // Decrement book stock
            book.setQuantity(book.getQuantity() - 1);
            BookDAO.updateBook(book);
            response.sendRedirect("issuedbooklist.html?msg=issued");
        } else {
            response.sendRedirect("userissuebook.html?error=failed");
        }
    }

    private void sendError(HttpServletResponse response, String message, String backUrl) throws IOException {
        response.sendRedirect(backUrl + "?error=invalid");
    }
}