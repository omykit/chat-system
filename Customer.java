package com.chat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;

/**
 * Customer model — now stores an id and creation time and can persist a record to data/customers.txt
 */
public class Customer {
    private final String id;
    private final String name;
    private final Instant createdAt;

    public Customer(String name) {
        this(UUID.randomUUID().toString(), name, Instant.now());
    }

    public Customer(String id, String name, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt == null ? Instant.now() : createdAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Customer{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", createdAt=" + createdAt + '}';
    }

    /**
     * Save this customer to data/customers.txt (appends a line: id|name|createdAt)
     */
    public void save() {
        try {
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) Files.createDirectories(dataDir);
            File file = dataDir.resolve("customers.txt").toFile();
            try (BufferedWriter w = new BufferedWriter(new FileWriter(file, true))) {
                String line = String.format("%s|%s|%s", id, escape(name), createdAt.toString());
                w.write(line);
                w.newLine();
                w.flush();
            }
        } catch (IOException e) {
            // best effort — print a message but do not throw to avoid breaking chat flow
            System.err.println("Failed to save customer data: " + e.getMessage());
        }
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("|", "\\|");
    }

    /**
     * Convenience: create and persist a customer by name
     */
    public static Customer createAndSave(String name) {
        Customer c = new Customer(name);
        c.save();
        return c;
    }
}

