# 📖 Telegram Polish–Ukrainian Quiz Bot

A Telegram bot that helps you learn Polish and Ukrainian languages through short quizzes.

Implemented in Java + Maven, using:
* **TelegramBots** (long polling API)
* **Hibernate** (for database interaction)
* **PostgreSQL** (or any other SQL database)

## Functionality ⚙️

* `/start` - greeting
* `/quizua <count>` - a quiz from Ukrainian → Polish
* `/quizpl <count>` - a quiz from Polish → Ukrainian
* `/help` - shows available commands

After each answer, the bot indicates if your answer was correct and displays your final score along with a sticker. Stickers vary depending on your result.

## Project Structure 📂

```
src/main/java
├── functionality
│    ├── Bot.java          # Main bot logic
│    ├── BotCommands.java  # Commands and helper methods
│    └── DictionaryData.java # Get data from DB
├── model
│    └── BotDictionary.java # Word model
├── utility
│    ├── HibernateUtil.java # Hibernate session utility
│    └── TokenEncryption.java # Handles bot token reading
└── App.java               # Entry point
```

## Telegram Token 🔑
The bot expects the token either in: 
* A secret.txt file (encoded in Base64) **or**
* As an environment variable `TELEGRAM_BOT_TOKEN`

> ⚠️ **Recommended:** use environment variables for security, especially in production.

## Database 🗄️

By default, it uses Hibernate with the `hibernate.cfg.xml` config.
You can override connection settings via environment variables.
You need to create a `dictionary` table with the following fields:

```sql
CREATE TABLE dictionary (
    id BIGINT PRIMARY KEY,
    polishWord VARCHAR(255) NOT NULL,
    ukrainianWord VARCHAR(255) NOT NULL
);
```

## Possible Improvements 💡

* Add support for more languages.
* Improve UI – use inline keyboards or buttons for answers.
* Dockerization – package the bot and PostgreSQL in Docker for easy deployment.
* Environment variable management – use a `.env` file or a secret manager for better security.
* Webhook support – deploy via webhook in production instead of long polling.
* Separate bot logic into dedicated classes/services for better maintainability.
* Persist quiz state in the database for scalability and to survive restarts.
* Improve exception handling and error reporting.
* Optimize database queries – fetch random rows directly from the database for large dictionaries instead of loading all words into memory.

## Prerequisites

- **Java 17+** installed and added to your `PATH`
- **Maven** installed and added to your `PATH`
- **PostgreSQL** (or another SQL database) running locally
- A Telegram bot token obtained from [BotFather](https://t.me/BotFather)

## How to Run Locally
1. Clone the repository
```bash
git clone <repo-url>
cd TelegramPolishBot
```
2. Configure env. variables or create `secret.txt` in project folder.
- Windows
```powershell
$Env:TELEGRAM_BOT_TOKEN="YOUR_BOT_TOKEN_HERE"
```
- Linux/macOS
```bash
export TELEGRAM_BOT_TOKEN="YOUR_BOT_TOKEN_HERE"
```
3. Configure the Database

   3.1. Create a database in PostgreSQL (e.g., `telegram_bot_db`).  

   3.2. Create the `dictionary` table:

   3.3. Update your `hibernate.cfg.xml` if needed for:

      - Database URL
      - Username
      - Password
      - Dialect (`org.hibernate.dialect.PostgreSQLDialect`)


      ```xml
      <?xml version="1.0" encoding="UTF-8"?>
      <!DOCTYPE hibernate-configuration PUBLIC
              "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
              "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd">
  
      <hibernate-configuration>
          <session-factory>
              <!-- DB connection -->
              <property name="hibernate.connection.driver_class">org.postgresql.Driver</property>
              <property name="hibernate.connection.url">jdbc:postgresql://<host>:<port>/<database></property>
              <property name="hibernate.connection.username"><user></property>
              <property name="hibernate.connection.password"><password></property>
  
              <!-- Hibernate settings -->
              <property name="hibernate.dialect">org.hibernate.dialect.PostgreSQLDialect</property>
              <property name="hibernate.show_sql">true</property>
              <property name="hibernate.format_sql">true</property>
              <property name="hibernate.hbm2ddl.auto">update</property>
  
          </session-factory>
      </hibernate-configuration>
      ```
4. Build the project
   ```bash
   mvn clean package
   ```
6. Run the bot
   ```bash
   java -jar target/<your_jar>
   ```
   - `<your_jar>` should include the `.jar` extension. 
8. Start quizzing!
