package org.leveranstjanst.client;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceController {
    private static final String HASHING_ALGORITHM = "SHA-256";
    private static final String USER_DATA_TABLE = "userdata";

    public static boolean userLogIn(String userName, String userPassword) {
        String hashedPassword = hashPassword(userPassword);
        return checkUserCredentials(userName, hashedPassword);
    }

    public static void registerUser(String userName, String userPass, String otherData) {
        String hashedPassword = hashPassword(userPass);
        addUserToDatabase(userName, hashedPassword, otherData);
    }

    public static String showData(String userName) {
        return getUserDataFromDatabase(userName);
    }

    public static void deleteData(String userName, String userPass) {
        String hashedPassword = hashPassword(userPass);
        boolean success = deleteUserFromDatabase(userName, hashedPassword);
        if (!success) {
            System.err.println("Failed to delete user: " + userName);
        } else {
            System.out.println("User deleted successfully: " + userName);
        }
    }

    private static boolean deleteUserFromDatabase(String userName, String hashedPassword) {
        String sql = "DELETE FROM " + USER_DATA_TABLE + " WHERE username = ?";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userName);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Deleted " + rowsAffected + " row(s)");
                return true;
            } else {
                System.out.println("No matching record found");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Exception during deletion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASHING_ALGORITHM);
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }

    private static boolean checkUserCredentials(String userName, String hashedPassword) {
        String sql = "SELECT * FROM " + USER_DATA_TABLE + " WHERE username = ?";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedPasswordHash = rs.getString("userpass");
                if (hashedPassword.equals(storedPasswordHash)) {
                    System.out.println("User logged in successfully.");
                    return true;
                } else {
                    System.out.println("Incorrect password");
                    return false;
                }
            } else {
                System.out.println("No user found. Please register.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Login failed: " + e.getMessage());
            return false;
        }
    }

    private static void addUserToDatabase(String userName, String hashedPassword, String otherData) {
        String sql = "INSERT INTO " + USER_DATA_TABLE + " (username, userpass, otherdata1) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userName);
            ps.setString(2, hashedPassword);
            ps.setString(3, otherData);
            ps.executeUpdate();
            System.out.println("Update successful");
        } catch (SQLException e) {
            System.err.println("Exception during registration: " + e.getMessage());
        }
    }

    private static String getUserDataFromDatabase(String userName) {
        String sql = "SELECT * FROM " + USER_DATA_TABLE + " WHERE username = ?";
        String otherData = "";
        String dateCreated = "";

        try (Connection conn = Database.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                otherData = "Other data: " + rs.getString("otherdata1") + " Date created: " + rs.getDate("datecreated");
            }
        } catch (SQLException e) {
            System.err.println("Exception during data retrieval: " + e.getMessage());
        }
        return otherData;
    }

}
