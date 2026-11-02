<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.sql.*, java.util.*" %>
<%!
    private static final String URL  = "jdbc:mysql://127.0.0.1:3306/library_management";
    private static final String USER = "root";
    private static final String PASS = "RoshanGupta@09";
%>
<%
    String error = null;
    List<String> tables = new ArrayList<String>();
    Connection conn = null;
    String dbName = "library_management";

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(URL, USER, PASS);
        DatabaseMetaData md = conn.getMetaData();
        dbName = conn.getCatalog();
        ResultSet trs = md.getTables(dbName, null, "%", new String[]{"TABLE"});
        while (trs.next()) tables.add(trs.getString("TABLE_NAME"));
        trs.close();
    } catch (Exception e) {
        error = e.getMessage();
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Database Viewer - LibraryOS</title>
    <style>
        * { box-sizing: border-box; }
        body {
            margin: 0; padding: 2rem;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            background: #0f172a; color: #e2e8f0;
        }
        h1 { font-size: 1.6rem; margin: 0 0 .4rem; color: #34D399; }
        .meta { color: #94a3b8; font-size: .9rem; margin-bottom: 2rem; }
        .meta code { background: #1e293b; padding: .15rem .45rem; border-radius: 5px; color: #7dd3fc; }
        .table-card {
            background: #1e293b; border: 1px solid #334155; border-radius: 12px;
            padding: 1.25rem 1.5rem; margin-bottom: 2rem; overflow-x: auto;
        }
        .table-card h2 {
            margin: 0 0 1rem; font-size: 1.15rem; color: #f8fafc;
            display: flex; align-items: center; gap: .6rem;
        }
        .badge {
            background: #064e3b; color: #6ee7b7; font-size: .75rem;
            padding: .2rem .6rem; border-radius: 20px; font-weight: 600;
        }
        table { border-collapse: collapse; width: 100%; font-size: .88rem; }
        th, td {
            text-align: left; padding: .55rem .8rem;
            border-bottom: 1px solid #334155; white-space: nowrap;
        }
        th {
            background: #0f172a; color: #34D399; font-weight: 600;
            position: sticky; top: 0; font-size: .78rem;
            text-transform: uppercase; letter-spacing: .5px;
        }
        tr:hover td { background: #243044; }
        td.null { color: #64748b; font-style: italic; }
        .empty { color: #94a3b8; font-style: italic; padding: .5rem 0; }
        .error {
            background: #450a0a; border: 1px solid #7f1d1d; color: #fca5a5;
            padding: 1rem 1.25rem; border-radius: 10px; margin-bottom: 2rem;
        }
    </style>
</head>
<body>
    <h1>Database Viewer</h1>
    <div class="meta">
        MySQL &middot; database <code><%= dbName %></code> &middot; <code>127.0.0.1:3306</code>
        &middot; <%= tables.size() %> table(s)
    </div>

<% if (error != null) { %>
    <div class="error"><strong>Connection error:</strong> <%= error %></div>
<% } else {
       for (String t : tables) {
           int count = 0;
           List<String> cols = new ArrayList<String>();
           List<List<String>> rows = new ArrayList<List<String>>();
           Statement st = conn.createStatement();
           ResultSet cnt = st.executeQuery("SELECT COUNT(*) FROM `" + t + "`");
           if (cnt.next()) count = cnt.getInt(1);
           cnt.close();
           ResultSet rs = st.executeQuery("SELECT * FROM `" + t + "` LIMIT 500");
           ResultSetMetaData rsMeta = rs.getMetaData();
           int n = rsMeta.getColumnCount();
           for (int i = 1; i <= n; i++) cols.add(rsMeta.getColumnLabel(i));
           while (rs.next()) {
               List<String> row = new ArrayList<String>();
               for (int i = 1; i <= n; i++) {
                   Object v = rs.getObject(i);
                   row.add(v == null ? null : v.toString());
               }
               rows.add(row);
           }
           rs.close();
           st.close();
%>
    <div class="table-card">
        <h2><%= t %> <span class="badge"><%= count %> rows</span></h2>
        <% if (rows.isEmpty()) { %>
            <div class="empty">No data in this table.</div>
        <% } else { %>
        <table>
            <thead><tr>
                <% for (String c : cols) { %><th><%= c %></th><% } %>
            </tr></thead>
            <tbody>
                <% for (List<String> row : rows) { %>
                <tr>
                    <% for (String v : row) {
                           if (v == null) { %><td class="null">NULL</td><%
                           } else { %><td><%= v %></td><%
                           }
                       } %>
                </tr>
                <% } %>
            </tbody>
        </table>
        <% } %>
    </div>
<%     }
   }
   if (conn != null) { try { conn.close(); } catch (Exception ignore) {} }
%>
</body>
</html>
