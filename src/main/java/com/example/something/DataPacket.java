package com.example.something;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;

public class DataPacket {
    private String username;
    private String userPassword;
    private String repeatPassword;
    private int userAge;
    DataPacket(String username, String userPassword, String repeatPassword, int userAge){
        this.username = username;
        this.userPassword = userPassword;
        this.repeatPassword = repeatPassword;
        this.userAge = userAge;
    }

    public int getUserAge() {
        return userAge;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUsername() {
        return username;
    }
}
