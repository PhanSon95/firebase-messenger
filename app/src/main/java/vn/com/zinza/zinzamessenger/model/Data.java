package vn.com.zinza.zinzamessenger.model;

/**
 * Created by ASUS on 03/01/2017.
 */

public class Data {
    private String id;
    private String token;
    private String avatarURL;
    private String type;

    public Data(String id, String token,String avatarURL, String type) {
        this.id = id;
        this.token = token;
        this.avatarURL = avatarURL;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
