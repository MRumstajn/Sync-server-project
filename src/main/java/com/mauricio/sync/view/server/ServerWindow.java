package com.mauricio.sync.view.server;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ServerWindow extends Stage {
    private TabPane tabPane;
    private Label portLabel;
    private Label statusLabel;
    private Label uptimeLabel;
    private Label clientCountLabel;
    private Label threadCountLabel;
    private ProgressBar cpuBar;
    private ProgressBar memoryBar;
    private ProgressBar networkBar;
    private ListView<HBox> clientList;
    private ListView<HBox> fileList;
    private Image fileIcon;
    private Image folderIcon;
    private IServerWindowListener listener;
    private static final double BUTTON_SIZE = 100;

    public ServerWindow(){
        loadIcons();
        initWindow();
    }

    private void loadIcons() {
        try {
            BufferedImage scaledFile = scaleBuffImage(ImageIO.read(new File("file.png")), 16, 16);
            BufferedImage scaledFolder = scaleBuffImage(ImageIO.read(new File("file.png")), 16, 16);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(scaledFile, "png", baos);
            byte[] bytes = baos.toByteArray();
            fileIcon = new Image(new ByteArrayInputStream(bytes));
            baos.reset();
            ImageIO.write(scaledFolder, "png", baos);
            bytes = baos.toByteArray();
            folderIcon = new Image(new ByteArrayInputStream(bytes));
            baos.close();
        } catch (IOException e){
            showErrorDialog("Failed to load icons, using fallback method");
            e.printStackTrace();
        }
    }

    private void initWindow(){
        VBox root = new VBox();
        root.setPrefWidth(400);
        root.setPadding(new Insets(10, 10, 10, 10));

        // Tab panel

        tabPane = new TabPane();
        tabPane.setPrefHeight(200);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setPadding(new Insets(0, 10, 0, 10));

        // Info tab

        Tab infoTab = new Tab("Info");

        HBox hBox = new HBox();
        hBox.spacingProperty().set(20);

        VBox left = new VBox();
        left.spacingProperty().set(10);
        left.getChildren().add(new Label("Port:"));
        left.getChildren().add(new Label("Server status:"));
        left.getChildren().add(new Label("Uptime:"));
        left.getChildren().add(new Label("Clients connected:"));
        left.getChildren().add(new Label("Thread count:"));

        hBox.getChildren().add(left);

        VBox right = new VBox();
        right.setSpacing(10);
        portLabel = new Label("-");
        statusLabel = new Label("offline");
        uptimeLabel = new Label("0h 0m 0s");
        clientCountLabel = new Label("0");
        threadCountLabel = new Label("0");
        right.getChildren().add(portLabel);
        right.getChildren().add(statusLabel);
        right.getChildren().add(uptimeLabel);
        right.getChildren().add(clientCountLabel);
        right.getChildren().add(threadCountLabel);

        hBox.getChildren().add(right);

        infoTab.setContent(hBox);

        tabPane.getTabs().add(infoTab);

        // Resources tab

        Tab resourcesTab = new Tab("Resources");

        HBox hBoxRes = new HBox();
        hBoxRes.spacingProperty().set(20);

        VBox leftRes = new VBox();
        leftRes.spacingProperty().set(10);
        leftRes.getChildren().add(new Label("CPU:"));
        leftRes.getChildren().add(new Label("Memory:"));
        leftRes.getChildren().add(new Label("Network:"));

        hBoxRes.getChildren().add(leftRes);

        VBox rightRes = new VBox();
        rightRes.setSpacing(10);
        cpuBar = new ProgressBar();
        memoryBar = new ProgressBar();
        networkBar = new ProgressBar();
        rightRes.getChildren().add(cpuBar);
        rightRes.getChildren().add(memoryBar);
        rightRes.getChildren().add(networkBar);

        hBoxRes.getChildren().add(rightRes);

        resourcesTab.setContent(hBoxRes);

        tabPane.getTabs().add(resourcesTab);

        // Clients tab

        Tab clientsTab = new Tab("Clients");

        clientList = new ListView<>();

        clientsTab.setContent(clientList);

        root.getChildren().add(tabPane);

        tabPane.getTabs().add(clientsTab);

        // Control panel

        TitledPane controlBoxPanel = new TitledPane();
        controlBoxPanel.setText("Controls");

        HBox controlPanelBox = new HBox();
        controlPanelBox.setAlignment(Pos.CENTER);
        controlPanelBox.spacingProperty().set(20);

        Button stopBtn = new Button("  Stop  ");
        stopBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                listener.onStopButtonClicked();
            }
        });
        Button logBtn = new Button("  Log   ");
        logBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                listener.onLogButtonClicked();
            }
        });
        Button settingsBtn = new Button("Settings");
        settingsBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                listener.onSettingsButtonClicked();
            }
        });

        controlPanelBox.getChildren().add(stopBtn);
        controlPanelBox.getChildren().add(logBtn);
        controlPanelBox.getChildren().add(settingsBtn);

        controlBoxPanel.setContent(controlPanelBox);
        controlBoxPanel.setCollapsible(false);

        root.getChildren().add(controlBoxPanel);

        // File list

        fileList = new ListView<>();
        fileList.setPrefHeight(200);

        root.getChildren().add(fileList);

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void setListener(IServerWindowListener listener) {
        this.listener = listener;
    }

    public void setPortLabel(String text){
        portLabel.setText(text);
    }

    public void setStatusLabel(String text){
        statusLabel.setText(text);
    }

    public void setUptimeLabel(String text){
        uptimeLabel.setText(text);
    }

    public void setClientCountLabel(String text){
        clientCountLabel.setText(text);
    }

    public void setThreadCountLabel(String text){
        threadCountLabel.setText(text);
    }

    public void setCPUBarPercent(double percent){
        cpuBar.setProgress(percent);
    }

    public void setMemoryBarPercent(double percent){
        memoryBar.setProgress(percent);
    }

    public void setNetworkBarPercent(double percent){
        networkBar.setProgress(percent);
    }

    public void addClientToList(String username, String ip, int port){
        HBox row = new HBox();
        row.getChildren().add(new Label(username));
        row.getChildren().add(new Label("(" + ip + ":" + port + ")"));
        row.getChildren().add(new Button("Disconnect"));
        clientList.getItems().add(row);
    }

    public void removeClientFromList(String username){
        HBox toRemove = null;
        for (HBox item : clientList.getItems()) {
            if (((Label) item.getChildren().get(0)).getText().equals(username)){
                toRemove = item;
                break;
            }
        }
        if (toRemove != null){
            clientList.getItems().remove(toRemove);
        }
    }

    public void addFileToList(String filename, String host, boolean isDir){
        HBox row = new HBox();
        if (isDir) {
            Node prefix;
            if (folderIcon != null){
                prefix = new ImageView(folderIcon);
            } else {
                prefix = new Label("[Folder]");
            }
            row.getChildren().add(prefix);
        } else {
            Node prefix;
            if (fileIcon != null){
                prefix = new ImageView(folderIcon);
            } else {
                prefix = new Label("[File]");
            }
            row.getChildren().add(prefix);
        }
        row.getChildren().add(new Label(filename));
        row.getChildren().add(new Label("(" + host + ")"));
        fileList.getItems().add(row);
    }

    public void removeFileFromList(String filename, String host, boolean isDir){
        for (HBox item : fileList.getItems()) {
            // check if the image/text prefix matches the isDir state
            Node prefix = item.getChildren().get(0);
            if (prefix instanceof ImageView){
                if (((ImageView) prefix).getImage() == fileIcon && isDir){
                    continue;
                } else if (((ImageView) prefix).getImage() == fileIcon && !isDir){
                    continue;
                }
            } else {
                if (((Label) prefix).getText().equals("[File]") && isDir){
                    continue;
                } else if (((Label) prefix).getText().equals("[Folder]") && !isDir){
                    continue;
                }
            }
            // check if filename matches label
            if (!((Label) item.getChildren().get(1)).getText().equals(filename)){
                continue;
            }
            // check if host matches label
            if (!((Label) item.getChildren().get(2)).getText().equals("(" + host + ")")){
                continue;
            }

        }
    }

    private void showErrorDialog(String msg){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.show();
    }

    private BufferedImage scaleBuffImage(BufferedImage src, int w, int h){
        java.awt.Image scaled = src.getScaledInstance(w, h, BufferedImage.SCALE_FAST);
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) result.getGraphics();
        g2d.drawImage(scaled, 0, 0, null);
        g2d.dispose();
        return result;
    }
}
