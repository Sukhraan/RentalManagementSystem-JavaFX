package rental.ui.main;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;

import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import rental.database.DatabaseHandler;
import rental.ui.main.MainController;
import org.controlsfx.control.textfield.TextFields;

/**   
 * This class contains methods 
 * for the main window and connects 
 * other screens to the main screen
 * @author  Sukhraan Singh
 * @version 1.0
 * @since   2018-07-16 
 */
public class MainController  implements Initializable {

	@FXML
	private HBox article_info;
	@FXML
	private HBox member_info;
	 @FXML
	 private TextField articleIDInput;
	 @FXML
	 private Text articleName;
	 @FXML
	 private Text articleDesc;
	 @FXML
	 private Text articleStatus;
	
	 DatabaseHandler databaseHandler;
	 
	 @FXML
	 private TextField memberIDInput;
	  @FXML
	  private Text memberName;
	  @FXML
	  private Text memberMobile;
	  
	  @FXML
	  private JFXTextField articleID;
	  
	  @FXML
	  private ListView<String> issueDataList;
	  
	  Boolean isReadyForSubmission = false;
	  @FXML
	    private StackPane rootPane;
	  
	    private ArrayList<String>possibleSuggestions = null;
	
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    	JFXDepthManager.setDepth(article_info, 1);
    	JFXDepthManager.setDepth(member_info, 1);
    	
    	databaseHandler = DatabaseHandler.getInstance();
    	
    	possibleSuggestions = databaseHandler.execArticle();
    	if(possibleSuggestions!=null)TextFields.bindAutoCompletion(articleID, possibleSuggestions);
    	
    }

    /**   
     * Method to launch add
     * member screen
     * 
     */
    @FXML
    private void loadAddMember(ActionEvent event) {
        loadWindow("/rental/ui/addmember/member_add.fxml", "Add New Member");
    }
    
    /**   
     * Method to launch add
     * article screen
     * 
     */

    @FXML
    private void loadAddArticle(ActionEvent event) {
        loadWindow("/rental/ui/addarticle/article_add.fxml", "Add New Article");
    }

    /**   
     * Method to launch load
     * member table
     * 
     */

    @FXML
    private void loadMemberTable(ActionEvent event) {
        loadWindow("/rental/ui/listmember/member_list.fxml", "Member List");
    }
    
    /**   
     * Method to launch load
     * article table
     * 
     */

    @FXML
    private void loadArticleTable(ActionEvent event) {
        loadWindow("/rental/ui/listarticle/article_list.fxml", "Article List");

    }
    
    /**   
     * Method to launch 
     * settings screen
     * 
     */
    @FXML
    private void loadSettings(ActionEvent event) {
        loadWindow("/rental/settings/settings.fxml", "Settings");
    }

    /**   
     * Method to launch 
     * main window
     * 
     */
    void loadWindow(String loc, String title) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(loc));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void clearBookCache()
    {
        articleName.setText("");
        articleDesc.setText("");
        articleStatus.setText("");
    }
    
    void clearMemberCache()
    {
        memberName.setText("");
        memberMobile.setText("");
    }
    
    /**   
     * Method to load selected
     * article info on screen
     * 
     */
    

    @FXML
    private void loadArticleInfo(ActionEvent event) {
    	clearBookCache();
        
        String id  = articleIDInput.getText();
        String qu = "SELECT * FROM ARTICLE WHERE id = '" + id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;
        try {
            while(rs.next())
            {
                String bName = rs.getString("title");
                String bDesc = rs.getString("description");
                Boolean bStatus = rs.getBoolean("isAvail");
                
                articleName.setText(bName);
                articleDesc.setText(bDesc);
                String status = (bStatus)?"Available" : "Not Available";
                articleStatus.setText(status);
                
                flag = true;
            }
            
            if(!flag){
                articleName.setText("No Such Article Available");
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**   
     * Method to load selected
     * member info on screen
     * 
     */
    
    @FXML
    private void loadMemberInfo(ActionEvent event) {
        clearMemberCache();
        
        String id  = memberIDInput.getText();
        String qu = "SELECT * FROM MEMBER WHERE id = '" + id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;
        try {
            while(rs.next())
            {
                String mName = rs.getString("name");
                String mMobile = rs.getString("mobile");
                
                memberName.setText(mName);
                memberMobile.setText(mMobile);
                
                flag = true;
            }
            
            if(!flag){
                memberName.setText("No Such Member Available");
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**   
     * Method to issue
     * article to the member
     * 
     */
    
    
    @FXML
    private void loadIssueOperation(ActionEvent event) {
        String memberID = memberIDInput.getText();
        String articleID = articleIDInput.getText();
        
       if(memberID.equals("") || articleID.equals(""))
       {
    	   Alert alert1 = new Alert(Alert.AlertType.ERROR);
           alert1.setTitle("Failed");
           alert1.setHeaderText(null);
           alert1.setContentText("Input all fields.");
           alert1.showAndWait();
           articleIDInput.setText("");
           memberIDInput.setText("");
           return;
       }
        
  

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Issue Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure want to issue the article " + articleName.getText() + "\n to " + memberName.getText() + " ?");

        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK) {
            String str = "INSERT INTO ISSUE(memberID,articleID) VALUES ("
                    + "'" + memberID + "',"
                    + "'" + articleID + "')";
            String str2 = "UPDATE ARTICLE SET isAvail = false WHERE id = '" + articleID + "'";
            System.out.println(str + " and " + str2);

            if (databaseHandler.execAction(str) && databaseHandler.execAction(str2)) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Article Issue Complete");
                articleIDInput.setText("");
                articleName.setText("Article Name");
                articleDesc.setText("Description");
                articleStatus.setText("Description");
                memberIDInput.setText("");
                memberName.setText("Member Name");
                memberMobile.setText("Contact");
                alert1.showAndWait();
                
                
            } else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Issue Operation Failed");
                articleIDInput.setText("");
                articleName.setText("Article Name");
                articleDesc.setText("Description");
                articleStatus.setText("Description");
                memberIDInput.setText("");
                memberName.setText("Member Name");
                memberMobile.setText("Contact");
                alert1.showAndWait();
            }
        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Cancelled");
            alert1.setHeaderText(null);
            alert1.setContentText("Issue Operation cancelled");
            alert1.showAndWait();
        }
    }
    
    
    
    private String fetchIdFromTitle(String t)
    {
    	  String artID = null;
          String ex = "SELECT * FROM ARTICLE WHERE title = '" + t + "'";
          ResultSet rse = databaseHandler.execQuery(ex);
          try {
              while (rse.next()) {
              	System.out.println(rse.getString("isAvail"));
              	if( rse.getString("isAvail")=="false") {artID =rse.getString("id"); }
              }
          } catch (SQLException sqex) {
              Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, sqex);
          }
          
          return artID;
    }
    /**   
     * Method to display
     * article information on renew/reissue screen
     * 
     */
     @FXML
    private void loadArticleInfo2(ActionEvent event) {
    	 
        ObservableList<String> issueData = FXCollections.observableArrayList();
        isReadyForSubmission = false;
        //changed search criteria to article title
      //  String title = articleID.getText();
        
        String artID = fetchIdFromTitle(articleID.getText());
        
        String qu = "SELECT * FROM ISSUE WHERE articleID = '" + artID + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        try {
            while (rs.next()) {
                String mArticleID = rs.getString("articleID");;
                String mMemberID = rs.getString("memberID");
                Timestamp mIssueTime = rs.getTimestamp("issueTime");
                int mRenewCount = rs.getInt("renew_count");

                issueData.add("Issue Date and Time :" + mIssueTime.toGMTString());
                issueData.add("Renew Count :" + mRenewCount);

                issueData.add("Article Information:-");
                qu = "SELECT * FROM ARTICLE WHERE ID = '" + mArticleID + "'";
                ResultSet r1 = databaseHandler.execQuery(qu);
                
                while (r1.next()) {
                    issueData.add("\tArticle Name :" + r1.getString("title"));
                    issueData.add("\tArticle ID :" + r1.getString("id"));
                    issueData.add("\tArticle Description :" + r1.getString("description"));
                    issueData.add("\tArticle Brand :" + r1.getString("brand"));
                }
                qu = "SELECT * FROM MEMBER WHERE ID = '" + mMemberID + "'";
                r1 = databaseHandler.execQuery(qu);
                issueData.add("Member Information:-");
                
                while (r1.next()) {
                    issueData.add("\tName :" + r1.getString("name"));
                    issueData.add("\tMobile :" + r1.getString("mobile"));
                    issueData.add("\tEmail :" + r1.getString("email"));
                }
                isReadyForSubmission = true;
            }
        } catch (SQLException sqex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, sqex);
        }
        
        issueDataList.getItems().setAll(issueData);
    }
     
    
    /**   
     * Method to return
     * article 
     * 
     */
    @FXML
    private void loadSubmissionOp(ActionEvent event) {
        if (!isReadyForSubmission) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Please select a article to submit");
            alert.showAndWait();
            articleID.setText("");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Submission Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure want to return the article ?");

        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK) {
        	  String id = fetchIdFromTitle(articleID.getText());
          //  String id = articleID.getText();
            String ac1 = "DELETE FROM ISSUE WHERE ARTICLEID = '" + id + "'";
            String ac2 = "UPDATE ARTICLE SET ISAVAIL = TRUE WHERE ID = '" + id + "'";

            if (databaseHandler.execAction(ac1) && databaseHandler.execAction(ac2)) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Article Has Been Returned");
                alert1.showAndWait();
                issueDataList.getItems().clear();
                articleID.setText("");
            } else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Return Failed");
                alert1.showAndWait();
                articleID.setText("");
            }
        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Cancelled");
            alert1.setHeaderText(null);
            alert1.setContentText("Submission Operation cancelled");
            alert1.showAndWait();
        }
    }
    

    /**   
     * Method to renew
     * article 
     * 
     */
    
    
    @FXML
    private void loadRenewOp(ActionEvent event) {
    	
    	
        if (!isReadyForSubmission) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Please select a article to renew");
            alert.showAndWait();
            articleID.setText("");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Renew Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure want to renew the article ?");

        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK) {
        	  String artID = fetchIdFromTitle(articleID.getText());
            String ac = "UPDATE ISSUE SET issueTime = CURRENT_TIMESTAMP, renew_count = renew_count+1 WHERE ARTICLEID = '" +  artID + "'";
            System.out.println(ac);
            if (databaseHandler.execAction(ac)) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Article Has Been Renewed");
                alert1.showAndWait();
            } else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Renew Has Been Failed");
                alert1.showAndWait();
            }
        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Cancelled");
            alert1.setHeaderText(null);
            alert1.setContentText("Renew Operation cancelled");
            alert1.showAndWait();
        }
    }
    

    /**   
     * Method to close main
     * window from menu
     * 
     */
    
    @FXML
    private void handleMenuClose(ActionEvent event) {
        ((Stage) rootPane.getScene().getWindow()).close();
    }
    
    /**   
     * Method to launch add article
     * screen from menu 
     * 
     */
    
    @FXML
    private void handleMenuAddArticle(ActionEvent event) {
    	loadWindow("/rental/ui/addarticle/article_add.fxml", "Add New Article");
    }

    /**   
     * Method to launch add member
     * screen from menu 
     * 
     */
    @FXML
    private void handleMenuAddMember(ActionEvent event) {
    	loadWindow("/rental/ui/addmember/member_add.fxml", "Add New Member");
    }
    /**   
     * Method to launch view article
     * screen from menu 
     * 
     */
    
    @FXML
    private void handleMenuViewArticle(ActionEvent event) {
    	loadWindow("/rental/ui/listarticle/article_list.fxml", "Article List");
    }

    /**   
     * Method to launch view member
     * screen from menu 
     * 
     */
    @FXML
    private void handleMenuViewMember(ActionEvent event) {
    	loadWindow("/rental/ui/listmember/member_list.fxml", "Member List");
    }    
    
    /**   
     * Method to maximize the main
     * screen from menu 
     * 
     */
    @FXML
    private void handleMenuFullScreen(ActionEvent event) {
        Stage stage = ((Stage) rootPane.getScene().getWindow());
        stage.setFullScreen(!stage.isFullScreen());
    }
    
    

    
}
