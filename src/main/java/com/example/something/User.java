package com.example.something;



public class User {
    private String username;
    private String password;
    private int age = 0;

    public User() {
    }

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, int age){
        this.username = username;
        this.password = password;
        this.age = age;
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age){
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
