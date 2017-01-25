package belcer.remoteserverconnector.controller;

import belcer.remoteserverconnector.Main;
import belcer.remoteserverconnector.model.FtpConnection;
import belcer.remoteserverconnector.model.dao.ConnectionProfileDao;
import belcer.remoteserverconnector.model.dao.UserDao;
import belcer.remoteserverconnector.model.dao.oracle_impl.ConnectionProfileDaoOracleImpl;
import belcer.remoteserverconnector.model.dao.oracle_impl.UserDaoImpl;
import belcer.remoteserverconnector.model.entity.ConnectionProfile;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.*;

public class CmpController {
    private UserDao userDao = new UserDaoImpl();
    private ConnectionProfileDao connectionDao = new ConnectionProfileDaoOracleImpl();

    private List<ConnectionProfile> connectionsList = new ArrayList<>();
    static final List<String> choiceBoxItems = new ArrayList<>();

    private FtpConnection ftp = new FtpConnection();

    @FXML
    private ChoiceBox connectionsChoiceBox;

    @FXML
    private TextField title;
    @FXML
    private TextField host;
    @FXML
    private TextField port;
    @FXML
    private TextField user;
    @FXML
    private TextField password;
    @FXML
    private ChoiceBox protocol;
    @FXML
    private Button saveChangesButton;
    @FXML
    private Label errorLabel;

    public void init() {
        System.out.println("CmpController.init");
        if (FrontController.getUser() != null) {
            saveChangesButton.setDisable(false);
            connectionsList = connectionDao.getAllForUser(FrontController.getUser().getUsername());
            choiceBoxItems.clear();
            for (ConnectionProfile connectionProfile : connectionsList) {
                System.out.println("connection = " + connectionProfile);
                choiceBoxItems.add(connectionProfile.getTitle());
            }
            connectionsChoiceBox.setItems(FXCollections.observableArrayList(choiceBoxItems));
            connectionsChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                Object selectedItem = connectionsChoiceBox.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    String chosenConnectionTitle = selectedItem.toString();
                    if (chosenConnectionTitle != null && !chosenConnectionTitle.isEmpty()) {
                        ConnectionProfile connectionToSaving = getConnection(chosenConnectionTitle);
                        if(connectionToSaving != null) {
                            saveEnteredConfigsAs(connectionToSaving);
                        }
                    }
                }

                if ((Integer) newValue >= 0) {
                    ConnectionProfile connection = connectionsList.get((Integer) newValue);
                    title.setText(connection.getTitle());
                    host.setText(connection.getHost());
                    port.setText(String.valueOf(connection.getPort()));
                    user.setText(connection.getConnectionUser());
                    password.setText(connection.getConnectionPass());
                }
            });
//      connectionsChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
//        @Override
//        public void handle(ActionEvent event) {
//          connectionsChoiceBox.getSel
//        }
//      });
            protocol.setItems(FXCollections.observableArrayList(Collections.singletonList("FTP")));
        } else {
            saveChangesButton.setDisable(true);
        }
    }

    public void newConnection() {
        System.out.println("CmpController.newConnection");
        String newTitle = "Untitled";
        int counter = 1;
        boolean titleIsChosen = false;
        while (!titleIsChosen) {
            titleIsChosen = true;
            for (String title : choiceBoxItems) {
                if ((newTitle + "-" + counter).equals(title)) {
                    counter++;
                    titleIsChosen = false;
                    break;
                }
            }
        }
        connectionsList.add(new ConnectionProfile(
                newTitle + "-" + counter, "", 0, "",
                "", "", 0,
                FrontController.getUser() != null ? FrontController.getUser().getUsername() : null)
        );
        choiceBoxItems.add(newTitle + "-" + counter);
        connectionsChoiceBox.setItems(FXCollections.observableArrayList(choiceBoxItems));
    }

    public void deleteConnection() {
        System.out.println("CmpController.deleteConnection");
        String itemTitle = (String) connectionsChoiceBox.getSelectionModel().getSelectedItem();
        if (itemTitle != null) {
            errorLabel.setText("");
            connectionDao.delete(FrontController.getUser().getUsername(),
                    (String) connectionsChoiceBox.getSelectionModel().getSelectedItem()
            );
            choiceBoxItems.remove(itemTitle);
            connectionsChoiceBox.getItems().remove(connectionsChoiceBox.getSelectionModel().getSelectedIndex());
            if (itemTitle != null) {
                connectionsList.removeIf(next -> itemTitle.equals(next.getTitle()));
            }
        } else {
            errorLabel.setText("Please, choose item for deletion first");
        }
    }

    public void connectToChosenConnection() {
        System.out.println("CmpController.connectToChosenConnection");
        for (ConnectionProfile connectionProfile : connectionsList) {
            if (connectionProfile.getConnectionUser() != null && !connectionProfile.getConnectionUser().isEmpty()
                    && connectionProfile.getConnectionPass() != null && !connectionProfile.getConnectionPass().isEmpty()
                    && connectionProfile.getHost() != null && !connectionProfile.getHost().isEmpty()
                    && connectionProfile.getPort() != 0
                    ) {
                System.out.println("connection = " + connectionProfile);
//                new Thread(() ->
//                        connectionDao.saveOrUpdate(connectionProfile)).start();
            } else {
                errorLabel.setText("Wrong input. Please, fill all fields.");
            }
        }
//        Properties properties = System.getProperties();
//        FtpConnection client = new FtpConnection();
//        client.init();
//        client.uploadFile("client.txt", "server.txt");

//        client.downloadFile("sss.txt", "AAA.txt");
    }

    public void cancel() {
        System.out.println("CmpController.cancel");
        Main.INSTANCE.setStage(AppWindows.MAIN);
    }

    public void save() {
        System.out.println("CmpController.save");
        Object selectedItem = connectionsChoiceBox.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String chosenConnectionTitle = selectedItem.toString();
            ConnectionProfile connectionToSaving = getConnection(chosenConnectionTitle);
            saveEnteredConfigsAs(connectionToSaving);
        }

        for (ConnectionProfile connectionProfile : connectionsList) {
//      getConnection(co);
            System.out.println("connection = " + connectionProfile);
            new Thread(() ->
                    connectionDao.saveOrUpdate(connectionProfile)).start();
        }
    }

    private void saveEnteredConfigsAs(ConnectionProfile connection) {
        connection.setHost(host.getText());
        connection.setPort(Integer.parseInt(port.getText()));
        connection.setConnectionUser(user.getText());
        connection.setConnectionPass(password.getText());
        Object selectedItem = protocol.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            connection.setProtocol(selectedItem.toString());
        }
    }

    private ConnectionProfile getConnection(String title) {
        for (ConnectionProfile connectionProfile : connectionsList) {
            if (title.equals(connectionProfile.getTitle())) {
                return connectionProfile;
            }
        }
//        try {
//            throw new Exception("Title '" + title + "' doesn't find.");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return null;
    }
}