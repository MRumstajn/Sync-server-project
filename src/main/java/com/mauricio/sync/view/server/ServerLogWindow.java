package com.mauricio.sync.view.server;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Mauricio Rumštajn
 */
public class ServerLogWindow extends Stage {
    private TextArea console;

    public ServerLogWindow(){
        initWindow();
    }

    /**
     * Init components.
     */
    private void initWindow(){
        console = new TextArea();
        console.setEditable(false);

        Scene scene = new Scene(console);
        setScene(scene);
        initModality(Modality.WINDOW_MODAL);
    }

    /**
     * Append text to the console.
     *
     * @param text
     */
    public void appendToConsole(String text){
        console.appendText(text + "\n");
    }
}
