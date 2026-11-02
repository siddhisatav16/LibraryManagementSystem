package com.finlogic.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import com.finlogic.dao.BookDAO;
import com.finlogic.model.Book;

public class UserIssueBookFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        String userName = "";
        String userId = "";
        if (session != null && "user".equals(session.getAttribute("role"))) {
            userName = (String) session.getAttribute("memberName");
            userId = String.valueOf(session.getAttribute("memberId"));
        }

        String bidStr = request.getParameter("bid");
        String bidVal = bidStr != null ? bidStr : "";
        String titleVal = "";
        if (bidStr != null && !bidStr.isEmpty()) {
            try {
                Book b = BookDAO.getBookById(Integer.parseInt(bidStr));
                if (b != null) {
                    titleVal = b.getTitle();
                }
            } catch (Exception e) {}
        }

        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("    <title>Issue Book - LibraryOS</title>");
        out.println("    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css\">");
        out.println("    <link rel=\"stylesheet\" href=\"css/style.css\">");
        out.println("</head>");
        out.println("<body>");
        out.println("    <div class=\"blob-container\">");
        out.println("        <div class=\"blob blob-1\"></div>");
        out.println("        <div class=\"blob blob-3\"></div>");
        out.println("    </div>");

        out.println("    <!-- Top Navbar -->");
        out.println("    <nav class=\"top-navbar\">");
        out.println("        <div class=\"nav-container container\">");
        out.println("            <a href=\"userdashboard.html\" class=\"nav-logo\">");
        out.println("                <i class=\"fas fa-layer-group\"></i> LibraryOS");
        out.println("            </a>");
        out.println("            <div class=\"nav-links\">");
        out.println("                <a href=\"userdashboard.html\" class=\"nav-link\"><i class=\"fas fa-home\"></i> Home</a>");
        out.println("                <a href=\"booklist.html\" class=\"nav-link\"><i class=\"fas fa-book\"></i> View Books</a>");
        out.println("                <a href=\"userissuebook.html\" class=\"nav-link active\"><i class=\"fas fa-book-open\"></i> Issue Book</a>");
        out.println("                <a href=\"ReturnBookServlet\" class=\"nav-link\"><i class=\"fas fa-undo\"></i> Return Book</a>");
        out.println("                <a href=\"issuedbooklist.html\" class=\"nav-link\"><i class=\"fas fa-list\"></i> My Issued Books</a>");
        out.println("                <a href=\"LogoutServlet\" class=\"lib-btn lib-btn-secondary\" style=\"height: 40px; padding: 0 1rem; font-size: 0.9rem; border-radius: 12px; margin-left: 1rem;\">");
        out.println("                    <i class=\"fas fa-sign-out-alt\"></i> Logout");
        out.println("                </a>");
        out.println("            </div>");
        out.println("        </div>");
        out.println("    </nav>");

        out.println("    <div class=\"container\" style=\"max-width: 600px; margin-top: 4rem; margin-bottom: 4rem;\">");
        out.println("        <div class=\"lib-card\">");
        out.println("            <div style=\"text-align: center; margin-bottom: 2rem;\">");
        out.println("                <div class=\"stat-orb\" style=\"margin: 0 auto 1.5rem auto; background: linear-gradient(to bottom right, #FBBF24, #F59E0B);\">");
        out.println("                    <i class=\"fas fa-exchange-alt\"></i>");
        out.println("                </div>");
        out.println("                <h1 class=\"text-section\">Issue Book</h1>");
        out.println("                <p class=\"text-muted\">Enter the book ID to issue</p>");
        out.println("            </div>");

        out.println("            <form action=\"IssueBook\" method=\"post\">");
        out.println("                <div class=\"form-group\">");
        out.println("                    <label for=\"userName\">User Name</label>");
        out.println("                    <input type=\"text\" id=\"userName\" name=\"userName\" class=\"lib-input\" value=\"" + userName + "\" readonly style=\"background-color: var(--bg); cursor: not-allowed;\" required>");
        out.println("                </div>");

        out.println("                <div class=\"form-group\">");
        out.println("                    <label for=\"mid\">User ID</label>");
        out.println("                    <input type=\"text\" id=\"mid\" name=\"mid\" class=\"lib-input\" value=\"" + userId + "\" readonly style=\"background-color: var(--bg); cursor: not-allowed;\" required>");
        out.println("                </div>");

        out.println("                <div class=\"form-group\">");
        out.println("                    <label for=\"bid\">Book ID</label>");
        out.println("                    <input type=\"text\" id=\"bid\" name=\"bid\" class=\"lib-input\" value=\"" + bidVal + "\" " + (!bidVal.isEmpty() ? "readonly style=\"background-color: var(--bg); cursor: not-allowed;\"" : "") + " required>");
        out.println("                </div>");

        out.println("                <div class=\"form-group\">");
        out.println("                    <label for=\"bookTitle\">Book Title</label>");
        out.println("                    <input type=\"text\" id=\"bookTitle\" name=\"bookTitle\" class=\"lib-input\" value=\"" + titleVal + "\" " + (!titleVal.isEmpty() ? "readonly style=\"background-color: var(--bg); cursor: not-allowed;\"" : "") + " required>");
        out.println("                </div>");

        out.println("                <div class=\"form-group\">");
        out.println("                    <label for=\"issue\">Issue Date</label>");
        out.println("                    <input type=\"date\" id=\"issue\" name=\"issue\" class=\"lib-input\" required>");
        out.println("                </div>");

        out.println("                <div class=\"form-group\">");
        out.println("                    <label for=\"return\">Due Date</label>");
        out.println("                    <input type=\"date\" id=\"return\" name=\"return\" class=\"lib-input\" required>");
        out.println("                </div>");
        
        out.println("                <div class=\"form-group\" style=\"background: var(--surface); padding: 1rem; border-radius: 12px; border-left: 4px solid var(--warning); margin-bottom: 1.5rem;\">");
        out.println("                    <p style=\"color: var(--warning); margin: 0; font-weight: bold;\"><i class=\"fas fa-exclamation-triangle\"></i> Library Rules:</p>");
        out.println("                    <p class=\"text-muted\" style=\"margin-top: 0.5rem; font-size: 0.9rem;\">A penalty of <strong>₹" + com.finlogic.dao.SettingsDAO.getPenaltyRate() + " per day</strong> will be automatically applied if you do not return the book before the due date. The book cannot be returned until the accumulated fine is paid.</p>");
        out.println("                </div>");

        out.println("                <div style=\"display: flex; gap: 1rem; margin-top: 2rem;\">");
        out.println("                    <button type=\"submit\" class=\"lib-btn lib-btn-primary\" style=\"flex: 1;\">Issue Book</button>");
        out.println("                    <a href=\"userdashboard.html\" class=\"lib-btn lib-btn-secondary\" style=\"flex: 1;\">Cancel</a>");
        out.println("                </div>");
        out.println("            </form>");
        out.println("        </div>");
        out.println("    </div>");
        out.println("</body>");
        out.println("</html>");
    }
}
