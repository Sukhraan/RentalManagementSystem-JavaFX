package rental.ui.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import rental.settings.Preferences;
import rental.ui.main.MainController;
import org.apache.commons.codec.digest.DigestUtils;


/**   
 * This class controller class
 * for Login fucntionality
 * @author  Sukhraan Singh
 * @version 1.0
 * @since   2018-07-13 
 */
public class LoginController implements Initializable {

    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;

    Preferences preference;
    @FXML
    private Label titleLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        preference = Preferences.getPreferences();
    }
    
    /**   
     * Method to check for input 
     * validations 
     * 
     */

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        titleLabel.setText("Login");
        titleLabel.setStyle("-fx-background-color:black;-fx-text-fill:white");

        String uname = username.getText();
        String pword = DigestUtils.shaHex(password.getText());

        if (uname.equals(preference.getUsername()) && pword.equals(preference.getPassword())) {
            closeStage();
            loadMain();
        } else {
            titleLabel.setText("Invalid Credentails");
            titleLabel.setStyle("-fx-background-color:#d32f2f;-fx-text-fill:white");
            username.setText("");
            password.setText("");
        }

    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        System.exit(0);
    }

    private void closeStage() {
        ((Stage) username.getScene().getWindow()).close();
    }

    /**   
     * Method to launch main screen
     * 
     */
    void loadMain() {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/rental/ui/main/main.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Rental Management System");
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
