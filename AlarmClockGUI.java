package com.example.alarmclock;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmClockGUI extends Application {
    private final Timer timer;
    private LocalDateTime alarmDateTime;
    private boolean alarmTriggered;
    private Label timeLabel;
    private Label dateLabel;
    private TextField hourField;
    private TextField minuteField;
    private DatePicker datePicker;
    private Button setAlarmButton;

    public AlarmClockGUI() {
        timer = new Timer();
        alarmDateTime = null;
        alarmTriggered = false;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Relógio");

        // Cria os componentes da interface
        dateLabel = new Label();
        dateLabel.setStyle("-fx-font-size: 36px;");

        timeLabel = new Label();
        timeLabel.setStyle("-fx-font-size: 64px;");

        Label hourLabel = new Label("Horas: ");
        hourField = new TextField();
        hourField.setMaxWidth(60);

        Label minuteLabel = new Label("Minutos: ");
        minuteField = new TextField();
        minuteField.setMaxWidth(60);

        Label dateLabel2 = new Label("Data:");
        datePicker = new DatePicker();
        datePicker.setMaxWidth(120);
        datePicker.setValue(LocalDate.now());

        setAlarmButton = new Button("Acionar Alarme");
        setAlarmButton.setOnAction(e -> setAlarm());

        HBox hbox = new HBox(timeLabel, dateLabel);
        hbox.setSpacing(20);
        hbox.setAlignment(Pos.CENTER);

        HBox alarmBox = new HBox(hourLabel, hourField, minuteLabel, minuteField,dateLabel2, datePicker, setAlarmButton);
        alarmBox.setSpacing(5);
        hbox.setAlignment(Pos.CENTER);
        
        VBox vbox = new VBox(hbox, alarmBox);
        vbox.setSpacing(10);

        Scene scene = new Scene(vbox, 580, 150);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Inicia o relógio em segundo plano
        Task<Void> clockTask = createClockTask();
        Thread clockThread = new Thread(clockTask);
        clockThread.setDaemon(true);
        clockThread.start();
    }

    private Task<Void> createClockTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                TimerTask timerTask = new TimerTask() {
                    public void run() {
                        Platform.runLater(() -> {
                            datePicker.setValue(LocalDate.now());
                            LocalTime currentTime = LocalTime.now();
                            String timeString = currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                            timeLabel.setText(timeString); dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                            LocalDate currentDate = LocalDate.now();
                            String dateString = currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                            dateLabel.setText(dateString);
                            
                            if (!alarmTriggered && alarmDateTime != null && LocalDateTime.now().isAfter(alarmDateTime)) {
                                showAlert("ACORDA VIADO!");
                                alarmTriggered = true;
                            }
                        });
                    }
                };

                timer.scheduleAtFixedRate(timerTask, 0, 1000); // Executa a cada 1 segundo

                return null;
            }
        };
    }

    private void setAlarm() {
        String hourText = hourField.getText();
        String minuteText = minuteField.getText();
        LocalDate selectedDate = datePicker.getValue();

        try {
            int hour = Integer.parseInt(hourText);
            int minute = Integer.parseInt(minuteText);

            if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                showAlert("Hora inválida! Por favor, insira uma hora válida (0-23) e um minuto válido (0-59).");
            } else if (selectedDate == null) {
                showAlert("Data inválida! Por favor, selecione uma data.");
            } else {
                alarmDateTime = LocalDateTime.of(selectedDate, LocalTime.of(hour, minute));
                showAlert("Alarme definido para " + alarmDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
        } catch (NumberFormatException e) {
            showAlert("Formato de hora ou minuto inválido! Por favor, insira números inteiros.");
        }
    }

    private void showAlert(String message) {

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Relógio");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.initOwner(null);

            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
