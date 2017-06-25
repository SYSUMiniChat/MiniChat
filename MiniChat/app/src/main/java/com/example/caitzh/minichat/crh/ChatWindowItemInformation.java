package com.example.caitzh.minichat.crh;

import com.example.caitzh.minichat.MyDB.userDB;

/**
 * 聊天窗口每个聊天的item
 */

public class ChatWindowItemInformation {
    private String path;
    private String username;
    private String information;
    private String time;
    private String userID;

    protected ChatWindowItemInformation(String inputUserID, String inputUsername,
                                        String inputInformation, String inputTime, String inputPath) {
        this.userID = inputUserID;
        this.username = inputUsername;
        this.information = inputInformation;
        this.time = inputTime;
        this.path = inputPath;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
    public void setUsername(String inputUsername){this.username = inputUsername;}
    public void setInformation(String inputInformation){this.information = inputInformation;}
    public void setTime(String inputTime){this.time = inputTime;}
    public String getUsername() {return this.username;}
    public String getInformation() {return this.information;}
    public String getTime() {return this.time;}
    public String getUserID() {
        return userID;
    }

    public String getPath() {
        return path;
    }
}
