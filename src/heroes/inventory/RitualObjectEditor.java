/*
 * Copyright 2017 DSATool team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package heroes.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dsa41basis.inventory.Ritual;
import dsa41basis.inventory.RitualObject;
import dsa41basis.util.DSAUtil;
import dsatool.gui.GUIUtil;
import dsatool.resources.ResourceManager;
import dsatool.util.ErrorLogger;
import dsatool.util.GraphicTableCell;
import dsatool.util.ReactiveSpinner;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import jsonant.value.JSONArray;
import jsonant.value.JSONObject;

public class RitualObjectEditor {
	@FXML
	private VBox root;
	@FXML
	private HBox materialBox;
	@FXML
	private VBox typeBox;
	@FXML
	private HBox volumeBox;
	@FXML
	private TextField name;
	@FXML
	private TextField notes;
	@FXML
	private Button okButton;
	@FXML
	private ReactiveSpinner<Double> weight;
	@FXML
	private ReactiveSpinner<Integer> volume;
	@FXML
	private ComboBox<String> material;
	@FXML
	private Button cancelButton;
	@FXML
	private TableView<Ritual> ritualTable;
	@FXML
	private TableColumn<Ritual, Object> choiceColumn;
	@FXML
	private Button ritualAddButton;
	@FXML
	private ComboBox<String> ritualList;

	private final Map<String, CheckBox> types = new HashMap<>();
	private final String ritualGroupName;

	public RitualObjectEditor(final Window window, final RitualObject ritualObject) {
		final FXMLLoader fxmlLoader = new FXMLLoader();

		fxmlLoader.setController(this);

		try {
			fxmlLoader.load(getClass().getResource("RitualObjectEditor.fxml").openStream());
		} catch (final Exception e) {
			ErrorLogger.logError(e);
		}

		ritualGroupName = ritualObject.getRitualGroupName();

		final Stage stage = new Stage();
		stage.setTitle("Bearbeiten");
		stage.setScene(new Scene(root, 330, 475 + ("Stabzauber".equals(ritualGroupName) ? 27 : 0)));
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(window);

		final JSONObject ritualGroups = ResourceManager.getResource("data/Ritualgruppen");
		final JSONObject ritualGroup = ritualGroups.getObj(ritualGroupName);

		name.setText(ritualObject.getName());
		weight.getValueFactory().setValue(ritualObject.getWeight());
		notes.setText(ritualObject.getNotes());

		DSAUtil.foreach(group -> group.getString("Ritualobjekt") != null, (name, group) -> {
			final String ritualObjectName = group.getString("Ritualobjekt");
			final CheckBox check = new CheckBox(ritualObjectName);
			typeBox.getChildren().add(check);
			types.put(ritualObjectName, check);
		}, ritualGroups);

		final List<String> actualTypes = ritualObject.getTypes();
		for (final String type : actualTypes) {
			types.get(type).setSelected(true);
		}

		if (!"Stabzauber".equals(ritualGroupName)) {
			root.getChildren().remove(volumeBox);
		} else {
			volume.getValueFactory().setValue(ritualObject.getVolume());
		}

		if (!ritualGroup.containsKey("Material")) {
			root.getChildren().remove(materialBox);
		} else {
			final JSONArray materials = ritualGroup.getArr("Material");
			for (int i = 0; i < materials.size(); ++i) {
				material.getItems().add(materials.getString(i));
			}
			material.getSelectionModel().select(ritualObject.getMaterial());
		}

		initializeRitualTable();
		ritualTable.getItems().setAll(ritualObject.getRituals());
		updateList();

		okButton.setOnAction(event -> {
			ritualObject.setName(name.getText());
			ritualObject.setWeight(weight.getValue());
			ritualObject.setNotes(notes.getText());

			final List<String> newTypes = new ArrayList<>();
			for (final String type : types.keySet()) {
				if (types.get(type).isSelected()) {
					newTypes.add(type);
				}
			}
			ritualObject.setTypes(newTypes);

			if ("Stabzauber".equals(ritualGroupName)) {
				ritualObject.setVolume(volume.getValueFactory().getValue());
			}

			if (ritualGroup.containsKey("Material")) {
				ritualObject.setMaterial(material.getSelectionModel().getSelectedItem());
			}

			ritualObject.setRituals(ritualTable.getItems());

			stage.close();
		});

		cancelButton.setOnAction(event -> stage.close());

		stage.show();
	}

	@FXML
	private void add() {
		ritualTable.getItems().add(new Ritual(ritualGroupName, ritualList.getSelectionModel().getSelectedItem(), new JSONObject(null)));
		updateList();
	}

	private void initializeRitualTable() {
		GUIUtil.autosizeTable(ritualTable, 0, 2);
		GUIUtil.cellValueFactories(ritualTable, "name", "choice");

		choiceColumn.setCellFactory(c -> new GraphicTableCell<Ritual, Object>(false) {
			@Override
			protected void createGraphic() {
				final Object choice = getTableView().getItems().get(getIndex()).getChoice();
				if (choice == null) {
					final Label l = new Label();
					createGraphic(l, () -> "", s -> {});
				} else if (choice instanceof Integer) {
					final ReactiveSpinner<Integer> r = new ReactiveSpinner<>(1, 99, (Integer) choice);
					r.setEditable(true);
					createGraphic(r, () -> r.getValue(), s -> r.getValueFactory().setValue((Integer) s));
				} else if (choice instanceof String) {
					final TextField t = new TextField((String) choice);
					createGraphic(t, () -> t.getText(), s -> t.setText((String) s));
				}
			}
		});
		choiceColumn.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setChoice(t.getNewValue()));

		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem contextMenuItem = new MenuItem("Löschen");
		contextMenu.getItems().add(contextMenuItem);
		contextMenuItem.setOnAction(o -> {
			final Ritual item = ritualTable.getSelectionModel().getSelectedItem();
			if (item != null) {
				ritualTable.getItems().remove(item);
				updateList();
			}
		});
		contextMenu.setOnShowing(o -> {
			contextMenuItem.setVisible(ritualTable.getSelectionModel().getSelectedItem() != null);
		});
		ritualTable.setContextMenu(contextMenu);

		ritualTable.setPrefHeight(151);
		ritualTable.setMinHeight(151);
		ritualTable.setMaxHeight(151);
	}

	private void updateList() {
		ritualList.getItems().clear();

		final JSONObject ritualGroup = ResourceManager.getResource("data/Rituale").getObj(ritualGroupName);
		DSAUtil.foreach(ritual -> true, (name, ritual) -> {
			ritualList.getItems().add(name);
		}, ritualGroup);

		for (final Ritual ritual : ritualTable.getItems()) {
			final String multipleTimes = ritualGroup.getObj(ritual.getName()).getString("Mehrfach");
			if (multipleTimes != null && !"Anzahl".equals(multipleTimes)) {
				continue;
			}
			ritualList.getItems().remove(ritual.getName());
		}

		if (ritualList.getItems().size() > 0) {
			ritualList.getSelectionModel().select(0);
			ritualAddButton.setDisable(false);
		} else {
			ritualAddButton.setDisable(true);
		}
	}
}
