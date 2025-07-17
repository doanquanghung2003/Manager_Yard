package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import bean.TimeSlot;

public class ModalHoursController implements Initializable {

    @FXML private FlowPane flp_modalHours;
    @FXML private Button btn_confirm;

    private final List<String> timeSlots = generateTimeSlots();
    private final Map<String, Button> buttonMap = new HashMap<>();
    private final Set<String> bookedSlots = new HashSet<>();
    private final Set<String> pastSlots = new HashSet<>();
    private final LinkedHashSet<String> selectedSlots = new LinkedHashSet<>();
    private boolean confirmed = false;

    public boolean isConfirmed() {
        return confirmed;
    }

    private LocalDate bookingDate;
    private String firstClick = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	btn_confirm.setOnAction(e -> {
    	    if (selectedSlots.isEmpty()) {
    	        showAlert("Bạn chưa chọn khung giờ nào!");
    	    } else {
    	        confirmed = true; 
    	        Stage stage = (Stage) btn_confirm.getScene().getWindow();
    	        stage.close();
    	    }
    	});


    }

    private static List<String> generateTimeSlots() {
        List<String> slots = new ArrayList<>();
        LocalTime start = LocalTime.of(5, 0);
        LocalTime end = LocalTime.of(22, 0);
        while (!start.isAfter(end)) {
            slots.add(start.format(DateTimeFormatter.ofPattern("HH:mm")));
            start = start.plusMinutes(30);
        }
        return slots;
    }

    private void createTimeButtons() {
        flp_modalHours.getChildren().clear();
        buttonMap.clear();

        for (String time : timeSlots) {
            Button btn = new Button(time);
            btn.setPrefWidth(100);

            if (bookedSlots.contains(time)) {
                btn.setDisable(true);
                btn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            } else if (pastSlots.contains(time)) {
                btn.setDisable(true);
                btn.setStyle("-fx-background-color: black; -fx-text-fill: white;"); 
            } else {
                btn.setOnAction(e -> handleTimeSelection(time));
            }

            flp_modalHours.getChildren().add(btn);
            buttonMap.put(time, btn);
        }
    }

    private void handleTimeSelection(String time) {
       
        if (selectedSlots.contains(time)) {
            selectedSlots.remove(time);
            highlightButton(time, "reset");
            return;
        }

        if (firstClick == null) {
            firstClick = time;
            highlightButton(time, "start");
        } else {
            int startIndex = timeSlots.indexOf(firstClick);
            int endIndex = timeSlots.indexOf(time);

            if (startIndex > endIndex) {
                int temp = startIndex;
                startIndex = endIndex;
                endIndex = temp;
            }

            for (int i = startIndex; i <= endIndex; i++) {
                String slot = timeSlots.get(i);
                if (!bookedSlots.contains(slot) && !pastSlots.contains(slot)) {
                    selectedSlots.add(slot);
                    highlightButton(slot, "selected");
                }
            }

            highlightButton(firstClick, "selected"); 
            firstClick = null;
        }
    }

    private void highlightButton(String time, String mode) {
        Button btn = buttonMap.get(time);
        if (btn == null) return;

        switch (mode) {
            case "start":
                btn.setStyle("-fx-background-color: yellow;");
                break;
            case "selected":
                btn.setStyle("-fx-background-color: lightgreen;");
                break;
            case "reset":
                btn.setStyle("");
                break;
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void setBookingDate(LocalDate date) {
        this.bookingDate = date;
        lockPastTimeSlots();
        maybeRenderSlots();
    }

    public void setBookedSlots(List<String> bookedTimes) {
        bookedSlots.clear();
        if (bookedTimes != null) {
            bookedSlots.addAll(bookedTimes);
        }
        maybeRenderSlots();
    }

    private void maybeRenderSlots() {
        if (this.bookingDate != null) {
            createTimeButtons();
        }
    }

    private void lockPastTimeSlots() {
        pastSlots.clear();
        if (bookingDate != null && bookingDate.equals(LocalDate.now())) {
            LocalTime now = LocalTime.now();
            for (String time : timeSlots) {
                LocalTime slotTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
                if (slotTime.plusMinutes(30).isBefore(now)) {
                    pastSlots.add(time);
                }
            }
        }
    }
    public List<TimeSlot> getSelectedTimeSlots() {
        List<TimeSlot> list = new ArrayList<>();
        for (String time : selectedSlots) {
            LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            LocalDateTime start = LocalDateTime.of(bookingDate, localTime);
            LocalDateTime end = start.plusMinutes(30);

            TimeSlot ts = new TimeSlot();
            ts.setSlotId(UUID.randomUUID().toString());
            ts.setStartTime(start);
            ts.setEndTime(end);
            ts.setBooked(false); 
            ts.setSlotType("maintenance");
            list.add(ts);
        }
        return list;
    }

    public List<String> getSelectedTimeRanges() {
        return new ArrayList<>(selectedSlots);
    }

    public LocalTime getSelectedStartTime() {
        if (selectedSlots.isEmpty()) return null;
        return selectedSlots.stream()
            .map(time -> LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")))
            .min(LocalTime::compareTo)
            .orElse(null);
    }

    public LocalTime getSelectedEndTime() {
        if (selectedSlots.isEmpty()) return null;
        return selectedSlots.stream()
            .map(time -> LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")))
            .max(LocalTime::compareTo)
            .orElse(null);
    }

}
