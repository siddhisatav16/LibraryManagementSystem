package com.finlogic.dao;

import com.finlogic.config.DBConnection;
import com.finlogic.model.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Member operations.
 */
public class MemberDAO {

    // AUTHENTICATE
    public static Member authenticate(String email, String password) {
        String sql = "SELECT * FROM members WHERE email=? AND password=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapMember(rs);
            }
        } catch (SQLException e) {
            System.err.println("[MemberDAO.authenticate] " + e.getMessage());
        }
        return null;
    }

    // INSERT
    public static int addMember(Member member) {
        String sql = "INSERT INTO members (name, email, phone, password, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setLong(3, member.getPhone());
            ps.setString(4, member.getPassword() != null ? member.getPassword() : "123456");
            ps.setString(5, member.getRole() != null ? member.getRole() : "user");
            return ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[MemberDAO.addMember] " + e.getMessage());
        }
        return 0;
    }

    /** @deprecated use addMember() — kept for backward-compat with old servlet call */
    public static int AddMember(Member member) {
        return addMember(member);
    }

    // UPDATE
    public static int updateMember(Member member) {
        String sql = "UPDATE members SET name=?, email=?, phone=?, password=?, role=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setLong(3, member.getPhone());
            ps.setString(4, member.getPassword());
            ps.setString(5, member.getRole());
            ps.setInt(6, member.getId());
            return ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[MemberDAO.updateMember] " + e.getMessage());
        }
        return 0;
    }

    // GET BY ID
    public static Member getMemberById(int id) {
        String sql = "SELECT * FROM members WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapMember(rs);
            }
        } catch (SQLException e) {
            System.err.println("[MemberDAO.getMemberById] " + e.getMessage());
        }
        return null;
    }

    // GET ALL
    public static List<Member> getAllMembers() {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT * FROM members ORDER BY id DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapMember(rs));

        } catch (SQLException e) {
            System.err.println("[MemberDAO.getAllMembers] " + e.getMessage());
        }
        return list;
    }

    // COUNTS
    public static int getTotalMembers() {
        String sql = "SELECT COUNT(*) FROM members";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("[MemberDAO.getTotalMembers] " + e.getMessage());
        }
        return 0;
    }

    public static int getActiveMembers() {
        String sql = "SELECT COUNT(DISTINCT member_id) FROM issue_books WHERE returned = false";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("[MemberDAO.getActiveMembers] " + e.getMessage());
        }
        return 0;
    }

    // PRIVATE HELPER
    private static Member mapMember(ResultSet rs) throws SQLException {
        String password = null;
        String role = "user";
        try { password = rs.getString("password"); } catch (SQLException e) {}
        try { role = rs.getString("role"); } catch (SQLException e) {}
        
        return new Member(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getLong("phone"),
            password,
            role
        );
    }
}