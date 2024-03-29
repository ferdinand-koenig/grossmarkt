package grossmarkt.controller;

import static grossmarkt.controller.ControllerUtility.featureAlert;
import static grossmarkt.controller.ControllerUtility.switchScene;

import grossmarkt.application.Produzent;
import grossmarkt.controller.ControllerUtility.View;
import grossmarkt.maps.MapReference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;


/**
 * Controller for Produzent (Producer) View
 *
 * @author Gruppe 2: Clara, Ferdinand, Florian, Jonas
 * @version 1.0
 * @since 27.04.2021
 */
@SuppressWarnings({"DuplicatedCode", "unchecked"})
public class ProduzentController implements Controller {

  @FXML
  private TableView<Produzent> produzentenTableView;
  @FXML
  private TextField produzentenSearchTxtfield;
  @FXML
  private Button delBtn, addBtn, navLager, navStart, navKunde, navLieferant;

  private MapReference reference;

  public void setMapReference(MapReference reference) {
    this.reference = reference;
  }

  /**
   * initializes ProduzentHinzufügenController sets MapReference, init bar, sets up TableView, and
   * add listener to buttons
   *
   * @param reference global references to HashMaps
   */
  public void init(MapReference reference) {
    setMapReference(reference);
    initEvents();
    setUpTableView();

    delBtn.setOnAction(event -> deleteProduzenten(
            new ArrayList<>(produzentenTableView.getSelectionModel().getSelectedItems())));

    Image image = new Image("https://img.icons8.com/metro/344/ffffff/delete.png", 16, 16, true, true);
    ImageView imageView = new ImageView(image);
    delBtn.setGraphic(imageView);

    addBtn.setOnAction(event -> showProduzent(null));
  }

  /**
   * Function to call dialog to manipulate or add instance
   *
   * @param produzent Current instance or null
   */
  public void showProduzent(Produzent produzent) {
    Parent root;
    ProduzentPopUpController produzentPopUpController;
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("../ProduzentPopUp.fxml"));
      root = loader.load();
      produzentPopUpController = loader.getController();
      produzentPopUpController.init(reference);
      produzentPopUpController.setUp(produzent);
      Stage addStage = new Stage();
      addStage.setScene(new Scene(root, 600, 420));
      addStage.setResizable(false);
      addStage.initModality(Modality.APPLICATION_MODAL);
      addStage.initOwner(navKunde.getScene().getWindow());
      addStage.getIcons().addAll(((Stage) navKunde.getScene().getWindow()).getIcons());
      addStage.showAndWait();

      safeTableViewRefresh();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private void setUpTableView() {
    TableColumn<Produzent, String> lNummer = new TableColumn<>("Produzentennummer"),
        lVorname = new TableColumn<>("Vorname"),
        lNachname = new TableColumn<>("Nachname"),
        lAdresse = new TableColumn<>("Geschäftsadresse"),
        lPreisliste = new TableColumn<>("Preisliste");
    lNummer.setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getId()));
    lVorname.setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getVorname()));
    lNachname.setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getNachname()));
    lAdresse.setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getAdressString()));

    lPreisliste.setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getLinkPreisliste()));

    produzentenTableView.getColumns().addAll(lNummer, lVorname, lNachname, lAdresse, lPreisliste);
    produzentenTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    filterProduzentenAndSetUpSearch().forEach(produzent -> produzentenTableView.getItems().add(produzent));

    produzentenTableView.setRowFactory(tv -> {
      TableRow<Produzent> row = new TableRow<>();
      row.setOnMouseClicked(event -> {
        if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
            && event.getClickCount() == 2) {

          Produzent clickedProduzent = row.getItem();
          showProduzent(clickedProduzent);
        }
      });
      return row;
    });

    lNachname.setSortType(SortType.ASCENDING);
    produzentenTableView.getSortOrder().add(lNachname);
    produzentenTableView.sort();
  }

  private FilteredList<Produzent> filterProduzentenAndSetUpSearch() {
    FilteredList<Produzent> filteredProduzenten = filterProduzenten();

    produzentenSearchTxtfield.textProperty().addListener((observable, oldValue, newValue) -> {
          setPredicate(filteredProduzenten, newValue);
          produzentenTableView.getItems().clear();
          filteredProduzenten.forEach(produzent -> produzentenTableView.getItems().add(produzent));
        });

    return filteredProduzenten;
  }

  private FilteredList<Produzent> filterProduzenten() {
    ObservableList<Produzent> observableProduzentList = FXCollections.observableArrayList();
    observableProduzentList.addAll(reference.getProduzentMap().getProduzentHashMap().values());
    return new FilteredList<>(observableProduzentList, p -> true);
  }

  private void setPredicate(FilteredList<Produzent> filteredProduzenten, String newValue) {
    filteredProduzenten.setPredicate(produzent -> {
      if (newValue == null || newValue.isEmpty()) {
        return true;
      }
      if (produzent.getVorname().toLowerCase().contains(newValue.toLowerCase()) ||
          produzent.getNachname().toLowerCase().contains(newValue.toLowerCase()) ||
          produzent.getVorname().concat(" ").concat(produzent.getNachname()).toLowerCase()
              .contains(newValue.toLowerCase())) {
        return true;
      }
      return Integer.toString(produzent.getId()).contains(newValue);
    });
  }

  private void deleteProduzenten(ArrayList<Produzent> produzents) {
    if (produzents.size() == 0) {
      return;
    }

    Alert alert = new Alert(AlertType.NONE);
    alert.setTitle("Möchten Sie die Produzenten unwiderruflich löschen?");
    AtomicReference<String> content = new AtomicReference<>("Ausgewählte Produzenten:");
    produzents.forEach(produzent -> content.set(content.get().concat(String
            .format("\n\t• %d  %s %s", produzent.getId(), produzent.getVorname(), produzent.getNachname()))));
    alert.setContentText(content.get()
            .concat("\n\nTipp: Es können auch mehrere Produzenten mit STRG + Klick ausgewählt werden."));
    alert.initOwner(delBtn.getScene().getWindow());

    alert.setHeaderText(null);
    ButtonType buttonTypeCancel = new ButtonType("Nein", ButtonData.CANCEL_CLOSE);
    ButtonType buttonTypeAgree = new ButtonType("Ja", ButtonData.NEXT_FORWARD);

    alert.getButtonTypes().setAll(buttonTypeCancel, buttonTypeAgree);

    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.setStyle("-fx-background-color: #282c34;");
    dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white");
    dialogPane.setMinWidth(500);

    if (alert.showAndWait().get().getButtonData() == ButtonData.NEXT_FORWARD) {
      produzents.forEach(produzent -> reference.getProduzentMap().deleteProduzent(produzent.getId()));
      safeTableViewRefresh();
    }
  }

  private void initEvents() {
    EventHandler<ActionEvent> featureAlert = event -> featureAlert(navStart.getScene().getWindow());
    navKunde.setOnAction(featureAlert);

    navStart.setOnAction(event -> switchScene(View.HOME, navStart.getScene(), getClass(), reference));
    navLager.setOnAction(event -> switchScene(View.LAGER, navLager.getScene(), getClass(), reference));
    navLieferant.setOnAction(event -> switchScene(View.LIEFERANT, navLieferant.getScene(), getClass(), reference));
  }

  private void safeTableViewRefresh() {
    FilteredList<Produzent> filteredProduzenten = filterProduzenten();
    setPredicate(filteredProduzenten, produzentenSearchTxtfield.getText());

    produzentenSearchTxtfield.textProperty().addListener((observable, oldValue, newValue) -> {
          setPredicate(filteredProduzenten, newValue);
          produzentenTableView.getItems().clear();
          filteredProduzenten.forEach(produzent -> produzentenTableView.getItems().add(produzent));
        });
    produzentenTableView.getItems().clear();
    filteredProduzenten.forEach(produzent -> produzentenTableView.getItems().add(produzent));
    produzentenTableView.sort();
  }
}
