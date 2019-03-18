package jmt.com.myapplication.models;

public class UserToken {
    private String uid;
    private String token;

    public UserToken(){

    }
    public UserToken(String uid, String token){
        this.token = token;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
