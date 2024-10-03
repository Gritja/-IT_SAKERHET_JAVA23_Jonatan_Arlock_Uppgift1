package org.leveranstjanst.client;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    static String url = System.getenv("DB_HOST");
    static String port = System.getenv("DB_PORT");
    static String database = System.getenv("DB_NAME");
    static String userName = System.getenv("DB_USER");
    static String password = System.getenv("DB_PASSWORD");
    private static Database db;
    private MysqlDataSource dataSource;
    private Database() {
        initializeDataSource();
    }
    public static Connection getConnection() {
        if (db == null) {
            db = new Database();
            db.initializeDataSource();
        }
        return db.createConnection();
    }
    private void initializeDataSource() {
        dataSource = new MysqlDataSource();
        dataSource.setUser(userName);
        dataSource.setPassword(password);
        dataSource.setURL("jdbc:mysql://" + url + ":" + port + "/" + database + "?serverTimezone=UTC");
    }
    private Connection createConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException ex) {
            PrintSQLException(ex);
            return null;
        }
    }
    public static void PrintSQLException(SQLException sqle) {
        PrintSQLException(sqle, false);
    }
    public static void PrintSQLException(SQLException sqle, boolean printStackTrace) {
        while (sqle != null) {
            System.out.println("SQL exception!\nSQLState: " + sqle.getSQLState() +  "\nErrorcode: " + sqle.getErrorCode() + "\nMessage: " + sqle.getMessage());
            if (printStackTrace) sqle.printStackTrace();
            sqle = sqle.getNextException();
        }
    }
}
