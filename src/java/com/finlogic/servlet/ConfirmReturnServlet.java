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

public class ConfirmReturnServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String issueIdParam    = request.getParameter("issueId");
        String penaltyParam    = request.getParameter("penalty");
        String penaltyPaidParam = request.getParameter("penaltyPaid");
        String bookIdParam     = request.getParameter("bookId");
        String memberIdParam   = request.getParameter("memberId");

        // Validate
        if (issueIdParam == null || penaltyParam == null || penaltyPaidParam == null ||
            bookIdParam == null  || memberIdParam == null) {
            response.sendRedirect("ReturnBookServlet?error=invalid");
            return;
        }

        int    issueId;
        double penalty;
        boolean penaltyPaid;
        int bookId, memberId;
        try {
            issueId     = Integer.parseInt(issueIdParam.trim());
            penalty     = Double.parseDouble(penaltyParam.trim());
            penaltyPaid = Boolean.parseBoolean(penaltyPaidParam.trim());
            
            String cleanBid = bookIdParam.trim();
            if (cleanBid.toUpperCase().startsWith("BK")) {
                cleanBid = cleanBid.substring(2);
            }
            bookId = Integer.parseInt(cleanBid);
            
            memberId    = Integer.parseInt(memberIdParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect("ReturnBookServlet?error=invalid");
            return;
        }

        IssueBook ib = IssueBookDAO.getIssueBookById(issueId);
        if (ib == null) {
            response.sendRedirect("issuedbooklist.html?error=failed");
            return;
        }
        if (ib.isReturned()) {
            response.sendRedirect("issuedbooklist.html?error=failed");
            return;
        }

        // Mark as returned
        ib.setReturned(true);
        ib.setActualReturnDate(new Date(System.currentTimeMillis()));
        ib.setPenaltyAmount(penalty);
        ib.setPenaltyPaid(penaltyPaid);

        int result = IssueBookDAO.updateIssueBook(ib);
        if (result > 0) {
            // Restore book stock
            Book book = BookDAO.getBookById(bookId);
            if (book != null) {
                book.setQuantity(book.getQuantity() + 1);
                BookDAO.updateBook(book);
            }

            // Success page
            out.println("<!DOCTYPE html><html lang='en'><head>");
            out.println("<meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Return Successful - LibraryOS</title>");
            out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css'>");
            out.println("<link rel='stylesheet' href='css/style.css'>");
            out.println("</head><body>");

            out.println("<div class='blob-container'><div class='blob blob-1'></div><div class='blob blob-3'></div></div>");

            printNavbar(out, "return", request);

            out.println("<div class='container flex-center min-h-screen-nav'>");
            out.println("<div class='lib-card max-w-480 text-center' style='padding: 3rem 2rem;'>");
            out.println("<div class='stat-orb m-auto orb-green-gradient fs-2rem'><i class='fas fa-check-circle'></i></div>");
            out.println("<h2 class='text-section'>Return Successful!</h2>");
            out.println("<p class='text-muted mb-2'>Issue #" + issueId + " has been processed.</p>");
            
            out.println("<div class='lib-table-container mb-2'>");
            out.println("<table class='lib-table w-full text-left'>");
            out.println("<tr><th>Penalty</th><td class='font-black fs-1-1rem'>₹" + String.format("%.2f", penalty) + "</td></tr>");
            String badgeClass = penaltyPaid ? "badge-success" : "badge-danger";
            out.println("<tr><th>Paid Status</th><td><span class='badge " + badgeClass + "'>" + (penaltyPaid ? "Paid ✅" : "Unpaid ❌") + "</span></td></tr>");
            out.println("</table></div>");

            out.println("<div class='flex gap-1 justify-center'>");
            out.println("<a href='issuedbooklist.html' class='lib-btn lib-btn-secondary flex-1'>All Issues</a>");
            out.println("<a href='ReturnBookServlet' class='lib-btn lib-btn-primary flex-1'>Return Another</a>");
            out.println("</div></div></div>");
            out.println("<script src='script.js'></script></body></html>");
        } else {
            response.sendRedirect("ReturnBookServlet?error=failed");
        }
    }

    private void printNavbar(PrintWriter out, String active, HttpServletRequest request) {
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        boolean isUser = (session != null && "user".equals(session.getAttribute("role")));

        out.println("<nav class='top-navbar'>");
        out.println("<div class='nav-container container'>");
        out.println("<a href='" + (isUser ? "userdashboard.html" : "dashboard.html") + "' class='nav-logo'><i class='fas fa-layer-group'></i> LibraryOS</a>");
        out.println("<div class='nav-links'>");
        
        if (isUser) {
            out.println(navItem("userdashboard.html", "fa-home", "Home", "dashboard".equals(active)));
            out.println(navItem("userissuebook.html", "fa-book-open", "Issue Book", "issue".equals(active)));
            out.println(navItem("ReturnBookServlet", "fa-undo", "Return Book", "return".equals(active)));
        } else {
            out.println(navItem("dashboard.html",         "fa-home",         "Dashboard",   "dashboard".equals(active)));
            out.println(navItem("bookadd.html",        "fa-book",         "Books",       "books".equals(active)));
            out.println(navItem("memberadd.html",      "fa-users",        "Members",     "members".equals(active)));
            out.println(navItem("issuedbooklist.html",          "fa-exchange-alt", "Issued Books", "issue".equals(active) || "return".equals(active)));
        }
        
        out.println("<a href='LogoutServlet' class='lib-btn lib-btn-secondary h-40 px-4 fs-md' style='border-radius: 12px; margin-left: 1rem;'><i class='fas fa-sign-out-alt'></i> Logout</a>");
        out.println("</div></div></nav>");
    }

    private String navItem(String href, String icon, String label, boolean active) {
        return "<a href='" + href + "' class='nav-link" + (active ? " active" : "") +
               "'><i class='fas " + icon + "'></i> " + label + "</a>";
    }
}