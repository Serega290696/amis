package belcer.remoteserverconnector;

import belcer.remoteserverconnector.controller.AdminController;
import belcer.remoteserverconnector.controller.AppWindows;
import belcer.remoteserverconnector.controller.CmpController;
import belcer.remoteserverconnector.controller.FrontController;
import belcer.remoteserverconnector.model.dao.RoleDao;
import belcer.remoteserverconnector.model.dao.UserDao;
import belcer.remoteserverconnector.model.dao.oracle_impl.RoleDaoImpl;
import belcer.remoteserverconnector.model.dao.oracle_impl.UserDaoImpl;
import belcer.remoteserverconnector.model.entity.Role;
import belcer.remoteserverconnector.model.entity.User;
import belcer.remoteserverconnector.model.utils.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.ftpserver.command.impl.USER;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

//-Duser.country=en -Duser.language=en
public class Main extends Application {
    public static Main INSTANCE;

    public static final boolean DEBUG_MODE = true;
    public static final Scanner SCANNER = new Scanner(System.in);

    private static final String MAIN_WINDOW_TITLE = "Main window";
    private static final String MAIN_WINDOW_RESOURCE = "/fxml/main.fxml";
    private Stage mainStage;
    private Stage stage;
    private FXMLLoader loader;
    private FrontController frontController;

    static final String ADMIN_USERNAME = "admin";
    static final String ADMIN_PASSWORD = "admin";

    @Override
    public void start(Stage primaryStage) {
        INSTANCE = this;
        loader = new FXMLLoader();
        mainStage = primaryStage;
        mainStage.setTitle(MAIN_WINDOW_TITLE);
        mainStage.setScene(new Scene(initWindow(MAIN_WINDOW_RESOURCE)));
        mainStage.show();
        Object controller = loader.getController();
        if (controller instanceof FrontController) {
            frontController = (FrontController) controller;
        }
        new Thread(() -> {
            RoleDao roleDao = new RoleDaoImpl();
            if (roleDao.getAll().size() == 0) {
                roleDao.save(Role.USER);
                roleDao.save(Role.DB_ADMIN);
                roleDao.save(Role.IS_ADMIN);
            }
            UserDao usersDao = new UserDaoImpl();
            boolean adminExists = false;
            try {
                adminExists = usersDao.getAll().stream().filter(u -> ADMIN_USERNAME.equals(u.getUsername().toLowerCase())).count() > 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!adminExists) {
                System.out.println("Admin doesn't exist. Generate new admin with password 'admin'.");
                usersDao.save(new User(ADMIN_USERNAME, "admin", Utils.hashPassword(ADMIN_PASSWORD), Role.DB_ADMIN));
            }
        }).start();
        new Thread(() -> {
            RoleDao roleDao = new RoleDaoImpl();
            UserDao userDao = new UserDaoImpl();
            StringBuilder builder = new StringBuilder("Roles: ");
            for (Role r : roleDao.getAll()) {
                builder.append(r).append(", ");
            }
            builder.append(". \r\nUsers: ");
            try {
                for (User u : userDao.getAll()) {
                    builder.append(u).append(", ");
                }
                builder.append(".");
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(builder);
        }).start();

    }

    public static void main(String[] args) {
        launch(args);
    }

    public void setStage(AppWindows window, Object... args) {
        String rootResource = null;
        String title = null;
        switch (window) {
            case MAIN:
                rootResource = "/fxml/main.fxml";
                title = "Main window";
//        if (args.length != 0 && args[0] instanceof User) {
//
//        }
                break;
            case SIGNUP:
                rootResource = "/fxml/signup.fxml";
                title = "Registration";
                break;
            case LOGIN:
                rootResource = "/fxml/login.fxml";
                title = "Login";
                break;
            case CREATE_CONNECTION:
                rootResource = "/fxml/cmp.fxml";
                title = "Create new connection";
                break;
            case ADMIN_PANEL:
                rootResource = "/fxml/admin.fxml";
                title = "Administrator panel";
                break;
        }

        if (rootResource == null) {
            throw new IllegalArgumentException("Wrong window");
        } else if (title == null) {
            throw new IllegalArgumentException("Wrong window title");
        } else {
            if (rootResource.equals("/fxml/main.fxml")) {
                mainStage.setTitle(title);
                mainStage.setScene(new Scene(initWindow(rootResource)));
                mainStage.show();
            } else {
                if (stage != null && stage.isShowing()) {
                    stage.close();
                }
                stage = new Stage(StageStyle.DECORATED);
                stage.setTitle(title);
                System.out.println("rootResource = " + rootResource);
                stage.setScene(new Scene(initWindow(rootResource)));
                stage.show();
                if (window == AppWindows.CREATE_CONNECTION) {
                    Object controller = loader.getController();
                    if (controller instanceof CmpController) {
                        ((CmpController) controller).init();
                    }
                } else if (window == AppWindows.ADMIN_PANEL) {
                    Object controller = loader.getController();
                    if (controller instanceof AdminController) {
                        ((AdminController) controller).init();
                    }
                }

            }
        }
    }

    public Parent initWindow(String resource) {
        try {
            loader = new FXMLLoader();
            System.out.println("loader.getLocation() = " + loader.getLocation());
            loader.setLocation(Main.class.getResource(resource));
            System.out.println("loader.getLocation() = " + loader.getLocation());
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void closeAdditionalWindow() {
        System.out.println("Main.closeAdditionalWindow");
        stage.close();
        update();
    }

    public void update() {
        System.out.println("Main.update");
        if (frontController != null) {
            frontController.update();
        }
    }
}
