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
import java.sql.Date;
import java.util.List;

public class DashboardDataServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        int totalBooks   = BookDAO.getTotalBooks();
        int totalMembers = MemberDAO.getTotalMembers();
        int issuedBooks  = BookDAO.getIssuedBooksCount();
        int overdueCount = BookDAO.getOverdueBooksCount();

        List<Book>      recentBooks  = BookDAO.getRecentBooks(5);
        List<IssueBook> overdueBooks = IssueBookDAO.getOverdueBooks();
        Date today = new Date(System.currentTimeMillis());

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"totalBooks\":").append(totalBooks).append(",");
        json.append("\"totalMembers\":").append(totalMembers).append(",");
        json.append("\"issuedBooks\":").append(issuedBooks).append(",");
        json.append("\"overdueBooks\":").append(overdueCount).append(",");

        // recentBooks array
        json.append("\"recentBooks\":[");
        for (int i = 0; i < recentBooks.size(); i++) {
            Book b = recentBooks.get(i);
            json.append("{");
            json.append("\"id\":").append(b.getId()).append(",");
            json.append("\"title\":\"").append(escape(b.getTitle())).append("\",");
            json.append("\"author\":\"").append(escape(b.getAuthor())).append("\",");
            json.append("\"category\":\"").append(escape(b.getCategory())).append("\",");
            json.append("\"quantity\":").append(b.getQuantity());
            json.append("}");
            if (i < recentBooks.size() - 1) json.append(",");
        }
        json.append("],");

        // overdueBooksList array (up to 5 entries for dashboard widget)
        json.append("\"overdueBooksList\":[");
        int limit = Math.min(overdueBooks.size(), 5);
        for (int i = 0; i < limit; i++) {
            IssueBook ib   = overdueBooks.get(i);
            Book   book    = BookDAO.getBookById(ib.getBookId());
            Member member  = MemberDAO.getMemberById(ib.getMemberId());
            int daysOverdue = IssueBookDAO.calculateDaysOverdue(ib, today);

            String title      = (book   != null) ? escape(book.getTitle())    : "Unknown";
            String memberName = (member != null) ? escape(member.getName())   : "Unknown";

            json.append("{");
            json.append("\"issueId\":").append(ib.getId()).append(",");
            json.append("\"title\":\"").append(title).append("\",");
            json.append("\"memberName\":\"").append(memberName).append("\",");
            json.append("\"daysOverdue\":").append(daysOverdue);
            json.append("}");
            if (i < limit - 1) json.append(",");
        }
        json.append("]");

        json.append("}");

        out.print(json);
        out.flush();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}