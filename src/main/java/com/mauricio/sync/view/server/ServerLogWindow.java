package com.mauricio.sync.view.server;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ServerLogWindow extends Stage {
    private TextArea console;

    public ServerLogWindow(){
        initWindow();
    }

    private void initWindow(){
        console = new TextArea();
        console.setEditable(false);

        Scene scene = new Scene(console);
        setScene(scene);
        initModality(Modality.WINDOW_MODAL);
    }

    public void appendToConsole(String text){
        console.appendText(text + "\n");
    }
}
