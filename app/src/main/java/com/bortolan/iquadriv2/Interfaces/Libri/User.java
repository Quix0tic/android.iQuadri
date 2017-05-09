package com.bortolan.iquadriv2.Interfaces.Libri;

public class User {
    private String name, phone, city, created_at;

    public User(String name, String phone, String city, String created_at) {
        this.name = name;
        this.phone = phone;
        this.created_at = created_at;
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }
}
