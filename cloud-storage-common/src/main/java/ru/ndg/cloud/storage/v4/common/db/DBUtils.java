package ru.ndg.cloud.storage.v4.common.db;

import com.sun.istack.internal.Nullable;
import lombok.extern.log4j.Log4j2;
import ru.ndg.cloud.storage.v4.common.model.User;

import java.sql.*;

/**
 * Класс singleton для соединения, отправки и получения данных из базы данных SQLite
 *
 * @author Nikulin D
 **/
@Log4j2
public class DBUtils {
    private static Connection connection;
    private static final String DRIVER_CLASS = "org.sqlite.JDBC";
    private static final String URL = "jdbc:sqlite:D:/java_project/cloud-storage-v4/cloud_storage.db";
    private static final String GET_USER_SQL = "SELECT users.login AS login, users.password AS PASSWORD FROM users WHERE users.login=? AND users.password=?";
    private static final String SAVE_USER_SQL = "INSERT INTO users (login, password) VALUES (?, ?)";

    private DBUtils() {
    }

    /**
     * Получения коннекта к базе данных
     * @return Connection возвращает коннект к базе данных
     */
    private static Connection getConnection() {
        Connection localConnection = connection;
        if (localConnection == null) {
            synchronized (DBUtils.class) {
                localConnection = connection;
                if (localConnection == null) {
                    try {
                        Class.forName(DBUtils.DRIVER_CLASS);
                        connection = DriverManager.getConnection(DBUtils.URL);
                    } catch (Exception e) {
                        log.debug(e);
                    }
                }
            }
        }
        return connection;
    }

    /**
     * Получение пользователя по совпадению логина и пароля.
     * @param user класс для представления сущности пользователя.
     * @return User если объекта нет в базе данных возвращаеться пустой объект.
     * @see User
     */
    public static User getUser(@Nullable User user) {
        User userFromDB = new User();
        if (user == null) return userFromDB;
        PreparedStatement statement = null;
        try {
            statement = DBUtils.getConnection().prepareStatement(DBUtils.GET_USER_SQL);
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                userFromDB.setLogin(resultSet.getString("login"));
                userFromDB.setPassword(resultSet.getString("password"));
            }
        } catch (SQLException e) {
            log.debug(e);
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
                log.debug(e);
            }
        }
        return userFromDB;
    }

    /**
     * Сохранение переданного объекта User в базу данных
     * @param user класс для представления сущности пользователя.
     * @return boolean если сохранение прошло успешно возвращает true иначе false
     */
    public static boolean saveUser(@Nullable User user) {
        if (user == null) return false;
        PreparedStatement statement = null;
        try {
            statement = DBUtils.getConnection().prepareStatement(SAVE_USER_SQL);
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            int row = statement.executeUpdate();
            return row > 0;
        } catch (SQLException e) {
            log.debug(e);
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
                log.debug(e);
            }
        }
        return false;
    }

    /**
     * Закрывает connection к базе данных
     */
    public static void close() {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            log.debug(e);
        }
    }
}