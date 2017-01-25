package belcer.remoteserverconnector.controller;

import belcer.remoteserverconnector.model.dao.ConnectionProfileDao;
import belcer.remoteserverconnector.model.dao.UserDao;
import belcer.remoteserverconnector.model.dao.oracle_impl.ConnectionProfileDaoOracleImpl;
import belcer.remoteserverconnector.model.dao.oracle_impl.UserDaoImpl;
import belcer.remoteserverconnector.model.entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

import static belcer.remoteserverconnector.model.entity.Role.DB_ADMIN;
import static belcer.remoteserverconnector.model.entity.Role.IS_ADMIN;

public class AdminController {
    private UserDao userDao = new UserDaoImpl();
    private ConnectionProfileDao connectionDao = new ConnectionProfileDaoOracleImpl();
    private List<User> users;
    @FXML
    private Button ban;
    @FXML
    private TableView<User> usersTable;
    private static final double COLUMN_WIDTH = 100;

    public void init() {
        System.out.println("AdminController.init");


        usersTable.setEditable(true);
        TableColumn username = createColumn("Username");
        TableColumn email = createColumn("Email");
        TableColumn password = createColumn("Password", "password", 0.5d);
        TableColumn registrationDate = createColumn("Registration", "registrationDate", 0.5d);
        TableColumn lastLogin = createColumn("Last login", "lastLogin", 0.5d);
        TableColumn role = createColumn("Role", "role", 0.4);
        TableColumn deleted = createColumn("Deleted", "deleted", 0.2);
//        TableColumn banned = createColumn("Banned", "banned", 0.2);

        TableColumn banned = new TableColumn<>("Banned");
        banned.setMinWidth(COLUMN_WIDTH);
        banned.setCellValueFactory(
                new PropertyValueFactory<User, Byte>("banned"));

        usersTable.getColumns().addAll(
                username,
                email, password,
                registrationDate
                ,
                lastLogin, role, deleted, banned
        );
        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null
                    && !newSelection.getRole().equals(IS_ADMIN)
                    && !newSelection.getRole().equals(DB_ADMIN)) {
                ban.setDisable(false);
            } else {
                ban.setDisable(true);
            }
        });

        updateUserList();
    }

    private TableColumn createColumn(String title) {
        return createColumn(title, title.substring(0, 1).toLowerCase() + title.substring(1), 1);
    }

    private TableColumn createColumn(String title, String fieldName, double widthFactor) {
        TableColumn column = new TableColumn(title);
        column.setMinWidth(COLUMN_WIDTH * widthFactor);
        column.setCellValueFactory(
                new PropertyValueFactory<>(fieldName));
        return column;
    }

    @FXML
    private void ban() {
        System.out.println("AdminController.ban");
        User user = usersTable.getSelectionModel().getSelectedItem();
        boolean isUserBanned = user.getBanned() == 1;
        System.out.println("isUserBanned = " + isUserBanned);
        user.setBanned((byte) (1));
//        System.out.println("AdminController.ban: " + user);
//        System.out.println(user.getBanned() == 0);

        new Thread(() ->
                userDao.update(user)).start();
        updateUserList();
    }


    @FXML
    private void updateUserList() {
        new Thread(() -> {
            try {
                users = userDao.getAll().stream()
//                        .filter(u -> u.getRole() != IS_ADMIN && u.getRole() != DB_ADMIN)
                        .collect(Collectors.toList());
                System.out.println("Users in admin panel: ");
                users.forEach(System.out::println);
                System.out.println("---");
                final ObservableList<User> usersObservableList =
                        FXCollections.observableArrayList(users);
                usersTable.getItems().removeAll();
                usersTable.setItems(usersObservableList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}