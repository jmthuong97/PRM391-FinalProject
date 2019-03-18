package jmt.com.myapplication.models;

public class ToDoList {
    private int id;
    private String name;
    private String desciption;
    private String date;
    private String userID;
    private boolean status;



    public ToDoList(int id, String name, String desciption, String date, boolean status, String userID) {
        this.id = id;
        this.name = name;
        this.desciption = desciption;
        this.date = date;
        this.userID = userID;
        this.status = status;
    }

    public ToDoList() {

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
