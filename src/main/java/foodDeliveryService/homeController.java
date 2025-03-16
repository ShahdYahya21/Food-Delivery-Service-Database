package foodDeliveryService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class homeController {
    @FXML
    void goToMenuTable(MouseEvent mouseEvent) {
        try {
            // Load the FXML file for the menu item table
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuItem.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage currentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

            // Create a new scene with the loaded FXML content
            Scene scene = new Scene(root);

            // Set the scene on the current stage and show it
            currentStage.setScene(scene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
