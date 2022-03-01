package com.mauricio.sync.view.server;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerLauncherWindow extends Stage {
    private TextField passwordField;
    private IServerLauncherWindowListener listener;

    public ServerLauncherWindow(){
        initWindow();
    }

    private void initWindow(){
        VBox root = new VBox();
        root.spacingProperty().set(10);

        HBox hBox = new HBox();
        hBox.spacingProperty().set(10);

        VBox left = new VBox();
        left.spacingProperty().set(10);
        left.getChildren().add(new Label("Port:"));
        HBox pswRow = new HBox();
        CheckBox usePasswordCheckBox = new CheckBox();
        usePasswordCheckBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                passwordField.setDisable(!usePasswordCheckBox.isSelected());
            }
        });
        pswRow.getChildren().add(usePasswordCheckBox);
        pswRow.getChildren().add(new Label("Password:"));
        left.getChildren().add(pswRow);

        hBox.getChildren().add(left);

        VBox right = new VBox();
        right.spacingProperty().set(2);
        TextField portField = new TextField();
        passwordField = new TextField();
        passwordField.setDisable(true);
        right.getChildren().add(portField);
        right.getChildren().add(passwordField);

        hBox.getChildren().add(right);

        root.getChildren().add(hBox);

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        Button button = new Button("Start");
        button.setPrefWidth(100);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int port;
                try {
                    port = Integer.parseInt(portField.getText());
                } catch (NumberFormatException e){
                    showErrorDialog("Port must be a positive integer");
                    return;
                }
                boolean usePassword = usePasswordCheckBox.isSelected();
                String password = passwordField.getText();
                if (password.length() == 0 && usePassword){
                    showErrorDialog("Password is required");
                    return;
                }
                listener.onStart(port, usePassword, password);
            }
        });
        buttonBox.getChildren().add(button);

        root.getChildren().add(buttonBox);

        Scene scene = new Scene(root);
        setScene(scene);
        show();
    }

    private void showErrorDialog(String msg){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.show();
    }

    public void setListener(IServerLauncherWindowListener listener) {
        this.listener = listener;
    }
}
