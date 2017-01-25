package belcer.remoteserverconnector.controller;

import belcer.remoteserverconnector.Main;
import belcer.remoteserverconnector.model.dao.UserDao;
import belcer.remoteserverconnector.model.dao.oracle_impl.UserDaoImpl;
import belcer.remoteserverconnector.model.entity.User;
import belcer.remoteserverconnector.model.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.ftpserver.command.impl.SYST;

import java.sql.Date;
import java.sql.Timestamp;

public class LoginController {
    private UserDao userDao = new UserDaoImpl();

    @FXML
    private TextField loginUsername;
    @FXML
    private TextField loginPassword;
    @FXML
    private Label errorLabel;

    public void login() {
        System.out.println("LoginController.login");
        String username = loginUsername.getText();
        String password = loginPassword.getText();
        User user = userDao.get(username);
        boolean loginDataCorrect = true;
        if (user == null) {
            loginDataCorrect = false;
            System.err.println("Wrong username");
            errorLabel.setText("Wrong username");
        } else if (user.getBanned() != 0) {
            loginDataCorrect = false;
            System.err.println("User is banned");
            errorLabel.setText("User is banned");
        } else if (user.getDeleted() != 0) {
            loginDataCorrect = false;
            System.err.println("User is deleted");
            errorLabel.setText("User is deleted");
        } else if (!Utils.checkPassword(password, user.getPassword())) {
            loginDataCorrect = false;
            System.err.println("Wrong password");
            errorLabel.setText("Wrong password");
        }

        if (loginDataCorrect) {
            errorLabel.setText("");
            System.out.println("Login success");
            System.out.println("user = " + user);
            user.setLastLogin(new Timestamp(System.currentTimeMillis()));
            new Thread(() -> userDao.update(user)).start();
//      Main.INSTANCE.setStage(AppWindows.MAIN);
            FrontController.setUser(user);
            Main.INSTANCE.closeAdditionalWindow();
        }
    }
}
