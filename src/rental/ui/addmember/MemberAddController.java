package rental.ui.addmember;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import rental.ui.listmember.MemberListController.Member;
import rental.alert.AlertMaker;
import rental.ui.listmember.MemberListController;
import rental.database.DatabaseHandler;
import rental.database.DatabaseHelper;

/**   
 * This class is controller class
 * for adding member in
 * the system
 * @author  Sukhraan Singh
 * @version 1.0
 * @since   2018-07-20
 */
public class MemberAddController implements Initializable {

    DatabaseHandler handler;

    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField id;
    @FXML
    private JFXTextField mobile;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;
    
    private Boolean isInEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();
    }
    /**   
     * Method close the add member screen
     */
    @FXML
    private void cancel(ActionEvent event) {
    	((Stage)cancelButton.getScene().getWindow()).close();
    }
    
    /**   
     * Method to add member data 
     * in the database
     */

    @FXML
    private void addMember(ActionEvent event) {
        String mName = name.getText();
        String mID = id.getText();
        String mMobile = mobile.getText();
        String mEmail = email.getText();

        Boolean flag = mName.isEmpty() || mID.isEmpty() || mMobile.isEmpty() || mEmail.isEmpty();
        if (flag) {
        	Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Enter in all fields");
            alert.showAndWait();
            return;
        }
        
        Boolean inputValFlag = !(isAlphabets(mName) && isNumeric(mID) && isNumeric(mMobile) && isValidemail(mEmail));
        if (inputValFlag) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please fill valid data");
            if(!isAlphabets(mName))name.setText("");
            if(!isNumeric(mID))id.setText(" ");
            if(!isNumeric(mMobile))mobile.setText(" ");
            if(!isValidemail(mEmail))email.setText(" ");
            alert.showAndWait();
            return;
        }

        if (DatabaseHelper.isMemberExists(mID) && !isInEditMode) {
            AlertMaker.showSimpleAlert( "Duplicate member id", "Member with same id exists.\nPlease use new ID");
            return;
        }

        if (isInEditMode) {
        	updateMember();
            return;
        }

        Member member = new Member(mName, mID, mMobile, mEmail);
        boolean result = DatabaseHelper.insertNewMember(member);
        if (result) {
        	AlertMaker.showSimpleAlert( "New member added", mName + " has been added");
            clearEntries();
        } else {
        	AlertMaker.showSimpleAlert( "Failed to add new member", "Check you entries and try again.");
        }
    }
    
    /**   
     * Method to update member data 
     */
    private void updateMember() {
        Member member = new MemberListController.Member(name.getText(), id.getText(), mobile.getText(), email.getText());
        if (DatabaseHandler.getInstance().updateMember(member)) {
        	AlertMaker.showSimpleAlert( "Success", "Member data updated.");
        } else {
        	AlertMaker.showSimpleAlert( "Failed", "Cant update member.");
        }
    }
    
    /**   
     * Method to clear the inputs
     */
    private void clearEntries() {
        name.clear();
        id.clear();
        mobile.clear();
        email.clear();
    }
    
    /**   
     * Method to check for alphanumeric data 
     */
    private boolean isAlphabets(String s){
        String pattern= "[a-zA-Z ]*";
        return s.matches(pattern);
    }
    
    /**   
     * Method to check for numeric data 
     */
    private boolean isNumeric(String s){
        String pattern= "^[0-9]*$";
        return s.matches(pattern);
    }
    
    /**   
     * Method to check for email pattern 
     */
    private boolean isValidemail(String s){
    	   String pattern= "^[a-zA-Z0-9_!#$%&�*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    	   return s.matches(pattern);
    }
    /**   
     * Method to fetch data from UI
     */
    public void infalteUI(MemberListController.Member member) {
        name.setText(member.getName());
        id.setText(member.getId());
        id.setEditable(false);
        mobile.setText(member.getMobile());
        email.setText(member.getEmail());
        isInEditMode = Boolean.TRUE;
        
    }

}
