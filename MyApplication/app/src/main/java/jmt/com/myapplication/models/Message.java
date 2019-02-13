package jmt.com.myapplication.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Map;

import jmt.com.myapplication.helpers.Helper;

public class Message {
    private String id;
    private String content;
    private User sender;
    private Long timestamp;
    private String type;

    public Message(String id, String content, User sender, String type) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.type = type;
    }

    public Message() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getSender() {
        return Helper.GetCurrentUser();
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Map<String, String> getTimestamp() {
        return ServerValue.TIMESTAMP;
    }

    @Exclude
    public Long getTimestampLong() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}


