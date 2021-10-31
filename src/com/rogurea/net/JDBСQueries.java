package com.rogurea.net;


import com.rogurea.GameLoop;
import com.rogurea.base.Debug;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.view.ViewObjects;

import java.sql.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class JDBСQueries {

    private static Connection JDBConnect;

    private static final String INSERT_NEW_GS_QUERY = "insert into game_session (id_session, is_session_ended, id_user)\n" +
            "values (?,?,?);";

    private static final String INSERT_NEW_USER_QUERY = "insert into user (first_start_playing, nickname)\n" +
            "values (?,?);";

    private static final String UPDATE_USER = "update user " +
            "set current_score = ? " +
            "where id = ?";

    private static final String UPDATE_GS = "update game_session " +
            "set kills = ?," +
            "last_room = ?," +
            "last_floor = ?," +
            "items = ?," +
            "score_earned = ?," +
            "money_earned = ?, " +
            "play_time = ? "+
            "where id_user = ? AND is_session_ended is false ";

    private static final String END_GS = "update game_session "+
            "set is_session_ended = true " +
            "where id_user = ? AND is_session_ended is false ";

    private static final String UPD_USER_SCORE_HASH = "update user " +
            " set current_score_hash = ? " +
            "where id = ?";


    private static final String GET_USER_NICKNAME = "select nickname " +
            "from user " +
            "where nickname = ? ";

    public static void setConnection(Connection connection){
        JDBConnect = connection;
    }

    public static boolean checkConnect(){
        try {
            if(JDBConnect != null) {
                return JDBConnect.isValid(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean isNickNameIsAlreadyUsed(String nickname){
        try{
            PreparedStatement getUserNick = JDBConnect.prepareStatement(GET_USER_NICKNAME);

            String nick = ViewObjects.getTrimString(nickname);

            getUserNick.setString(1,nick);
            ResultSet rs = getUserNick.executeQuery();

            if(rs.next()) {
                String inputNick = rs.getString(1);
                if (inputNick != null) {
                    if (inputNick.hashCode() == nick.hashCode()) {
                        Debug.toLog("[JDBC] nickname is found " + inputNick);

                        return true;
                    }
                }
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public static void createNewUser(){
        try {
            PreparedStatement createNewUser = JDBConnect.prepareStatement(INSERT_NEW_USER_QUERY);

            String playerNameTrim = ViewObjects.getTrimString(Dungeon.player.getPlayerData().getPlayerName());

            createNewUser.setDate(1, Date.valueOf(GameLoop.startPlayTime.toLocalDate()));
            createNewUser.setString(2,playerNameTrim);

            createNewUser.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static int getNewUserId(){
        String SELECT_USER_ID = "select id " +
                "from user " +
                "where nickname = ?";
        try {
            PreparedStatement getUserID = JDBConnect.prepareStatement(SELECT_USER_ID);

            getUserID.setString(1, ViewObjects.getTrimString(Dungeon.player.getPlayerData().getPlayerName()));

            ResultSet rs = getUserID.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            } else {
                return 0;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    public static void updUserHash(String hash){
        try {
            PreparedStatement updHash = JDBConnect.prepareStatement(UPD_USER_SCORE_HASH);
            updHash.setString(1, hash);
            updHash.setInt(2, Dungeon.player.getPlayerData().getPlayerID());
            updHash.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void createGameSession(){
        try{
            PreparedStatement createGSStatement = JDBConnect.prepareStatement(INSERT_NEW_GS_QUERY);

            int session_id = ThreadLocalRandom.current().nextInt(1000,9999);

            createGSStatement.setInt(1, session_id);
            createGSStatement.setBoolean(2, false);
            createGSStatement.setInt(3, Dungeon.player.getPlayerData().getPlayerID());

            createGSStatement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void endGameSession(){
        try{
            PreparedStatement endGSStatement = JDBConnect.prepareStatement(END_GS);

            endGSStatement.setInt(1, Dungeon.player.getPlayerData().getPlayerID());

            endGSStatement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    static Semaphore threadLimit = new Semaphore(1,true);

    public static void updateGameSession(){

        try {
            threadLimit.acquire();
            PreparedStatement updGSStatement = JDBConnect.prepareStatement(UPDATE_GS);
            updGSStatement.setInt(1,Dungeon.player.getPlayerData().getKills());
            updGSStatement.setInt(2,Dungeon.player.getCurrentRoom());
            updGSStatement.setInt(3,Dungeon.getCurrentFloor().getFloorNumber());
            updGSStatement.setInt(4,Dungeon.player.Inventory.size());
            updGSStatement.setInt(5,Dungeon.player.getPlayerData().getScore());
            updGSStatement.setInt(6,Dungeon.player.getPlayerData().getMoney());
            updGSStatement.setInt(7, GameLoop.playTime);
            updGSStatement.setInt(8,Dungeon.player.getPlayerData().getPlayerID());

            updGSStatement.execute();
            threadLimit.release();
        } catch (SQLException | InterruptedException e){
            e.printStackTrace();
        }
    }


    public static void updateUserScore(){
        try {
            PreparedStatement updUserScore = JDBConnect.prepareStatement(UPDATE_USER);
            updUserScore.setInt(1, Dungeon.player.getPlayerData().getScore());
            updUserScore.setInt(2, Dungeon.player.getPlayerData().getPlayerID());

            updUserScore.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static int getUserScoreHash(){
        String SELECT_SCORE_HASH = "select current_score_hash " +
                "from user " +
                "where id = ?";
        try{
            PreparedStatement getUserScoreHash = JDBConnect.prepareStatement(SELECT_SCORE_HASH);

            getUserScoreHash.setInt(1, Dungeon.player.getPlayerData().getPlayerID());

            ResultSet rs = getUserScoreHash.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

}
