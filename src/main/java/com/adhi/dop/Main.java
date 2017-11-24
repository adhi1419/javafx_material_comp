package com.adhi.dop;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {

	private List<Material> materialList;
	private List<String> properties;
	private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) throws Exception {

		this.primaryStage = primaryStage;

		GridPane gp = new GridPane();
		gp.setMinSize(400, 200);
		gp.setPadding(new Insets(10, 10, 10, 10));
		gp.setVgap(5);
		gp.setHgap(5);
		gp.setAlignment(Pos.TOP_CENTER);

		Label fileStatus = new Label("No file selected!");
		fileStatus.setMinWidth(400);
		Button buttonLoad = new Button("Load");

		VBox contentsMain = new VBox();
		contentsMain.setPadding(new Insets(10, 10, 10, 10));
		contentsMain.setSpacing(0);
		contentsMain.setAlignment(Pos.CENTER);

		HBox fileLoader = new HBox(fileStatus, buttonLoad);
		gp.addRow(0, fileLoader);
		gp.addRow(1, contentsMain);

		buttonLoad.setOnAction(event -> {

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select Data File");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
			fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Microsoft Excel Files", "*.xls*"),
					new FileChooser.ExtensionFilter("All Files", "*.*"));
			File selectedFile = fileChooser.showOpenDialog(null);

			if (selectedFile != null) {
				fileStatus.setText("File Selected: " + selectedFile.getAbsolutePath());
				try {
					loadContent(contentsMain, selectedFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		Scene scene = new Scene(gp);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

	void loadContent(VBox vbox, File file) throws Exception {
		FileInputStream excelFile = new FileInputStream(file);
		Workbook workbook = new XSSFWorkbook(excelFile);
		Sheet datatypeSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = datatypeSheet.iterator();
		properties = new ArrayList<String>();
		materialList = new ArrayList<Material>();
		Material m = null;

		while (iterator.hasNext()) {

			Row currentRow = iterator.next();
			Iterator<Cell> cellIterator = currentRow.iterator();

			while (cellIterator.hasNext()) {
				Cell currentCell = cellIterator.next();

				if (currentCell.getRowIndex() == 0 && currentCell.getColumnIndex() != 0) {
					properties.add(currentCell.getStringCellValue());
				}

				if (currentCell.getRowIndex() != 0 && currentCell.getColumnIndex() <= properties.size()) {
					if (currentCell.getColumnIndex() == 0)
						m = new Material(currentCell.getStringCellValue());
					else {
						switch (currentCell.getCellTypeEnum()) {
						case BLANK:
							m.addProperty(properties.get(currentCell.getColumnIndex() - 1), "N/A");
							break;
						case NUMERIC:
							m.addProperty(properties.get(currentCell.getColumnIndex() - 1),
									"" + currentCell.getNumericCellValue());
							break;
						case STRING:
							m.addProperty(properties.get(currentCell.getColumnIndex() - 1),
									currentCell.getStringCellValue());
							break;
						default:
							throw (new Exception("The cell can contain only String or Numeric values"));

						}
					}
				}
			}

			if (currentRow.getRowNum() != 0)
				materialList.add(m);

		}

		populateMainArea(vbox);

		workbook.close();
	}

	private void populateMainArea(VBox vbox) {
		HBox comboBoxes = new HBox();
		comboBoxes.setSpacing(5);

		final ComboBox<Material> comboBox1 = new ComboBox<>();
		final ComboBox<Material> comboBox2 = new ComboBox<>();

		for (Material m : materialList) {
			comboBox1.getItems().add(m);
			comboBox2.getItems().add(m);
		}

		comboBox1.setValue(materialList.get(0));
		comboBox2.setValue(materialList.get(1));

		comboBoxes.getChildren().addAll(comboBox1, comboBox2);

		TableView<TableRow> table = new TableView<>();
		table.setEditable(true);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		TableColumn<TableRow, String> propertyNameCol = new TableColumn<>("Property");
		propertyNameCol.setCellValueFactory(new PropertyValueFactory<TableRow, String>("propertyName"));
		TableColumn<TableRow, String> material1Col = new TableColumn<>("Material 1");
		material1Col.setCellValueFactory(new PropertyValueFactory<TableRow, String>("material1Val"));
		TableColumn<TableRow, String> material2Col = new TableColumn<>("Material 2");
		material2Col.setCellValueFactory(new PropertyValueFactory<TableRow, String>("material2Val"));
		table.getColumns().add(propertyNameCol);
		table.getColumns().add(material1Col);
		table.getColumns().add(material2Col);
		table.setItems(getTableRowList(materialList.get(0), materialList.get(1)));
		primaryStage.sizeToScene();

		comboBox1.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
			table.setItems(getTableRowList(ov.getValue(), comboBox2.getValue()));
		});

		comboBox2.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
			table.setItems(getTableRowList(comboBox1.getValue(), ov.getValue()));
		});

		vbox.setSpacing(5);
		vbox.getChildren().clear();
		vbox.getChildren().addAll(comboBoxes, table);

	}

	private ObservableList<TableRow> getTableRowList(Material m1, Material m2) {
		ObservableList<TableRow> data = FXCollections.observableArrayList();

		for (int i = 0; i < properties.size(); i++) {
			String propertyName = properties.get(i);
			data.add(new TableRow(propertyName, m1.getPropertyValue(propertyName), m2.getPropertyValue(propertyName)));
		}

		return data;
	}

}
