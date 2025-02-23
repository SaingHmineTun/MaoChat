package it.saimao.maochat.controller;

import it.saimao.maochat.model.ChatMessage;
import it.saimao.maochat.tool.ChatMessageCellFactory;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatController implements Initializable {

    @FXML
    private Button btSend;

    @FXML
    private ListView<ChatMessage> lvMessages;

    @FXML
    private TextField tfMessage;


    private BufferedReader bufferedReader;
    private PrintWriter printWriter;


    public ChatController(BufferedReader bufferedReader, PrintWriter printWriter) {
        this.bufferedReader = bufferedReader;
        this.printWriter = printWriter;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvMessages.setCellFactory(new ChatMessageCellFactory());
        startReaderThread();
        tfMessage.setOnAction(event -> {
            btSend.fire();
        });
        btSend.setOnAction(event -> {
            String message = tfMessage.getText();
            printWriter.println(message);
            lvMessages.getItems().add(new ChatMessage("Me", message, new Date()));
            tfMessage.clear();
        });
    }

    private void startReaderThread() {
        ExecutorService readerService = Executors.newVirtualThreadPerTaskExecutor();
        readerService.submit(() -> {
            String message;
            while (true) {
                try {
                    message = bufferedReader.readLine();
                    String finalMessage = message;
                    Platform.runLater(() -> lvMessages.getItems().add(new ChatMessage("Others", finalMessage, new Date())));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
