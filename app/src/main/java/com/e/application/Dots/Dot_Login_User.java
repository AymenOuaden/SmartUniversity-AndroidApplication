package com.e.application.Dots;

public class Dot_Login_User
{
    private String user = "";
    private String pass = "";
    private boolean isAndroid = true;

    public Dot_Login_User()
    {

    }

    public Dot_Login_User(String user, String pass)
    {
        this.user = user;
        this.pass = pass;
        this.isAndroid = true;
    }

    public String getUser()
    {
        return user;
    }


    public String getPass()
    {
        return pass;
    }


    public void setUser(String user)
    {
        this.user = user;
    }


    public void setPass(String pass)
    {
        this.pass = pass;
    }

    public boolean isAndroid()
    {
        return true;
    }

    public void setAndroid()
    {
        this.isAndroid = true;
    }



}
