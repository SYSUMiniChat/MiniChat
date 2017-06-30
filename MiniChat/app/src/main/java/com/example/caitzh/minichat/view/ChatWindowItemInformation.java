package com.example.caitzh.minichat.view;

/**
 * 聊天窗口每个聊天的item
 */

public class ChatWindowItemInformation {
    private String username;
    private String information;
    private String time;
    private String userID;
    private boolean isReaded;

    public ChatWindowItemInformation(String inputUserID, String inputUsername,
                                        String inputInformation, String inputTime,
                                        boolean inputIsReaded) {
        this.userID = inputUserID;
        this.username = inputUsername;
        this.information = inputInformation;
        this.time = inputTime;
        this.isReaded = inputIsReaded;
    }

    public void setReaded(boolean readed) {
        isReaded = readed;
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
    public boolean getReaded() {
        return isReaded;
    }
}
