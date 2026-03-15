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

            // Таблица пользователей (без роли)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    full_name TEXT NOT NULL,
                    password TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    phone TEXT UNIQUE NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Таблица ролей
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS roles (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE NOT NULL
                )
            """);

            // Таблица связей пользователь-роль
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS user_roles (
                    user_id INTEGER NOT NULL,
                    role_id INTEGER NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
                    PRIMARY KEY (user_id, role_id)
                )
            """);

            // Добавляем роли по умолчанию
            stmt.execute("INSERT OR IGNORE INTO roles (name) VALUES ('CLIENT')");
            stmt.execute("INSERT OR IGNORE INTO roles (name) VALUES ('MECHANIC')");
            stmt.execute("INSERT OR IGNORE INTO roles (name) VALUES ('ADMIN')");

            // Остальные таблицы без изменений
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS car_brands (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE NOT NULL,
                    created_by INTEGER,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (created_by) REFERENCES users(id)
                )
            """);

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

            createDefaultAdmin(conn);
            logger.info("Таблицы созданы успешно");

        } catch (SQLException e) {
            logger.severe("Ошибка инициализации БД: " + e.getMessage());
        }
    }

    private static void createDefaultAdmin(Connection conn) {
        String checkSql = "SELECT COUNT(*) FROM users u " +
                "JOIN user_roles ur ON u.id = ur.user_id " +
                "JOIN roles r ON ur.role_id = r.id " +
                "WHERE r.name = 'ADMIN'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                String insertUserSql = "INSERT INTO users (full_name, password, email, phone) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, "Главный администратор");
                    pstmt.setString(2, "admin123");
                    pstmt.setString(3, "admin@autoservice.ru");
                    pstmt.setString(4, "+70000000000");
                    pstmt.executeUpdate();

                    ResultSet rs2 = pstmt.getGeneratedKeys();
                    if (rs2.next()) {
                        int userId = rs2.getInt(1);
                        String insertRoleSql = "INSERT INTO user_roles (user_id, role_id) " +
                                "SELECT ?, id FROM roles WHERE name = 'ADMIN'";
                        try (PreparedStatement pstmt2 = conn.prepareStatement(insertRoleSql)) {
                            pstmt2.setInt(1, userId);
                            pstmt2.executeUpdate();
                        }
                    }
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