package rental.ui.addarticle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import rental.ui.addarticle.ArticleAddController;
import rental.ui.listarticle.ArticleListController;
import rental.ui.listarticle.ArticleListController.Article;
import rental.alert.AlertMaker;
import rental.database.DatabaseHandler;
import rental.database.DatabaseHelper;

/**   
 * This class is used to add 
 * new article to the database
 * @author  Sukhraan Singh
 * @version 1.0
 * @since   2018-07-17 
 */

public class ArticleAddController implements Initializable {

    @FXML
    private JFXTextField title;
    @FXML
    private JFXTextField id;
    @FXML
    private JFXTextField description;
    @FXML
    private JFXTextField brand;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;
    
    DatabaseHandler databaseHandler;
    @FXML
    private AnchorPane rootPane;
    
    private Boolean isInEditMode = Boolean.FALSE;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        
        checkData();
    }  
    
    /**   
     * Method to add article to
     * table ARTICLE in the database
     * 
     */

    @FXML
    private void addArticle(ActionEvent event) {
    	 String articleID = id.getText();
         String articleDescription = description.getText();
         String articleTitle = title.getText();
         String articleBrand = brand.getText();
         
         
         if (articleID.isEmpty() || articleDescription.isEmpty() || articleTitle.isEmpty() || articleBrand.isEmpty())
         {
             Alert alert = new Alert(Alert.AlertType.ERROR);
             alert.setHeaderText(null);
             alert.setContentText("Please enter in all fields");
             alert.showAndWait();
             return;}
         
         if (!(isNumeric(articleID) && isAlphaNumeric(articleDescription) && isAlphaNumeric(articleTitle) && isAlphaNumeric(articleBrand)))
         {
             Alert alert = new Alert(Alert.AlertType.ERROR);
             alert.setHeaderText(null);
             alert.setContentText("Please enter valid values");
             
             if(!isNumeric(articleID))id.setText("");
             if(!isAlphaNumeric(articleDescription))description.setText(" ");
             if(!isAlphaNumeric(articleTitle))title.setText(" ");
             if(!isAlphaNumeric(articleBrand))brand.setText(" ");
             
             System.out.println(articleID);
             System.out.println(isNumeric(articleID));
             
             alert.showAndWait();         
             return;}
         
         if (DatabaseHelper.isArticleExists(articleID) && !isInEditMode ){
             
             AlertMaker.showSimpleAlert( "Duplicate article id", 
            		 "Article with same Article ID exists.\nPlease use new ID");
             return;
         }

         if (isInEditMode) {
             handleEditOperation();
             return;
         }

        
         Article ar = new Article(articleTitle,articleID,articleDescription,articleBrand,Boolean.TRUE);
         boolean result = DatabaseHelper.insertNewArticle(ar);
         if (result) {                     
         AlertMaker.showSimpleAlert("Success!!", "Article created.");
         clearEntries();        
          } else {
    	  AlertMaker.showSimpleAlert("Failed", "Could not create article.");}
         
    }
    
    private void clearEntries() {
        title.clear();
        id.clear();
        description.clear();
        brand.clear();
    }

    /**   
     * Method to close add screen
     * 
     */
    @FXML
    private void cancel(ActionEvent event) {
    	((Stage)cancelButton.getScene().getWindow()).close();
    	
    }
    
    /**   
     * Method to check if
     * data is alphanumeric
     * 
     */
    
    private boolean isAlphaNumeric(String s){
        String pattern= "[a-zA-Z0-9 ]*";
        return s.matches(pattern);
    }
    
    /**   
     * Method to check if
     * data is numeric
     * 
     */
    
    private boolean isNumeric(String s){
        String pattern= "^[0-9]*$";
        return s.matches(pattern);
    }
    
    /**   
     * Method to fetch data
     * from the database
     * 
     */
    private void checkData() {
        String qu = "SELECT title FROM ARTICLE";
        ResultSet rs = databaseHandler.execQuery(qu);
        try {
            while(rs.next()){
                String titlex = rs.getString("title");
                System.out.println(titlex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ArticleAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void fetchInfofrmUI(ArticleListController.Article ar) {
        
        id.setText(ar.getId());
        description.setText(ar.getDesc());
        title.setText(ar.getTitle());
        brand.setText(ar.getBrand());
        id.setEditable(false);
        isInEditMode = Boolean.TRUE;
    }
    
    private void handleEditOperation() {
    	ArticleListController.Article ar = 
    			new ArticleListController.Article(title.getText(), id.getText(), description.getText(), brand.getText(), true);
        if (databaseHandler.updateArticle(ar)) {
        	  AlertMaker.showSimpleAlert("Success!!", "Update completed.");
        	       	 
            
        } else {
        	  AlertMaker.showSimpleAlert("Failed", "Could not update data");
         
        }
    }
    
}
