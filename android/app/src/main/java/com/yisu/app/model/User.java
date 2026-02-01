package com.yisu.app.model;

import java.io.Serializable;

public class User implements Serializable {
    public int id;
    public String username;
    public String password;
    public String phone;
    public String email;
    public String avatar;
    public String role; // CONSUMER for Android users
}
