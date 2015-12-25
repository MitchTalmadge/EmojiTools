/*
 * Emoji Tools helps users and developers of Android, iOS, and OS X extract, modify, and repackage Emoji fonts.
 * Copyright (C) 2015 Mitch Talmadge
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact Mitch Talmadge at mitcht@liveforcode.net
 */

package net.liveforcode.EmojiTools2.GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import net.liveforcode.EmojiTools2.Versioning;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Label donationLabel;

    @FXML
    private Label copyrightLabel;

    @FXML
    private Label headerLabel;

    @FXML
    private TabPane tabPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.headerLabel.setText(new Versioning().getProgramNameWithVersion());

        addTabs();
    }

    private void addTabs() {
        try {
            tabPane.getTabs().addAll(
                    new Tab("Extractor", (Parent) FXMLLoader.load(getClass().getResource("/GUI/Tabs/Extractor.fxml"))),
                    new Tab("Renamer", (Parent) FXMLLoader.load(getClass().getResource("/GUI/Tabs/Renamer.fxml"))),
                    new Tab("Converter", (Parent) FXMLLoader.load(getClass().getResource("/GUI/Tabs/Converter.fxml"))),
                    new Tab("Packager", (Parent) FXMLLoader.load(getClass().getResource("/GUI/Tabs/Packager.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onDonateLabelFired(MouseEvent mouseEvent) {
        try {
            Desktop.getDesktop().browse(new URI("https://donate.liveforcode.net"));
        } catch (IOException | URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

    @FXML
    void onCopyrightLabelFired(MouseEvent mouseEvent) {
        try {
            Desktop.getDesktop().browse(new URI("https://liveforcode.net"));
        } catch (IOException | URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

}