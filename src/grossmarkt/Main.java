package grossmarkt;

import grossmarkt.controller.HomeController;
import grossmarkt.maps.MapReference;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class Main extends Application {

  private static final MapReference MAP_REFERENCE = new MapReference();

  @Override
  public void start(Stage homeStage) throws Exception{
    //Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));

    FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
    Parent root = loader.load();

    HomeController homeController = loader.getController();
    homeController.setMapReference(MAP_REFERENCE);

    homeStage.setTitle("Obst & Gemüse Meier oHG Verwaltung");
    homeStage.setMinHeight(576);
    homeStage.setMinWidth(1024);
    homeStage.setScene(new Scene(root, homeStage.getMinWidth(), homeStage.getMinHeight()));
    homeStage.getIcons().add(new Image("https://img.icons8.com/cotton/344/vegetables-box--v1.png"));
    homeStage.show();
  }


  public static void main(String[] args) {
    launch(args);
  }
}