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
        String sql = "INSERT INTO users (full_name, password, role, email, phone, linked_user_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhone());
            pstmt.setObject(6, user.getLinkedUserId() != 0 ? user.getLinkedUserId() : null);

            int result = pstmt.executeUpdate();

            if (result > 0) {
                String idSql = "SELECT last_insert_rowid() as id";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(idSql)) {
                    if (rs.next()) {
                        user.setId(rs.getInt("id"));
                        logger.info("Пользователь создан с ID: " + user.getId());
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            logger.severe("Ошибка создания пользователя: " + e.getMessage());
            e.printStackTrace();
            return false;
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
                return extractUserFromResultSet(rs);
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
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка поиска пользователя: " + e.getMessage());
        }
        return null;
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
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения пользователей: " + e.getMessage());
        }
        return users;
    }

    @Override
    public List<User> getClients() {
        List<User> clients = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'CLIENT' ORDER BY full_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clients.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения клиентов: " + e.getMessage());
        }
        return clients;
    }

    @Override
    public List<User> getMechanics() {
        List<User> mechanics = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'MECHANIC' ORDER BY full_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                mechanics.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения механиков: " + e.getMessage());
        }
        return mechanics;
    }

    @Override
    public List<User> getAdmins() {
        List<User> admins = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'ADMIN' ORDER BY full_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                admins.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка получения администраторов: " + e.getMessage());
        }
        return admins;
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
                return extractUserFromResultSet(rs);
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
        user.setRole(rs.getString("role"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setLinkedUserId(rs.getObject("linked_user_id") != null ? rs.getInt("linked_user_id") : 0);
        return user;
    }
}