package com.rogurea.net;

import java.util.Arrays;

public class SessionData {
    private String sessionToken;
    private String playerName;
    private Long playerID;
    private byte[] sessionKey;
    private int score;
    private int kills;
    private int floor;
    private int room;

    @Override
    public String toString() {
        return "{"
                + "\"sessionToken\":\"" + sessionToken + "\""
                + ", \"playerName\":\"" + playerName + "\""
                + ", \"playerID\":\"" + playerID + "\""
                + ", \"sessionKey\":" + Arrays.toString(sessionKey)
                + ", \"score\":\"" + score + "\""
                + ", \"kills\":\"" + kills + "\""
                + ", \"floor\":\"" + floor + "\""
                + ", \"room\":\"" + room + "\""
                + "}";
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Long getPlayerID() {
        return playerID;
    }

    public void setPlayerID(Long playerID) {
        this.playerID = playerID;
    }

    public byte[] getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(byte[] sessionKey) {
        this.sessionKey = sessionKey;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }
}
