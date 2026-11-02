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
import java.util.List;

public class IssuedBookServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        jakarta.servlet.http.HttpSession session = request.getSession(false);
        boolean isUser = (session != null && "user".equals(session.getAttribute("role")));
        Integer currentUserId = (session != null) ? (Integer) session.getAttribute("memberId") : null;

        List<IssueBook> list;
        if (isUser && currentUserId != null) {
            list = IssueBookDAO.getIssuedBooksByMemberId(currentUserId);
        } else {
            list = IssueBookDAO.getAllIssuedBooks();
        }

        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < list.size(); i++) {
            IssueBook ib = list.get(i);
            Book book = BookDAO.getBookById(ib.getBookId());
            Member member = MemberDAO.getMemberById(ib.getMemberId());

            json.append("{");
            json.append("\"id\":").append(ib.getId()).append(",");
            json.append("\"bookId\":").append(ib.getBookId()).append(",");
            json.append("\"memberId\":").append(ib.getMemberId()).append(",");
            json.append("\"bookTitle\":\"").append(book != null ? esc(book.getTitle()) : "Unknown").append("\",");
            json.append("\"memberName\":\"").append(member != null ? esc(member.getName()) : "Unknown").append("\",");
            json.append("\"issueDate\":\"").append(ib.getIssueDate()).append("\",");
            json.append("\"returnDate\":\"").append(ib.getReturnDate()).append("\",");
            json.append("\"returned\":").append(ib.isReturned());
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