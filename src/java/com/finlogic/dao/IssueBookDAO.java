package com.finlogic.dao;

import com.finlogic.config.DBConnection;
import com.finlogic.model.IssueBook;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IssueBookDAO {

    // INSERT
    public static int issue(IssueBook ib) {
        String sql = "INSERT INTO issue_books (book_id, member_id, issue_date, return_date, returned, penalty_amount, penalty_paid) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, ib.getBookId());
            ps.setInt(2, ib.getMemberId());
            ps.setDate(3, ib.getIssueDate());
            ps.setDate(4, ib.getReturnDate());
            ps.setBoolean(5, false);
            ps.setDouble(6, 0.0);
            ps.setBoolean(7, false);
            return ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[IssueBookDAO.issue] " + e.getMessage());
        }
        return 0;
    }

    // GET ALL  (BUG FIX: was missing returned/penalty fields)
    public static List<IssueBook> getAllIssuedBooks() {
        List<IssueBook> list = new ArrayList<>();
        String sql = "SELECT * FROM issue_books ORDER BY id DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapFull(rs));

        } catch (SQLException e) {
            System.err.println("[IssueBookDAO.getAllIssuedBooks] " + e.getMessage());
        }
        return list;
    }

    // GET BY MEMBER ID
    public static List<IssueBook> getIssuedBooksByMemberId(int memberId) {
        List<IssueBook> list = new ArrayList<>();
        String sql = "SELECT * FROM issue_books WHERE member_id=? ORDER BY id DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapFull(rs));
            }

        } catch (SQLException e) {
            System.err.println("[IssueBookDAO.getIssuedBooksByMemberId] " + e.getMessage());
        }
        return list;
    }

    // GET BY ID
    public static IssueBook getIssueBookById(int id) {
        String sql = "SELECT * FROM issue_books WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapFull(rs);
            }
        } catch (SQLException e) {
            System.err.println("[IssueBookDAO.getIssueBookById] " + e.getMessage());
        }
        return null;
    }

    // GET BY BOOK + MEMBER (for return flow)
    public static IssueBook getIssuedBook(int bookId, int memberId) {
        String sql = "SELECT * FROM issue_books WHERE book_id=? AND member_id=? AND returned=false ORDER BY id DESC LIMIT 1";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            ps.setInt(2, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapFull(rs);
            }
        } catch (SQLException e) {
            System.err.println("[IssueBookDAO.getIssuedBook] " + e.getMessage());
        }
        return null;
    }

    // GET BY BOOK ONLY (for simplified return flow)
    public static IssueBook getIssuedBookByBookId(int bookId) {
        String sql = "SELECT * FROM issue_books WHERE book_id=? AND returned=false ORDER BY id DESC LIMIT 1";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapFull(rs);
            }
        } catch (SQLException e) {
            System.err.println("[IssueBookDAO.getIssuedBookByBookId] " + e.getMessage());
        }
        return null;
    }

    // UPDATE (mark returned)
    public static int updateIssueBook(IssueBook ib) {
        String sql = "UPDATE issue_books SET returned=?, actual_return_date=?, penalty_amount=?, penalty_paid=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setBoolean(1, ib.isReturned());
            ps.setDate(2, ib.getActualReturnDate());
            ps.setDouble(3, ib.getPenaltyAmount());
            ps.setBoolean(4, ib.isPenaltyPaid());
            ps.setInt(5, ib.getId());
            return ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[IssueBookDAO.updateIssueBook] " + e.getMessage());
        }
        return 0;
    }

    // CONVENIENCE returnBook()
    public static void returnBook(int issueId, Date actualReturnDate, double penalty, boolean penaltyPaid) {
        String sql = "UPDATE issue_books SET actual_return_date=?, returned=true, penalty_amount=?, penalty_paid=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, actualReturnDate);
            ps.setDouble(2, penalty);
            ps.setBoolean(3, penaltyPaid);
            ps.setInt(4, issueId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[IssueBookDAO.returnBook] " + e.getMessage());
        }
    }

    // PENALTY CALCULATION
    public static double calculatePenalty(IssueBook ib, Date tillDate) {
        if (ib == null || ib.getReturnDate() == null || tillDate == null) return 0.0;
        long lateDays = (tillDate.getTime() - ib.getReturnDate().getTime()) / (1000L * 60 * 60 * 24);
        double rate = com.finlogic.dao.SettingsDAO.getPenaltyRate();
        return lateDays > 0 ? lateDays * rate : 0.0;
    }

    public static int calculateDaysOverdue(IssueBook ib, Date currentDate) {
        if (ib == null || ib.getReturnDate() == null) return 0;
        long days = (currentDate.getTime() - ib.getReturnDate().getTime()) / (1000L * 60 * 60 * 24);
        return (int) Math.max(0, days);
    }

    // OVERDUE LIST FOR DASHBOARD
    /**
     * Returns a list of maps with keys: issueId, bookId, memberId, daysOverdue.
     * Use with BookDAO.getBookById() and MemberDAO.getMemberById() to get names.
     */
    public static List<IssueBook> getOverdueBooks() {
        List<IssueBook> list = new ArrayList<>();
        String sql = "SELECT * FROM issue_books WHERE returned=false AND return_date < CURDATE() ORDER BY return_date ASC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapFull(rs));

        } catch (SQLException e) {
            System.err.println("[IssueBookDAO.getOverdueBooks] " + e.getMessage());
        }
        return list;
    }

    // PRIVATE MAPPER
    private static IssueBook mapFull(ResultSet rs) throws SQLException {
        IssueBook ib = new IssueBook();
        ib.setId(rs.getInt("id"));
        ib.setBookId(rs.getInt("book_id"));
        ib.setMemberId(rs.getInt("member_id"));
        ib.setIssueDate(rs.getDate("issue_date"));
        ib.setReturnDate(rs.getDate("return_date"));
        ib.setActualReturnDate(rs.getDate("actual_return_date"));
        ib.setReturned(rs.getBoolean("returned"));
        ib.setPenaltyAmount(rs.getDouble("penalty_amount"));
        ib.setPenaltyPaid(rs.getBoolean("penalty_paid"));
        return ib;
    }
}