package com.example.caitzh.minichat.crh;

import com.example.caitzh.minichat.userDB;

/**
 * 聊天窗口每个聊天的item
 */

public class ChatWindowItemInformation {
    // TODO:图片形式，以及time的类型是否需要修改
    private String username;
    private String information;
    private String time;
    private String userID;

    private ChatWindowItemInformation(String inputUserID, String inputUsername, String inputInformation, String inputTime) {
        this.userID = inputUserID;
        this.username = inputUsername;
        this.information = inputInformation;
        this.time = inputTime;
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
}
