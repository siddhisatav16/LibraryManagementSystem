package com.finlogic.dao;

import com.finlogic.config.DBConnection;
import com.finlogic.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // INSERT
    public static int insertBook(Book book) {
        String sql = "INSERT INTO books (title, author, category, quantity) VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCategory());
            ps.setInt(4, book.getQuantity());
            return ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[BookDAO.insertBook] " + e.getMessage());
        }
        return 0;
    }

    // UPDATE
    public static int updateBook(Book book) {
        String sql = "UPDATE books SET title=?, author=?, category=?, quantity=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCategory());
            ps.setInt(4, book.getQuantity());
            ps.setInt(5, book.getId());
            return ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[BookDAO.updateBook] " + e.getMessage());
        }
        return 0;
    }

    // DELETE
    public static int deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("[BookDAO.deleteBook] " + e.getMessage());
        }
        return 0;
    }

    // GET BY ID
    public static Book getBookById(int id) {
        String sql = "SELECT * FROM books WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapBook(rs);
            }
        } catch (SQLException e) {
            System.err.println("[BookDAO.getBookById] " + e.getMessage());
        }
        return null;
    }

    // GET ALL
    public static List<Book> getAllBooks() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY id DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapBook(rs));

        } catch (SQLException e) {
            System.err.println("[BookDAO.getAllBooks] " + e.getMessage());
        }
        return list;
    }

    // GET RECENT
    public static List<Book> getRecentBooks(int limit) {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY id DESC LIMIT ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapBook(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BookDAO.getRecentBooks] " + e.getMessage());
        }
        return list;
    }

    // COUNTS
    public static int getTotalBooks() {
        return countQuery("SELECT COUNT(*) FROM books");
    }

    public static int getIssuedBooksCount() {
        return countQuery("SELECT COUNT(*) FROM issue_books WHERE returned = false");
    }

    public static int getOverdueBooksCount() {
        return countQuery("SELECT COUNT(*) FROM issue_books WHERE returned = false AND return_date < CURDATE()");
    }

    public static int getTotalBookQuantity() {
        return countQuery("SELECT COALESCE(SUM(quantity), 0) FROM books");
    }

    // PRIVATE HELPERS
    private static int countQuery(String sql) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("[BookDAO.countQuery] " + e.getMessage());
        }
        return 0;
    }

    private static Book mapBook(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("category"),
            rs.getInt("quantity")
        );
    }
}