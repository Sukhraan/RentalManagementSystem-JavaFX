package rental.ui.listarticle;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import rental.alert.AlertMaker;
import rental.database.DatabaseHandler;
import rental.ui.addarticle.ArticleAddController;
import rental.ui.main.MainController;

/**   
 * This class is used to connect to the database
 * and handles all database related queries
 * such as creating , adding, removing & updating data
 * in the system
 * @author  Jasleen Tung
 * @version 1.0
 * @since   2018-07-09 
 */
public class ArticleListController implements Initializable {

    ObservableList<Article> list = FXCollections.observableArrayList();

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<Article> tableView;
    @FXML
    private TableColumn<Article, String> titleCol;
    @FXML
    private TableColumn<Article, String> idCol;
    @FXML
    private TableColumn<Article, String> descCol;
    @FXML
    private TableColumn<Article, String> brandCol;
    @FXML
    private TableColumn<Article, Boolean> availabilityCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }

    /**   
     * Method to add column headers 
     */
    private void initCol() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("desc"));
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availabilty"));
    }
    
    /**   
     * Method to load data from
     * table ARTICLE in the database 
     */

    private void loadData() {
    	 list.clear();
        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM ARTICLE";
        ResultSet rs = handler.execQuery(qu);
        try {
            while(rs.next()){
                String titlex = rs.getString("title");
                String desc = rs.getString("description");
                String id = rs.getString("id");
                String brand = rs.getString("brand");
                Boolean avail = rs.getBoolean("isAvail");
                //System.out.println(desc);
                Article article = new Article(titlex, id, desc, brand, avail);
                list.add(article);
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(ArticleAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       // tableView.getItems().setAll(list);
        tableView.setItems(list);
        
        
        
    } 
    
    /**   
     * Method to delete data from
     * table ARTICLE in the database 
     */
    
    @FXML
    private void deleteArtice(ActionEvent event) {
        //Fetch the selected row
        Article selectedarticle = tableView.getSelectionModel().getSelectedItem();
        if (selectedarticle == null) {
            AlertMaker.showErrorMessage("No article selected", "Please select a article for deletion.");
            return;
        }
        if (DatabaseHandler.getInstance().isArticleIssued(selectedarticle)) {
            AlertMaker.showErrorMessage("Cant be deleted", "This article is already issued and cant be deleted.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting article");
        alert.setContentText("Are you sure want to delete the article " + selectedarticle.getTitle() + " ?");
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK) {
            Boolean result = DatabaseHandler.getInstance().deleteArticle(selectedarticle);
            if (result) {
                AlertMaker.showSimpleAlert("Article deleted", selectedarticle.getTitle() + " was deleted successfully.");
                list.remove(selectedarticle);
            } else {
                AlertMaker.showSimpleAlert("Failed", selectedarticle.getTitle() + " could not be deleted");
            }
        } else {
            AlertMaker.showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
        }
    }
    /**   
     * Method to update data in
     * table ARTICLE in the database 
     */
    @FXML
    private void editArticle(ActionEvent event) {
        //Fetch the selected row
        Article selectedArticle = tableView.getSelectionModel().getSelectedItem();
        if (selectedArticle == null) {
            AlertMaker.showErrorMessage("No article selected", "Please select an article for edit.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rental/ui/addarticle/article_add.fxml"));
            Parent parent = loader.load();

            ArticleAddController controller = (ArticleAddController) loader.getController();
            controller.fetchInfofrmUI(selectedArticle);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Article");
            stage.setScene(new Scene(parent));
            stage.show();
                stage.setOnCloseRequest((e) -> {
                refreshArticleList(new ActionEvent());
            });

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**   
     * Method to refresh the tableView i.e.
     * get data from
     * table ARTICLE in the database 
     */
    @FXML
    public void refreshArticleList(ActionEvent event)
    {
    	loadData();
    }
   
    /**   
     * Wrapper class for Article object
     * 
     */

    public static class Article {

        private final SimpleStringProperty title;
        private final SimpleStringProperty id;
        private final SimpleStringProperty desc;
        private final SimpleStringProperty brand;
        private final SimpleBooleanProperty availabilty;

        public Article(String title, String id, String desc, String brand, Boolean avail) {
            this.title = new SimpleStringProperty(title);
            this.id = new SimpleStringProperty(id);
            this.desc = new SimpleStringProperty(desc);
            this.brand = new SimpleStringProperty(brand);
            this.availabilty = new SimpleBooleanProperty(avail);
        }

        public String getTitle() {
            return title.get();
        }

        public String getId() {
            return id.get();
        }

        public String getDesc() {
            return desc.get();
        }

        public String getBrand() {
            return brand.get();
        }

        public Boolean getAvailabilty() {
            return availabilty.get();
        }

    }

}
