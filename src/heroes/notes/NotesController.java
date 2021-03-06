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
package heroes.notes;

import com.sun.javafx.scene.web.skin.HTMLEditorSkin;

import heroes.ui.HeroTabController;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.HTMLEditor;
import jsonant.event.JSONListener;
import jsonant.value.JSONObject;
import jsonant.value.JSONValue;

public class NotesController extends HeroTabController implements JSONListener {

	private HTMLEditor text;

	private String currentText;

	public NotesController(TabPane tabPane) {
		super(tabPane);
	}

	@Override
	protected void changeEditable() {
		// Nothing to do here, notes are always editable
	}

	@Override
	protected Node getControl() {
		return text;
	}

	@Override
	protected String getText() {
		return "Notizen";
	}

	@Override
	public void init() {
		text = new HTMLEditor();

		text.setOnKeyReleased(e -> textChanged());
		text.setOnMouseClicked(e -> textChanged());
		for (final Node n : ((GridPane) ((HTMLEditorSkin) text.getSkin()).getChildren().get(0)).getChildren()) {
			n.setOnMouseExited(e -> textChanged());
			n.focusedProperty().addListener((o, oldV, newV) -> {
				if (!newV) {
					textChanged();
				}
			});
		}

		super.init();
	}

	@Override
	public void notifyChanged(JSONValue changed) {
		final String newText = hero.getStringOrDefault("Notizen", "");
		if (!newText.equals(currentText)) {
			currentText = newText;
			text.setHtmlText(currentText);
		}
	}

	@Override
	public void setHero(JSONObject hero) {
		hero.removeListener(this);
		super.setHero(hero);
	}

	private void textChanged() {
		if (!text.getHtmlText().equals(currentText)) {
			currentText = text.getHtmlText();
			hero.put("Notizen", currentText);
			hero.notifyListeners(this);
		}
	}

	@Override
	protected void update() {
		notifyChanged(hero);
		hero.addLocalListener(this);
	}

}
