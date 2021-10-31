package com.rogurea.net;

import com.mysql.cj.jdbc.Driver;
import com.rogurea.base.Debug;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Callable;

import static com.rogurea.net.Credentials.*;

public class ConnectingWorker implements Callable<Optional<Connection>> {

    private String DecodePassword;

    @Override
    public Optional<Connection> call() throws Exception {
        decode();
        try {
            decode();
            Debug.toLog("[JDBC] Try to connect " + URL);
            Driver driver = new Driver();

            Properties pr = new Properties();
            pr.put("user", USER);
            pr.put("password", DecodePassword);

            Optional<Connection> connection = Optional.ofNullable(driver.connect(URL, pr));

            Debug.toLog("[JDBC] Connection successful");

            return connection;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }
    private void decode(){
        DecodePassword = new String(Base64.getDecoder().decode(PASSWORD));
    }
}
