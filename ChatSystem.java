package com.chat;

// Scanner used via fully-qualified reference to avoid accidental imports clash

public class ChatSystem {
    private boolean chatAccepted = false;
    private Customer currentCustomer;
    private final OrderDatabase db;
    private final java.util.Scanner scanner;

    public ChatSystem(OrderDatabase db, java.util.Scanner scanner) {
        this.db = db;
        this.scanner = scanner;
    }

    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        this.chatAccepted = false;
    }

    public synchronized boolean acceptChat(Agent agent) {
        if (!chatAccepted) {
            chatAccepted = true;
            System.out.println(agent.getName() + " is connecting with the customer...");
            return true;
        }
        return false;
    }
    public void startChat(Agent agent) {
        Customer customer = this.currentCustomer;
        System.out.println(agent.getName() + ": Hello " + customer.getName() + ", I'm " + agent.getName() + ". How can I help you?\n");

        // Sample catalog
        String[] catalog = new String[] {"Widget", "Gizmo", "Gadget", "Thingamajig"};

        while (true) {
            System.out.print(customer.getName() + ": ");
            String message = null;
            try {
                message = this.scanner.nextLine();
            } catch (Exception e) {
                break;
            }
            if (message == null) break;

            String lower = message.trim().toLowerCase();
            if (lower.equals("exit") || lower.equals("bye")) {
                System.out.println(agent.getName() + ": Goodbye — chat ended.");
                break;
            }

            // SHOW order by id: "show order #12345" or just send "#12345"
            if (message.matches(".*#\\d+.*")) {
                String id = message.replaceAll(".*#(\\d+).*", "$1");
                OrderDatabase.Order ord = db.getOrder(id);
                if (ord == null) {
                    System.out.println(agent.getName() + ": I couldn't find order #" + id + ". Would you like me to create it? (yes/no)");
                    System.out.print(customer.getName() + ": ");
                    String resp = this.scanner.nextLine();
                    if (resp.equalsIgnoreCase("yes")) {
                        // create empty order placeholder
                        db.createOrder(id, "created", "Placed by " + customer.getName());
                        System.out.println(agent.getName() + ": I've created order #" + id + ". Status: created.");
                    } else {
                        System.out.println(agent.getName() + ": Okay, I won't create it.");
                    }
                } else {
                    System.out.println(agent.getName() + ": Order #" + id + " -> status: " + ord.getStatus());
                    // show items if present
                    String details = ord.getDetails();
                    if (details != null && details.contains("items:")) {
                        String itemsPart = details.replaceFirst(".*items:", "").replaceFirst(";by:.*", "");
                        System.out.println(agent.getName() + ": Items -> " + itemsPart);
                    } else {
                        System.out.println(agent.getName() + ": Details -> " + ord.getDetails());
                    }
                }

                continue;
            }

            // Create new order flow
            if (lower.contains("create") && lower.contains("order")) {
                System.out.println(agent.getName() + ": Sure — I can create a new order for you. Here are available items:");
                for (int i = 0; i < catalog.length; i++) {
                    System.out.println("  " + (i + 1) + ") " + catalog[i]);
                }
                System.out.println(agent.getName() + ": Please enter the numbers of items to add (comma separated), or type 'cancel'.");
                System.out.print(customer.getName() + ": ");
                String sel = this.scanner.nextLine();
                if (sel == null) break;
                if (sel.trim().equalsIgnoreCase("cancel")) {
                    System.out.println(agent.getName() + ": Okay, cancelled order creation.");
                    continue;
                }
                String[] parts = sel.split("[,\\s]+");
                java.util.List<String> chosen = new java.util.ArrayList<>();
                for (String p : parts) {
                    try {
                        int idx = Integer.parseInt(p.trim()) - 1;
                        if (idx >= 0 && idx < catalog.length) chosen.add(catalog[idx]);
                    } catch (NumberFormatException nfe) {
                        // ignore
                    }
                }
                if (chosen.isEmpty()) {
                    System.out.println(agent.getName() + ": No valid items selected. Please try 'create order' again.");
                    continue;
                }

                System.out.println(agent.getName() + ": You selected: " + String.join(", ", chosen));
                System.out.println(agent.getName() + ": Confirm order? (yes/no)");
                System.out.print(customer.getName() + ": ");
                String conf = this.scanner.nextLine();
                if (conf.equalsIgnoreCase("yes") || conf.equalsIgnoreCase("y")) {
                    String newId = db.generateOrderId();
                    db.createOrderWithItems(newId, "submitted", chosen, customer.getName());
                    System.out.println(agent.getName() + ": Order submitted. Thank you for ordering! Your order id is #" + newId);
                } else {
                    System.out.println(agent.getName() + ": Order not submitted.");
                }

                continue;
            }

            // Check order status without id -> ask for id
            if (lower.contains("check") && lower.contains("status") && !lower.matches(".*#\\d+.*")) {
                System.out.println(agent.getName() + ": Sure — please provide your order id (e.g. #12345).");
                System.out.print(customer.getName() + ": ");
                String idmsg = this.scanner.nextLine();
                if (idmsg == null) break;
                if (idmsg.matches(".*#\\d+.*")) {
                    String id = idmsg.replaceAll(".*#(\\d+).*", "$1");
                    OrderDatabase.Order ord = db.getOrder(id);
                    if (ord == null) {
                        System.out.println(agent.getName() + ": I couldn't find order #" + id + ".");
                    } else {
                        System.out.println(agent.getName() + ": Order #" + id + " status: " + ord.getStatus());
                    }
                } else {
                    System.out.println(agent.getName() + ": That doesn't look like an order id. Try again with a '#'.");
                }
                continue;
            }

            // Generic help
            System.out.println(agent.getName() + ": I can help with orders. Try 'create order', 'show order #12345', or 'check order status'. Say 'bye' to exit.");
        }

        // do not close System.in scanner here — main / environment may reuse it
    }
}

