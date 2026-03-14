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
            logger.info("SQLite JDBC Driver loaded successfully");
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

            // Таблица пользователей с linked_user_id
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    full_name TEXT NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL CHECK(role IN ('CLIENT', 'MECHANIC', 'ADMIN')),
                    email TEXT UNIQUE NOT NULL,
                    phone TEXT UNIQUE NOT NULL,
                    linked_user_id INTEGER,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (linked_user_id) REFERENCES users(id)
                )
            """);

            // Таблица марок автомобилей
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS car_brands (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE NOT NULL,
                    created_by INTEGER,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (created_by) REFERENCES users(id)
                )
            """);

            // Таблица моделей автомобилей
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS car_models (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    brand_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    created_by INTEGER,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (brand_id) REFERENCES car_brands(id) ON DELETE CASCADE,
                    FOREIGN KEY (created_by) REFERENCES users(id),
                    UNIQUE(brand_id, name)
                )
            """);

            // Таблица автомобилей (обновленная)
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

            // Таблица уведомлений
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

            // Создаем администратора по умолчанию (если нет ни одного)
            createDefaultAdmin(conn);

            logger.info("Таблицы созданы успешно");

        } catch (SQLException e) {
            logger.severe("Ошибка инициализации БД: " + e.getMessage());
        }
    }

    private static void createDefaultAdmin(Connection conn) {
        String checkSql = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO users (full_name, password, role, email, phone) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, "Главный администратор");
                    pstmt.setString(2, "admin123"); // Пароль по умолчанию
                    pstmt.setString(3, "ADMIN");
                    pstmt.setString(4, "admin@autoservice.ru");
                    pstmt.setString(5, "+70000000000");
                    pstmt.executeUpdate();
                    logger.info("Создан администратор по умолчанию");
                }
            }
        } catch (SQLException e) {
            logger.severe("Ошибка создания администратора: " + e.getMessage());
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