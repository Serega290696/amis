package belcer.remoteserverconnector.controller;

import belcer.remoteserverconnector.Main;
import belcer.remoteserverconnector.model.dao.UserDao;
import belcer.remoteserverconnector.model.dao.oracle_impl.UserDaoImpl;
import belcer.remoteserverconnector.model.entity.User;
import belcer.remoteserverconnector.model.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.Date;

public class LoginController {
    private UserDao userDao = new UserDaoImpl();

    @FXML
    private TextField loginUsername;
    @FXML
    private TextField loginPassword;

    public void login() {
        System.out.println("LoginController.login");
        String username = loginUsername.getText();
        String password = loginPassword.getText();
        User user = userDao.get(username);
        boolean loginDataCorrect = true;
        if (user == null) {
            loginDataCorrect = false;
            System.err.println("Wrong username");
        } else if (!Utils.checkPassword(password, user.getPassword())) {
            loginDataCorrect = false;
            System.err.println("Wrong password");
        }

        if (loginDataCorrect) {
            System.out.println("Login success");
            System.out.println("user = " + user);
            user.setLastLogin(new Date(System.currentTimeMillis()));
            new Thread(() -> userDao.update(user)).start();
//      Main.INSTANCE.setStage(AppWindows.MAIN);
            FrontController.setUser(user);
            Main.INSTANCE.closeAdditionalWindow();
        }
    }
}
