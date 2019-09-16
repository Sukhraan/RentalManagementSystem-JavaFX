package rental.ui.listmember;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import rental.alert.AlertMaker;
import rental.database.DatabaseHandler;
import rental.ui.addarticle.ArticleAddController;
import rental.ui.addmember.MemberAddController;
import rental.ui.listarticle.ArticleListController;

/**   
 * This class is the controller class for 
 * member list window
 * @author  Jasleen Tung
 * @version 1.0
 * @since   2018-07-16
 */

public class MemberListController implements Initializable {

    ObservableList<Member> list = FXCollections.observableArrayList();

    @FXML
    private TableView<Member> tableView;
    @FXML
    private TableColumn<Member, String> nameCol;
    @FXML
    private TableColumn<Member, String> idCol;
    @FXML
    private TableColumn<Member, String> mobileCol;
    @FXML
    private TableColumn<Member, String> emailCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }

    private void initCol() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    }
    
    /**   
     * Method to load member
     * data from database in the window
     */
        private void loadData() {
        list.clear();
        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM MEMBER";
        ResultSet rs = handler.execQuery(qu);
        try {
            while (rs.next()) {
                String name = rs.getString("name");
                String mobile = rs.getString("mobile");
                String id = rs.getString("id");
                String email = rs.getString("email");
               
                list.add(new Member(name, id, mobile, email));
               
            }
        } catch (SQLException ex) {
            Logger.getLogger(ArticleAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        tableView.setItems(list);
    }
        
        /**   
         * Method to delete member
         * data from database in the window
         */
        @FXML
        private void deleteMember(ActionEvent event) {
            //Fetch the selected row
            MemberListController.Member selectedmember = tableView.getSelectionModel().getSelectedItem();
            if (selectedmember == null) {
            	AlertMaker.showErrorMessage("No member selected", "Please select a member.");
                return;
            }
            if (DatabaseHandler.getInstance().isMemberHasAnyBooks(selectedmember)) {
                AlertMaker.showErrorMessage("Cant be deleted", "This member has some books.");
                return; }
           
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Deleting member");
            alert.setContentText("Are you sure want to delete " + selectedmember.getName() + " ?");
            Optional<ButtonType> answer = alert.showAndWait();
            if (answer.get() == ButtonType.OK) {
                Boolean result = DatabaseHandler.getInstance().deleteMember(selectedmember);
                if (result) {
                    AlertMaker.showSimpleAlert("Member deleted", selectedmember.getName() + " was deleted successfully.");
                    list.remove(selectedmember);
                } else {
                    AlertMaker.showSimpleAlert("Failed", selectedmember.getName() + " could not be deleted");
                }
            } else {
                AlertMaker.showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
            }
        }
        /**   
         * Method to edit member
         * data in database
         */
        
        @FXML
        private void editMember(ActionEvent event) {
            //Fetch the selected row
            Member selectedForEdit = tableView.getSelectionModel().getSelectedItem();
            if (selectedForEdit == null) {
                AlertMaker.showErrorMessage("No member selected", "Please select a member for edit.");
                return;
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/rental/ui/addmember/member_add.fxml"));
                Parent parent = loader.load();

                MemberAddController controller = (MemberAddController) loader.getController();
                controller.infalteUI(selectedForEdit);

                Stage stage = new Stage(StageStyle.DECORATED);
                stage.setTitle("Edit Member");
                stage.setScene(new Scene(parent));
                stage.show();
         

                stage.setOnCloseRequest((e) -> {
                    handleRefresh(new ActionEvent());
                });

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        
      

        @FXML
        private void handleRefresh(ActionEvent event) {
            loadData();
        }

        /**   
         * Wrapper class for member object
         */


    public static class Member {

        private final SimpleStringProperty name;
        private final SimpleStringProperty id;
        private final SimpleStringProperty mobile;
        private final SimpleStringProperty email;

        public Member(String name, String id, String mobile, String email) {
            this.name = new SimpleStringProperty(name);
            this.id = new SimpleStringProperty(id);
            this.mobile = new SimpleStringProperty(mobile);
            this.email = new SimpleStringProperty(email);
        }

        public String getName() {
            return name.get();
        }

        public String getId() {
            return id.get();
        }

        public String getMobile() {
            return mobile.get();
        }

        public String getEmail() {
            return email.get();
        }

    }

}
