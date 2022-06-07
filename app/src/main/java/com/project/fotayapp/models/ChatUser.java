package com.project.fotayapp.models;

import java.io.Serializable;

/*
Created by Yette
*/public class ChatUser implements Serializable {

    //Variables
    private String userChatId;
    private String userPhoto;
    private String userName;
    private String lastMessage;
    private int countMessages;

    public ChatUser() {
    }

    //Constructor
    public ChatUser(String userChatId, String userPhoto, String userName, String lastMessage, int countMessages){
        this.userChatId = userChatId;
        this.userPhoto = userPhoto;
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.countMessages = countMessages;
    }

    //Getters
    public String getUserChatId(){
        return userChatId;
    }
    public String getUserPhoto(){
        return userPhoto;
    }
    public String getUserName(){
        return userName;
    }
    public String getLastMessage(){
        return lastMessage;
    }
    public int getCountMessages(){
        return countMessages;
    }
    //Setters
    public void setUserChatId(String userChatId){
        this.userChatId = userChatId;
    }
    public void setUserPhoto(String userPhoto){
        this.userPhoto = userPhoto;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }
    public void setLastMessage(String lastMessage){
        this.lastMessage = lastMessage;
    }
    public void setCountMessages(int countMessages){
        this.countMessages = countMessages;
    }

}
