package dao.impl;

import dao.interfaces.IUserDAO;
import model.User;
import model.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserDAO implements IUserDAO {
    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    @Override
    public boolean createUser(User user) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Вставляем пользователя (БЕЗ RETURN_GENERATED_KEYS)
            String userSql = "INSERT INTO users (full_name, password, email, phone) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(userSql);
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPhone());
            pstmt.executeUpdate();

            // Получаем ID последней вставленной записи через отдельный запрос
            String idSql = "SELECT last_insert_rowid() as id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(idSql);

            int userId;
            if (rs.next()) {
                userId = rs.getInt("id");
                user.setId(userId);
            } else {
                conn.rollback();
                return false;
            }

            // Добавляем роли
            String roleSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, (SELECT id FROM roles WHERE name = ?))";
            pstmt = conn.prepareStatement(roleSql);
            for (String role : user.getRoles()) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, role);
                pstmt.addBatch();
            }
            pstmt.executeBatch();

            conn.commit();
            logger.info("Пользователь создан с ID: " + userId);
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.severe("Ошибка отката: " + ex.getMessage());
                }
            }
            logger.severe("Ошибка создания пользователя: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    logger.severe("Ошибка сброса autoCommit: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public User authenticate(String emailOrPhone, String password) {
        String sql = "SELECT * FROM users WHERE (email = ? OR phone = ?) AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, emailOrPhone);
            pstmt.setString(2, emailOrPhone);
            pstmt.setString(3, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = extractUserFromResultSet(rs);
                loadUserRoles(conn, user); // Загружаем роли пользователя
                return user;
            }
        } catch (SQLException e) {
            logger.severe("Ошибка аутентификации: " + e.getMessage());
        }
        return null;
    }

    @Override
    public User findByEmailOrPhone(String emailOrPhone) {
        String sql = "SELECT * FROM users WHERE email = ? OR phone = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, emailOrPhone);
            pstmt.setString(2, emailOrPhone);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = extractUserFromResultSet(rs);
                loadUserRoles(conn, user);
                return user;
            }
        } catch (SQLException e) {
            logger.severe("Ошибка поиска пользователя: " + e.getMessage());
        }
        return null;
    }

    private void loadUserRoles(Connection conn, User user) throws SQLException {
        String sql = "SELECT r.name FROM roles r " +
                "JOIN user_roles ur ON r.id = ur.role_id " +
                "WHERE ur.user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                user.addRole(rs.getString("name"));
            }
        }
    }

    @Override
    public boolean emailExists(String email) {
        return checkExists("email", email);
    }

    @Override
    public boolean phoneExists(String phone) {
        return checkExists("phone", phone);
    }

    @Override
    public boolean fullNameExists(String fullName) {
        return checkExists("full_name", fullName);
    }

    private boolean checkExists(String column, String value) {
        String sql = "SELECT id FROM users WHERE " + column + " = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            logger.severe("Ошибка проверки " + column + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean emailExistsForOtherUser(String email, int userId) {
        return checkExistsForOtherUser("email", email, userId);
    }

    @Override
    public boolean phoneExistsForOtherUser(String phone, int userId) {
        return checkExistsForOtherUser("phone", phone, userId);
    }

    @Override
    public boolean fullNameExistsForOtherUser(String fullName, int userId) {
        return checkExistsForOtherUser("full_name", fullName, userId);
    }

    private boolean checkExistsForOtherUser(String column, String value, int userId) {
        String sql = "SELECT id FROM users WHERE " + column + " = ? AND id != ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            logger.severe("Ошибка проверки " + column + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY full_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                loadUserRoles(conn, user);
                users.add(user);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения пользователей: " + e.getMessage());
        }
        return users;
    }

    @Override
    public List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();

        String sql;
        if ("CLIENT".equals(role)) {
            sql = "SELECT u.* FROM users u " +
                    "WHERE u.id IN (SELECT user_id FROM user_roles ur " +
                    "               JOIN roles r ON ur.role_id = r.id " +
                    "               WHERE r.name = 'CLIENT') " +
                    "AND u.id NOT IN (SELECT user_id FROM user_roles ur " +
                    "                  JOIN roles r ON ur.role_id = r.id " +
                    "                  WHERE r.name = 'ADMIN') " +
                    "ORDER BY u.full_name";
        } else {
            sql = "SELECT u.* FROM users u " +
                    "JOIN user_roles ur ON u.id = ur.user_id " +
                    "JOIN roles r ON ur.role_id = r.id " +
                    "WHERE r.name = ? ORDER BY u.full_name";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (!"CLIENT".equals(role)) {
                pstmt.setString(1, role);
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                loadUserRoles(conn, user);
                users.add(user);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения пользователей с ролью " + role + ": " + e.getMessage());
        }
        return users;
    }

    @Override
    public boolean addRoleToUser(int userId, String role) {
        String sql = "INSERT INTO user_roles (user_id, role_id) " +
                "SELECT ?, id FROM roles WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, role);
            int result = pstmt.executeUpdate();
            logger.info("Роль " + role + " добавлена пользователю ID: " + userId);
            return result > 0;
        } catch (SQLException e) {
            logger.severe("Ошибка добавления роли: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeRoleFromUser(int userId, String role) {
        String sql = "DELETE FROM user_roles " +
                "WHERE user_id = ? AND role_id = (SELECT id FROM roles WHERE name = ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, role);
            int result = pstmt.executeUpdate();
            logger.info("Роль " + role + " удалена у пользователя ID: " + userId);
            return result > 0;
        } catch (SQLException e) {
            logger.severe("Ошибка удаления роли: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET email = ?, phone = ?, full_name = ?, password = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPhone());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getPassword());
            pstmt.setInt(5, user.getId());

            int result = pstmt.executeUpdate();
            logger.info("Пользователь обновлен: " + user.getFullName());
            return result > 0;

        } catch (SQLException e) {
            logger.severe("Ошибка обновления пользователя: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);

            int result = pstmt.executeUpdate();
            logger.info("Пароль обновлен для пользователя ID: " + userId);
            return result > 0;

        } catch (SQLException e) {
            logger.severe("Ошибка обновления пароля: " + e.getMessage());
            return false;
        }
    }

    @Override
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = extractUserFromResultSet(rs);
                loadUserRoles(conn, user);
                return user;
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения пользователя: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int result = pstmt.executeUpdate();
            logger.info("Пользователь удален ID: " + userId + ", результат: " + result);
            return result > 0;

        } catch (SQLException e) {
            logger.severe("Ошибка удаления пользователя: " + e.getMessage());
            return false;
        }
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFullName(rs.getString("full_name"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        return user;
    }
}