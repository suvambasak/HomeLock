package com.example.codebox.homelock;

/**
 * Created by suvam on 4/10/17.
 */

public class Member {
    private String name;
    private String email;

    public Member(String name, String email){
        this.name = name;
        this.email = email;
    }

    public String getName(){
        return name;
    }
    public String getemail(){
        return email;
    }
}
