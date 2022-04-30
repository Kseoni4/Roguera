package com.rogurea.net;

import com.rogurea.GameLoop;
import com.rogurea.base.Debug;
import com.rogurea.gamemap.Dungeon;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RogueraSpring {
    static HttpRequest getRequest;
    static HttpRequest postRequest;
    static HttpRequest putRequest;

    private static String ROGUERA_URL;
    private static String USERS;
    private static String GAME_SESSIONS;
    private static String GAME_SESSIONS_ID;
    private static String USER_ID;
    private static String USER_TOKEN;
    private static String USER_NICKNAME;

    static {
        Properties databaseProps = new Properties();
        try {
            databaseProps.load(RogueraSpring.class.getResource("/database.properties").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ROGUERA_URL= databaseProps.getProperty("ROGUERA_URL");
        USERS = databaseProps.getProperty("USERS");
        GAME_SESSIONS = databaseProps.getProperty("GAME_SESSIONS");
        GAME_SESSIONS_ID = databaseProps.getProperty("GAME_SESSIONS_ID");
        USER_ID = databaseProps.getProperty("USER_ID");
        USER_TOKEN = databaseProps.getProperty("USER_TOKEN");
        USER_NICKNAME = databaseProps.getProperty("USER_NICKNAME");
    }

    public static void getUser(int playerID) throws URISyntaxException, IOException, InterruptedException {

        getRequest = HttpRequest.newBuilder()
                .uri(new URI(ROGUERA_URL+USERS+"?id="+playerID))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(getRequest, HttpResponse.BodyHandlers.ofString());
        HttpHeaders responseHeaders = response.headers();
    }

    public static String createNewUser(String nickName) throws URISyntaxException, IOException, InterruptedException {

        String tokenID = Dungeon.player.getPlayerData().getToken();

        postRequest = HttpRequest.newBuilder()
                .uri(new URI(ROGUERA_URL + USERS + USER_NICKNAME + nickName + (!tokenID.equals("") ? "&tokenID="+tokenID : "")))
                .POST(HttpRequest.BodyPublishers.ofString(nickName))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(postRequest, HttpResponse.BodyHandlers.ofString());

        return response.body().replace("\"","").replace("[","").replace("]","");
    }

    public static int createGameSession() throws URISyntaxException, IOException, InterruptedException{

        postRequest = HttpRequest.newBuilder()
                .uri(new URI(ROGUERA_URL
                        + GAME_SESSIONS
                        + USER_ID
                        + Dungeon.player.getPlayerData().getPlayerID()
                        +"&"
                        +USER_TOKEN+Dungeon.player.getPlayerData().getToken()))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(postRequest, HttpResponse.BodyHandlers.ofString());
        Debug.toLog("[HTTP_POST][GAME_SESSION]Create Result: "+response.body());

        return Integer.parseInt(response.body());
    }

    static Semaphore sm = new Semaphore(1,true);

    public static void updateGameSession() throws URISyntaxException, IOException, InterruptedException {
        sm.acquire();

        GameLoop.calculatePlayTime();

        var values = new HashMap<String, String>() {{
            put("\"kills\"", String.valueOf(Dungeon.player.getPlayerData().getKills()));
            put ("\"money_earned\"", ""+Dungeon.player.getPlayerData().getMoney());
            put ("\"last_floor\"",""+Dungeon.getCurrentFloor().get().getFloorNumber());
            put ("\"last_room\"", ""+Dungeon.getCurrentRoom().roomNumber);
            put ("\"score_earned\"",""+Dungeon.player.getPlayerData().getScore());
            put ("\"items\"",""+Dungeon.player.Inventory.size());
            put ("\"play_time\"",""+GameLoop.playTime);
            put ("\"id_session\"",""+GameLoop.gameSessionId);
        }};

        putRequest = HttpRequest.newBuilder().uri(
                new URI(ROGUERA_URL
                        + GAME_SESSIONS+"/update"
                        + USER_ID
                        + Dungeon.player.getPlayerData().getPlayerID()
                        + "&"
                        + USER_TOKEN
                        + Dungeon.player.getPlayerData().getToken()))
                .headers("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(values.toString().replace("=",":")))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(putRequest, HttpResponse.BodyHandlers.ofString());

       // Debug.toLog("[HTTP_PUT][GAME_SESSION] Update Result: "+response.body());

        sm.release();
    }

    public static void finalizeGameSession() throws URISyntaxException, IOException, InterruptedException {
        putRequest = HttpRequest.newBuilder().uri(
                        new URI(ROGUERA_URL
                                + GAME_SESSIONS+"/finalize"
                                + GAME_SESSIONS_ID
                                + GameLoop.gameSessionId))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(putRequest, HttpResponse.BodyHandlers.ofString());

       // Debug.toLog("[HTTP_PUT][GAME_SESSION] Finalized Result: "+response.body());
    }

    public static boolean checkNickName(String nickName) throws URISyntaxException, IOException, InterruptedException {
        getRequest = HttpRequest.newBuilder().uri(
                new URI(ROGUERA_URL
                        +USERS
                        +"/checkNameForValid"
                        +USER_NICKNAME
                        +nickName))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(getRequest, HttpResponse.BodyHandlers.ofString());

        String result = response.body();
        Debug.toLog("[HTTP_GET][CHECK_NICKNAME] Result for "+ nickName+" is "+result);
        return result.equals("false");

    }

    public static boolean getConnection() throws URISyntaxException, IOException, InterruptedException {
        getRequest = HttpRequest.newBuilder().uri(new URI(ROGUERA_URL)).GET().build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(getRequest, HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 200;
    }
}
