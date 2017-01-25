package belcer.remoteserverconnector.controller;

import belcer.remoteserverconnector.Main;
import belcer.remoteserverconnector.model.entity.ConnectionProfile;
import belcer.remoteserverconnector.model.entity.Role;
import belcer.remoteserverconnector.model.entity.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.HashMap;
import java.util.Map;

public class FrontController {
    @FXML
    private TextField connectionTitle;
    @FXML
    private TextField connectionHost;
    @FXML
    private TextField connectionPort;
    @FXML
    private TextField connectionUser;
    @FXML
    private TextField connectionPass;
    @FXML
    private TextField connectionProtocol;

    @FXML
    private TextArea log;
    @FXML
    private TreeTableView localFileList;
    @FXML
    private TreeTableView remoteFileList;

    @FXML
    private MenuItem loginMenu;
    @FXML
    private MenuItem logoutMenu;
    @FXML
    private Menu adminButton;

    private static User user;// = new User("dog", "may@may", "$2a$10$pHpXXX8EWK2hQaNxzprAeekm3hnE38LvY4OagB6DOvd/ssm2vGghS", Role.USER);
    private Map<String, ConnectionProfile> connections = new HashMap<>();

    public void localPathChanged() {

    }

    public void login() {
        Main.INSTANCE.setStage(AppWindows.LOGIN);
    }

    public void register() {
        Main.INSTANCE.setStage(AppWindows.SIGNUP);
    }

    public void logout() {
        setUser(null);
        adminButton.setDisable(true);
        loginMenu.setDisable(false);
        logoutMenu.setDisable(true);
    }

    public void closeConnection() {

    }

    public void updateLocalPath() {

    }

    public void updateRemotePath() {

    }

    @FXML
    private void newConnection() {
        System.out.println("FrontController.newConnection");
        Main.INSTANCE.setStage(AppWindows.CREATE_CONNECTION);
//    String title = connectionTitle.getText();
//    String host = connectionHost.getText();
//    String port = connectionPort.getText();
//    String connUser = connectionUser.getText();
//    String connPass = connectionPass.getText();
//    String protocol = connectionProtocol.getText();
//    //todo: not ConnectionProfile!
//    connections.put(title, new ConnectionProfile(title, host, port, connUser, connPass, protocol, user));
//    if (title != null && !(title = title.trim()).isEmpty() && connections)
    }

    public void showAdminPanel() {
        System.out.println("FrontController.showAdminPanel");
        Main.INSTANCE.setStage(AppWindows.ADMIN_PANEL);
    }

    public static void setUser(User user) {
        System.out.println("==========");
        System.out.println("User is changed: " + user);
        FrontController.user = user;
    }

    public static User getUser() {
        return user;
    }

    public void update() {
        System.out.println("FrontController.update");
        if (user != null){
            loginMenu.setDisable(true);
            logoutMenu.setDisable(false);
            if (user != null && user.getRole() == Role.DB_ADMIN) {
                adminButton.setDisable(false);
            }
        }
    }
}