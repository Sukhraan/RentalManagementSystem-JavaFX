package rental.settings;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import rental.settings.Preferences;

/**   
 * This class is the controller class
 * for the system settings such as 
 * password and admin name
 * @author  Sukhraan Singh
 * @version 1.0
 * @since   2018-07-18
 */
public class SettingsController implements Initializable {

    @FXML
    private JFXTextField nDaysWithoutFine;
    @FXML
    private JFXTextField finePerDay;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    	initDefaultValues();
    	
    }    

    /**   
     * Method to save new admin name and password
     * in the database
     */
    
    @FXML
    private void handleSaveButtonAction(ActionEvent event) {
    	
    	 int ndays = Integer.parseInt(nDaysWithoutFine.getText());
         float fine = Float.parseFloat(finePerDay.getText());
         String uname = username.getText();
         String pass = password.getText();
         
         Preferences preferences = Preferences.getPreferences();
         preferences.setnDaysWithoutFine(ndays);
         preferences.setFinePerDay(fine);
         preferences.setUsername(uname);
         preferences.setPassword(pass);
         
         Preferences.writePreferenceToFile(preferences);
    	
    }
    
    /**   
     * Method to close
     * the Settings window
     * 
     */
    

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
    	((Stage)nDaysWithoutFine.getScene().getWindow()).close();
    }
    
    /**   
     * Method to fetch default 
     * Preferences values
     * 
     */
    
    private void initDefaultValues() {
        Preferences preferences = Preferences.getPreferences();
        nDaysWithoutFine.setText(String.valueOf(preferences.getnDaysWithoutFine()));
        finePerDay.setText(String.valueOf(preferences.getFinePerDay()));
        username.setText(String.valueOf(preferences.getUsername()));
        password.setText(String.valueOf(preferences.getPassword()));
    }
    
}
