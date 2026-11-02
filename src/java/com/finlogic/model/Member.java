package com.finlogic.model;

public class Member {

    private int    id;
    private String name;
    private String email;
    private long   phone;
    private String password;
    private String role; // "admin" or "user"

    public Member() {}

    public Member(String name, String email, long phone) {
        this.name  = name;
        this.email = email;
        this.phone = phone;
        this.password = "123456"; // Default password
        this.role = "user";
    }

    public Member(int id, String name, String email, long phone) {
        this.id    = id;
        this.name  = name;
        this.email = email;
        this.phone = phone;
        this.password = "123456"; // Default password
        this.role = "user";
    }

    public Member(String name, String email, long phone, String password) {
        this.name     = name;
        this.email    = email;
        this.phone    = phone;
        this.password = password;
        this.role     = "user"; // default
    }

    public Member(int id, String name, String email, long phone, String password, String role) {
        this.id       = id;
        this.name     = name;
        this.email    = email;
        this.phone    = phone;
        this.password = password;
        this.role     = role;
    }

    public int    getId()          { return id; }
    public void   setId(int id)    { this.id = id; }

    public String getName()             { return name; }
    public void   setName(String name)  { this.name = name; }

    public String getEmail()              { return email; }
    public void   setEmail(String email)  { this.email = email; }

    public long getPhone()            { return phone; }
    public void setPhone(long phone)  { this.phone = phone; }

    public String getPassword()             { return password; }
    public void   setPassword(String password) { this.password = password; }

    public String getRole()             { return role; }
    public void   setRole(String role)  { this.role = role; }
}