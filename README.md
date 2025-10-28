
# Java Chat System

Interactive console-based Java chat system (demo project).

Short description
-----------------
Interactive Java console chat system with a simple file-backed OrderDatabase. Agent threads compete to accept a customer's chat then handle order-related requests (create order, show order, check status).

Features
--------
- Agent threads race to accept a customer chat (multithreading + synchronization).
- Interactive agent-customer conversation via console (Scanner-based I/O).
- Simple file-backed order database: orders stored in `data/orders.txt` as `id|status|details`.
- Demo flows: create order (select items), show order (#ID), check order status.

Requirements
------------
- Java JDK (tested with Oracle JDK 25 in this workspace). Set `JAVA_HOME` to your JDK and add its `bin` to PATH.

Quick start (Windows PowerShell)
-------------------------------
1. Compile

```powershell
New-Item -ItemType Directory -Path out -Force
$env:JAVA_HOME = 'C:\Path\To\jdk'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$files = Get-ChildItem -Path .\src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $files
```

2. Run

```powershell
java -cp out com.chat.Main
```

Interaction examples
--------------------
- Create an order
   - Type: `create order`
   - Select items by number (comma separated), e.g. `1,2`
   - Confirm with `yes` → agent replies with generated order id `#123456`.

- Show an order
   - Type the order id (e.g. `#123456`) or `show order #123456` → agent displays status and items.

- Check order status
   - Type `check order status` and when prompted provide `#123456` → agent displays status.

Data format
-----------
Orders are stored in `data/orders.txt` as plain lines in the format:

```
<id>|<status>|<details>
```

Where `details` may contain `items:Item1,Item2;by:CustomerName` for created orders.

Project layout
--------------
- `src/com/chat/` — Java source files
- `README.md` — this file
- `.gitignore` — ignored files/patterns

Notes & next steps
------------------
- This is a demo project. Recommended improvements:
   - Replace the file DB with SQLite (JDBC) for concurrent access and queries.
   - Add quantities, prices and order totals in the create-order flow.
   - Add unit tests and a CI workflow (GitHub Actions).

License
-------
This project is licensed under the MIT License — see the `LICENSE` file for details.

Contributing
------------
Feel free to open issues or submit pull requests. For major changes (migrating DB, refactoring IO) please open an issue first to discuss the design.

