package ru.ndg.cloud.storage.v4.client.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.log4j.Log4j2;
import ru.ndg.cloud.storage.v4.client.network.Network;
import ru.ndg.cloud.storage.v4.common.CallBack;
import ru.ndg.cloud.storage.v4.common.services.FileClientService;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Log4j2
public class MainController implements Initializable, CallBack {

    private Network network;
    private String login;
    private boolean isAuthenticated;
    private boolean isConnected;

    @FXML
    public HBox hBoxConnection;
    @FXML
    public TextField hostField;
    @FXML
    public TextField portField;
    @FXML
    public HBox hBoxAuthentication;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public VBox vBoxWorker;
    @FXML
    public ListView<String> listFilesClient;
    @FXML
    public ListView<String> listFilesServer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listFilesClient.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listFilesServer.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        refreshScene();
    }

    public void connectionServer(ActionEvent actionEvent) {
        String host = this.hostField.getText();
        String port = this.portField.getText();
        if (isHostPortCorrect(host, port)) {
            CountDownLatch latch = new CountDownLatch(1);
            Thread t = new Thread(() -> {
                this.network = Network.builder()
                        .host(host)
                        .port(Integer.parseInt(port))
                        .callBack(this)
                        .build();

                this.network.run(latch);
            });
            t.setDaemon(true);
            t.start();
            try {
                latch.await();
                this.isConnected = true;
                refreshScene();
            } catch (InterruptedException e) {
                log.debug(e);
                this.network = null;
                this.isAuthenticated = false;
            }
        } else {
            String message = "Введите корректные данные для подключения!";
            showAlert(message);
        }
    }

    public void authentication(ActionEvent actionEvent) {
        this.login = loginField.getText();
        String password = passwordField.getText();
        if (isLoginPasswordCorrect(login, password)) {
            this.network.authentication(login, password);
        } else {
            String message = "Введите логин и пароль!";
            showAlert(message);
        }
    }

    public void registration(ActionEvent actionEvent) {
        this.login = loginField.getText();
        String password = passwordField.getText();
        if (isLoginPasswordCorrect(login, password)) {
            this.network.registration(login, password);
        } else {
            String message = "Введите логин и пароль!";
            showAlert(message);
        }
    }

    public void uploadFile(ActionEvent actionEvent) {
        String selectedItem = this.listFilesClient.getSelectionModel().getSelectedItem();
        if (selectedItem != null && !selectedItem.isEmpty()) {
            this.network.uploadFile(this.login, selectedItem);
        }
    }

    public void downloadFile(ActionEvent actionEvent) {
        String selectedItem = this.listFilesServer.getSelectionModel().getSelectedItem();
        if (selectedItem != null && !selectedItem.isEmpty()) {
            this.network.downloadFile(this.login, selectedItem);
        }
    }

    public void deleteFile(ActionEvent actionEvent) {
        String selectedItem = this.listFilesServer.getSelectionModel().getSelectedItem();
        if (selectedItem != null && !selectedItem.isEmpty()) {
            this.network.deleteFile(this.login, selectedItem);
        }
    }

    public void renameFile(ActionEvent actionEvent) {
        String selectedItem = this.listFilesServer.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedItem.isEmpty()) return;
        TextInputDialog dialog = new TextInputDialog(selectedItem);
        dialog.setHeaderText("Введите новое имя файла");
        dialog.setContentText("Имя файла");
        Optional<String> optional = dialog.showAndWait();
        String newFileName = optional.orElse("");
        if (newFileName.isEmpty()) return;
        this.network.renameFile(this.login, selectedItem, newFileName);
    }

    public void refreshFiles(ActionEvent actionEvent) {
        refreshListFilesServer();
    }

    private boolean isHostPortCorrect(String host, String port) {
        if (host.isEmpty() || port.isEmpty()) return false;
        try {
            int portInteger = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            log.debug(e);
            return false;
        }
        return true;
    }

    private boolean isLoginPasswordCorrect(String login, String password) {
        return !login.isEmpty() && !password.isEmpty();
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
            alert.show();
        });
    }

    private void refreshListFilesServer() {
        this.network.getListFile(this.login);
    }

    private void refreshListFilesClient() {
        Platform.runLater(() -> {
            Path path = Paths.get(FileClientService.CLIENT_STORAGE_DIRECTORY);
            String listFiles = "";
            try {
                listFiles = Files.list(path).map(p -> p.getFileName().toString()).collect(Collectors.joining(","));
                this.listFilesClient.getItems().clear();
                this.listFilesClient.getItems().addAll(listFiles.split(","));
            } catch (IOException e) {
                log.debug(e);
            }
        });
    }

    private void refreshListFilesServer(String fileList) {
        Platform.runLater(() -> {
            this.listFilesServer.getItems().clear();
            this.listFilesServer.getItems().addAll(fileList.split(","));
        });
    }

    private void refreshScene() {
        if (!this.isConnected) {
            hBoxAuthentication.setVisible(false);
            hBoxAuthentication.setManaged(false);
            hBoxConnection.setVisible(true);
            hBoxConnection.setManaged(true);
            vBoxWorker.setVisible(false);
            vBoxWorker.setManaged(false);
        } else {
            if (!this.isAuthenticated) {
                hBoxAuthentication.setVisible(true);
                hBoxAuthentication.setManaged(true);
                hBoxConnection.setVisible(false);
                hBoxConnection.setManaged(false);
                vBoxWorker.setVisible(false);
                vBoxWorker.setManaged(false);
            } else {
                hBoxAuthentication.setVisible(false);
                hBoxAuthentication.setManaged(false);
                vBoxWorker.setVisible(true);
                vBoxWorker.setManaged(true);
            }
        }
    }

    @Override
    public void authenticationCallback(boolean result) {
        if (result) {
            this.isAuthenticated = true;
            refreshScene();
            refreshListFilesServer();
            refreshListFilesClient();
        } else {
            this.isAuthenticated = false;
        }
    }

    @Override
    public void registrationCallback(boolean result) {
        if (result) {
            this.isAuthenticated = true;
            refreshScene();
            refreshListFilesServer();
            refreshListFilesClient();
        } else {
            this.isAuthenticated = false;
        }
    }

    @Override
    public void fileListCallback(String fileList) {
        if (fileList != null && !fileList.isEmpty()) {
            refreshListFilesServer(fileList);
        }
    }

    @Override
    public void fileUploadCallback(String fileList) {
        if (fileList != null && !fileList.isEmpty()) {
            refreshListFilesServer(fileList);
        }
    }

    @Override
    public void fileDownloadCallback(String fileName, byte[] data) {
        if (data == null) return;
        Path path = Paths.get(FileClientService.CLIENT_STORAGE_DIRECTORY, fileName);
        try {
            Files.write(path, data, StandardOpenOption.CREATE);
            refreshListFilesClient();
        } catch (IOException e) {
            log.debug(e);
        }
    }

    @Override
    public void fileDeleteCallback(String fileList) {
            refreshListFilesServer(fileList);
    }

    @Override
    public void fileRenameCallback(String fileList) {
        if (fileList != null && !fileList.isEmpty()) {
            refreshListFilesServer(fileList);
        }
    }
}