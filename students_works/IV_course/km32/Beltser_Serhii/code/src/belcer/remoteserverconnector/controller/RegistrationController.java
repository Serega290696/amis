package belcer.remoteserverconnector.controller;

import belcer.remoteserverconnector.Main;
import belcer.remoteserverconnector.model.dao.UserDao;
import belcer.remoteserverconnector.model.dao.oracle_impl.UserDaoImpl;
import belcer.remoteserverconnector.model.entity.Role;
import belcer.remoteserverconnector.model.entity.User;
import belcer.remoteserverconnector.model.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class RegistrationController {

  private UserDao userDao = new UserDaoImpl();

  @FXML
  private TextField regLogin;
  @FXML
  private TextField regPass;
  @FXML
  private TextField regPass2;
  @FXML
  private TextField regEmail;

  public void register() {
    System.out.println("RegistrationController.register");
    String username = regLogin.getText();
    String pass = regPass.getText();
    String pass2 = regPass2.getText();
    String email = regEmail.getText();
    boolean registrationDataCorrect = true;
    if (username.isEmpty()) {
      System.err.println("Username is empty");
      registrationDataCorrect = false;
    } else if (userDao.isUserWithSuchUsernameExist(username)) {
      System.err.println("User with such username is exist");
      registrationDataCorrect = false;
    } else if (pass == null || pass.isEmpty() || !pass.equals(pass2)) {
      System.err.println("Pass is empty or password isn't equals");
      registrationDataCorrect = false;
//    } else if (email.isEmpty()) {
//      registrationDataCorrect = false;
    }

    if (registrationDataCorrect) {
      System.out.println("Registration data is OK");
      User newUser = new User(username, email, Utils.hashPassword(pass), Role.USER);
      new Thread(() -> userDao.save(newUser)).start();
      User savedUser = userDao.get(username);
      System.out.println("savedUser = " + savedUser);
      FrontController.setUser(savedUser);
      Main.INSTANCE.closeAdditionalWindow();
    }
  }
}
