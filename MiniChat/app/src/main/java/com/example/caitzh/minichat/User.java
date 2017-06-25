package com.example.caitzh.minichat;

/**
 * Created by littlestar on 2017/6/25.
 */
public class User {
    private String id;
    private String nickname;
    private String sex;
    private String city;
    private String signature;
    private String avatar;
    private String finalDate;
    public User(String _id, String _nickname, String _sex, String _city,
                String _signature, String _avatar, String _finalDate) {
        id = _id; nickname = _nickname; sex = _sex; city = _city;
        signature = _signature; avatar = _avatar; finalDate = _finalDate;
    }
    public User() {};
    public void setId(String _id) { id = _id; }
    public void setNickname(String _nickname) { nickname = _nickname; }
    public void setSex(String _sex) { sex = _sex; }
    public void setCity(String _city) { city = _city; }
    public void setSignature(String _signature) { signature = _signature; }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setFinalDate(String finalDate) {
        this.finalDate = finalDate;
    }

    public String getId(){ return id; }

    public String getNickname() {
        return nickname;
    }

    public String getCity() {
        return city;
    }

    public String getFinalDate() {
        return finalDate;
    }

    public String getSex() {
        return sex;
    }

    public String getSignature() {
        return signature;
    }

    public String getAvatar() {
        return avatar;
    }
}
