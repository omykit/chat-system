package com.chat;

public class Agent implements Runnable {
    private String name;
    private ChatSystem chatSystem;

    public Agent(String name, ChatSystem chatSystem) {
        this.name = name;
        this.chatSystem = chatSystem;
    }

    @Override
    public void run() {
        try {
            Thread.sleep((int) (Math.random() * 2000)); // simulate random response time
            if (chatSystem.acceptChat(this)) {
                System.out.println(name + " accepted the chat!");
                chatSystem.startChat(this);
            } else {
                System.out.println(name + " missed the chat.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }
}

