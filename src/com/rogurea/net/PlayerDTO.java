package com.rogurea.net;

import java.io.Serializable;

public class PlayerDTO implements Serializable {
    private String nickName;
    private Long id;
    private String playerHash;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlayerHash() {
        return playerHash;
    }

    public void setPlayerHash(String playerHash) {
        this.playerHash = playerHash;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "{"
                + "\"nickName\":\"" + nickName + "\""
                + ", \"id\":\"" + id + "\""
                + ", \"playerHash\":\"" + playerHash + "\""
                + "}";
    }
}
