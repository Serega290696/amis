package belcer.remoteserverconnector.model.dao.oracle_impl;

import belcer.remoteserverconnector.model.entity.ConnectionProfile;
import belcer.remoteserverconnector.model.entity.Role;
import belcer.remoteserverconnector.model.entity.User;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OracleConnector {
//    INSTANCE,;

    private final static String HOST = "localhost";
    //    private final static String HOST = "77.47.134.131";
    private final static String PORT = "1521";
    private final static String SCHEMA = "orcl";
    //    private final static String SCHEMA = "xe";
    private final static String URL_SCHEMA = "jdbc:oracle:thin:@%1$s:%2$s/%3$s";
    private final static String USER_LOGIN = "system";
    private final static String USER_PASS = "root";
//    private Connection conn;

    private static final String usersTable = "\"app_user\"";
    private static final String connectionsTable = "\"app_connection_profile\"";
    private static final String roleTable = "\"app_role\"";
//  private List<ConnectionProfile> allConnections;

    public Connection open() {
        System.out.println("Try open connection. . .");
        Connection conn = null;
        try {
            conn = createConnection();
        } catch (SQLException | ClassNotFoundException | NamingException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public void close(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
            System.out.println("Connection is closed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection createConnection() throws SQLException, ClassNotFoundException, NamingException {
        DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
//    return DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe", USER_LOGIN, USER_PASS);
        System.out.println("DB connection is created");
        return DriverManager.getConnection(String.format(URL_SCHEMA, HOST, PORT, SCHEMA), USER_LOGIN, USER_PASS);
    }

    // Users
    public User getUser(String username) {
        Connection conn = open();
        PreparedStatement ins;
        User user = null;
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            conn.setAutoCommit(false);
            ins = conn.prepareStatement("SELECT \"username\", \"email\", " +
                    "\"password\", \"registration_date\", \"last_login\", \"role_title\", \"deleted\", \"banned\" " +
                    "FROM " + usersTable + " WHERE \"username\"=?");
            ins.setString(1, username);
            ResultSet resultSet = ins.executeQuery();
            if (!resultSet.isBeforeFirst()) {
                return null;
            }
            resultSet.next();
            user = new User(resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getTimestamp(4),
                    resultSet.getTimestamp(5),
                    Role.parseString(resultSet.getString(6)),
                    resultSet.getInt(7), resultSet.getInt(8));
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            close(conn);
        }
        return user;
    }

    public void saveUser(User user) {
        Connection conn = open();
        PreparedStatement ins;
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            ins = conn.prepareStatement(
                    "INSERT INTO " + usersTable + " (\"username\", \"email\"," +
                            " \"password\", \"registration_date\", \"last_login\", \"role_title\") " +
                            " VALUES (?, ?, ?, ?, ?, ?)");
            ins.setString(1, user.getUsername());
            ins.setString(2, user.getEmail());
            ins.setString(3, user.getPassword());
            ins.setTimestamp(4, user.getRegistrationDate());
            ins.setTimestamp(5, user.getLastLogin());
            ins.setString(6, user.getRole().toString());
            ins.executeQuery();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            close(conn);
        }
    }

    public List<User> getAllUsers() throws Exception {
        Connection conn = open();
        if (conn == null) {
            throw new Exception("Connection is failed");
        }
        PreparedStatement ins;
        List<User> users = new ArrayList<>();
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            conn.setAutoCommit(false);
            ins = conn.prepareStatement("SELECT \"username\", \"email\", " +
                    "\"password\", \"registration_date\", \"last_login\", \"role_title\", \"deleted\", \"banned\" FROM " + usersTable + "");
            ResultSet resultSet = ins.executeQuery();
            if (!resultSet.isBeforeFirst()) {
                return users;
            }
            while (resultSet.next()) {
                users.add(new User(resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getTimestamp(4),
                        resultSet.getTimestamp(5),
                        Role.parseString(resultSet.getString(6)),
                        resultSet.getInt(7),
                        resultSet.getInt(8)
                ));
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            close(conn);
        }
        return users;
    }

    public User updateUser(String username, User user) {
        Connection conn = open();
        System.out.println("OracleConnector.updateUser: " + user);
        PreparedStatement ins;
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            ins = conn.prepareStatement(
                    "UPDATE " + usersTable + " " +
                            " SET" +
                            " \"username\"=?, " +
                            " \"email\"=?, " +
                            " \"password\"=?, " +
                            " \"registration_date\"=?, " +
                            " \"last_login\"=?, " +
                            " \"role_title\"=?, " +
                            " \"banned\"=? " +
                            " WHERE \"username\"= ?"
            );
            ins.setString(1, user.getUsername());
            ins.setString(2, user.getEmail());
            ins.setString(3, user.getPassword());
            ins.setTimestamp(4, user.getRegistrationDate());
            ins.setTimestamp(5, user.getLastLogin());
            ins.setString(6, user.getRole().toString());
            ins.setInt(7, user.getBanned());
            ins.setString(8, username);
            ins.executeQuery();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            close(conn);
        }
        return user;
    }

    // Connections
    public List<ConnectionProfile> getConnections(String username) {
        System.out.println("---OracleConnector.getConnections");
        Connection conn = open();
        PreparedStatement ins;
        List<ConnectionProfile> connections = new ArrayList<>();
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            conn.setAutoCommit(false);
            ins = conn.prepareStatement("SELECT \"title\", \"connection_pass\", \"connection_user\", \"port\", \"host\", \"protocol\", " +
                    " \"date_modified\", \"last_connection_date\", \"deleted\", \"saved_by_user\", \"username\", \"date_created\" FROM " + connectionsTable + " WHERE \"username\" = ? ");
            ins.setString(1, username);
            ResultSet resultSet = ins.executeQuery();
            System.out.println("resultSet = " + resultSet);
            if (resultSet == null) {
                return connections;
            }
            while (resultSet.next()) {
                System.out.println("NEXT:");
                String title = resultSet.getString(1);
                String pass = resultSet.getString(2);
                String user = resultSet.getString(3);
                int port = resultSet.getInt(4);
                String host = resultSet.getString(5);
                String protocol = resultSet.getString(6);
                Date data_mod = resultSet.getDate(7);
                Date date_last_con = resultSet.getDate(8);
                int deleted = resultSet.getByte(9);
                int saved_by_user = resultSet.getByte(10);
                String usernameT = resultSet.getString(11);
                Date created_date = resultSet.getDate(12);
                connections.add(new ConnectionProfile(title, host, port, user, pass, protocol, created_date, data_mod, date_last_con, deleted, saved_by_user, usernameT));
//        System.out.println(new ConnectionProfile(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4),
//            resultSet.getString(5), resultSet.getString(6),
//            resultSet.getDate(7), resultSet.getDate(8), resultSet.getDate(9), resultSet.getBoolean(10),
//            resultSet.getBoolean(11), resultSet.getString(12)));
//        connections.add(new ConnectionProfile(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4),
//            resultSet.getString(5), resultSet.getString(6),
//            resultSet.getDate(7), resultSet.getDate(8), resultSet.getDate(9), resultSet.getBoolean(10),
//            resultSet.getBoolean(11), resultSet.getString(12)

            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            close(conn);
        }
        System.out.println("---connections = " + connections);
        return connections;
    }

    public ConnectionProfile getConnection(String title, String username) {
        Connection conn = open();
        PreparedStatement ins;
        ConnectionProfile connection = null;
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            conn.setAutoCommit(false);
            ins = conn.prepareStatement("SELECT \"title\" , \"connection_pass\" , \"connection_user\" , \"port\" , \"host\" , \"protocol\" , " +
                    " \"date_modified\" , \"last_connection_date\" , \"deleted\" , \"saved_by_user\" , \"username\" , \"date_created\" FROM " + connectionsTable + " " +
                    " WHERE \"username\" = ? AND \"title\" = ?");
            ins.setString(1, username);
            ins.setString(2, title);
            ResultSet resultSet = ins.executeQuery();
            if (!resultSet.isBeforeFirst()) {
                return null;
            }
            resultSet.next();
            String titleT = resultSet.getString(1);
            String pass = resultSet.getString(2);
            String user = resultSet.getString(3);
            int port = resultSet.getInt(4);
            String host = resultSet.getString(5);
            String protocol = resultSet.getString(6);
            Date data_mod = resultSet.getDate(7);
            Date date_last_con = resultSet.getDate(8);
            int deleted = resultSet.getByte(9);
            int saved_by_user = resultSet.getByte(10);
            String usernameT = resultSet.getString(11);
            Date created_date = resultSet.getDate(12);
            connection = new ConnectionProfile(titleT, host, port, user, pass, protocol, created_date, data_mod, date_last_con, deleted, saved_by_user, usernameT);

//      connection = new ConnectionProfile(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4),
//          resultSet.getString(5), resultSet.getString(6), resultSet.getDate(7), resultSet.getDate(8), resultSet.getDate(9), resultSet.getBoolean(10),
//          resultSet.getBoolean(11), resultSet.getString(12)
//      );
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            close(conn);
        }
        return connection;
    }

    public List<ConnectionProfile> getAllConnections() {
        Connection conn = open();
        PreparedStatement ins;
        List<ConnectionProfile> connections = new ArrayList<>();
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            conn.setAutoCommit(false);
            ins = conn.prepareStatement("SELECT * FROM " + connectionsTable);
            ResultSet resultSet = ins.executeQuery();
            if (!resultSet.isBeforeFirst()) {
                return null;
            }
            while (resultSet.next()) {
//        user = new User(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getDate(4), resultSet.getDate(5));
                connections.add(new ConnectionProfile(resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6),
                        resultSet.getDate(7),
                        resultSet.getDate(8),
                        resultSet.getDate(9),
                        resultSet.getByte(10),
                        resultSet.getByte(11),
                        resultSet.getString(12)
                ));
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            close(conn);
        }
        return connections;
    }

    public void deleteConnection(String username, String title) {
        Connection conn = open();
        PreparedStatement ins;
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            ins = conn.prepareStatement("DELETE FROM " + connectionsTable + " WHERE \"username\"=? AND \"title\" = ?");
            ins.setString(1, username);
            ins.setString(2, title);
            ins.executeQuery();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            close(conn);
        }
    }

    public void deleteUser(String username) {
        Connection conn = open();
        PreparedStatement ins;
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            ins = conn.prepareStatement("DELETE FROM " + usersTable + " WHERE \"username\"=?");
            ins.setString(1, username);
            ins.executeQuery();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            close(conn);
        }
    }

    public void deleteConnectionProfile(String title, String username) {
        Connection conn = open();
        PreparedStatement ins;
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            ins = conn.prepareStatement("DELETE FROM " + connectionsTable + " WHERE \"username\"=? AND \"title\"=?");
            ins.setString(1, username);
            ins.setString(2, title);
            ins.executeQuery();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            close(conn);
        }
    }

    public void saveConnection(ConnectionProfile connectionProfile) {
        Connection conn = open();
        PreparedStatement ins;
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            ins = conn.prepareStatement(
                    "INSERT INTO " + connectionsTable + " (" +
                            "\"title\", \"connection_pass\", \"connection_user\", \"port\", \"host\", \"protocol\", " +
                            "\"date_modified\", \"last_connection_date\", " +
                            "\"deleted\", \"saved_by_user\", \"username\", \"date_created\") " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            System.out.println(connectionProfile);
            ins.setString(1, connectionProfile.getTitle());
            ins.setString(2, connectionProfile.getConnectionPass());
            ins.setString(3, connectionProfile.getConnectionUser());
            ins.setInt(4, connectionProfile.getPort());
            ins.setString(5, connectionProfile.getHost());
            ins.setString(6, connectionProfile.getProtocol());
            ins.setDate(7, connectionProfile.getDateModified());
            ins.setDate(8, connectionProfile.getLastConnectionDate());
            ins.setByte(9, (byte) connectionProfile.isDeleted());
            ins.setByte(10, (byte) connectionProfile.isSavedByUser());
            ins.setString(11, connectionProfile.getUsername());
            ins.setDate(12, connectionProfile.getDateCreated());
            ins.executeQuery();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            close(conn);
        }
    }

    public void updateConnection(ConnectionProfile connectionProfile) {
        Connection conn = open();
        PreparedStatement ins;
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
//      "title", "connection_password", "connection_user", "port", "host", "protocol", " +
//      "\"date_modified\", \"last_connection_date\", " +
//          ""deleted", "saved_by_user", "username_fk", "created_date"
            ins = conn.prepareStatement(
                    "UPDATE " + connectionsTable + " " +
                            " SET" +
                            " \"title\"=?, " +
                            " \"connection_pass\"=?, " +
                            " \"connection_user\"=?, " +
                            " \"port\"=?, " +
                            " \"host\"=?, " +
                            " \"protocol\"=?, " +
                            " \"date_modified\"=?, " +
                            " \"last_connection_date\"=?, " +
                            " \"deleted\"=?, " +
                            " \"saved_by_user\"=?, " +
                            " \"username\"=?, " +
                            " \"date_created\"=? " +
                            " WHERE \"username\" = ? AND \"title\" = ?"
            );
            ins.setString(1, connectionProfile.getTitle());
            ins.setString(2, connectionProfile.getConnectionPass());
            ins.setString(3, connectionProfile.getConnectionUser());
            ins.setInt(4, connectionProfile.getPort());
            ins.setString(5, connectionProfile.getHost());
            ins.setString(6, connectionProfile.getProtocol());
            ins.setDate(7, connectionProfile.getDateModified());
            ins.setDate(8, connectionProfile.getLastConnectionDate());
            ins.setByte(9, (byte) connectionProfile.isDeleted());
            ins.setByte(10, (byte) connectionProfile.isSavedByUser());
            ins.setString(11, connectionProfile.getUsername());
            ins.setDate(12, connectionProfile.getDateCreated());
            ins.setString(13, connectionProfile.getUsername());
            ins.setString(14, connectionProfile.getTitle());
            ins.executeQuery();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            close(conn);
        }
    }

    public void saveRole(Role role) {
        Connection conn = open();
        PreparedStatement ins;
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            ins = conn.prepareStatement(
                    "INSERT INTO " + roleTable + " (\"role_title\") " +
                            "VALUES (?)");
            ins.setString(1, role.getRoleTitle());
            ins.executeQuery();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            close(conn);
        }
    }

    public List<Role> getAllRoles() {
        Connection conn = open();
        PreparedStatement ins;
        List<Role> roles = new ArrayList<>();
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            conn.setAutoCommit(false);
            ins = conn.prepareStatement("SELECT * FROM " + roleTable);
            ResultSet resultSet = ins.executeQuery();
            if (!resultSet.isBeforeFirst()) {
                return roles;
            }
            while (resultSet.next()) {
//        user = new User(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getDate(4), resultSet.getDate(5));
                roles.add(Role.parseString(resultSet.getString(1)));
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            close(conn);
        }
        return roles;
    }
}