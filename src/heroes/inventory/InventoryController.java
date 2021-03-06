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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import dsa41basis.fight.Armor;
import dsa41basis.fight.CloseCombatWeapon;
import dsa41basis.fight.DefensiveWeapon;
import dsa41basis.fight.RangedWeapon;
import dsa41basis.inventory.Artifact;
import dsa41basis.inventory.Clothing;
import dsa41basis.inventory.InventoryItem;
import dsa41basis.inventory.RitualObject;
import dsa41basis.util.DSAUtil;
import dsa41basis.util.HeroUtil;
import dsatool.gui.GUIUtil;
import dsatool.resources.ResourceManager;
import dsatool.resources.Settings;
import dsatool.util.ErrorLogger;
import dsatool.util.GraphicTableCell;
import dsatool.util.ReactiveSpinner;
import heroes.ui.HeroTabController;
import heroes.util.UiUtil;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import jsonant.event.JSONListener;
import jsonant.value.JSONArray;
import jsonant.value.JSONObject;
import jsonant.value.JSONValue;

public class InventoryController extends HeroTabController {
	@FXML
	private TableColumn<Artifact, String> artifactNameColumn;
	@FXML
	private TableColumn<Artifact, String> artifactNotesColumn;
	@FXML
	private TableView<Artifact> artifactTable;
	@FXML
	private TableColumn<Clothing, String> clothingNameColumn;
	@FXML
	private TableColumn<Clothing, String> clothingNotesColumn;
	@FXML
	private TableView<Clothing> clothingTable;
	@FXML
	private ReactiveSpinner<Integer> ducats;
	@FXML
	private TableColumn<InventoryItem, String> equipmentNameColumn;
	@FXML
	private TableColumn<InventoryItem, String> equipmentNotesColumn;
	@FXML
	private TableView<InventoryItem> equipmentTable;
	@FXML
	private ReactiveSpinner<Integer> heller;
	@FXML
	private VBox inventoryBox;
	@FXML
	private ReactiveSpinner<Integer> kreuzer;
	@FXML
	private HBox moneyBox;
	@FXML
	private ScrollPane pane;
	@FXML
	private TableColumn<CloseCombatWeapon, Integer> closeCombatBFColumn;
	@FXML
	private TableView<RangedWeapon> rangedTable;
	@FXML
	private TableColumn<DefensiveWeapon, Double> rangedWeightColumn;
	@FXML
	private TableView<CloseCombatWeapon> closeCombatTable;
	@FXML
	private TableColumn<DefensiveWeapon, Double> closeCombatWeightColumn;
	@FXML
	private ReactiveSpinner<Integer> silver;
	@FXML
	private TableColumn<DefensiveWeapon, Integer> defensiveWeaponsBFColumn;
	@FXML
	private TableView<DefensiveWeapon> defensiveWeaponsTable;
	@FXML
	private TableColumn<DefensiveWeapon, Double> defensiveWeaponsWeightColumn;
	@FXML
	private TableColumn<DefensiveWeapon, Integer> shieldsBFColumn;
	@FXML
	private TableColumn<DefensiveWeapon, Double> shieldsWeightColumn;
	@FXML
	private TableView<DefensiveWeapon> shieldsTable;
	@FXML
	private TableView<Armor> armorTable;
	@FXML
	private TableColumn<Armor, Integer> armorHeadColumn;
	@FXML
	private TableColumn<Armor, Integer> armorBreastColumn;
	@FXML
	private TableColumn<Armor, Integer> armorBackColumn;
	@FXML
	private TableColumn<Armor, Integer> armorBellyColumn;
	@FXML
	private TableColumn<Armor, Integer> armorLarmColumn;
	@FXML
	private TableColumn<Armor, Integer> armorRarmColumn;
	@FXML
	private TableColumn<Armor, Integer> armorLlegColumn;
	@FXML
	private TableColumn<Armor, Integer> armorRlegColumn;
	@FXML
	private TableColumn<Armor, Double> armorRsColumn;
	@FXML
	private TableColumn<Armor, Double> armorBeColumn;
	@FXML
	private TableColumn<Armor, Double> armorWeightColumn;
	@FXML
	private TableColumn<Armor, String> armorNameColumn;
	@FXML
	private TableView<RitualObject> ritualObjectTable;
	@FXML
	private TableColumn<RitualObject, String> ritualObjectNameColumn;
	@FXML
	private TableColumn<RitualObject, String> ritualObjectTypeColumn;
	@FXML
	private ComboBox<String> closeCombatList;
	@FXML
	private ComboBox<String> rangedList;
	@FXML
	private ComboBox<String> shieldsList;
	@FXML
	private ComboBox<String> defensiveWeaponsList;
	@FXML
	private ComboBox<String> armorList;
	@FXML
	private ComboBox<String> ritualObjectList;
	@FXML
	private TextField newArtifactField;
	@FXML
	private ComboBox<String> clothingList;
	@FXML
	private ComboBox<String> equipmentList;
	@FXML
	private Button closeCombatAddButton;
	@FXML
	private Button rangedAddButton;
	@FXML
	private Button shieldsAddButton;
	@FXML
	private Button defensiveWeaponsAddButton;
	@FXML
	private Button armorAddButton;
	@FXML
	private Button ritualObjectAddButton;
	@FXML
	private Button artifactAddButton;
	@FXML
	private TitledPane ritualObjectPane;

	private final JSONObject equipment;
	private JSONArray items;
	private ChangeListener<? super Integer> moneyListener;
	private final JSONListener heroMoneyListener = o -> refreshMoney();
	private final JSONListener heroItemListener = o -> refreshTables();

	private final List<String> ritualObjectGroups = new ArrayList<>();

	private final String[] categoryNames = { "Nahkampfwaffe", "Fernkampfwaffe", "Schild", "Parierwaffe", "Rüstung", "Kleidung", "Ritualobjekt" };
	private final String[] categoryLongNames = { "Nahkampfwaffen", "Fernkampfwaffen", "Schilde", "Parierwaffen", "Rüstung", "Kleidung", "Ritualobjekte" };

	public InventoryController(final TabPane tabPane) {
		super(tabPane);
		equipment = ResourceManager.getResource("data/Ausruestung");
	}

	public void addArtifact() {
		final String itemName = newArtifactField.getText();
		newArtifactField.setText("");
		final JSONObject item = new JSONObject(items);
		item.put("Name", itemName);
		item.put("Kategorien", new JSONArray(Arrays.asList("Artefakt"), item));
		items.add(item);
		items.notifyListeners(null);
	}

	public void addItem(final ComboBox<String> list) {
		final String itemName = list.getSelectionModel().getSelectedItem();
		final JSONObject item = equipment.getObj(itemName).clone(items);
		item.put("Name", itemName);
		if (list == clothingList) {
			final JSONArray categories = new JSONArray(item);
			categories.add("Kleidung");
			item.put("Kategorien", categories);
		}
		items.add(item);
		items.notifyListeners(null);
	}

	@Override
	protected void changeEditable() {
		// Nothing to do here, inventory is always editable
	}

	private final <T extends InventoryItem> Callback<TableView<T>, TableRow<T>> contextMenu(final String name, final String category) {
		return tableView -> {
			final TableRow<T> row = new TableRow<>();

			row.setOnDragDetected(e -> {
				if (row.isEmpty()) return;
				final Dragboard dragBoard = tableView.startDragAndDrop(TransferMode.MOVE);
				final ClipboardContent content = new ClipboardContent();
				content.put(DataFormat.PLAIN_TEXT, row.getIndex());
				dragBoard.setContent(content);
			});

			row.setOnDragDropped(e -> {
				final InventoryItem toMove = tableView.getItems().get((Integer) e.getDragboard().getContent(DataFormat.PLAIN_TEXT));
				int appearance = 0;
				int index = tableView.getItems().indexOf(toMove);
				while (index != (Integer) e.getDragboard().getContent(DataFormat.PLAIN_TEXT)) {
					++appearance;
					index += 1 + tableView.getItems().subList(index + 1, tableView.getItems().size()).indexOf(toMove);
				}
				final JSONObject item = toMove.getItem();
				index = items.indexOf(item);
				for (; appearance > 0; --appearance) {
					index = items.indexOf(item, index + 1);
				}
				items.removeAt(index);
				final InventoryItem target = row.getItem();
				appearance = 0;
				int targetIndex = tableView.getItems().indexOf(target);
				while (targetIndex != row.getIndex()) {
					++appearance;
					targetIndex += 1 + tableView.getItems().subList(targetIndex + 1, tableView.getItems().size()).indexOf(target);
				}
				final JSONObject targetItem = target.getItem();
				targetIndex = items.indexOf(targetItem);
				for (; appearance > 0; --appearance) {
					targetIndex = items.indexOf(targetItem, targetIndex + 1);
				}
				if (targetIndex == -1 || targetIndex > items.size()) {
					items.add(item);
				} else {
					items.add(targetIndex, item);
				}
				e.setDropCompleted(true);
				items.notifyListeners(null);
			});

			row.setOnDragOver(e -> {
				if (e.getGestureSource() == row.getTableView()) {
					e.acceptTransferModes(TransferMode.MOVE);
				}
			});

			final ContextMenu rowMenu = new ContextMenu();

			final Consumer<Object> edit = obj -> {
				final InventoryItem item = row.getItem();
				final Window window = pane.getScene().getWindow();
				switch (category) {
				case "Nahkampfwaffe":
					new CloseCombatWeaponEditor(window, (CloseCombatWeapon) item);
					break;
				case "Fernkampfwaffe":
					new RangedWeaponEditor(window, (RangedWeapon) item);
					break;
				case "Schild":
				case "Parierwaffe":
					new DefensiveWeaponEditor(window, (DefensiveWeapon) item);
					break;
				case "Rüstung":
					new ArmorEditor(window, (Armor) item);
					break;
				case "Ritualobjekt":
					new RitualObjectEditor(window, (RitualObject) item);
					break;
				case "Artefakt":
					new ArtifactEditor(window, (Artifact) item);
					break;
				case "Kleidung":
					new ClothingEditor(window, (Clothing) item);
					break;
				default:
					new ItemEditor(window, item);
					break;
				}
			};

			if (!("Kleidung".equals(name) || "Ausrüstung".equals(name))) {
				row.setOnMouseClicked(event -> {
					if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
						edit.accept(null);
					}
				});
			}

			final MenuItem editItem = new MenuItem("Bearbeiten");
			editItem.setOnAction(event -> edit.accept(null));

			final JSONObject ritualGroups = ResourceManager.getResource("data/Ritualgruppen");

			final Menu addItem = new Menu("Hinzufügen zu ...");
			for (int i = 0; i < categoryNames.length; ++i) {
				final MenuItem addCatItem = new MenuItem(categoryLongNames[i]);
				final String categoryName = categoryNames[i];
				addCatItem.setOnAction(event -> {
					final JSONObject item = row.getItem().getItem();
					JSONArray categories = item.getArr("Kategorien");
					if (categories == null) {
						categories = new JSONArray(item);
						item.put("Kategorien", categories);
					}
					if ("Ritualobjekte".equals(name)) {
						boolean found = false;
						for (final String ritualObjectGroup : ritualObjectGroups) {
							final String ritualObjectName = ritualGroups.getObj(ritualObjectGroup).getString("Ritualobjekt");
							if (item.containsKey(ritualObjectName)) {
								categories.add(ritualObjectName);
								found = true;
							}
						}
						if (!found) {
							categories.add(ritualGroups.getObj(ritualObjectGroups.get(0)).getString("Ritualobjekt"));
						}
					} else {
						if (!categories.contains(categoryName)) {
							categories.add(categoryName);
						}
					}
					categories.notifyListeners(null);
				});
				addItem.getItems().add(addCatItem);
			}

			final MenuItem removeItem = new MenuItem("Entfernen aus " + name);
			removeItem.setOnAction(event -> {
				final JSONObject item = row.getItem().getItem();
				final JSONArray categories = item.getArr("Kategorien");

				if ("Ritualobjekte".equals(name)) {
					for (final String ritualObjectGroup : ritualObjectGroups) {
						final String ritualObjectName = ritualGroups.getObj(ritualObjectGroup).getString("Ritualobjekt");
						categories.remove(ritualObjectName);
					}
				} else {
					categories.remove(category);
				}

				if (categories.size() == 0) {
					item.removeKey("Kategorien");
				}
				item.notifyListeners(null);
			});

			final Menu location = new Menu("Ort");

			final JSONListener animalListener = o -> {
				if (row.getItem() != null) {
					updateLocationMenu(row.getItem().getItem(), location);
				}
			};
			final JSONArray[] animals = new JSONArray[] { hero.getArr("Tiere") };
			row.itemProperty().addListener((o, oldV, newV) -> {
				if (newV != null) {
					updateLocationMenu(newV.getItem(), location);
					animals[0].removeListener(animalListener);
					animals[0] = hero.getArr("Tiere");
					animals[0].addListener(animalListener);
				}
			});

			final MenuItem deleteItem = new MenuItem("Löschen");
			deleteItem.setOnAction(event -> {
				final JSONObject item = row.getItem().getItem();
				final JSONValue parent = item.getParent();
				parent.remove(item);
				parent.notifyListeners(null);
			});

			rowMenu.getItems().addAll(editItem, addItem);
			if (!"Ausrüstung".equals(category)) {
				rowMenu.getItems().add(removeItem);
			}
			rowMenu.getItems().addAll(location, deleteItem);
			row.setContextMenu(rowMenu);

			return row;
		};
	}

	@Override
	protected Node getControl() {
		return pane;
	}

	@Override
	protected String getText() {
		return "Inventar";
	}

	@Override
	public void init() {
		final FXMLLoader fxmlLoader = new FXMLLoader();

		fxmlLoader.setController(this);

		try {
			fxmlLoader.load(getClass().getResource("Inventory.fxml").openStream());
		} catch (final Exception e) {
			ErrorLogger.logError(e);
		}

		super.init();

		final JSONObject ritualGroups = ResourceManager.getResource("data/Ritualgruppen");
		DSAUtil.foreach(group -> group.getString("Ritualobjekt") != null, (name, group) -> {
			ritualObjectGroups.add(name);
		}, ritualGroups);

		ducats.setConverter(new IntegerStringConverter());
		silver.setConverter(new IntegerStringConverter());
		heller.setConverter(new IntegerStringConverter());
		kreuzer.setConverter(new IntegerStringConverter());

		initializeCloseCombatTable();
		initializeRangedTable();
		initializeShieldsTable();
		initializeDefensiveWeaponsTable();
		initializeArmorTable();
		initializeRitualObjectTable();
		initializeArtifactTable();
		initializeClothingTable();
		initializeEquipmentTable();

		equipment.addListener(o -> updateLists());

		updateLists();
	}

	private void initializeArmorTable() {
		final String armorSetting = Settings.getSettingStringOrDefault("Zonenrüstung", "Kampf", "Rüstungsart");

		DoubleBinding armorWidth = armorTable.widthProperty().subtract(2);
		if ("Zonenrüstung".equals(armorSetting)) {
			armorWidth = armorWidth.subtract(armorHeadColumn.widthProperty());
			armorWidth = armorWidth.subtract(armorBreastColumn.widthProperty());
			armorWidth = armorWidth.subtract(armorBackColumn.widthProperty());
			armorWidth = armorWidth.subtract(armorBellyColumn.widthProperty());
			armorWidth = armorWidth.subtract(armorLarmColumn.widthProperty());
			armorWidth = armorWidth.subtract(armorRarmColumn.widthProperty());
			armorWidth = armorWidth.subtract(armorLlegColumn.widthProperty());
			armorWidth = armorWidth.subtract(armorRlegColumn.widthProperty());
			armorRsColumn.setVisible(false);
		} else {
			armorWidth = armorWidth.subtract(armorRsColumn.widthProperty());
			armorHeadColumn.setVisible(false);
			armorBreastColumn.setVisible(false);
			armorBackColumn.setVisible(false);
			armorBellyColumn.setVisible(false);
			armorLarmColumn.setVisible(false);
			armorRarmColumn.setVisible(false);
			armorLlegColumn.setVisible(false);
			armorRlegColumn.setVisible(false);
		}
		armorWidth = armorWidth.subtract(armorBeColumn.widthProperty());
		armorWidth = armorWidth.subtract(armorWeightColumn.widthProperty());

		armorNameColumn.prefWidthProperty().bind(armorWidth);
		if ("Gesamtrüstung".equals(armorSetting)) {
			GUIUtil.cellValueFactories(armorTable, "name", "head", "breast", "back", "belly", "larm", "rarm", "lleg", "rleg", "totalrs", "totalbe", "weight");
		} else {
			GUIUtil.cellValueFactories(armorTable, "name", "head", "breast", "back", "belly", "larm", "rarm", "lleg", "rleg", "zoners", "zonebe", "weight");
		}

		armorTable.setRowFactory(contextMenu("Rüstung", "Rüstung"));
	}

	private void initializeArtifactTable() {
		GUIUtil.cellValueFactories(artifactTable, "name", "notes");

		artifactTable.setRowFactory(contextMenu("Artefakte", "Artefakt"));
	}

	@SuppressWarnings("unchecked")
	private void initializeCloseCombatTable() {
		GUIUtil.autosizeTable(closeCombatTable, 0, 2);
		GUIUtil.cellValueFactories(closeCombatTable, "name", "tp", "tpkk", "weight", "length", "bf", "ini", "wm", "special", "dk");

		closeCombatWeightColumn.setCellFactory(UiUtil.integerCellFactory);
		closeCombatBFColumn.setCellFactory(UiUtil.integerCellFactory);

		closeCombatTable.setRowFactory(contextMenu("Nahkampfwaffen", "Nahkampfwaffe"));
	}

	private void initializeClothingTable() {
		GUIUtil.cellValueFactories(clothingTable, "name", "notes");

		clothingTable.setRowFactory(contextMenu("Kleidung", "Kleidung"));

		clothingNameColumn.setCellFactory(o -> {
			final TableCell<Clothing, String> cell = new GraphicTableCell<Clothing, String>(false) {
				@Override
				protected void createGraphic() {
					final TextField t = new TextField();
					createGraphic(t, () -> t.getText(), s -> t.setText(s));
				}
			};
			return cell;
		});

		clothingNameColumn.setOnEditCommit(event -> {
			final JSONObject item = clothingTable.getItems().get(event.getTablePosition().getRow()).getItem();
			item.put("Name", event.getNewValue());
			item.notifyListeners(null);
		});

		clothingNotesColumn.setCellFactory(o -> {
			final TableCell<Clothing, String> cell = new GraphicTableCell<Clothing, String>(false) {
				@Override
				protected void createGraphic() {
					final TextField t = new TextField();
					createGraphic(t, () -> t.getText(), s -> t.setText(s));
				}
			};
			return cell;
		});
		clothingNotesColumn.setOnEditCommit(event -> {
			final String note = event.getNewValue();
			final JSONObject item = clothingTable.getItems().get(event.getTablePosition().getRow()).getItem();
			if ("".equals(note)) {
				item.removeKey("Anmerkungen");
			} else {
				item.put("Anmerkungen", note);
			}
			item.notifyListeners(null);
		});
	}

	@SuppressWarnings("unchecked")
	private void initializeDefensiveWeaponsTable() {
		GUIUtil.autosizeTable(defensiveWeaponsTable, 0, 2);
		GUIUtil.cellValueFactories(defensiveWeaponsTable, "name", "wm", "ini", "bf", "weight");

		defensiveWeaponsBFColumn.setCellFactory(UiUtil.integerCellFactory);
		defensiveWeaponsWeightColumn.setCellFactory(UiUtil.integerCellFactory);

		defensiveWeaponsTable.setRowFactory(contextMenu("Parierwaffen", "Parierwaffe"));
	}

	private void initializeEquipmentTable() {
		GUIUtil.cellValueFactories(equipmentTable, "name", "notes");

		equipmentTable.setRowFactory(contextMenu("Ausrüstung", ""));

		equipmentNameColumn.setCellFactory(o -> {
			final TableCell<InventoryItem, String> cell = new GraphicTableCell<InventoryItem, String>(false) {
				@Override
				protected void createGraphic() {
					final TextField t = new TextField();
					createGraphic(t, () -> t.getText(), s -> t.setText(s));
				}
			};
			return cell;
		});
		equipmentNameColumn.setOnEditCommit(event -> {
			final JSONObject item = equipmentTable.getItems().get(event.getTablePosition().getRow()).getItem();
			item.put("Name", event.getNewValue());
			item.notifyListeners(null);
		});

		equipmentNotesColumn.setCellFactory(o -> {
			final TableCell<InventoryItem, String> cell = new GraphicTableCell<InventoryItem, String>(false) {
				@Override
				protected void createGraphic() {
					final TextField t = new TextField();
					createGraphic(t, () -> t.getText(), s -> t.setText(s));
				}
			};
			return cell;
		});
		equipmentNotesColumn.setOnEditCommit(event -> {
			final String note = event.getNewValue();
			final JSONObject item = equipmentTable.getItems().get(event.getTablePosition().getRow()).getItem();
			if ("".equals(note)) {
				item.removeKey("Anmerkungen");
			} else {
				item.put("Anmerkungen", note);
			}
			item.notifyListeners(null);
		});
	}

	@SuppressWarnings("unchecked")
	private void initializeRangedTable() {
		GUIUtil.autosizeTable(rangedTable, 0, 2);
		GUIUtil.cellValueFactories(rangedTable, "name", "tp", "distance", "distancetp", "weight", "load");

		rangedWeightColumn.setCellFactory(UiUtil.integerCellFactory);

		rangedTable.setRowFactory(contextMenu("Fernkampfwaffen", "Fernkampfwaffe"));
	}

	private void initializeRitualObjectTable() {
		GUIUtil.autosizeTable(ritualObjectTable, 0, 2);
		GUIUtil.cellValueFactories(ritualObjectTable, "name", "type");

		ritualObjectTable.setRowFactory(contextMenu("Ritualobjekte", "Ritualobjekt"));
	}

	@SuppressWarnings("unchecked")
	private void initializeShieldsTable() {
		GUIUtil.autosizeTable(shieldsTable, 0, 2);
		GUIUtil.cellValueFactories(shieldsTable, "name", "wm", "ini", "bf", "weight");

		shieldsWeightColumn.setCellFactory(UiUtil.integerCellFactory);
		shieldsBFColumn.setCellFactory(UiUtil.integerCellFactory);

		shieldsTable.setRowFactory(contextMenu("Schilde", "Schild"));
	}

	private void refreshMoney() {
		final JSONObject money = hero.getObj("Besitz").getObj("Geld");
		ducats.getValueFactory().setValue(money.getIntOrDefault("Dukaten", 0));
		silver.getValueFactory().setValue(money.getIntOrDefault("Silbertaler", 0));
		heller.getValueFactory().setValue(money.getIntOrDefault("Heller", 0));
		kreuzer.getValueFactory().setValue(money.getIntOrDefault("Kreuzer", 0));
	}

	private void refreshTables() {
		closeCombatTable.getItems().clear();
		rangedTable.getItems().clear();
		shieldsTable.getItems().clear();
		defensiveWeaponsTable.getItems().clear();
		armorTable.getItems().clear();
		ritualObjectTable.getItems().clear();
		artifactTable.getItems().clear();
		clothingTable.getItems().clear();
		equipmentTable.getItems().clear();

		final JSONObject ritualGroups = ResourceManager.getResource("data/Ritualgruppen");

		HeroUtil.foreachInventoryItem(hero, item -> true, (item, fromAnimal) -> {
			final JSONArray categories = item.getArr("Kategorien");
			boolean found = false;
			if (categories != null) {
				if (categories.contains("Kleidung")) {
					final JSONObject actual = item.getObjOrDefault("Kleidung", item);
					final Clothing newItem = new Clothing(actual, item);
					newItem.recompute();
					clothingTable.getItems().add(newItem);
					found = true;
				}
				if (categories.contains("Nahkampfwaffe")) {
					final JSONObject actual = item.getObjOrDefault("Nahkampfwaffe", item);
					closeCombatTable.getItems().add(new CloseCombatWeapon(null, actual, item, null, null));
					found = true;
				}
				if (categories.contains("Fernkampfwaffe")) {
					final JSONObject actual = item.getObjOrDefault("Fernkampfwaffe", item);
					rangedTable.getItems().add(new RangedWeapon(null, actual, item, null, null));
					found = true;
				}
				if (categories.contains("Schild")) {
					final JSONObject actual = item.getObjOrDefault("Schild", item);
					shieldsTable.getItems().add(new DefensiveWeapon(true, null, actual, item));
					found = true;
				}
				if (categories.contains("Parierwaffe")) {
					final JSONObject actual = item.getObjOrDefault("Parierwaffe", item);
					defensiveWeaponsTable.getItems().add(new DefensiveWeapon(false, null, actual, item));
					found = true;
				}
				if (categories.contains("Rüstung")) {
					final JSONObject actual = item.getObjOrDefault("Rüstung", item);
					armorTable.getItems().add(new Armor(actual, item));
					found = true;
				}
				if (categories.contains("Artefakt")) {
					final JSONObject actual = item.getObjOrDefault("Artefakt", item);
					artifactTable.getItems().add(new Artifact(actual, item));
					found = true;
				}
				for (final String ritualGroupName : ritualObjectGroups) {
					final JSONObject ritualGroup = ritualGroups.getObj(ritualGroupName);
					if (categories.contains(ritualGroup.getString("Ritualobjekt"))) {
						final JSONObject actual = item.getObjOrDefault(ritualGroup.getString("Ritualobjekt"), item);
						ritualObjectTable.getItems().add(new RitualObject(actual, item, ritualGroupName));
						found = true;
					}
				}
			}
			if (!found && !fromAnimal) {
				final InventoryItem newItem = new InventoryItem(item, item);
				newItem.recompute();
				equipmentTable.getItems().add(newItem);
			}
		});

		closeCombatTable.setPrefHeight((closeCombatTable.getItems().size() + 1) * 25 + 1);
		closeCombatTable.setMinHeight((closeCombatTable.getItems().size() + 1) * 25 + 1);
		rangedTable.setPrefHeight((rangedTable.getItems().size() + 1) * 25 + 1);
		rangedTable.setMinHeight((rangedTable.getItems().size() + 1) * 25 + 1);
		shieldsTable.setPrefHeight((shieldsTable.getItems().size() + 1) * 25 + 1);
		shieldsTable.setMinHeight((shieldsTable.getItems().size() + 1) * 25 + 1);
		defensiveWeaponsTable.setPrefHeight((defensiveWeaponsTable.getItems().size() + 1) * 25 + 1);
		defensiveWeaponsTable.setMinHeight((defensiveWeaponsTable.getItems().size() + 1) * 25 + 1);
		armorTable.setPrefHeight((armorTable.getItems().size() + 1) * 25 + 1);
		armorTable.setMinHeight((armorTable.getItems().size() + 1) * 25 + 1);
		ritualObjectTable.setPrefHeight((ritualObjectTable.getItems().size() + 1) * 25 + 1);
		ritualObjectTable.setMinHeight((ritualObjectTable.getItems().size() + 1) * 25 + 1);
		artifactTable.setPrefHeight((artifactTable.getItems().size() + 1) * 26 + 1);
		artifactTable.setMinHeight((artifactTable.getItems().size() + 1) * 25 + 1);
		clothingTable.setPrefHeight((clothingTable.getItems().size() + 1) * 26 + 1);
		clothingTable.setMinHeight((clothingTable.getItems().size() + 1) * 25 + 1);
		equipmentTable.setPrefHeight((equipmentTable.getItems().size() + 1) * 26 + 1);
		equipmentTable.setMinHeight((equipmentTable.getItems().size() + 1) * 25 + 1);
	}

	@Override
	public void setHero(final JSONObject hero) {
		if (this.hero != null) {
			hero.getObj("Besitz").getObj("Geld").removeListener(heroMoneyListener);
			if (items != null) {
				items.removeListener(heroItemListener);
			}
			hero.getArr("Tiere").removeListener(heroItemListener);
		}
		super.setHero(hero);
	}

	private void setState(final ComboBox<String> list, final Button button) {
		if (list.getItems().size() > 0) {
			list.getSelectionModel().select(0);
			button.setDisable(false);
		} else {
			button.setDisable(true);
		}
	}

	@Override
	protected void update() {
		final JSONObject inventory = hero.getObj("Besitz");

		final JSONObject money = inventory.getObj("Geld");
		money.addLocalListener(heroMoneyListener);

		if (moneyListener != null) {
			ducats.valueProperty().removeListener(moneyListener);
			silver.valueProperty().removeListener(moneyListener);
			heller.valueProperty().removeListener(moneyListener);
			kreuzer.valueProperty().removeListener(moneyListener);
		}

		moneyListener = (observable, oldValue, newValue) -> {
			if (oldValue == newValue || newValue == null || oldValue == null) return;
			money.put("Dukaten", ducats.getValue());
			money.put("Silbertaler", silver.getValue());
			money.put("Heller", heller.getValue());
			money.put("Kreuzer", kreuzer.getValue());
			money.notifyListeners(heroMoneyListener);
		};

		refreshMoney();

		ducats.valueProperty().addListener(moneyListener);
		silver.valueProperty().addListener(moneyListener);
		heller.valueProperty().addListener(moneyListener);
		kreuzer.valueProperty().addListener(moneyListener);

		items = inventory.getArr("Ausrüstung");

		inventoryBox.getChildren().remove(ritualObjectPane);
		if (HeroUtil.isMagical(hero)) {
			inventoryBox.getChildren().add(inventoryBox.getChildren().size() - 2, ritualObjectPane);
		}

		refreshTables();

		items.addListener(heroItemListener);
		hero.getArr("Tiere").addListener(heroItemListener);
	}

	private void updateLists() {
		closeCombatList.getItems().clear();
		rangedList.getItems().clear();
		shieldsList.getItems().clear();
		defensiveWeaponsList.getItems().clear();
		armorList.getItems().clear();
		ritualObjectList.getItems().clear();
		clothingList.getItems().clear();
		equipmentList.getItems().clear();

		DSAUtil.foreach(item -> true, (itemName, item) -> {
			final JSONArray categories = item.getArr("Kategorien");
			boolean found = false;
			if (categories.contains("Kleidung")) {
				clothingList.getItems().add(itemName);
				found = true;
			}
			if (categories.contains("Nahkampfwaffe")) {
				closeCombatList.getItems().add(itemName);
				found = true;
			}
			if (categories.contains("Fernkampfwaffe")) {
				rangedList.getItems().add(itemName);
				found = true;
			}
			if (categories.contains("Schild")) {
				shieldsList.getItems().add(itemName);
				found = true;
			}
			if (categories.contains("Parierwaffe")) {
				defensiveWeaponsList.getItems().add(itemName);
				found = true;
			}
			if (categories.contains("Rüstung")) {
				armorList.getItems().add(itemName);
				found = true;
			}
			for (final String ritualObject : ritualObjectGroups) {
				if (categories.contains(ritualObject)) {
					ritualObjectList.getItems().add(itemName);
					found = true;
					break;
				}
			}
			if (!found) {
				equipmentList.getItems().add(itemName);
			}
		}, equipment);

		setState(closeCombatList, closeCombatAddButton);
		setState(rangedList, rangedAddButton);
		setState(shieldsList, shieldsAddButton);
		setState(defensiveWeaponsList, defensiveWeaponsAddButton);
		setState(armorList, armorAddButton);
		setState(ritualObjectList, ritualObjectAddButton);
	}

	private void updateLocationMenu(final JSONObject item, final Menu location) {
		location.getItems().clear();
		final ToggleGroup locationGroup = new ToggleGroup();
		final RadioMenuItem heroLocationItem = new RadioMenuItem(hero.getObj("Biografie").getString("Vorname"));
		hero.getObj("Biografie").addLocalListener(o -> heroLocationItem.setText(hero.getObj("Biografie").getString("Vorname")));
		heroLocationItem.setToggleGroup(locationGroup);
		location.getItems().add(heroLocationItem);
		final JSONArray animals = hero.getArr("Tiere");
		final Map<JSONObject, RadioMenuItem> names = new HashMap<>(animals.size());
		for (int i = 0; i < animals.size(); ++i) {
			final JSONObject animal = animals.getObj(i);
			final String name = animal.getObj("Biografie").getString("Name");
			final RadioMenuItem animalLocationItem = new RadioMenuItem(name);
			animalLocationItem.setToggleGroup(locationGroup);
			names.put(animal, animalLocationItem);
			location.getItems().add(animalLocationItem);
		}
		location.setOnShowing(e -> {
			final JSONValue possessor = item.getParent() != null ? item.getParent().getParent() : null;
			if (possessor != null && possessor.getParent() instanceof JSONObject) {
				heroLocationItem.setSelected(true);
				for (final JSONObject animal : names.keySet()) {
					names.get(animal).setOnAction(e2 -> {
						final JSONValue parent = item.getParent();
						parent.remove(item);
						parent.notifyListeners(null);
						final JSONArray equipment = animal.getArr("Ausrüstung");
						equipment.add(item.clone(equipment));
						equipment.notifyListeners(null);
					});
				}
			} else {
				heroLocationItem.setOnAction(e2 -> {
					final JSONValue parent = item.getParent();
					parent.remove(item);
					parent.notifyListeners(null);
					final JSONArray equipment = hero.getObj("Besitz").getArr("Ausrüstung");
					equipment.add(item.clone(equipment));
					equipment.notifyListeners(null);
				});
				for (final JSONObject animal : names.keySet()) {
					if (possessor != null && animal.equals(possessor)) {
						names.get(animal).setSelected(true);
					} else {
						names.get(animal).setOnAction(e2 -> {
							final JSONValue parent = item.getParent();
							parent.remove(item);
							parent.notifyListeners(null);
							final JSONArray equipment = animal.getArr("Ausrüstung");
							equipment.add(item.clone(equipment));
							equipment.notifyListeners(null);
						});
					}
				}
			}
		});
	}

}
