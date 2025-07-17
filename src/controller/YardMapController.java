package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import singleton.DuLieu;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import bean.TimeSlot;
import bean.YardModel;
import bean.BookingModel;
import bean.BookingYardModel;

public class YardMapController implements Initializable {
    @FXML private Label headerLabel;
    @FXML private DatePicker datePicker;
    @FXML private GridPane bookingGrid;
    @FXML private Label noteLabel;
    @FXML private Slider zoomSlider;
    @FXML private ScrollPane scrollPane;
    @FXML private Button btn_maintenance, btn_event;
    private final Map<TimeSlot, YardModel> slotToYardMap = new HashMap<>();
    private final List<TimeSlot> selectedSlots = new ArrayList<>();

    private final String[] timeSlotsWeekday = { "5:00", "5:30", "6:00", "6:30", "7:00", "7:30", "8:00", "8:30", "9:00",
            "9:30", "10:00", "10:30", "11:00", "11:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00",
            "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00",
            "23:30" };

    private final String[] timeSlotsWeekend = { "5:00", "5:30", "6:00", "6:30", "7:00", "7:30", "8:00", "8:30", "9:00",
            "9:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00",
            "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00",
            "21:30", "22:00", "22:30", "23:00", "23:30" };

    private String[] getCurrentTimeSlots() {
        LocalDate currentDate = datePicker.getValue();
        if (currentDate != null && currentDate.getDayOfWeek().getValue() >= 6)
            return timeSlotsWeekend;
        return timeSlotsWeekday;
    }

    private static final double MIN_CELL_WIDTH = 32, MAX_CELL_WIDTH = 60;
    private static final double MIN_CELL_HEIGHT = 26, MAX_CELL_HEIGHT = 32;
    private static final double MIN_FONT = 10, MAX_FONT = 16;
    private static final double HEADER_HEIGHT = 40;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DuLieu.getInstance().loadBookingsFromFile("bookings.json");
        DuLieu.getInstance().loadYardsFromFile("yards.json");
        datePicker.setValue(LocalDate.now());
        datePicker.valueProperty()
                .addListener((obs, oldDate, newDate) -> drawHourView(zoomSlider != null ? zoomSlider.getValue() : 2.0));
        if (scrollPane != null) {
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        }
        if (zoomSlider != null) {
            zoomSlider.setMin(1.0);
            zoomSlider.setMax(2.0);
            zoomSlider.setValue(2.0);
            zoomSlider.setMajorTickUnit(0.1);
            zoomSlider.setSnapToTicks(false);
            zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> drawHourView(newVal.doubleValue()));
        }
        drawHourView(zoomSlider != null ? zoomSlider.getValue() : 2.0);
        btn_maintenance.setOnAction(e -> handleMaintenanceSlots());
        btn_event.setOnAction(e -> {
            if (selectedSlots.isEmpty()) {
                showAlert("Bạn chưa chọn ô nào để gán sự kiện.");
                return;
            }
            int count = handleChooseEvent(new HashSet<>(selectedSlots), slotToYardMap);
            showAlert("Đã gán sự kiện cho " + count + " khung giờ.");
            selectedSlots.clear();
            slotToYardMap.clear();
            drawHourView(zoomSlider.getValue());
        });
    }

    private void drawHourView(double scale) {
        bookingGrid.getChildren().clear();
        selectedSlots.clear();
        String[] currentTimeSlots = getCurrentTimeSlots();
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null)
            return;
        double cellWidth = MIN_CELL_WIDTH + (MAX_CELL_WIDTH - MIN_CELL_WIDTH) * (scale - 1.0);
        double cellHeight = MIN_CELL_HEIGHT + (MAX_CELL_HEIGHT - MIN_CELL_HEIGHT) * (scale - 1.0);
        double fontSize = MIN_FONT + (MAX_FONT - MIN_FONT) * (scale - 1.0);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
        for (int i = 0; i < currentTimeSlots.length; i++) {
            Label timeLabel = new Label(currentTimeSlots[i]);
            timeLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-font-size: " + fontSize
                    + "px; -fx-background-color: #e3f2fd; -fx-border-color: #bbb;");
            timeLabel.setPrefSize(cellWidth, HEADER_HEIGHT);
            timeLabel.setAlignment(javafx.geometry.Pos.CENTER);
            bookingGrid.add(timeLabel, i + 1, 0);
        }
        List<YardModel> yards = DuLieu.getInstance().getYards();
        if (yards == null)
            return;
        for (int i = 0; i < yards.size(); i++) {
            YardModel yard = yards.get(i);
            if (yard == null)
                continue;
            Label courtLabel = new Label(yard.getYardName() != null ? yard.getYardName() : "Không tên sân");
            courtLabel.setStyle(
                    "-fx-font-weight: bold; -fx-alignment: center; -fx-background-color: #e3f2fd; -fx-border-color: #bbb;");
            courtLabel.setPrefSize(100, cellHeight);
            bookingGrid.add(courtLabel, 0, i + 1);

            // 1. Lấy tất cả các dải booking của sân này từ bookings
            List<BookingModel> allBookings = DuLieu.getInstance().getBookings();
            List<BookingYardModel> allBookingYards = new ArrayList<>();
            for (BookingModel booking : allBookings) {
                for (BookingYardModel bym : booking.getBookingYards()) {
                    if (bym.getYardId().equals(yard.getYardId())) {
                        allBookingYards.add(bym);
                    }
                }
            }
            // 2. Vẽ từng dải booking (không gộp các dải rời nhau)
            boolean[] slotDrawn = new boolean[currentTimeSlots.length];
            for (BookingModel booking : allBookings) {
                for (BookingYardModel bym : booking.getBookingYards()) {
                    if (!bym.getYardId().equals(yard.getYardId())) continue;
                    for (TimeSlot bookingRange : bym.getSlots()) {
                        if (!bookingRange.getStartTime().toLocalDate().equals(selectedDate)) continue;
                        int startIdx = -1, endIdx = -1;
                        for (int j = 0; j < currentTimeSlots.length; j++) {
                            LocalTime slotTime = LocalTime.parse(currentTimeSlots[j], timeFormatter);
                            LocalDateTime slotStart = LocalDateTime.of(selectedDate, slotTime);
                            if (slotStart.equals(bookingRange.getStartTime())) startIdx = j;
                            if (slotStart.equals(bookingRange.getEndTime())) endIdx = j;
                        }
                        if (startIdx == -1) continue;
                        int span = (endIdx == -1 ? 1 : endIdx - startIdx);
                        String timeLabel = bookingRange.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                                + " - " + bookingRange.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                        Button btn = new Button(timeLabel);
                        btn.setPrefSize(cellWidth * span, cellHeight);
                        btn.setStyle("-fx-background-color: linear-gradient(#FF0000, #CC0000); -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #900;");
                        // Tooltip thông tin booking
                        String tooltipText = "Khách: " + booking.getCustomerName() + "\nSĐT: " + booking.getCustomerPhone()
                                + "\nGiờ: " + timeLabel;
                        Tooltip.install(btn, new Tooltip(tooltipText));
                        
                        // Khi click: hiện dialog chuyển sân, huỷ đặt
                        btn.setOnAction(e -> showBookingOptionsDialog(booking, bym, bookingRange));
                        bookingGrid.add(btn, startIdx + 1, i + 1, span, 1);
                        for (int k = startIdx; k < startIdx + span; k++) slotDrawn[k] = true;
                    }
                }
            }
            // 3. Vẽ các slot còn lại (bảo trì, sự kiện, slot trống)
            for (int j = 0; j < currentTimeSlots.length; j++) {
                if (slotDrawn[j]) continue;
                LocalTime slotTime;
                try {
                    slotTime = LocalTime.parse(currentTimeSlots[j], timeFormatter);
                } catch (Exception ex) {
                    continue;
                }
                LocalDateTime slotStart = LocalDateTime.of(selectedDate, slotTime);
                LocalDateTime slotEnd = slotStart.plusMinutes(30);
                boolean isPast = slotEnd.isBefore(LocalDateTime.now());
                // Kiểm tra slot bảo trì
                TimeSlot maintenanceSlot = null;
                for (TimeSlot ts : yard.getMaintenanceSlots()) {
                    LocalDateTime mStart = ts.getStartTime();
                    LocalDateTime mEnd = ts.getEndTime();
                    boolean overlap = !(slotEnd.isBefore(mStart) || slotStart.isAfter(mEnd));
                    if (overlap) {
                        maintenanceSlot = ts;
                        break;
                    }
                }
                // Kiểm tra slot sự kiện
                TimeSlot eventSlot = null;
                for (TimeSlot ts : yard.getEventSlots()) {
                    LocalDateTime eStart = ts.getStartTime();
                    LocalDateTime eEnd = ts.getEndTime();
                    boolean overlap = !(slotEnd.isBefore(eStart) || slotStart.isAfter(eEnd));
                    if (overlap) {
                        eventSlot = ts;
                        break;
                    }
                }
                if (maintenanceSlot != null || eventSlot != null) {
                    final boolean isMaintenance = maintenanceSlot != null;
                    final TimeSlot finalSlot = isMaintenance ? maintenanceSlot : eventSlot;
                    String labelText = isMaintenance ? "Bảo trì" : "Sự kiện";
                    String style = isMaintenance ? "-fx-background-color: linear-gradient(#FFA500, #FF8C00); -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #CC6600;"
                                                : "-fx-background-color: #9932CC; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: black;";
                    Button btn = new Button(labelText);
                    btn.setPrefSize(cellWidth, cellHeight);
                    btn.setStyle(style);
                    btn.setDisable(false); // vẫn cho chọn sân
                    // Tooltip
                    String tooltipText = (isMaintenance ? "Bảo trì" : "Sự kiện") + "\nGiờ: " +
                        finalSlot.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) +
                        " - " + finalSlot.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                    Tooltip.install(btn, new Tooltip(tooltipText));
                    // Khi click: hiện dialog chuyển sân/huỷ
                    btn.setOnAction(e -> showMaintenanceOrEventOptionsDialog(yard, finalSlot, isMaintenance));
                    bookingGrid.add(btn, j + 1, i + 1);
                    continue;
                }
                // Slot trống
                Button btn = new Button(" ");
                btn.setPrefSize(cellWidth, cellHeight);
                btn.setStyle((isPast ? "-fx-background-color: #ccc;" : "-fx-background-color: white;")
                        + " -fx-border-color: #bbb; -fx-font-size: " + fontSize + "px;");
                btn.setDisable(isPast);
                final LocalDateTime finalSlotStart = slotStart;
                final LocalDateTime finalSlotEnd = slotEnd;
                if (!isPast) {
                    btn.setOnAction(e -> {
                        TimeSlot temp = new TimeSlot(finalSlotStart, finalSlotEnd);
                        if (selectedSlots.contains(temp)) {
                            selectedSlots.remove(temp);
                            slotToYardMap.remove(temp);
                            btn.setStyle("-fx-background-color: white; -fx-border-color: #bbb; -fx-font-size: "
                                    + fontSize + "px;");
                        } else {
                            selectedSlots.add(temp);
                            slotToYardMap.put(temp, yard);
                            btn.setStyle(
                                    "-fx-background-color: #00cc66; -fx-border-color: black; -fx-font-weight: bold; -fx-font-size: "
                                            + fontSize + "px;");
                        }
                    });
                }
                bookingGrid.add(btn, j + 1, i + 1);
            }
        }
    }

    private void handleMaintenanceSlots() {
        if (selectedSlots.isEmpty()) {
            showAlert("Vui lòng chọn ít nhất một khung giờ để chuyển sang bảo trì.");
            return;
        }
        for (TimeSlot slot : selectedSlots) {
            YardModel yard = slotToYardMap.get(slot);
            if (yard == null)
                continue;
            boolean existed = false;
            for (TimeSlot existing : yard.getMaintenanceSlots()) {
                if (existing.getStartTime().equals(slot.getStartTime())
                        && existing.getEndTime().equals(slot.getEndTime())) {
                    existed = true;
                    break;
                }
            }
            if (!existed) {
                TimeSlot maintenanceSlot = new TimeSlot();
                maintenanceSlot.setSlotId(UUID.randomUUID().toString());
                maintenanceSlot.setStartTime(slot.getStartTime());
                maintenanceSlot.setEndTime(slot.getEndTime());
                maintenanceSlot.setSlotType("maintenance");
                maintenanceSlot.setBooked(true);
                yard.getMaintenanceSlots().add(maintenanceSlot);
            }
        }
        DuLieu.getInstance().saveYardToFile("yards.json");
        showAlert("Đã chuyển các khung giờ sang trạng thái bảo trì.");
        selectedSlots.clear();
        slotToYardMap.clear();
        drawHourView(zoomSlider.getValue());
    }

    private int handleChooseEvent(Set<TimeSlot> selectedSlots, Map<TimeSlot, YardModel> slotToYardMap) {
        LocalDateTime start = null, end = null;
        YardModel yard = null;
        for (TimeSlot slot : selectedSlots) {
            if (start == null || slot.getStartTime().isBefore(start)) start = slot.getStartTime();
            if (end == null || slot.getEndTime().isAfter(end)) end = slot.getEndTime();
            yard = slotToYardMap.get(slot);
        }
        if (start == null || end == null || yard == null) return 0;
        TimeSlot eventSlot = new TimeSlot();
        eventSlot.setSlotId(UUID.randomUUID().toString());
        eventSlot.setStartTime(start);
        eventSlot.setEndTime(end);
        eventSlot.setSlotType("event");
        eventSlot.setBooked(true);
        boolean exists = false;
        for (TimeSlot ts : yard.getEventSlots()) {
            if (ts.getStartTime().equals(start) && ts.getEndTime().equals(end)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            yard.getEventSlots().add(eventSlot);
        }
        DuLieu.getInstance().saveYardToFile("yards.json");
        return 1;
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showBookingOptionsDialog(BookingModel booking, BookingYardModel bym, TimeSlot bookingRange) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông tin đặt sân");
        alert.setHeaderText("Thông tin đặt sân");
        StringBuilder content = new StringBuilder();
        content.append("Khách: ").append(booking.getCustomerName()).append("\n");
        content.append("SĐT: ").append(booking.getCustomerPhone()).append("\n");
        content.append("Giờ: ")
               .append(bookingRange.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")))
               .append(" - ")
               .append(bookingRange.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        alert.setContentText(content.toString());
        ButtonType btnChange = new ButtonType("Chuyển sân");
        ButtonType btnCancel = new ButtonType("Huỷ đặt");
        ButtonType btnClose = new ButtonType("Đóng", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnChange, btnCancel, btnClose);
        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent() || result.get() == btnClose) return;
        if (result.get() == btnCancel) {
            // Xoá slot này khỏi booking
            bym.getSlots().remove(bookingRange);
            // Nếu bookingYard không còn slot nào, xoá luôn bookingYard
            if (bym.getSlots().isEmpty()) {
                booking.getBookingYards().remove(bym);
            }
            // Nếu booking không còn bookingYard nào, xoá luôn booking
            if (booking.getBookingYards().isEmpty()) {
                DuLieu.getInstance().getBookings().remove(booking);
            }
            DuLieu.getInstance().saveBookingToFile("bookings.json");
            drawHourView(zoomSlider.getValue());
        } else if (result.get() == btnChange) {
            // Chọn sân mới
            List<YardModel> allYards = DuLieu.getInstance().getYards();
            List<YardModel> otherYards = new ArrayList<>();
            for (YardModel y : allYards) {
                if (!y.getYardId().equals(bym.getYardId()))
                    otherYards.add(y);
            }
            if (otherYards.isEmpty()) {
                showAlert("Không còn sân nào khác để chuyển.");
                return;
            }
            ComboBox<YardModel> comboBoxYards = new ComboBox<>();
            comboBoxYards.getItems().addAll(otherYards);
            comboBoxYards.getSelectionModel().selectFirst();
            Alert chooseYardDialog = new Alert(Alert.AlertType.CONFIRMATION);
            chooseYardDialog.setTitle("Chọn sân mới");
            chooseYardDialog.setHeaderText("Chọn sân muốn chuyển đến:");
            chooseYardDialog.getDialogPane().setContent(comboBoxYards);
            Optional<ButtonType> chooseResult = chooseYardDialog.showAndWait();
            if (!chooseResult.isPresent() || chooseResult.get() != ButtonType.OK) return;
            YardModel newYard = comboBoxYards.getValue();
            if (newYard == null) return;
            // Kiểm tra trùng lịch ở sân mới
            boolean conflict = false;
            for (BookingModel b : DuLieu.getInstance().getBookings()) {
                for (BookingYardModel by : b.getBookingYards()) {
                    if (!by.getYardId().equals(newYard.getYardId())) continue;
                    for (TimeSlot ts : by.getSlots()) {
                        boolean overlap = !(bookingRange.getEndTime().isBefore(ts.getStartTime()) || bookingRange.getStartTime().isAfter(ts.getEndTime()));
                        if (overlap) {
                            conflict = true;
                            break;
                        }
                    }
                    if (conflict) break;
                }
                if (conflict) break;
            }
            if (conflict) {
                showAlert("Sân mới đã có người đặt trong khung giờ này!");
                return;
            }
            // Thực hiện chuyển sân
            bym.getSlots().remove(bookingRange);
            if (bym.getSlots().isEmpty()) {
                booking.getBookingYards().remove(bym);
            }
            // Tìm hoặc tạo bookingYard mới cho sân mới
            BookingYardModel targetYard = null;
            for (BookingYardModel by : booking.getBookingYards()) {
                if (by.getYardId().equals(newYard.getYardId())) {
                    targetYard = by;
                    break;
                }
            }
            if (targetYard == null) {
                targetYard = new BookingYardModel(newYard.getYardId(), new ArrayList<>());
                booking.getBookingYards().add(targetYard);
            }
            targetYard.getSlots().add(bookingRange);
            DuLieu.getInstance().saveBookingToFile("bookings.json");
            drawHourView(zoomSlider.getValue());
            showAlert("Chuyển sân thành công!");
        }
    }

    // Thêm hàm xử lý chuyển sân/huỷ cho bảo trì/sự kiện
    private void showMaintenanceOrEventOptionsDialog(YardModel yard, TimeSlot slot, boolean isMaintenance) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(isMaintenance ? "Bảo trì sân" : "Sự kiện sân");
        alert.setHeaderText(isMaintenance ? "Thông tin bảo trì" : "Thông tin sự kiện");
        StringBuilder content = new StringBuilder();
        content.append("Giờ: ")
               .append(slot.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")))
               .append(" - ")
               .append(slot.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        alert.setContentText(content.toString());
        ButtonType btnChange = new ButtonType("Chuyển sân");
        ButtonType btnCancel = new ButtonType("Huỷ " + (isMaintenance ? "bảo trì" : "sự kiện"));
        ButtonType btnClose = new ButtonType("Đóng", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnChange, btnCancel, btnClose);
        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent() || result.get() == btnClose) return;
        if (result.get() == btnCancel) {
            if (isMaintenance) {
                yard.getMaintenanceSlots().remove(slot);
            } else {
                yard.getEventSlots().remove(slot);
            }
            DuLieu.getInstance().saveYardToFile("yards.json");
            drawHourView(zoomSlider.getValue());
        } else if (result.get() == btnChange) {
            // Chọn sân mới
            List<YardModel> allYards = DuLieu.getInstance().getYards();
            List<YardModel> otherYards = new ArrayList<>();
            for (YardModel y : allYards) {
                if (!y.getYardId().equals(yard.getYardId()))
                    otherYards.add(y);
            }
            if (otherYards.isEmpty()) {
                showAlert("Không còn sân nào khác để chuyển.");
                return;
            }
            ComboBox<YardModel> comboBoxYards = new ComboBox<>();
            comboBoxYards.getItems().addAll(otherYards);
            comboBoxYards.getSelectionModel().selectFirst();
            Alert chooseYardDialog = new Alert(Alert.AlertType.CONFIRMATION);
            chooseYardDialog.setTitle("Chọn sân mới");
            chooseYardDialog.setHeaderText("Chọn sân muốn chuyển đến:");
            chooseYardDialog.getDialogPane().setContent(comboBoxYards);
            Optional<ButtonType> chooseResult = chooseYardDialog.showAndWait();
            if (!chooseResult.isPresent() || chooseResult.get() != ButtonType.OK) return;
            YardModel newYard = comboBoxYards.getValue();
            if (newYard == null) return;
            // Kiểm tra trùng lịch ở sân mới
            boolean conflict = false;
            List<TimeSlot> targetSlots = isMaintenance ? newYard.getMaintenanceSlots() : newYard.getEventSlots();
            for (TimeSlot ts : targetSlots) {
                boolean overlap = !(slot.getEndTime().isBefore(ts.getStartTime()) || slot.getStartTime().isAfter(ts.getEndTime()));
                if (overlap) {
                    conflict = true;
                    break;
                }
            }
            if (conflict) {
                showAlert("Sân mới đã có " + (isMaintenance ? "bảo trì" : "sự kiện") + " trong khung giờ này!");
                return;
            }
            // Thực hiện chuyển sân
            if (isMaintenance) {
                yard.getMaintenanceSlots().remove(slot);
                newYard.getMaintenanceSlots().add(slot);
            } else {
                yard.getEventSlots().remove(slot);
                newYard.getEventSlots().add(slot);
            }
            DuLieu.getInstance().saveYardToFile("yards.json");
            drawHourView(zoomSlider.getValue());
            showAlert("Chuyển sân thành công!");
        }
    }
}
