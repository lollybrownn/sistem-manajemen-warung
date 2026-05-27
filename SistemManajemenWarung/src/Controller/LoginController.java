/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author ASUS
 */
public class LoginController implements Initializable {

    @FXML
    private TextField UserField;
    @FXML
    private Button btnLogin;
    @FXML
    private PasswordField PassField;
    @FXML
    private TextField PassFieldHidden;
    @FXML
    private CheckBox checkPass;
    @FXML
    private ComboBox<String> UserRole;
    
    
    
    private String[] UserList = {"Admin", "Kasir"};
    
    public void showPasswordAction(ActionEvent event) {
        if(checkPass.isSelected()){
            PassFieldHidden.setText(PassField.getText());
            
            PassFieldHidden.setVisible(true);
            PassField.setVisible(false);
        } else {
            PassField.setText(PassFieldHidden.getText());
            
            PassField.setVisible(true);
            PassFieldHidden.setVisible(false);
        }
    }
    
    public void LoginAs() {
        List<String> UList = new ArrayList<>();
        
        for(String User: UserList){
            UList.add(User);
        }
        
        ObservableList listUser = FXCollections.observableArrayList(UList);
        UserRole.setItems(listUser);
    }

    /**
     * Initializes the controller class.
     */
    
    
    public void Login(){
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnLogin.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), btnLogin);
            st.setToX(1.1);
            st.setToY(1.1);
            st.play();
        });
        
        btnLogin.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), btnLogin);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
        
        LoginAs();
    }    
    
}
