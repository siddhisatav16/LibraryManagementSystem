package com.finlogic.servlet;

import com.finlogic.dao.MemberDAO;
import com.finlogic.model.Member;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ViewMemberServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        List<Member> list = MemberDAO.getAllMembers();

        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < list.size(); i++) {
            Member m = list.get(i);
            json.append("{");
            json.append("\"id\":").append(m.getId()).append(",");
            json.append("\"name\":\"").append(esc(m.getName())).append("\",");
            json.append("\"email\":\"").append(esc(m.getEmail())).append("\",");
            json.append("\"phone\":\"").append(esc(String.valueOf(m.getPhone()))).append("\",");
            json.append("\"role\":\"").append(esc(m.getRole())).append("\"");
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