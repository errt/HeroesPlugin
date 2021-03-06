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
package heroes.fight;

import dsa41basis.fight.Armor;
import dsa41basis.fight.CloseCombatWeapon;
import dsa41basis.fight.DefensiveWeapon;
import dsa41basis.fight.RangedWeapon;
import dsa41basis.util.DSAUtil;
import dsatool.gui.GUIUtil;
import dsatool.resources.ResourceManager;
import dsatool.resources.Settings;
import dsatool.util.ErrorLogger;
import dsatool.util.GraphicTableCell;
import dsatool.util.IntegerSpinnerTableCell;
import dsatool.util.ReactiveSpinner;
import heroes.ui.HeroTabController;
import heroes.util.UiUtil;
import javafx.beans.binding.DoubleBinding;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.ComboBoxTableCell;
import jsonant.event.JSONListener;
import jsonant.value.JSONArray;
import jsonant.value.JSONObject;

public class FightController extends HeroTabController {
	private static final JSONObject infight = new JSONObject(null);

	static {
		infight.put("Name", "Waffenlos");
		final JSONObject TP = new JSONObject(infight);
		TP.put("W6", 1);
		TP.put("Trefferpunkte", 0);
		TP.put("Ausdauerschaden", true);
		infight.put("Trefferpunkte", TP);
		final JSONObject TPKK = new JSONObject(infight);
		TPKK.put("Schwellenwert", 10);
		TPKK.put("Schadensschritte", 3);
		infight.put("Trefferpunkte/Körperkraft", TPKK);
		final JSONObject weaponModifiers = new JSONObject(infight);
		infight.put("Waffenmodifikatoren", weaponModifiers);
		final JSONArray distanceClasses = new JSONArray(null);
		distanceClasses.add("H");
		infight.put("Distanzklassen", distanceClasses);
		final JSONArray weaponTypes = new JSONArray(null);
		weaponTypes.add("Raufen");
		weaponTypes.add("Ringen");
		infight.put("Waffentypen", weaponTypes);
	}

	@FXML
	private TableColumn<Armor, Integer> armorBackColumn;
	@FXML
	private TableColumn<Armor, Double> armorBeColumn;
	@FXML
	private TableColumn<Armor, Integer> armorBellyColumn;
	@FXML
	private TableColumn<Armor, Integer> armorBreastColumn;
	@FXML
	private TableColumn<Armor, Integer> armorHeadColumn;
	@FXML
	private TableColumn<Armor, Integer> armorLarmColumn;
	@FXML
	private TableColumn<Armor, Integer> armorLlegColumn;
	@FXML
	private TableColumn<Armor, String> armorNameColumn;
	@FXML
	private TitledPane armorPane;
	@FXML
	private TableColumn<Armor, Integer> armorRarmColumn;
	@FXML
	private TableColumn<Armor, Integer> armorRlegColumn;
	@FXML
	private TableColumn<Armor, Double> armorRsColumn;
	@FXML
	private TableView<Armor> armorTable;
	@FXML
	private TableColumn<CloseCombatWeapon, Integer> closeCombatBFColumn;
	@FXML
	private TableColumn<CloseCombatWeapon, Integer> closeCombatEBEColumn;
	@FXML
	private TableColumn<CloseCombatWeapon, Integer> closeCombatPAColumn;
	@FXML
	private TableColumn<CloseCombatWeapon, Integer> closeCombatIniColumn;
	@FXML
	private TableColumn<CloseCombatWeapon, String> closeCombatNameColumn;
	@FXML
	private TableView<CloseCombatWeapon> closeCombatTable;
	@FXML
	private TableColumn<CloseCombatWeapon, String> closeCombatTypeColumn;
	@FXML
	private TableColumn<DefensiveWeapon, Integer> defensiveWeaponsBFColumn;
	@FXML
	private TableColumn<DefensiveWeapon, Integer> defensiveWeaponsIniColumn;
	@FXML
	private TableColumn<DefensiveWeapon, String> defensiveWeaponsNameColumn;
	@FXML
	private TableColumn<DefensiveWeapon, Integer> defensiveWeaponsPAColumn;
	@FXML
	private TableView<DefensiveWeapon> defensiveWeaponsTable;
	@FXML
	private ScrollPane pane;
	@FXML
	private TableColumn<RangedWeapon, String> rangedCombatAmmunitionColumn;
	@FXML
	private TableColumn<RangedWeapon, Integer> rangedCombatEBEColumn;
	@FXML
	private TableColumn<RangedWeapon, String> rangedCombatNameColumn;
	@FXML
	private TableView<RangedWeapon> rangedCombatTable;
	@FXML
	private TableColumn<RangedWeapon, String> rangedCombatTPColumn;
	@FXML
	private TableColumn<RangedWeapon, String> rangedCombatTypeColumn;
	@FXML
	private TableColumn<DefensiveWeapon, Integer> shieldsBFColumn;
	@FXML
	private TableColumn<DefensiveWeapon, Integer> shieldsIniColumn;
	@FXML
	private TableColumn<DefensiveWeapon, String> shieldsNameColumn;
	@FXML
	private TableView<DefensiveWeapon> shieldsTable;

	private final JSONListener listener = o -> fillTables();

	public FightController(final TabPane tabPane) {
		super(tabPane);
	}

	@Override
	protected void changeEditable() {
		// Nothing to do here, editing BFs is always possible
	}

	private void fillTables() {
		closeCombatTable.getItems().clear();
		rangedCombatTable.getItems().clear();
		shieldsTable.getItems().clear();
		defensiveWeaponsTable.getItems().clear();
		armorTable.getItems().clear();

		final JSONObject talents = ResourceManager.getResource("data/Talente");
		final JSONObject closeCombatTalents = talents.getObj("Nahkampftalente");
		final JSONObject actualCloseCombatTalents = hero.getObj("Talente").getObj("Nahkampftalente");
		final JSONObject rangedCombatTalents = talents.getObj("Fernkampftalente");
		final JSONObject actualRangedCombatTalents = hero.getObj("Talente").getObj("Fernkampftalente");
		final JSONObject inventory = hero.getObj("Besitz");
		final JSONArray items = inventory.getArr("Ausrüstung");

		closeCombatTable.getItems().add(new CloseCombatWeapon(hero, infight, infight, closeCombatTalents, actualCloseCombatTalents));

		DSAUtil.foreach(item -> item.containsKey("Kategorien"), item -> {
			final JSONArray categories = item.getArr("Kategorien");

			if (categories.contains("Nahkampfwaffe")) {
				if (item.containsKey("Nahkampfwaffe")) {
					closeCombatTable.getItems()
							.add(new CloseCombatWeapon(hero, item.getObj("Nahkampfwaffe"), item, closeCombatTalents, actualCloseCombatTalents));
				} else {
					closeCombatTable.getItems().add(new CloseCombatWeapon(hero, item, item, closeCombatTalents, actualCloseCombatTalents));
				}
			}
			if (categories.contains("Fernkampfwaffe")) {
				if (item.containsKey("Fernkampfwaffe")) {
					rangedCombatTable.getItems()
							.add(new RangedWeapon(hero, item.getObj("Fernkampfwaffe"), item, rangedCombatTalents, actualRangedCombatTalents));
				} else {
					rangedCombatTable.getItems().add(new RangedWeapon(hero, item, item, rangedCombatTalents, actualRangedCombatTalents));
				}
			}
			if (categories.contains("Schild")) {
				if (item.containsKey("Schild")) {
					shieldsTable.getItems().add(new DefensiveWeapon(true, hero, item.getObj("Schild"), item));
				} else {
					shieldsTable.getItems().add(new DefensiveWeapon(true, hero, item, item));
				}
			}
			if (categories.contains("Parierwaffe")) {
				if (item.containsKey("Parierwaffe")) {
					defensiveWeaponsTable.getItems().add(new DefensiveWeapon(false, hero, item.getObj("Parierwaffe"), item));
				} else {
					defensiveWeaponsTable.getItems().add(new DefensiveWeapon(false, hero, item, item));
				}
			}
			if (categories.contains("Rüstung")) {
				if (item.containsKey("Rüstung")) {
					armorTable.getItems().add(new Armor(item.getObj("Rüstung"), item));
				} else {
					armorTable.getItems().add(new Armor(item, item));
				}
			}
		}, items);

		closeCombatTable.setPrefHeight(closeCombatTable.getItems().size() * 28 + 25);
		closeCombatTable.setMinHeight(closeCombatTable.getItems().size() * 28 + 25);
		rangedCombatTable.setPrefHeight(rangedCombatTable.getItems().size() * 28 + 25);
		rangedCombatTable.setMinHeight(rangedCombatTable.getItems().size() * 28 + 25);
		shieldsTable.setPrefHeight(shieldsTable.getItems().size() * 28 + 25);
		shieldsTable.setMinHeight(shieldsTable.getItems().size() * 28 + 25);
		defensiveWeaponsTable.setPrefHeight(defensiveWeaponsTable.getItems().size() * 28 + 25);
		defensiveWeaponsTable.setMinHeight(defensiveWeaponsTable.getItems().size() * 28 + 25);
		armorTable.setPrefHeight(armorTable.getItems().size() * 28 + 25);
		armorTable.setMinHeight(armorTable.getItems().size() * 28 + 25);
	}

	@Override
	protected Node getControl() {
		return pane;
	}

	@Override
	protected String getText() {
		return "Kampf";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		final FXMLLoader fxmlLoader = new FXMLLoader();

		fxmlLoader.setController(this);

		try {
			fxmlLoader.load(getClass().getResource("Fight.fxml").openStream());
		} catch (final Exception e) {
			ErrorLogger.logError(e);
		}

		super.init();

		closeCombatTable.prefWidthProperty().bind(pane.widthProperty().subtract(17));

		closeCombatNameColumn.getStyleClass().add("left-aligned");

		GUIUtil.autosizeTable(closeCombatTable, 0, 0);
		GUIUtil.cellValueFactories(closeCombatTable, "name", "type", "ebe", "tp", "at", "pa", "ini", "dk", "bf");

		closeCombatTypeColumn.setCellFactory(p -> {
			final ComboBoxTableCell<CloseCombatWeapon, String> cell = new ComboBoxTableCell<CloseCombatWeapon, String>() {
				@Override
				public void updateItem(final String item, final boolean empty) {
					super.updateItem(item, empty);
					if (!empty) {
						setPadding(Insets.EMPTY);
						final ComboBox<String> comboBox = new ComboBox<>(getItems());
						comboBox.itemsProperty().bind(getTableView().getItems().get(getIndex()).talentsProperty());
						comboBox.setMaxWidth(Double.MAX_VALUE);
						comboBox.getSelectionModel().select(item);
						comboBox.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
							if (newValue != null) {
								getTableView().getItems().get(getIndex()).setType(newValue);
							}
						});
						setGraphic(comboBox);
						setText(null);
					}
				}
			};
			return cell;
		});
		closeCombatEBEColumn.setCellFactory(UiUtil.signedIntegerCellFactory);
		closeCombatPAColumn.setCellFactory(UiUtil.integerCellFactory);
		closeCombatIniColumn.setCellFactory(UiUtil.signedIntegerCellFactory);
		closeCombatBFColumn.setCellFactory(o -> new IntegerSpinnerTableCell<>(-12, 12, 1, false));
		closeCombatBFColumn.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setBf(t.getNewValue()));

		rangedCombatTable.prefWidthProperty().bind(pane.widthProperty().subtract(17));
		rangedCombatNameColumn.getStyleClass().add("left-aligned");

		GUIUtil.autosizeTable(rangedCombatTable, 0, 0);
		GUIUtil.cellValueFactories(rangedCombatTable, "name", "type", "ebe", "tp", "at", "load", "distance", "distancetp", "ammunition");

		rangedCombatTypeColumn.setCellFactory(p -> {
			final ComboBoxTableCell<RangedWeapon, String> cell = new ComboBoxTableCell<RangedWeapon, String>() {

				@Override
				public void updateItem(final String item, final boolean empty) {
					super.updateItem(item, empty);
					if (!empty) {
						setPadding(Insets.EMPTY);
						final ComboBox<String> comboBox = new ComboBox<>(getItems());
						comboBox.itemsProperty().bindBidirectional(getTableView().getItems().get(getIndex()).talentsProperty());
						comboBox.setMaxWidth(Double.MAX_VALUE);
						comboBox.getSelectionModel().select(item);
						comboBox.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
							if (newValue != null) {
								getTableView().getItems().get(getIndex()).setType(newValue);
							}
						});
						setGraphic(comboBox);
						setText(null);
					}
				}
			};
			return cell;
		});
		rangedCombatAmmunitionColumn.setCellFactory(o -> new GraphicTableCell<RangedWeapon, String>(false) {
			@Override
			protected void createGraphic() {
				if (!"Pfeile".equals(getItem()) && !"Bolzen".equals(getItem())) {
					final ReactiveSpinner<Integer> spinner = new ReactiveSpinner<>(0, 999);
					spinner.setEditable(true);
					createGraphic(spinner, () -> spinner.getValue().toString(), t -> spinner.getValueFactory().setValue(Integer.valueOf(t)));
				}
			}

			@Override
			public void updateItem(final String item, final boolean empty) {
				if ("Pfeile".equals(item) || "Bolzen".equals(item)) {
					final Button button = new Button(item);
					button.setOnAction(o -> {
						final int row = getTableRow().getIndex();
						final CellEditEvent<RangedWeapon, String> editEvent = new CellEditEvent<>(rangedCombatTable,
								new TablePosition<>(rangedCombatTable, row, rangedCombatAmmunitionColumn), TableColumn.editCommitEvent(), item);
						Event.fireEvent(rangedCombatAmmunitionColumn, editEvent);
					});
					setText(null);
					setGraphic(button);
				} else {
					super.updateItem(item, empty);
				}
			}
		});
		rangedCombatAmmunitionColumn.setOnEditCommit((final CellEditEvent<RangedWeapon, String> t) -> {
			if ("Pfeile".equals(t.getNewValue()) || "Bolzen".equals(t.getNewValue())) {
				new AmmunitionDialog(pane.getScene().getWindow(), t.getTableView().getItems().get(t.getTablePosition().getRow()));
			} else {
				t.getTableView().getItems().get(t.getTablePosition().getRow()).setAmmunition(Integer.valueOf(t.getNewValue()));
			}
		});

		shieldsTable.prefWidthProperty().bind(pane.widthProperty().subtract(17));

		shieldsNameColumn.getStyleClass().add("left-aligned");

		GUIUtil.autosizeTable(shieldsTable, 0, 0);
		GUIUtil.cellValueFactories(shieldsTable, "name", "at", "pa", "ini", "bf");

		shieldsIniColumn.setCellFactory(UiUtil.signedIntegerCellFactory);
		shieldsBFColumn.setCellFactory(o -> new IntegerSpinnerTableCell<>(-12, 12, 1, false));
		shieldsBFColumn.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setBf(t.getNewValue()));

		defensiveWeaponsTable.prefWidthProperty().bind(pane.widthProperty().subtract(17));

		defensiveWeaponsNameColumn.getStyleClass().add("left-aligned");

		GUIUtil.autosizeTable(defensiveWeaponsTable, 0, 0);
		GUIUtil.cellValueFactories(defensiveWeaponsTable, "name", "at", "pa", "ini", "bf");

		defensiveWeaponsPAColumn.setCellFactory(UiUtil.signedIntegerCellFactory);
		defensiveWeaponsIniColumn.setCellFactory(UiUtil.signedIntegerCellFactory);
		defensiveWeaponsBFColumn.setCellFactory(o -> new IntegerSpinnerTableCell<>(-12, 12, 1, false));
		defensiveWeaponsBFColumn.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setBf(t.getNewValue()));

		final String armorSetting = Settings.getSettingStringOrDefault("Zonenrüstung", "Kampf", "Rüstungsart");

		armorTable.prefWidthProperty().bind(pane.widthProperty().subtract(17));

		armorNameColumn.getStyleClass().add("left-aligned");

		DoubleBinding armorWidth = armorTable.widthProperty().subtract(0);
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

		armorNameColumn.prefWidthProperty().bind(armorWidth);
		if ("Gesamtrüstung".equals(armorSetting)) {
			GUIUtil.cellValueFactories(armorTable, "name", "head", "breast", "back", "belly", "larm", "rarm", "lleg", "rleg", "totalrs", "totalbe");
		} else {
			GUIUtil.cellValueFactories(armorTable, "name", "head", "breast", "back", "belly", "larm", "rarm", "lleg", "rleg", "zoners", "zonebe");
		}
	}

	@Override
	public void setHero(final JSONObject hero) {
		if (this.hero != null) {
			this.hero.removeListener(listener);
		}
		super.setHero(hero);
	}

	@Override
	protected void update() {
		fillTables();
		hero.addListener(listener);
	}
}
