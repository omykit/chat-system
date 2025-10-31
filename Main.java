package com.chat;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        try {
            // ensure data folder
            new File("data").mkdirs();
            OrderDatabase db = new OrderDatabase("data/orders.txt");
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            try {
                ChatSystem chatSystem = new ChatSystem(db, scanner);

                System.out.print("Enter your name: ");
                String name = scanner.nextLine();
                Customer customer = new Customer(name);
                chatSystem.setCustomer(customer);

            System.out.println("\nCustomer raised a chat request...");
            System.out.println("Broadcasting request to all agents...\n");

            Thread agent1 = new Thread(new Agent("Agent A", chatSystem));
            Thread agent2 = new Thread(new Agent("Agent B", chatSystem));
            Thread agent3 = new Thread(new Agent("Agent C", chatSystem));

                agent1.start();
                agent2.start();
                agent3.start();

                // wait for the agents to finish; the one that accepts will run the interactive chat
                agent1.join();
                agent2.join();
                agent3.join();

                System.out.println("All agents finished. Bye.");
            } finally {
                // close shared scanner only after chat threads finished
                scanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
