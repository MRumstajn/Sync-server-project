package com.mauricio.sync.view.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author Mauricio Rumštajn
 */
public class ClientWindow extends Stage {
    private Label ipLabel;
    private ListView<HBox> fileList;
    private TextField pathField;
    private Button browseButton;
    private Button setPathButton;
    private Image fileIcon;
    private Image folderIcon;
    private IClientWindowListener listener;

    public ClientWindow(){
        loadIcons();
        initWindow();
    }

    /**
     * Load icons for files and folders
     */
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

    /**
     * Init components
     */
    private void initWindow(){
        VBox root = new VBox();
        root.setSpacing(10);

        // ip label

        HBox ipLabelBox = new HBox();
        ipLabelBox.getChildren().add(new Label("Connected to:"));
        ipLabel = new Label("-");
        ipLabelBox.getChildren().add(ipLabel);

        root.getChildren().add(ipLabelBox);

        // file list

        fileList = new ListView<>();
        root.getChildren().add(fileList);

        // path field

        HBox pathFieldBox = new HBox();
        pathFieldBox.getChildren().add(new Label("Sync directory:"));
        pathField = new TextField();
        pathFieldBox.getChildren().add(pathField);
        browseButton = new Button("Browse");
        browseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selected = directoryChooser.showDialog(ClientWindow.this);
                if (selected != null){
                    pathField.setText(selected.getAbsolutePath());
                }
            }
        });
        pathFieldBox.getChildren().add(browseButton);
        setPathButton = new Button("Apply");
        setPathButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String path = pathField.getText();
                if (path.length() > 0){
                    File file = new File(path);
                    if (file.exists()){
                        listener.onSetSyncDir(file);
                    } else {
                        showErrorDialog("File does not exist");
                    }
                }

            }
        });
        pathFieldBox.getChildren().add(setPathButton);

        root.getChildren().add(pathFieldBox);

        Scene scene = new Scene(root);
        setScene(scene);
        setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                listener.onWindowClose();
            }
        });
        show();
    }

    /**
     * Set window listener.
     *
     * @param listener
     */
    public void setListener(IClientWindowListener listener) {
        this.listener = listener;
    }

    /**
     * Scale buffered image to w, h.
     *
     * @param src original image.
     * @param w width
     * @param h height
     * @return scaled image.
     */
    private BufferedImage scaleBuffImage(BufferedImage src, int w, int h){
        java.awt.Image scaled = src.getScaledInstance(w, h, BufferedImage.SCALE_FAST);
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) result.getGraphics();
        g2d.drawImage(scaled, 0, 0, null);
        g2d.dispose();
        return result;
    }

    /**
     * Display an error dialog.
     *
     * @param msg
     */
    public void showErrorDialog(String msg){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(msg);
                alert.show();
            }
        });
    }

    /**
     * Display an info dialog.
     * @param msg
     */
    public void showInfoDialog(String msg){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Info");
                alert.setContentText(msg);
                alert.show();
            }
        });

    }

    /**
     * Set ip label text.
     *
     * @param text
     */
    public void setIpLabelText(String text){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ipLabel.setText(text);
            }
        });
    }

    /**
     * Add file to file list.
     *
     * @param filename
     * @param host
     * @param isDir
     */
    public void addFileToList(String filename, String host, boolean isDir){
        System.out.println("adding to client window file list");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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
                        prefix = new ImageView(fileIcon);
                    } else {
                        prefix = new Label("[File]");
                    }
                    row.getChildren().add(prefix);
                }
                row.getChildren().add(new Label(filename));
                row.getChildren().add(new Label("(" + host + ")"));
                Button downloadBtn = new Button("Download");
                downloadBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        downloadBtn.setDisable(true);
                        // get index of item
                        int index = fileList.getItems().indexOf(row);
                        // get info about file on that item
                        HBox item = fileList.getItems().get(index);
                        Node prefix = item.getChildren().get(0);
                        boolean isDir;
                        if (prefix instanceof Label){
                            String prefixText = ((Label) prefix).getText();
                            if (prefixText.equals("[File]")){
                                isDir = false;
                            } else {
                                isDir = true;
                            }
                        } else {
                            if ((((ImageView) prefix).getImage() == fileIcon)){
                                isDir = false;
                            } else {
                                isDir = true;
                            }
                        }
                        String filename = ((Label) item.getChildren().get(1)).getText();
                        String host = ((Label) item.getChildren().get(2)).getText();
                        // notify listener
                        listener.onDownloadFile(filename, isDir, host);
                    }
                });
                row.getChildren().add(downloadBtn);
                fileList.getItems().add(row);
            }
        });
    }

    /**
     * Remove file from file list.
     *
     * @param filename
     * @param host
     * @param isDir
     */
    public void removeFileFromList(String filename, String host, boolean isDir){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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
                    fileList.getItems().remove(item);
                    break;
                }
            }
        });
    }

    /**
     * Clear the file list completely.
     */
    public void clearFileList(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("cleared window");
                fileList.getItems().clear();;
            }
        });

    }
}
