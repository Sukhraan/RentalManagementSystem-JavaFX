package rental.ui.main;


import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rental.database.DatabaseHandler;


/**   
 * This class is used to launch the login
 * screen
 * @author  Sukhraan Singh
 * @version 1.0
 * @since   2018-07-15
 */

public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/rental/ui/login/login.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Login");
        
        new Thread(new Runnable() {

			@Override
			public void run() {
				
				DatabaseHandler.getInstance();
			}}).start();
        
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
