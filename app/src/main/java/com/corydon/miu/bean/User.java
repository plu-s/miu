package com.corydon.miu.bean;

import java.io.Serializable;

public class User implements Serializable {
    public enum State {
        REGISTERED,
        UNACTIVE
    }
    private String mail;
    private String name;
    private String passwords;
    private State state;
    private String picUrl;

    public User() {
    }

    public User(String mail, String name, String passwords,State state) {
        this.mail = mail;
        this.name = name;
        this.passwords = passwords;
        this.state=state;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswords() {
        return passwords;
    }

    public void setPasswords(String passwords) {
        this.passwords = passwords;
    }

    public State getState() {
        return state;
    }

    public String getStateString(){
        if(state==State.REGISTERED)
            return "Registered";
        else
            return "UnActive";
    }

    public void setState(State state) {
        this.state = state;
    }
    public void setState(String state){
        if(state.equalsIgnoreCase("Registered")){
            this.state=State.REGISTERED;
        }
        else if(state.equalsIgnoreCase("UnActive")){
            this.state=State.UNACTIVE;
        }
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
