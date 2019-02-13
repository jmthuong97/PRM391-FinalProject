package jmt.com.myapplication.models;

import java.util.List;

public class Group {
    private String id;
    private String displayName;
    private String description;
    private String mainColor;
    private Boolean status;
    private List<String> members;

    public Group(String id, String displayName, String description, String mainColor, Boolean status, List<String> members) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.mainColor = mainColor;
        this.status = status;
        this.members = members;
    }

    public Group(String displayName, String description, String mainColor, Boolean status, List<String> members) {
        this.displayName = displayName;
        this.description = description;
        this.mainColor = mainColor;
        this.status = status;
        this.members = members;
    }

    public Group() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMainColor() {
        return mainColor;
    }

    public void setMainColor(String mainColor) {
        this.mainColor = mainColor;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                ", mainColor='" + mainColor + '\'' +
                ", members=" + members +
                '}';
    }
}
