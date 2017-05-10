package com.bortolan.iquadriv2.Interfaces.Libri;

import java.util.Date;

public class Announcement {
    private String uuid, title, isbn, subject, edition, grade, notes, phone, city, name;
    private int price;
    private Date createdAt, updatedAt;

    public Announcement(String uuid, String title, String isbn, String subject, String edition, String grade, String notes, String phone, int price, Date createdAt) {
        this.uuid = uuid;
        this.title = title;
        this.isbn = isbn;
        this.subject = subject;
        this.edition = edition;
        this.grade = grade;
        this.notes = notes;
        this.phone = phone;
        this.price = price;
        this.createdAt = createdAt;
    }

    public String getUnique_id() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getSubject() {
        return subject;
    }

    public String getEdition() {
        return edition;
    }

    public String getGrade() {
        return grade;
    }

    public String getNotes() {
        return notes;
    }

    public int getPrice() {
        return price;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean contains(String s) {

        return isbn.contains(s) ||
                title.toLowerCase().contains(s) ||
                subject.toLowerCase().contains(s) ||
                notes.toLowerCase().contains(s);

    }
}
