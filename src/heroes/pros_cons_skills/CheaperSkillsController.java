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
package heroes.pros_cons_skills;

import dsa41basis.hero.ProOrCon;
import dsa41basis.util.DSAUtil;
import dsa41basis.util.HeroUtil;
import dsatool.resources.ResourceManager;
import dsatool.util.IntegerSpinnerTableCell;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.cell.PropertyValueFactory;
import jsonant.value.JSONArray;
import jsonant.value.JSONObject;

public class CheaperSkillsController extends ProsOrConsController {

	public CheaperSkillsController(ScrollPane parent) {
		super(parent, "Verbilligte Sonderfertigkeiten", "Sonderfertigkeit", true, true, null, "Verbilligte Sonderfertigkeiten",
				new SimpleBooleanProperty(true));

		valueColumn.setText("Verbilligungen");
		valueColumn.setPrefWidth(100);
		valueColumn.setCellValueFactory(new PropertyValueFactory<ProOrCon, Integer>("numCheaper"));
		valueColumn.setCellFactory(o -> new IntegerSpinnerTableCell<>(1, 9, 1, false));
		valueColumn.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setNumCheaper(t.getNewValue()));

		deleteItem.setOnAction(o -> {
			final JSONObject actual = hero.getObj(category);
			final ProOrCon item = table.getSelectionModel().getSelectedItem();
			if (item != null) {
				final String skillName = item.getName();
				final JSONObject skill = HeroUtil.findSkill(skillName);
				if (skill.containsKey("Auswahl") || skill.containsKey("Freitext")) {
					actual.getArr(skillName).remove(item.getActual());
				} else {
					actual.removeKey(skillName);
				}
				actual.notifyListeners(null);
				fillList();
			}
		});
	}

	@Override
	@FXML
	protected void add() {
		final JSONObject actual = hero.getObj(category);
		final String skillName = list.getSelectionModel().getSelectedItem();
		final JSONObject skill = HeroUtil.findSkill(skillName);

		JSONObject actualProOrCon;
		if (skill.containsKey("Auswahl") || skill.containsKey("Freitext")) {
			JSONArray proOrConList = actual.getArr(skillName);
			if (proOrConList == null) {
				proOrConList = new JSONArray(actual);
				actual.put(skillName, proOrConList);
			}
			actualProOrCon = new JSONObject(proOrConList);
			proOrConList.add(actualProOrCon);
		} else {
			actualProOrCon = new JSONObject(actual);
			actual.put(skillName, actualProOrCon);
		}
		actual.notifyListeners(null);
	}

	@Override
	protected void fillList() {
		list.getItems().clear();

		final JSONObject actual = hero.getObj(category);

		final JSONObject specialSkills = ResourceManager.getResource("data/Sonderfertigkeiten");
		final JSONObject rituals = ResourceManager.getResource("data/Rituale");
		final JSONObject liturgies = ResourceManager.getResource("data/Liturgien");
		final JSONObject shamanistic = ResourceManager.getResource("data/Schamanenrituale");

		DSAUtil.foreach(group -> true, (groupName, group) -> {
			DSAUtil.foreach(skill -> true, (skillName, skill) -> {
				if (!actual.containsKey(skillName) || skill.containsKey("Auswahl") || skill.containsKey("Freitext")) {
					list.getItems().add(skillName);
				}
			}, group);
		}, specialSkills, rituals);
		DSAUtil.foreach(skill -> true, (skillName, skill) -> {
			if (!actual.containsKey(skillName) || skill.containsKey("Auswahl") || skill.containsKey("Freitext")) {
				list.getItems().add(skillName);
			}
		}, liturgies, shamanistic);

		if (list.getItems().size() > 0) {
			list.getSelectionModel().select(0);
			addButton.setDisable(false);
		}
	}

	@Override
	protected void fillTable() {
		table.getItems().clear();

		final JSONObject actual = hero.getObj(category);

		for (final String skillName : actual.keySet()) {
			final JSONObject skill = HeroUtil.findSkill(skillName);
			if (skill == null) {
				continue;
			}
			if (skill.containsKey("Auswahl") || skill.containsKey("Freitext")) {
				final JSONArray list = actual.getArr(skillName);
				for (int i = 0; i < list.size(); ++i) {
					table.getItems().add(new ProOrCon(skillName, hero, skill, list.getObj(i)));
				}
			} else {
				table.getItems().add(new ProOrCon(skillName, hero, skill, actual.getObj(skillName)));
			}
		}

		table.setPrefHeight(table.getItems().size() * 28 + 26);
		table.setMinHeight(table.getItems().size() * 28 + 26);
	}
}
