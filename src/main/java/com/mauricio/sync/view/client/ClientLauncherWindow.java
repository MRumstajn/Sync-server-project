package com.mauricio.sync.view.client;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientLauncherWindow extends Stage {
    private TextField ipField;
    private TextField portField;
    private TextField passwordField;
    private IClientLauncherWindowListener listener;

    public ClientLauncherWindow(){
        initWindow();
    }

    private void initWindow(){
        VBox root = new VBox();
        root.spacingProperty().set(10);

        HBox hBox = new HBox();
        hBox.spacingProperty().set(10);

        VBox left = new VBox();
        left.spacingProperty().set(10);
        left.getChildren().add(new Label("IP:"));
        left.getChildren().add(new Label("Port:"));
        left.getChildren().add(new Label("Password:"));

        hBox.getChildren().add(left);

        VBox right = new VBox();
        right.spacingProperty().set(2);
        ipField = new TextField();
        portField = new TextField();
        passwordField = new TextField();
        right.getChildren().add(ipField);
        right.getChildren().add(portField);
        right.getChildren().add(passwordField);

        hBox.getChildren().add(right);

        root.getChildren().add(hBox);

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        Button button = new Button("Connect");
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
                String password = passwordField.getText();
                String ip = ipField.getText();
                if (ip.length() == 0){
                    showErrorDialog("IP is required");
                }
                listener.onConnect(ip, port, password);
            }
        });
        buttonBox.getChildren().add(button);

        root.getChildren().add(buttonBox);

        Scene scene = new Scene(root);
        setScene(scene);
        setResizable(false);
        show();
    }


    public void setListener(IClientLauncherWindowListener listener) {
        this.listener = listener;
    }

    private void showErrorDialog(String msg){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.show();
    }
}
