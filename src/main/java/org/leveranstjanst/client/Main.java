package org.leveranstjanst.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.leveranstjanst.client.ServiceController.*;

public class Main {

    //to-do method that handles inputs as forms instead of copying code
    private static List<Object> askForInputs() {
        Scanner sr = new Scanner(System.in);
        System.out.println("Input username: ");
        Object userName = sr.nextLine();
        System.out.println("Input password: ");
        Object userPass = sr.nextLine();
        sr.close();
        return new ArrayList<>(List.of(userName, userPass));
    }
    public static void main(String[] args) {
        try {
            System.out.println("Hello and welcome!");
            boolean isLoggedIn = false;
            String userInput;
            String userName = "";
            String userPassword;
            String otherData;
            Scanner sr = new Scanner(System.in);

            do {
                System.out.println("[1] Log in");
                System.out.println("[2] Register");
                System.out.println("[3] Display");
                System.out.println("[4] Delete");
                System.out.println("[5] Log out");
                System.out.println("[Q] Exit");
                userInput = sr.nextLine();

    switch (userInput) {
        case "1":
            System.out.println("Input username: ");
            userName = sr.nextLine();
            System.out.println("Input password: ");
            userPassword = sr.nextLine();
            if (!isLoggedIn && userLogIn(userName, userPassword)) {
                    isLoggedIn = true;
                } else {
                System.out.println("You are already logged in!");
            }
            break;
        case "2":
            //register
            System.out.println("Input username: ");
            userName = sr.nextLine();
            System.out.println("Input password: ");
            userPassword = sr.nextLine();
            System.out.println("Input something else: ");
            otherData = sr.nextLine();
            //do request
            ServiceController.registerUser(userName, userPassword, otherData);
            break;
        case "3":
            //display other data
            if (isLoggedIn) {
                System.out.println(ServiceController.showData(userName));
            } else {
                System.out.println("You need to log in first!");
            }
            break;
        case "4":
            //delete
            System.out.println("Input username: ");
            userName = sr.nextLine();
            System.out.println("Input password: ");
            userPassword = sr.nextLine();
            ServiceController.deleteData(userName, userPassword);
            break;
        case "5":
            //log out
            isLoggedIn = false;
            userName = "";
            userPassword = "";
            System.out.println("You are now logged out!");
            break;
    }
            } while (!userInput.equalsIgnoreCase("q"));
        } catch (Exception e) {
            System.out.print("An error occurred: " + e);
        }
    }
}