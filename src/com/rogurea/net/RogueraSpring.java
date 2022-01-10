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

    public static void getUser(int playerID) throws URISyntaxException, IOException, InterruptedException {

        getRequest = HttpRequest.newBuilder()
                .uri(new URI(Credentials.ROGUERA_URL+Credentials.USERS+"?id="+playerID))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(getRequest, HttpResponse.BodyHandlers.ofString());
        HttpHeaders responseHeaders = response.headers();
    }

    public static String createNewUser(String nickName) throws URISyntaxException, IOException, InterruptedException {

        String tokenID = Dungeon.player.getPlayerData().getToken();

        postRequest = HttpRequest.newBuilder()
                .uri(new URI(Credentials.ROGUERA_URL + Credentials.USERS + Credentials.USER_NICKNAME + nickName + (!tokenID.equals("") ? "&tokenID="+tokenID : "")))
                .POST(HttpRequest.BodyPublishers.ofString(nickName))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(postRequest, HttpResponse.BodyHandlers.ofString());

        return response.body().replace("\"","").replace("[","").replace("]","");
    }

    public static int createGameSession() throws URISyntaxException, IOException, InterruptedException{

        postRequest = HttpRequest.newBuilder()
                .uri(new URI(Credentials.ROGUERA_URL
                        + Credentials.GAME_SESSIONS
                        + Credentials.USER_ID
                        + Dungeon.player.getPlayerData().getPlayerID()
                        +"&"
                        +Credentials.USER_TOKEN+Dungeon.player.getPlayerData().getToken()))
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
                new URI(Credentials.ROGUERA_URL
                        + Credentials.GAME_SESSIONS+"/update"
                        + Credentials.USER_ID
                        + Dungeon.player.getPlayerData().getPlayerID()
                        + "&"
                        + Credentials.USER_TOKEN
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
                        new URI(Credentials.ROGUERA_URL
                                + Credentials.GAME_SESSIONS+"/finalize"
                                + Credentials.GAME_SESSIONS_ID
                                + GameLoop.gameSessionId))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(putRequest, HttpResponse.BodyHandlers.ofString());

       // Debug.toLog("[HTTP_PUT][GAME_SESSION] Finalized Result: "+response.body());
    }

    public static boolean checkNickName(String nickName) throws URISyntaxException, IOException, InterruptedException {
        getRequest = HttpRequest.newBuilder().uri(
                new URI(Credentials.ROGUERA_URL
                        +Credentials.USERS
                        +"/checkNameForValid"
                        +Credentials.USER_NICKNAME
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
        getRequest = HttpRequest.newBuilder().uri(new URI(Credentials.ROGUERA_URL)).GET().build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(getRequest, HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 200;
    }
}
