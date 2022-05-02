package com.rogurea.net;

import com.rogurea.base.Debug;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;
import java.util.concurrent.Semaphore;

public class ServerRequests {
    static HttpRequest getRequest;
    static HttpRequest postRequest;
    static HttpRequest putRequest;

    private static String ROGUERA_URL;
    private static String USERS;

    private static byte[] secretKey;

    static {
        Properties databaseProps = new Properties();
        try {
            databaseProps.load(ServerRequests.class.getResource("/database.properties").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ROGUERA_URL= databaseProps.getProperty("ROGUERA_URL");
        USERS = databaseProps.getProperty("USERS");
    }


    public static byte[] getSecretKey(){
        return secretKey;
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

        //String tokenID = Dungeon.player.getPlayerData().getToken();

        PlayerDTO playerDTO = new PlayerDTO();

        playerDTO.setNickName(nickName);

        postRequest = HttpRequest.newBuilder()
                .uri(new URI(ROGUERA_URL + "/new"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(playerDTO.toString()))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(postRequest, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public static byte[] createGameSession(SessionData sessionData) throws URISyntaxException, IOException, InterruptedException{

        Debug.toLog("Send session data = "+sessionData.toString());

        postRequest = HttpRequest.newBuilder()
                .uri(new URI(ROGUERA_URL+"/new"))
                .headers("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(sessionData.toString()))
                .build();
        HttpResponse<byte[]> response = HttpClient.newHttpClient()
                .send(postRequest, HttpResponse.BodyHandlers.ofByteArray());
        Debug.toLog("[HTTP_POST][GAME_SESSION]Create Result: "+ Arrays.toString(response.body()));

        return response.body();
    }

    static Semaphore sm = new Semaphore(1,true);

    public static void updateGameSession(SessionData sessionData) throws URISyntaxException, IOException, InterruptedException {
        sm.acquire();

        putRequest = HttpRequest.newBuilder().uri(
                new URI(ROGUERA_URL+"/upd"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(sessionData.toString()))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(putRequest, HttpResponse.BodyHandlers.ofString());

       Debug.toLog("[HTTP_PUT][GAME_SESSION] Update Result: "+response.body());

       sm.release();
    }

    public static void finalizeGameSession(SessionData sessionData) throws URISyntaxException, IOException, InterruptedException {
        putRequest = HttpRequest.newBuilder().uri(
                        new URI(ROGUERA_URL+"/end"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(sessionData.toString()))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(putRequest, HttpResponse.BodyHandlers.ofString());

       Debug.toLog("[HTTP_PUT][GAME_SESSION] Finalized Result: "+response.body());
    }

    public static boolean isNickNameAvailable(String nickName) throws URISyntaxException, IOException, InterruptedException {
        getRequest = HttpRequest.newBuilder().uri(
                new URI(ROGUERA_URL
                        +USERS
                        +nickName))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(getRequest, HttpResponse.BodyHandlers.ofString());

        String result = response.body();
        Debug.toLog("[HTTP_GET][CHECK_NICKNAME] Result for "+ nickName+" is "+result);
        return !result.equals("exists");

    }

    public static boolean getConnection() throws URISyntaxException, IOException, InterruptedException {
        getRequest = HttpRequest.newBuilder().uri(new URI(ROGUERA_URL+"/ping")).GET().build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(getRequest, HttpResponse.BodyHandlers.ofString());

        secretKey = Base64.getDecoder().decode(response.body());

        return response.statusCode() == 200;
    }

    public static boolean authenticationPlayer(PlayerDTO playerDTO) throws URISyntaxException, IOException, InterruptedException {
        postRequest = HttpRequest
                .newBuilder()
                .uri(new URI(ROGUERA_URL+"/auth"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(playerDTO.toString()))
                .build();

        HttpResponse<String> response = HttpClient
                .newHttpClient()
                .send(postRequest, HttpResponse.BodyHandlers.ofString());

        return response.body().equals("true");
    }
}
