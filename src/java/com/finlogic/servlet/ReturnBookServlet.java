package com.finlogic.servlet;

import com.finlogic.dao.IssueBookDAO;
import com.finlogic.model.IssueBook;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;

public class ReturnBookServlet extends HttpServlet {

    // GET: Show find-form OR confirmation page
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isBlank()) {
            showConfirmPage(request, response, idParam.trim());
        } else {
            showFindForm(request, response);
        }
    }

    // POST: Search by bookId + memberId and redirect to confirm page
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bookIdParam   = request.getParameter("bookId");
        String memberIdParam = request.getParameter("memberId"); // Might be null or empty

        if (bookIdParam == null || bookIdParam.isBlank()) {
            response.sendRedirect("ReturnBookServlet?error=missing_fields");
            return;
        }

        try {
            String cleanBid = bookIdParam.trim();
            if (cleanBid.toUpperCase().startsWith("BK")) {
                cleanBid = cleanBid.substring(2);
            }
            int bookId = Integer.parseInt(cleanBid);
            IssueBook ib = null;

            jakarta.servlet.http.HttpSession session = request.getSession(false);
            if (session != null && "user".equals(session.getAttribute("role"))) {
                // User returning their own book
                int memberId = (Integer) session.getAttribute("memberId");
                ib = IssueBookDAO.getIssuedBook(bookId, memberId);
            } else {
                // Admin returning a book - memberId is optional
                if (memberIdParam != null && !memberIdParam.isBlank()) {
                    int memberId = Integer.parseInt(memberIdParam.trim());
                    ib = IssueBookDAO.getIssuedBook(bookId, memberId);
                } else {
                    ib = IssueBookDAO.getIssuedBookByBookId(bookId);
                }
            }

            if (ib == null) {
                sendErrorJs(response, "No active issue record found for Book ID " + bookId + ".", "ReturnBookServlet");
                return;
            }
            // Redirect to confirm page with the issue id
            response.sendRedirect("ReturnBookServlet?id=" + ib.getId());

        } catch (NumberFormatException e) {
            response.sendRedirect("ReturnBookServlet?error=invalid_input");
        }
    }

    // FIND FORM
    private void showFindForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html><html lang='en'><head>");
        out.println("<meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Return Book - LibraryOS</title>");
        out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css'>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head><body>");
        
        out.println("<div class='blob-container'>");
        out.println("<div class='blob blob-1'></div>");
        out.println("<div class='blob blob-3'></div>");
        out.println("</div>");
        
        printNavbar(out, "return", request);

        out.println("<div class='container max-w-800 mt-4 mb-4'>");
        out.println("<div class='lib-card'>");
        out.println("<div class='text-center mb-2'>");
        out.println("<div class='stat-orb m-auto' style='background: linear-gradient(to bottom right, #34D399, #10B981);'>");
        out.println("<i class='fas fa-undo'></i>");
        out.println("</div>");
        out.println("<h1 class='text-section'>Return Book</h1>");
        out.println("<p class='text-muted'>Find the issued book record to process a return</p>");
        out.println("</div>");

        jakarta.servlet.http.HttpSession session = request.getSession(false);
        boolean isUser = (session != null && "user".equals(session.getAttribute("role")));
        String userName = isUser ? (String) session.getAttribute("memberName") : "";
        String userId = isUser ? String.valueOf(session.getAttribute("memberId")) : "";

        out.println("<form action='ReturnBookServlet' method='post'>");
        
        out.println("<div class='form-group'><label for='userName'>User Name</label>");
        out.println("<input type='text' class='lib-input bg-muted cursor-not-allowed' id='userName' name='userName' value='" + userName + "' " + (isUser ? "readonly" : "required") + "></div>");

        out.println("<div class='form-group'><label for='memberId'>User ID</label>");
        out.println("<input type='number' class='lib-input bg-muted cursor-not-allowed' id='memberId' name='memberId' min='1' value='" + userId + "' " + (isUser ? "readonly" : "required") + "></div>");

        out.println("<div class='form-group'><label for='bookTitle'>Book Title</label>");
        out.println("<input type='text' class='lib-input' id='bookTitle' name='bookTitle' required></div>");

        out.println("<div class='form-group'><label for='bookId'>Book ID</label>");
        out.println("<input type='text' class='lib-input' id='bookId' name='bookId' required></div>");

        out.println("<div class='form-group'><label for='issueDate'>Issue Date</label>");
        out.println("<input type='date' class='lib-input' id='issueDate' name='issueDate' required></div>");

        out.println("<div class='form-group'><label for='dueDate'>Due Date</label>");
        out.println("<input type='date' class='lib-input' id='dueDate' name='dueDate' required></div>");

        out.println("<div class='form-group'><label for='fine'>Fine (₹)</label>");
        out.println("<input type='text' class='lib-input bg-muted cursor-not-allowed' id='fine' name='fine' value='0.0' readonly></div>");
        
        out.println("<div class='flex gap-1 mt-2'>");
        out.println("<button type='submit' class='lib-btn lib-btn-primary flex-1'>Find Record</button>");
        out.println("<a href='" + (isUser ? "userdashboard.html" : "dashboard.html") + "' class='lib-btn lib-btn-secondary flex-1'>Cancel</a>");
        out.println("</div></form>");
        out.println("</div></div>");
        out.println("<script src='script.js'></script></body></html>");
    }

    // CONFIRM PAGE
    private void showConfirmPage(HttpServletRequest request, HttpServletResponse response, String idParam)
            throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        int issueId;
        try { issueId = Integer.parseInt(idParam); }
        catch (NumberFormatException e) { response.sendRedirect("issuedbooklist.html"); return; }

        IssueBook ib = IssueBookDAO.getIssueBookById(issueId);
        if (ib == null) {
            sendErrorJs(response, "Issue record #" + issueId + " not found.", "issuedbooklist.html");
            return;
        }
        if (ib.isReturned()) {
            sendErrorJs(response, "This book has already been returned!", "issuedbooklist.html");
            return;
        }

        Date   today      = new Date(System.currentTimeMillis());
        double penalty    = IssueBookDAO.calculatePenalty(ib, today);
        int    daysOverdue = IssueBookDAO.calculateDaysOverdue(ib, today);
        
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        boolean isUser = (session != null && "user".equals(session.getAttribute("role")));

        out.println("<!DOCTYPE html><html lang='en'><head>");
        out.println("<meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Confirm Return - LibraryOS</title>");
        out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css'>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head><body>");

        out.println("<div class='blob-container'>");
        out.println("<div class='blob blob-2'></div>");
        out.println("<div class='blob blob-3'></div>");
        out.println("</div>");

        printNavbar(out, "return", request);

        out.println("<div class='container max-w-800 mt-4 mb-4'>");
        out.println("<div class='lib-card'>");
        out.println("<div class='text-center mb-2'>");
        out.println("<div class='stat-orb m-auto orb-danger-gradient'>");
        out.println("<i class='fas fa-exclamation-circle'></i>");
        out.println("</div>");
        out.println("<h1 class='text-section'>Confirm Return</h1>");
        out.println("<p class='text-muted'>Review details before processing the return</p>");
        out.println("</div>");

        // Fetch Book and Member details
        com.finlogic.model.Book book = com.finlogic.dao.BookDAO.getBookById(ib.getBookId());
        com.finlogic.model.Member member = com.finlogic.dao.MemberDAO.getMemberById(ib.getMemberId());

        // Issue summary
        out.println("<div class='lib-table-container mb-2'>");
        out.println("<table class='lib-table w-full'>");
        out.println("<tr><th>Issue ID</th><td>#" + ib.getId() + "</td></tr>");
        out.println("<tr><th>User ID</th><td>" + ib.getMemberId() + "</td></tr>");
        out.println("<tr><th>User Name</th><td>" + (member != null ? member.getName() : "Unknown") + "</td></tr>");
        out.println("<tr><th>Book ID</th><td>" + ib.getBookId() + "</td></tr>");
        out.println("<tr><th>Book Title</th><td>" + (book != null ? book.getTitle() : "Unknown") + "</td></tr>");
        out.println("<tr><th>Issue Date</th><td>" + ib.getIssueDate() + "</td></tr>");
        out.println("<tr><th>Due Date</th><td>" + ib.getReturnDate() + "</td></tr>");
        out.println("<tr><th>Return Date (Today)</th><td>" + today + "</td></tr>");
        out.println("<tr><th>Days Overdue</th><td>" +
                    (daysOverdue > 0 ? "<span class='badge badge-danger'>" + daysOverdue + " days</span>" : "<span class='badge badge-success'>On time</span>") +
                    "</td></tr>");
        out.println("<tr><th>Fine / Penalty</th><td class='font-black fs-1-1rem' style='color: var(--accent-secondary);'>₹" + String.format("%.2f", penalty) + "</td></tr>");
        out.println("</table></div>");

        // Confirm form
        out.println("<form action='ConfirmReturnServlet' method='post'>");
        out.println("<input type='hidden' name='issueId' value='" + ib.getId()       + "'>");
        out.println("<input type='hidden' name='penalty' value='" + penalty          + "'>");
        out.println("<input type='hidden' name='bookId'  value='" + ib.getBookId()   + "'>");
        out.println("<input type='hidden' name='memberId' value='" + ib.getMemberId() + "'>");
        
        if (isUser) {
            out.println("<input type='hidden' name='penaltyPaid' value='true'>");
            if (penalty > 0) {
                out.println("<div class='form-group p-1 border-l-danger mb-1'>");
                out.println("<p style='color: #EF4444; margin: 0; font-weight: bold;'><i class='fas fa-exclamation-triangle'></i> Fine Payment Required</p>");
                out.println("<p class='text-muted mt-1 fs-sm'>You must pay the accumulated fine of ₹" + String.format("%.2f", penalty) + " to return this book. By clicking the button below, you confirm payment.</p>");
                out.println("</div>");
            }
            out.println("<div class='flex gap-1 mt-2'>");
            out.println("<button type='submit' class='lib-btn lib-btn-primary flex-1'>" + (penalty > 0 ? "Pay Fine & Return Book" : "Confirm Return") + "</button>");
        } else {
            out.println("<div class='form-group'>");
            out.println("<label for='penaltyPaid'>Penalty Paid?</label>");
            out.println("<select id='penaltyPaid' name='penaltyPaid' class='lib-input bg-muted'>");
            out.println("<option value='false'>No — will record as unpaid</option>");
            out.println("<option value='true'>Yes — penalty collected</option>");
            out.println("</select>");
            if (penalty == 0) out.println("<p class='text-muted mt-1'><small>No penalty applicable for this return.</small></p>");
            else out.println("<p class='text-muted mt-1'><small>₹" + com.finlogic.dao.SettingsDAO.getPenaltyRate() + " per overdue day × " + daysOverdue + " days = ₹" + String.format("%.2f", penalty) + "</small></p>");
            out.println("</div>");
            out.println("<div class='flex gap-1 mt-2'>");
            out.println("<button type='submit' class='lib-btn lib-btn-primary flex-1'>Confirm Return</button>");
        }
        
        out.println("<a href='IssuedBookInfoServlet?id=" + ib.getId() + "' class='lib-btn lib-btn-secondary' style='flex: 1;'>Cancel</a>");
        out.println("</div></form>");
        out.println("</div></div>");
        out.println("<script src='script.js'></script></body></html>");
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
        
        out.println("<a href='LogoutServlet' class='lib-btn lib-btn-secondary nav-logout'><i class='fas fa-sign-out-alt'></i> Logout</a>");
        out.println("</div></div></nav>");
    }

    private String navItem(String href, String icon, String label, boolean active) {
        return "<a href='" + href + "' class='nav-link" + (active ? " active" : "") +
               "'><i class='fas " + icon + "'></i> " + label + "</a>";
    }

    private void sendErrorJs(HttpServletResponse response, String msg, String redirectUrl) throws IOException {
        response.sendRedirect(redirectUrl + "?error=failed");
    }
}