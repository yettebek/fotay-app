package com.project.fotayapp.models;

/*
Created by Yette
*/public class Comment {

    //Variables
    private String commentPhoto;
    private String commentUser;
    private String commentDate;
    private String commentText;

    //Constructor
    public Comment(String commentPhoto, String commentUser, String commentDate, String commentText) {
        this.commentPhoto = commentPhoto;
        this.commentUser = commentUser;
        this.commentDate = commentDate;
        this.commentText = commentText;
    }

    //Getters
    public String getCommentPhoto() {
        return commentPhoto;
    }

    public String getCommentUser() {
        return commentUser;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public String getCommentText() {
        return commentText;
    }

}
