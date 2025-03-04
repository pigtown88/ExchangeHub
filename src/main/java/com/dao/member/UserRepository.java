package com.dao.member;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.model.member.User;
import com.model.member.UserToken;


/**
 * 用戶資料存取層
 * 負責處理與 users 表的所有數據庫操作
 */
public class UserRepository {
    // 數據庫連接配置
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String URL = "jdbc:sqlserver://172.16.46.181:1433;databaseName=ExchangeHub;" + 
                                    "encrypt=true;trustServerCertificate=true;characterEncoding=UTF-8;";
    private static final String USER = "sa";
    private static final String PASSWORD = "Passw0rd";

    /**
     * 靜態初始化塊，加載 JDBC 驅動
     * 在類首次加載時執行
     */
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load JDBC driver", e);
        }
    }

    /**
     * 獲取數據庫連接
     * @return Connection 數據庫連接對象
     * @throws SQLException 如果連接失敗拋出異常
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * 根據ID查找用戶
     * @param id 用戶ID
     * @return 查找到的用戶對象，如果未找到返回null
     * @throws RuntimeException 當數據庫操作失敗時拋出
     */
    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by id: " + id, e);
        }
        return null;
    }

    /**
     * 根據用戶名查找用戶
     * @param username 用戶名
     * @return 查找到的用戶對象，如果未找到返回null
     * @throws RuntimeException 當數據庫操作失敗時拋出
     */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by username: " + username, e);
        }
        return null;
    }

    /**
     * 創建新用戶
     * @param user 要創建的用戶對象，不需要設置id
     * @return 創建後的用戶對象，包含生成的id
     * @throws RuntimeException 當數據庫操作失敗時拋出
     */
    public User create(User user) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // 設置參數
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            
            // 執行插入
            ps.executeUpdate();
            
            // 獲取生成的主鍵
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                }
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating user: " + user.getUsername(), e);
        }
    }

    /**
     * 更新用戶資訊
     * @param user 要更新的用戶對象，必須包含id
     * @throws RuntimeException 當數據庫操作失敗時拋出
     */
    public void update(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, updated_at = GETDATE() WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setLong(3, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user: " + user.getId(), e);
        }
    }

    /**
     * 刪除用戶
     * @param id 要刪除的用戶ID
     * @throws RuntimeException 當數據庫操作失敗時拋出
     */
    public void delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user: " + id, e);
        }
    }

    /**
     * 將 ResultSet 映射為 User 對象
     * @param rs 結果集
     * @return 映射後的 User 對象
     * @throws SQLException 當結果集讀取失敗時拋出
     */
    private User mapToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return user;
    }
}