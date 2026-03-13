package model;

import java.sql.*;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private static final String DB_URL = "jdbc:sqlite:autoservice.db";
    private static Connection connection;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            logger.severe(e.getMessage());
        }

        initializeDatabase();
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                logger.info("База данных подключена: " + DB_URL);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка подключения к БД: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
        return connection;
    }

    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Таблица пользователей
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL CHECK(role IN ('CLIENT', 'MECHANIC')),
                    email TEXT UNIQUE NOT NULL,
                    phone TEXT UNIQUE NOT NULL,
                    full_name TEXT NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Таблица автомобилей
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS cars (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    brand TEXT NOT NULL,
                    model TEXT NOT NULL,
                    year INTEGER NOT NULL,
                    vin TEXT UNIQUE NOT NULL,
                    license_plate TEXT UNIQUE NOT NULL,
                    owner_id INTEGER NOT NULL,
                    registration_date TEXT,
                    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """);

            // Таблица ремонтов
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS repairs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    car_id INTEGER NOT NULL,
                    description TEXT NOT NULL,
                    status TEXT DEFAULT 'DIAGNOSTICS' CHECK(status IN ('DIAGNOSTICS', 'IN_REPAIR', 'COMPLETED', 'CANCELLED')),
                    cost REAL DEFAULT 0.0,
                    start_date TEXT,
                    end_date TEXT,
                    mechanic_id INTEGER NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE,
                    FOREIGN KEY (mechanic_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """);

            //таблица уведомлений
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS notifications (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    message TEXT NOT NULL,
                    is_read BOOLEAN DEFAULT FALSE,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """);

            logger.info("Таблицы созданы успешно");

        } catch (SQLException e) {
            logger.severe("Ошибка инициализации БД: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Соединение с БД закрыто");
            }
        } catch (SQLException e) {
            logger.severe("Ошибка закрытия соединения: " + e.getMessage());
        }
    }
}