package Controller;

import Model.User.DAOUser;
import Model.User.User;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController implements Initializable {

    @FXML
    private TextField UserField;

    @FXML
    private PasswordField PassField;

    @FXML
    private TextField PassFieldHidden;

    @FXML
    private CheckBox checkPass;

    @FXML
    private ComboBox<String> UserRole;

    @FXML
    private Button btnLogin;

    // ROLE LIST
    private final String[] roleList = {
        "Admin",
        "Kasir"
    };

    // DAO
    private DAOUser daoUser = new DAOUser();

    // SHOW PASSWORD
    @FXML
    private void showPasswordAction() {

        boolean show =
                checkPass.isSelected();

        PassFieldHidden.setVisible(show);
        PassFieldHidden.setManaged(show);

        PassField.setVisible(!show);
        PassField.setManaged(!show);
    }

    // LOGIN METHOD
    private void login() {

        String username =
                UserField.getText().trim();

        String password =
                checkPass.isSelected()
                ? PassFieldHidden.getText()
                : PassField.getText();

        String role =
                UserRole.getValue();

        // VALIDASI USERNAME
        if (username.isEmpty()) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Username wajib diisi!"
            );

            UserField.requestFocus();

            return;
        }

        // VALIDASI PASSWORD
        if (password.isEmpty()) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Password wajib diisi!"
            );

            PassField.requestFocus();

            return;
        }

        // VALIDASI ROLE
        if (role == null) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Role harus dipilih!"
            );

            return;
        }

        // LOGIN DATABASE
        User user =
                daoUser.login(
                        username,
                        password,
                        role
                );

        // LOGIN GAGAL
        if (user == null) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Login Gagal",
                    "Username, password, atau role salah!"
            );

            return;
        }

        // UPDATE STATUS ONLINE
        daoUser.updateStatusOnline(
                username,
                "Online"
        );

        // LOGIN BERHASIL
        showAlert(
                Alert.AlertType.INFORMATION,
                "Berhasil",
                "Selamat datang "
                + user.getNamaLengkap()
        );

        // PINDAH DASHBOARD
        openDashboard(user);
    }

    // OPEN DASHBOARD
    private void openDashboard(User user) {

        try {

            Parent root;

            // DASHBOARD ADMIN
            if (user.getRole().equals("Admin")) {

                root =
                        FXMLLoader.load(
                                getClass().getResource(
                                        "/View/Admin/adminDashboard.fxml"
                                )
                        );

            } else {

                // DASHBOARD KASIR
                root =
                        FXMLLoader.load(
                                getClass().getResource(
                                        "/View/Kasir/kasirDashboard.fxml"
                                )
                        );
            }

            Stage stage =
                    (Stage) btnLogin
                            .getScene()
                            .getWindow();

            Scene scene =
                    new Scene(root);

            stage.setScene(scene);

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // ALERT METHOD
    private void showAlert(
            Alert.AlertType type,
            String title,
            String message
    ) {

        Alert alert =
                new Alert(type);

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
    }

    @Override
    public void initialize(
            URL url,
            ResourceBundle rb
    ) {

        // BIND PASSWORD
        PassFieldHidden.textProperty()
                .bindBidirectional(
                        PassField.textProperty()
                );

        // HIDE PASSWORD TEXTFIELD
        PassFieldHidden.setVisible(false);
        PassFieldHidden.setManaged(false);

        // COMBOBOX ROLE
        UserRole.setItems(
                FXCollections.observableArrayList(
                        roleList
                )
        );

        // HOVER BUTTON MASUK
        btnLogin.setOnMouseEntered(e -> {

            ScaleTransition st =
                    new ScaleTransition(
                            Duration.millis(200),
                            btnLogin
                    );

            st.setToX(1.1);
            st.setToY(1.1);

            st.play();
        });

        // HOVER BUTTON KELUAR
        btnLogin.setOnMouseExited(e -> {

            ScaleTransition st =
                    new ScaleTransition(
                            Duration.millis(200),
                            btnLogin
                    );

            st.setToX(1.0);
            st.setToY(1.0);

            st.play();
        });

        // LOGIN ACTION
        btnLogin.setOnAction(e -> {
            login();
        });
    }
}