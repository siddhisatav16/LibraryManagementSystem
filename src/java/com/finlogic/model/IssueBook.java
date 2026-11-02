package com.finlogic.model;

import java.sql.Date;

public class IssueBook {

    private int     id;
    private int     bookId;
    private int     memberId;
    private Date    issueDate;
    private Date    returnDate;
    private Date    actualReturnDate;
    private boolean returned;
    private double  penaltyAmount;
    private boolean penaltyPaid;

    public IssueBook() {}

    public IssueBook(int bookId, int memberId, Date issueDate, Date returnDate,
                     Date actualReturnDate, boolean returned,
                     double penaltyAmount, boolean penaltyPaid) {
        this.bookId           = bookId;
        this.memberId         = memberId;
        this.issueDate        = issueDate;
        this.returnDate       = returnDate;
        this.actualReturnDate = actualReturnDate;
        this.returned         = returned;
        this.penaltyAmount    = penaltyAmount;
        this.penaltyPaid      = penaltyPaid;
    }

    public IssueBook(int id, int bookId, int memberId, Date issueDate, Date returnDate,
                     Date actualReturnDate, boolean returned,
                     double penaltyAmount, boolean penaltyPaid) {
        this.id               = id;
        this.bookId           = bookId;
        this.memberId         = memberId;
        this.issueDate        = issueDate;
        this.returnDate       = returnDate;
        this.actualReturnDate = actualReturnDate;
        this.returned         = returned;
        this.penaltyAmount    = penaltyAmount;
        this.penaltyPaid      = penaltyPaid;
    }

    // Getters & Setters
    public int     getId()            { return id; }
    public void    setId(int id)      { this.id = id; }

    public int     getBookId()              { return bookId; }
    public void    setBookId(int bookId)    { this.bookId = bookId; }

    public int     getMemberId()                { return memberId; }
    public void    setMemberId(int memberId)    { this.memberId = memberId; }

    public Date    getIssueDate()                   { return issueDate; }
    public void    setIssueDate(Date issueDate)     { this.issueDate = issueDate; }

    public Date    getReturnDate()                    { return returnDate; }
    public void    setReturnDate(Date returnDate)     { this.returnDate = returnDate; }

    public Date    getActualReturnDate()                        { return actualReturnDate; }
    public void    setActualReturnDate(Date actualReturnDate)   { this.actualReturnDate = actualReturnDate; }

    public boolean isReturned()                 { return returned; }
    public void    setReturned(boolean returned){ this.returned = returned; }

    public double  getPenaltyAmount()                       { return penaltyAmount; }
    public void    setPenaltyAmount(double penaltyAmount)   { this.penaltyAmount = penaltyAmount; }

    public boolean isPenaltyPaid()                      { return penaltyPaid; }
    public void    setPenaltyPaid(boolean penaltyPaid)  { this.penaltyPaid = penaltyPaid; }
}