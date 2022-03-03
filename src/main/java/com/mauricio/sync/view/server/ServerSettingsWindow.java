package com.mauricio.sync.view.server;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author Mauricio Rumštajn
 */
public class ServerSettingsWindow extends Stage {
    private CheckBox userPasswordCheckBox;
    private TextField passwordField;
    private Button applyButton;
    private IServerSettingsWindowListener listener;

    public ServerSettingsWindow(){
        initWindow();
    }

    /**
     * Init components
     */
    private void initWindow(){
        VBox root = new VBox();
        root.setPadding(new Insets(10, 10, 10, 10));

        // password field

        HBox pswBox = new HBox();
        userPasswordCheckBox = new CheckBox();
        passwordField = new TextField();
        pswBox.getChildren().add(userPasswordCheckBox);
        pswBox.getChildren().add(new Label("Password:"));
        pswBox.getChildren().add(passwordField);

        root.getChildren().add(pswBox);

        // btn
        applyButton = new Button("Apply");
        applyButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean usePsw = userPasswordCheckBox.isSelected();
                String psw = "";
                if (usePsw){
                    psw = passwordField.getText();
                }
                listener.onApply(usePsw, psw);
            }
        });
        root.getChildren().add(applyButton);

        Scene scene = new Scene(root);
        setScene(scene);
        show();
    }

    /**
     * Set window listener.
     *
     * @param listener
     */
    public void setListener(IServerSettingsWindowListener listener) {
        this.listener = listener;
    }
}
