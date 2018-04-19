package com.example.codebox.homelock;

/**
 * Created by suvam on 4/10/17.
 */

public class Member {
    private String name;
    private String email;
    private String permission;

    public Member(String name, String email,String permission){
        this.name = name;
        this.email = email;
        this.permission = permission;
    }

    public String getName(){
        return name;
    }
    public String getemail(){
        return email;
    }
    public String getPermission(){
        return permission;
    }
}
