package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Optional;

import bean.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import singleton.DuLieu;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.VBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import bean.DepositModel;
import java.util.ArrayList;
import bean.PaymentRecord;
import bean.PaymentStatus;

public class BookingController implements BaseController {

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private List<YardModel> selectedYards = new ArrayList<>();
    private Map<YardModel, List<String>> selectedSlotsPerYard = new HashMap<>();
    private Map<YardModel, LocalDate> selectedDatesPerYard = new HashMap<>(); 

    // Thêm biến để lưu dịch vụ đã chọn
    private Map<String, ServicesModel> selectedServicesPerYard = new HashMap<>();

    private List<PaymentRecord> payments = new ArrayList<>();

    @FXML private Button btn_addAll;
    @FXML private Button btn_chooseTime;
    @FXML private Button btn_payment;
    @FXML private CheckBox cb_friday;
    @FXML private CheckBox cb_monday;
    @FXML private CheckBox cb_saturday;
    @FXML private CheckBox cb_sunday;
    @FXML private CheckBox cb_thursday;
    @FXML private CheckBox cb_tuesday;
    @FXML private CheckBox cb_wednesday;
    @FXML private DatePicker dtp_endDay;
    @FXML private DatePicker dtp_startDay;
    @FXML private ToggleGroup group;
    @FXML private RadioButton rb_aboutDay;
    @FXML private RadioButton rb_weekDay;
    @FXML private TableView<YardInfoRow> tbl_infoYard;
    @FXML private TableView<YardInfoRow> tbl_payment;
    @FXML private TableView<ServicesModel> tbl_services;
    @FXML private TableView<BookingModel> tbl_times;
    @FXML private TextField tft_fullName;
    @FXML private TextField ttf_email;
    @FXML private TextField ttf_phone;
    @FXML private TableColumn<ServicesModel, String> colServiceName;
    @FXML private TableColumn<ServicesModel, String> colServiceDesc;
    @FXML private TableColumn<ServicesModel, Number> colServicePrice;
    @FXML private TableView<YardInfoRow> tbl_confirmedYards;
    @FXML private Label lblTotalAmount;

    public void initialize() {
        // Load data from files
        DuLieu.getInstance().loadBookingsFromFile("bookings.json");
        DuLieu.getInstance().loadServicesFromFile("services.json");
        constructorView();
        setOnAction();
        refresh();

        // Debug FXML injection
        System.out.println("rb_aboutDay: " + rb_aboutDay);
        System.out.println("rb_weekDay: " + rb_weekDay);
        System.out.println("tbl_infoYard: " + tbl_infoYard);
        System.out.println("tbl_services: " + tbl_services);
        System.out.println("tbl_payment: " + tbl_payment);
        System.out.println("colServiceName: " + colServiceName);
        System.out.println("colServiceDesc: " + colServiceDesc);
        System.out.println("colServicePrice: " + colServicePrice);

        // Configure tbl_infoYard columns
        if (tbl_infoYard != null) {
            if (tbl_infoYard.getColumns().isEmpty()) {
                TableColumn<YardInfoRow, String> sttCol = new TableColumn<>("STT");
                sttCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().stt));

                TableColumn<YardInfoRow, String> nameCol = new TableColumn<>("Tên sân");
                nameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().yardName));

                TableColumn<YardInfoRow, String> addressCol = new TableColumn<>("Địa chỉ");
                addressCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().yardAddress));

                TableColumn<YardInfoRow, String> startDateCol = new TableColumn<>("Ngày bắt đầu");
                startDateCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().startDate));

                TableColumn<YardInfoRow, String> endDateCol = new TableColumn<>("Ngày kết thúc");
                endDateCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().endDate));

                TableColumn<YardInfoRow, String> dayOfWeekCol = new TableColumn<>("Thứ");
                dayOfWeekCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().dayOfWeek));

                TableColumn<YardInfoRow, String> startTimeCol = new TableColumn<>("Giờ bắt đầu");
                startTimeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().startTime));

                TableColumn<YardInfoRow, String> endTimeCol = new TableColumn<>("Giờ kết thúc");
                endTimeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().endTime));

                TableColumn<YardInfoRow, String> priceCol = new TableColumn<>("Giá mỗi giờ");
                priceCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().pricePerHour));

                // Thêm cột ComboBox để chọn ngày
                TableColumn<YardInfoRow, Void> dateSelectionCol = new TableColumn<>("Chọn ngày");
                dateSelectionCol.setCellFactory(tc -> new TableCell<YardInfoRow, Void>() {
                    private final ComboBox<String> dateComboBox = new ComboBox<>();
                    {
                        dateComboBox.setOnAction(e -> {
                            YardInfoRow row = getTableView().getItems().get(getIndex());
                            String selectedDate = dateComboBox.getValue();
                            if (selectedDate != null) {
                                // Cập nhật ngày đã chọn cho sân này
                                row.selectedDate = selectedDate;
                                System.out.println("Đã chọn ngày " + selectedDate + " cho sân " + row.yardName);
                            }
                        });
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            YardInfoRow row = getTableView().getItems().get(getIndex());
                            // Lấy danh sách ngày có thể chọn
                            List<LocalDate> availableDates = getAvailableDatesForYard(row);
                            dateComboBox.getItems().clear();
                            dateComboBox.getItems().addAll(availableDates.stream()
                                    .map(date -> date.toString() + " (" + date.getDayOfWeek().toString() + ")")
                                    .collect(Collectors.toList()));
                            if (!dateComboBox.getItems().isEmpty()) {
                                dateComboBox.setValue(dateComboBox.getItems().get(0));
                            }
                            setGraphic(dateComboBox);
                        }
                    }
                });

                // Thêm cột Xác nhận
                TableColumn<YardInfoRow, Void> confirmCol = new TableColumn<>("Xác nhận");
                confirmCol.setCellFactory(tc -> new TableCell<YardInfoRow, Void>() {
                    private final Button btnConfirm = new Button("Xác nhận");
                    {
                        btnConfirm.setOnAction(e -> {
                            YardInfoRow row = getTableView().getItems().get(getIndex());
                            if (row.selectedDate == null || row.selectedDate.isEmpty()) {
                                showAlert("Thông báo", "Vui lòng chọn ngày cho sân " + row.yardName + " trước khi xác nhận.");
                                return;
                            }
                            
                            // Chuyển thông tin sân sang bảng thanh toán
                            moveToPaymentTable(row);
                            
                            // Xóa dòng này khỏi bảng thông tin
                            getTableView().getItems().remove(getIndex());
                            
                            showAlert("Thành công", "Đã xác nhận sân " + row.yardName + " cho ngày " + row.selectedDate);
                        });
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btnConfirm);
                    }
                });

                tbl_infoYard.getColumns().setAll(sttCol, nameCol, addressCol, startDateCol, endDateCol, dayOfWeekCol, startTimeCol, endTimeCol, priceCol, dateSelectionCol, confirmCol);
            }
        } else {
            System.out.println("Warning: tbl_infoYard is not initialized.");
            showAlert("Lỗi", "Bảng thông tin sân không được khởi tạo.");
        }

        // Configure tbl_services columns
        if (tbl_services != null) {
            if (tbl_services.getColumns().isEmpty()) {
                TableColumn<ServicesModel, String> nameCol = new TableColumn<>("Tên dịch vụ");
                nameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getServiceName()));

                TableColumn<ServicesModel, String> descCol = new TableColumn<>("Mô tả");
                descCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescription()));

                TableColumn<ServicesModel, Number> priceCol = new TableColumn<>("Giá");
                priceCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrice()));

                // Thêm cột Action với nút "Thêm"
                TableColumn<ServicesModel, Void> actionCol = new TableColumn<>("Action");
                actionCol.setCellFactory(tc -> new TableCell<ServicesModel, Void>() {
                    private final Button btnAdd = new Button("Thêm");
                    {
                        btnAdd.setOnAction(e -> {
                            ServicesModel service = getTableView().getItems().get(getIndex());
                            showServiceSelectionDialog(service);
                        });
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btnAdd);
                    }
                });

                tbl_services.getColumns().setAll(nameCol, descCol, priceCol, actionCol);
            } else if (colServiceName != null && colServiceDesc != null && colServicePrice != null) {
                colServiceName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getServiceName()));
                colServiceDesc.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescription()));
                colServicePrice.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrice()));
            } else {
                System.out.println("Warning: tbl_services columns (colServiceName, colServiceDesc, or colServicePrice) are not properly initialized.");
                showAlert("Lỗi", "Cột của bảng dịch vụ không được khởi tạo đúng.");
            }
            // Load all services into tbl_services
            tbl_services.setItems(FXCollections.observableArrayList(DuLieu.getInstance().getServices()));
            tbl_services.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        } else {
            System.out.println("Warning: tbl_services is not initialized.");
            showAlert("Lỗi", "Bảng dịch vụ không được khởi tạo.");
        }

        // Configure tbl_payment columns
        if (tbl_payment != null) {
            if (tbl_payment.getColumns().isEmpty()) {
                TableColumn<YardInfoRow, String> nameCol = new TableColumn<>("Tên sân");
                nameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().yardName));

                TableColumn<YardInfoRow, String> dayOfWeekCol = new TableColumn<>("Thứ");
                dayOfWeekCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().dayOfWeek));

                TableColumn<YardInfoRow, String> totalTimeCol = new TableColumn<>("Tổng thời gian");
                totalTimeCol.setCellValueFactory(cell -> {
                    String startTime = cell.getValue().startTime;
                    String endTime = cell.getValue().endTime;
                    if (startTime != null && endTime != null && !startTime.isEmpty() && !endTime.isEmpty()) {
                        try {
                            LocalTime start = LocalTime.parse(startTime);
                            LocalTime end = LocalTime.parse(endTime);
                            long minutes = java.time.Duration.between(start, end).toMinutes();
                            return new SimpleStringProperty(String.format("%d giờ %d phút", minutes / 60, minutes % 60));
                        } catch (Exception e) {
                            return new SimpleStringProperty("N/A");
                        }
                    }
                    return new SimpleStringProperty("N/A");
                });

                TableColumn<YardInfoRow, String> serviceCol = new TableColumn<>("Dịch vụ");
                serviceCol.setCellValueFactory(cell -> {
                    String serviceName = cell.getValue().serviceName;
                    if (serviceName != null && !serviceName.isEmpty()) {
                        return new SimpleStringProperty(serviceName);
                    } else {
                        return new SimpleStringProperty("Không có");
                    }
                });

                TableColumn<YardInfoRow, String> totalPriceCol = new TableColumn<>("Tổng tiền");
                totalPriceCol.setCellValueFactory(cell -> {
                    try {
                        YardInfoRow row = cell.getValue();
                        double pricePerHour = Double.parseDouble(row.pricePerHour);
                        String startTime = row.startTime;
                        String endTime = row.endTime;
                        double hours = 0;
                        if (startTime != null && endTime != null && !startTime.isEmpty() && !endTime.isEmpty()) {
                            LocalTime start = LocalTime.parse(startTime);
                            LocalTime end = LocalTime.parse(endTime);
                            hours = java.time.Duration.between(start, end).toMinutes() / 60.0;
                        }
                        double total = pricePerHour * hours;
                        // Cộng tiền dịch vụ của dòng này nếu có
                        if (row.serviceName != null && !row.serviceName.isEmpty()) {
                            ServicesModel service = DuLieu.getInstance().getServices().stream()
                                .filter(s -> s.getServiceName().equals(row.serviceName))
                                .findFirst().orElse(null);
                            if (service != null) {
                                total += service.getPrice();
                            }
                        }
                        return new SimpleStringProperty(String.format("%.0f", total));
                    } catch (Exception e) {
                        return new SimpleStringProperty("N/A");
                    }
                });

                tbl_payment.getColumns().setAll(nameCol, dayOfWeekCol, totalTimeCol, serviceCol, totalPriceCol);
            }
        } else {
            System.out.println("Warning: tbl_payment is not initialized.");
            showAlert("Lỗi", "Bảng thanh toán không được khởi tạo.");
        }

        // Configure tbl_times columns
        if (tbl_times != null && tbl_times.getColumns().isEmpty()) {
            TableColumn<BookingModel, String> sttCol = new TableColumn<>("STT");
            sttCol.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(tbl_times.getItems().indexOf(cell.getValue()) + 1)));

            TableColumn<BookingModel, String> dayOfWeekCol = new TableColumn<>("Thứ");
            dayOfWeekCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getBookerPhone()));

            TableColumn<BookingModel, String> dateCol = new TableColumn<>("Ngày");
            dateCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getBookerName()));

            TableColumn<BookingModel, Void> actionCol = new TableColumn<>("Action");
            actionCol.setCellFactory(tc -> new TableCell<BookingModel, Void>() {
                private final Button btnCancel = new Button("Cancel");
                {
                    btnCancel.setOnAction(e -> {
                        BookingModel booking = getTableView().getItems().get(getIndex());
                        System.out.println("Cancel booking: " + booking.getBookerName());
                        tbl_times.getItems().remove(getIndex());
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btnCancel);
                }
            });

            tbl_times.getColumns().setAll(sttCol, dayOfWeekCol, dateCol, actionCol);
        }

        // Add listeners for checkboxes to automatically update
        if (cb_monday != null) cb_monday.selectedProperty().addListener((obs, oldVal, newVal) -> updateTimesTable());
        if (cb_tuesday != null) cb_tuesday.selectedProperty().addListener((obs, oldVal, newVal) -> updateTimesTable());
        if (cb_wednesday != null) cb_wednesday.selectedProperty().addListener((obs, oldVal, newVal) -> updateTimesTable());
        if (cb_thursday != null) cb_thursday.selectedProperty().addListener((obs, oldVal, newVal) -> updateTimesTable());
        if (cb_friday != null) cb_friday.selectedProperty().addListener((obs, oldVal, newVal) -> updateTimesTable());
        if (cb_saturday != null) cb_saturday.selectedProperty().addListener((obs, oldVal, newVal) -> updateTimesTable());
        if (cb_sunday != null) cb_sunday.selectedProperty().addListener((obs, oldVal, newVal) -> updateTimesTable());

        // Add listener for radio buttons
        if (rb_weekDay != null) rb_weekDay.selectedProperty().addListener((obs, oldVal, newVal) -> updateTimesTable());
        if (rb_aboutDay != null) rb_aboutDay.selectedProperty().addListener((obs, oldVal, newVal) -> updateTimesTable());
    }

    private void updateTimesTable() {
        if (tbl_times != null) {
            LocalDate startDate = dtp_startDay != null ? dtp_startDay.getValue() : null;
            LocalDate endDate = dtp_endDay != null ? dtp_endDay.getValue() : null;
            boolean isWeekDay = rb_weekDay != null && rb_weekDay.isSelected();

            List<BookingModel> filteredBookings = new ArrayList<>();

            if (startDate != null && endDate != null && isWeekDay) {
                // Get selected weekdays
                List<String> selectedWeekdays = new ArrayList<>();
                if (cb_monday != null && cb_monday.isSelected()) selectedWeekdays.add("Monday");
                if (cb_tuesday != null && cb_tuesday.isSelected()) selectedWeekdays.add("Tuesday");
                if (cb_wednesday != null && cb_wednesday.isSelected()) selectedWeekdays.add("Wednesday");
                if (cb_thursday != null && cb_thursday.isSelected()) selectedWeekdays.add("Thursday");
                if (cb_friday != null && cb_friday.isSelected()) selectedWeekdays.add("Friday");
                if (cb_saturday != null && cb_saturday.isSelected()) selectedWeekdays.add("Saturday");
                if (cb_sunday != null && cb_sunday.isSelected()) selectedWeekdays.add("Sunday");

                // Get list of dates for selected weekdays
                List<LocalDate> validDates = getDatesForSelectedWeekdays(startDate, endDate, selectedWeekdays);

                // Create dummy booking list for time information display
                for (LocalDate date : validDates) {
                    BookingModel timeInfo = new BookingModel();
                    timeInfo.setBookerName("Ngày: " + date.toString());
                    timeInfo.setBookerPhone("Thứ: " + date.getDayOfWeek().toString());
                    timeInfo.setBookingTime(LocalDateTime.of(date, LocalTime.of(0, 0)));
                    timeInfo.setTotalAmount(0);
                    // Payment status sẽ được tính từ payments
                    filteredBookings.add(timeInfo);
                }
            } else {
                // Display all bookings if not filtering by day of week
                filteredBookings = DuLieu.getInstance().getBookings().stream()
                        .sorted((b1, b2) -> b2.getBookingTime().compareTo(b1.getBookingTime()))
                        .limit(10)
                        .collect(Collectors.toList());
            }

            tbl_times.setItems(FXCollections.observableArrayList(filteredBookings));
        }
    }

    private List<LocalDate> getDatesForSelectedWeekdays(LocalDate start, LocalDate end, List<String> selectedDays) {
        List<LocalDate> result = new ArrayList<>();
        if (start == null || end == null || selectedDays.isEmpty()) return result;

        // Convert day names to DayOfWeek
        Map<String, java.time.DayOfWeek> dayMap = new HashMap<>();
        dayMap.put("Monday", java.time.DayOfWeek.MONDAY);
        dayMap.put("Tuesday", java.time.DayOfWeek.TUESDAY);
        dayMap.put("Wednesday", java.time.DayOfWeek.WEDNESDAY);
        dayMap.put("Thursday", java.time.DayOfWeek.THURSDAY);
        dayMap.put("Friday", java.time.DayOfWeek.FRIDAY);
        dayMap.put("Saturday", java.time.DayOfWeek.SATURDAY);
        dayMap.put("Sunday", java.time.DayOfWeek.SUNDAY);

        List<java.time.DayOfWeek> daysOfWeek = selectedDays.stream()
                .map(day -> dayMap.get(day))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (daysOfWeek.contains(date.getDayOfWeek())) {
                result.add(date);
            }
        }
        return result;
    }

    @Override
    public void constructorView() {}

    @Override
    public void setOnAction() {
        if (btn_chooseTime != null) {
            btn_chooseTime.setOnAction(e -> {
                try {
                    openTimeSlotModal();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    showAlert("Lỗi", "Không thể mở modal chọn giờ: " + e1.getMessage());
                }
            });
        }
        if (btn_addAll != null) {
            btn_addAll.setOnAction(e -> handleAddToInfoTable());
        }
        if (btn_payment != null) {
            btn_payment.setOnAction(e -> {
                handleNewBooking();
                // Update tbl_payment with confirmed booking data
                if (tbl_payment != null) {
                    tbl_payment.setItems(tbl_infoYard.getItems());
                }
            });
        }
    }

    private void handleAddToInfoTable() {
        if (tbl_infoYard == null) {
            showAlert("Lỗi", "Bảng thông tin sân không được khởi tạo.");
            return;
        }

        if (selectedYards == null || selectedYards.isEmpty()) {
            showAlert("Thông báo", "Vui lòng chọn ít nhất một sân trước khi thêm.");
            return;
        }

        javafx.collections.ObservableList<YardInfoRow> yardRows = FXCollections.observableArrayList();
        int stt = 1;
        boolean isAboutDay = rb_aboutDay != null && rb_aboutDay.isSelected();
        boolean isWeekDay = rb_weekDay != null && rb_weekDay.isSelected();
        java.time.LocalDate startDate = dtp_startDay != null ? dtp_startDay.getValue() : null;
        java.time.LocalDate endDate = dtp_endDay != null ? dtp_endDay.getValue() : null;

        if (rb_aboutDay == null || rb_weekDay == null) {
            showAlert("Lỗi", "Không thể xác định chế độ đặt sân (theo ngày hoặc theo thứ).");
            return;
        }

        if (startDate == null || (isAboutDay && endDate == null)) {
            showAlert("Thông báo", "Vui lòng chọn ngày bắt đầu và ngày kết thúc (nếu theo ngày).");
            return;
        }

        // Get selected weekdays
        List<String> selectedWeekdays = new ArrayList<>();
        if (cb_monday != null && cb_monday.isSelected()) selectedWeekdays.add("Monday");
        if (cb_tuesday != null && cb_tuesday.isSelected()) selectedWeekdays.add("Tuesday");
        if (cb_wednesday != null && cb_wednesday.isSelected()) selectedWeekdays.add("Wednesday");
        if (cb_thursday != null && cb_thursday.isSelected()) selectedWeekdays.add("Thursday");
        if (cb_friday != null && cb_friday.isSelected()) selectedWeekdays.add("Friday");
        if (cb_saturday != null && cb_saturday.isSelected()) selectedWeekdays.add("Saturday");
        if (cb_sunday != null && cb_sunday.isSelected()) selectedWeekdays.add("Sunday");

        List<LocalDate> datesForSelectedWeekdays = getDatesForSelectedWeekdays(startDate, endDate, selectedWeekdays);

        for (YardModel yd : selectedYards) {
            if (yd == null) continue;
            List<String> slots = selectedSlotsPerYard.getOrDefault(yd, Collections.emptyList());
            if (slots.isEmpty()) {
                showAlert("Thông báo", "Vui lòng chọn khung giờ cho sân: " + yd.getYardName());
                continue;
            }
            slots.sort(Comparator.naturalOrder());
            String pricePerHour = String.valueOf(yd.getYardPrice());
            String startTime = slots.get(0);
            String endTime = slots.get(slots.size() - 1);
            if (isAboutDay) {
                String startDateStr = startDate != null ? startDate.toString() : "";
                String endDateStr = endDate != null ? endDate.toString() : "";
                String dayOfWeek = "";
                yardRows.add(new YardInfoRow(stt++, yd.getYardName(), yd.getYardAddress(), startDateStr, endDateStr, dayOfWeek, startTime, endTime, pricePerHour, null, null));
            } else if (isWeekDay && !selectedWeekdays.isEmpty()) {
                for (LocalDate date : datesForSelectedWeekdays) {
                    String dayOfWeek = date.getDayOfWeek().toString();
                    String dateStr = date.toString();
                    yardRows.add(new YardInfoRow(stt++, yd.getYardName(), yd.getYardAddress(), dateStr, dateStr, dayOfWeek, startTime, endTime, pricePerHour, null, null));
                }
            }
        }
        tbl_infoYard.setItems(yardRows);
    }

    // Thêm hàm để lấy danh sách ngày có thể chọn cho sân
    private List<LocalDate> getAvailableDatesForYard(YardInfoRow row) {
        LocalDate startDate = dtp_startDay != null ? dtp_startDay.getValue() : null;
        LocalDate endDate = dtp_endDay != null ? dtp_endDay.getValue() : null;
        
        if (startDate == null || endDate == null) {
            return new ArrayList<>();
        }

        // Get selected weekdays
        List<String> selectedWeekdays = new ArrayList<>();
        if (cb_monday != null && cb_monday.isSelected()) selectedWeekdays.add("Monday");
        if (cb_tuesday != null && cb_tuesday.isSelected()) selectedWeekdays.add("Tuesday");
        if (cb_wednesday != null && cb_wednesday.isSelected()) selectedWeekdays.add("Wednesday");
        if (cb_thursday != null && cb_thursday.isSelected()) selectedWeekdays.add("Thursday");
        if (cb_friday != null && cb_friday.isSelected()) selectedWeekdays.add("Friday");
        if (cb_saturday != null && cb_saturday.isSelected()) selectedWeekdays.add("Saturday");
        if (cb_sunday != null && cb_sunday.isSelected()) selectedWeekdays.add("Sunday");

        return getDatesForSelectedWeekdays(startDate, endDate, selectedWeekdays);
    }

    // Thêm hàm chuyển thông tin sân sang bảng thanh toán
    private void moveToPaymentTable(YardInfoRow row) {
        if (tbl_payment == null) {
            showAlert("Lỗi", "Bảng thanh toán không được khởi tạo.");
            return;
        }

        // Tạo một dòng mới trong bảng thanh toán với thông tin đã xác nhận
        YardInfoRow paymentRow = new YardInfoRow(
                tbl_payment.getItems().size() + 1,
                row.yardName,
                row.yardAddress,
                row.selectedDate, // Sử dụng ngày đã chọn
                row.selectedDate, // Ngày kết thúc cũng là ngày đã chọn
                getDayOfWeekFromDate(row.selectedDate), // Lấy thứ từ ngày đã chọn
                row.startTime,
                row.endTime,
                row.pricePerHour,
                null, // Không có hàm chỉnh sửa
                null  // Không có hàm xóa
        );

        // Thêm dòng mới vào bảng thanh toán
        tbl_payment.getItems().add(paymentRow);

        // Cập nhật tổng tiền nếu có Label hiển thị
        updateTotalAmount();
    }

    // Thêm hàm lấy thứ từ ngày
    private String getDayOfWeekFromDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }
        try {
            // Trích xuất ngày từ string (lấy phần trước dấu ngoặc)
            String datePart = dateString.split(" \\(")[0];
            LocalDate date = LocalDate.parse(datePart);
            return date.getDayOfWeek().toString();
        } catch (Exception e) {
            return "";
        }
    }

    // Thêm hàm cập nhật tổng tiền
    private void updateTotalAmount() {
        if (lblTotalAmount == null) {
            return;
        }

        updateTotalAmountWithServices();
    }

    // Thêm hàm tính số giờ
    private double calculateHours(String startTime, String endTime) {
        try {
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);
            return java.time.Duration.between(start, end).toMinutes() / 60.0;
        } catch (Exception e) {
            return 1; // Mặc định 1 giờ nếu không parse được
        }
    }

    // Thêm hàm hiển thị dialog chọn sân và ngày cho dịch vụ
    private void showServiceSelectionDialog(ServicesModel service) {
        if (selectedYards == null || selectedYards.isEmpty()) {
            showAlert("Thông báo", "Vui lòng chọn ít nhất một sân trước khi thêm dịch vụ.");
            return;
        }

        // Tạo dialog để chọn sân và ngày
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Thêm dịch vụ: " + service.getServiceName());
        alert.setHeaderText("Vui lòng chọn sân và ngày để thêm dịch vụ:");

        // Tạo ComboBox cho sân
        ComboBox<String> yardComboBox = new ComboBox<>();
        yardComboBox.getItems().addAll(selectedYards.stream()
                .map(YardModel::getYardName)
                .collect(Collectors.toList()));
        if (!yardComboBox.getItems().isEmpty()) {
            yardComboBox.setValue(yardComboBox.getItems().get(0));
        }

        // Tạo ComboBox cho ngày (nếu có ngày hợp lệ)
        ComboBox<String> dateComboBox = new ComboBox<>();
        LocalDate startDate = dtp_startDay != null ? dtp_startDay.getValue() : null;
        LocalDate endDate = dtp_endDay != null ? dtp_endDay.getValue() : null;
        
        if (startDate != null && endDate != null) {
            List<String> selectedWeekdays = new ArrayList<>();
            if (cb_monday != null && cb_monday.isSelected()) selectedWeekdays.add("Monday");
            if (cb_tuesday != null && cb_tuesday.isSelected()) selectedWeekdays.add("Tuesday");
            if (cb_wednesday != null && cb_wednesday.isSelected()) selectedWeekdays.add("Wednesday");
            if (cb_thursday != null && cb_thursday.isSelected()) selectedWeekdays.add("Thursday");
            if (cb_friday != null && cb_friday.isSelected()) selectedWeekdays.add("Friday");
            if (cb_saturday != null && cb_saturday.isSelected()) selectedWeekdays.add("Saturday");
            if (cb_sunday != null && cb_sunday.isSelected()) selectedWeekdays.add("Sunday");

            List<LocalDate> availableDates = getDatesForSelectedWeekdays(startDate, endDate, selectedWeekdays);
            dateComboBox.getItems().addAll(availableDates.stream()
                    .map(date -> date.toString() + " (" + date.getDayOfWeek().toString() + ")")
                    .collect(Collectors.toList()));
            if (!dateComboBox.getItems().isEmpty()) {
                dateComboBox.setValue(dateComboBox.getItems().get(0));
            }
        }

        // Tạo layout cho dialog
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("Chọn sân:"),
            yardComboBox,
            new Label("Chọn ngày:"),
            dateComboBox
        );
        alert.getDialogPane().setContent(content);

        // Hiển thị dialog và xử lý kết quả
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedYardName = yardComboBox.getValue();
            String selectedDate = dateComboBox.getValue();
            
            if (selectedYardName != null && selectedDate != null) {
                // Tìm sân tương ứng
                YardModel selectedYard = selectedYards.stream()
                        .filter(yard -> yard.getYardName().equals(selectedYardName))
                        .findFirst()
                        .orElse(null);
                
                if (selectedYard != null) {
                    // Lưu dịch vụ cho sân này
                    selectedServicesPerYard.put(selectedYard.getYardId(), service);
                    
                    // Tìm đúng dòng trong bảng thành tiền để cập nhật dịch vụ
                    if (tbl_payment != null) {
                        String selectedDateOnly = selectedDate.split(" \\(")[0]; // Lấy phần ngày không có thứ
                        for (YardInfoRow row : tbl_payment.getItems()) {
                            // So sánh tên sân và ngày (có thể là startDate hoặc endDate)
                            if (row.yardName.equals(selectedYardName) && 
                                (row.startDate.equals(selectedDateOnly) || row.endDate.equals(selectedDateOnly))) {
                                row.serviceName = service.getServiceName();
                                System.out.println("Đã cập nhật dịch vụ: " + service.getServiceName() + " cho sân: " + selectedYardName);
                                break; // Tìm thấy dòng đầu tiên thỏa mãn thì dừng
                            }
                        }
                        tbl_payment.refresh();
                    }
                    
                    // Cập nhật tổng tiền
                    updateTotalAmountWithServices();
                    
                    showAlert("Thành công", "Đã thêm dịch vụ " + service.getServiceName() + " cho sân " + selectedYardName + " vào ngày " + selectedDate);
                }
            } else {
                showAlert("Lỗi", "Vui lòng chọn đầy đủ sân và ngày.");        
            }
        }
    }

    // Thêm hàm cập nhật tổng tiền bao gồm dịch vụ
    private void updateTotalAmountWithServices() {
        if (lblTotalAmount == null) {
            return;
        }

        double total = 0;
        
        // Tính tiền sân
        for (YardInfoRow row : tbl_payment.getItems()) {
            try {
                double pricePerHour = Double.parseDouble(row.pricePerHour);
                double hours = calculateHours(row.startTime, row.endTime);
                total += pricePerHour * hours;
            } catch (Exception e) {
                // Bỏ qua nếu không parse được giá
            }
        }
        
        // Cộng tiền dịch vụ
        for (ServicesModel service : selectedServicesPerYard.values()) {
            total += service.getPrice();
        }

        lblTotalAmount.setText(String.format("Tổng tiền: %.2f", total));
    }

    // Thêm hàm lấy yardId từ tên sân
    private String getYardIdByName(String yardName) {
        if (selectedYards != null) {
            for (YardModel yard : selectedYards) {
                if (yard != null && yard.getYardName().equals(yardName)) {
                    return yard.getYardId();
                }
            }
        }
        return null;
    }

    private void openTimeSlotModal() throws IOException {
        if (selectedYards == null || selectedYards.isEmpty()) {
            showAlert("Thông báo", "Vui lòng chọn ít nhất một sân trước khi chọn khung giờ.");
            return;
        }

        LocalDate selectedDate = dtp_startDay != null ? dtp_startDay.getValue() : null;
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
            dtp_startDay.setValue(selectedDate);
        }

        for (YardModel yard : selectedYards) {
            if (yard == null || (selectedSlotsPerYard.containsKey(yard) && !selectedSlotsPerYard.get(yard).isEmpty())) continue;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlclient/ModalHours.fxml"));
            Parent root = loader.load();
            ModalHoursController controller = loader.getController();

            controller.setBookingDate(selectedDate);
            controller.setBookedSlots(getBookedTimeStringsForYard(yard, selectedDate));

            Stage modal = new Stage();
            modal.setTitle("Chọn khung giờ cho " + yard.getYardName());
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setScene(new Scene(root));
            modal.showAndWait();

            if (controller.isConfirmed()) {
                List<String> slots = controller.getSelectedTimeRanges();
                selectedSlotsPerYard.put(yard, slots);
            }
        }
    }

    public void handleNewBooking() {
        String bookerName = tft_fullName != null ? tft_fullName.getText() : "";
        String bookerPhone = ttf_phone != null ? ttf_phone.getText() : "";
        LocalDateTime bookingTime = LocalDateTime.now();
        if (bookerName.isEmpty() || bookerPhone.isEmpty()) {
            showAlert("Thông báo", "Vui lòng nhập họ tên và số điện thoại.");
            return;
        }
        if (selectedYards == null || selectedYards.isEmpty()) {
            showAlert("Thông báo", "Vui lòng chọn ít nhất một sân.");
            return;
        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        List<BookingYardModel> bookingYards = new ArrayList<>();

        LocalDate selectedDate = dtp_startDay != null ? dtp_startDay.getValue() : null;
        if (selectedDate == null) {
            showAlert("Thông báo", "Vui lòng chọn ngày thuê.");
            return;
        }

        for (YardModel yard : selectedYards) {
            if (yard == null) continue;
            List<String> timeStrings = selectedSlotsPerYard.getOrDefault(yard, Collections.emptyList());
            if (timeStrings.isEmpty()) {
                showAlert("Thông báo", "Vui lòng chọn khung giờ cho sân: " + yard.getYardName());
                continue;
            }

            timeStrings.sort(Comparator.naturalOrder());
            List<TimeSlot> yardSlots = new ArrayList<>();
            int n = timeStrings.size();
            int i = 0;
            while (i < n) {
                LocalTime startTime = LocalTime.parse(timeStrings.get(i), timeFormatter);
                LocalDateTime start = selectedDate.atTime(startTime);
                int j = i;
                while (j + 1 < n) {
                    LocalTime curr = LocalTime.parse(timeStrings.get(j), timeFormatter);
                    LocalTime next = LocalTime.parse(timeStrings.get(j + 1), timeFormatter);
                    if (curr.plusMinutes(30).equals(next)) {
                        j++;
                    } else {
                        break;
                    }
                }
                LocalTime endTime = LocalTime.parse(timeStrings.get(j), timeFormatter);
                LocalDateTime end = selectedDate.atTime(endTime).plusMinutes(30);
                TimeSlot slot = new TimeSlot(start, end);
                slot.setBooked(true);
                yardSlots.add(slot);
                i = j + 1;
            }
            bookingYards.add(new BookingYardModel(yard.getYardId(), yardSlots));
        }

        List<ServicesModel> selectedServices = tbl_services != null ? tbl_services.getSelectionModel().getSelectedItems() : new ArrayList<>();
        List<String> selectedServiceIds = new ArrayList<>();
        if (selectedServices != null) {
            for (ServicesModel service : selectedServices) {
                selectedServiceIds.add(service.getServiceId());
            }
        }

        double totalAmount = 0;
        for (BookingYardModel by : bookingYards) {
            YardModel yd = DuLieu.getInstance().findYardById(by.getYardId());
            if (yd != null) {
                double hours = by.getSlots().size() * 0.5;
                totalAmount += yd.getYardPrice() * hours;
            }
        }

        totalAmount += selectedServices.stream().mapToDouble(ServicesModel::getPrice).sum();
        if (lblTotalAmount != null) {
            lblTotalAmount.setText(String.format("%.2f", totalAmount));
        }

        // Tạo payment record khi thanh toán
        // TODO: Cho phép user chọn phương thức thanh toán từ UI
        PaymentMethod selectedPaymentMethod = PaymentMethod.CASH; // Mặc định tiền mặt
        PaymentRecord paymentRecord = new PaymentRecord(
            totalAmount,
            selectedPaymentMethod,
            PaymentStatus.PAID,
            LocalDateTime.now(),
            "Thanh toán đủ"
        );
        payments.add(paymentRecord);

        BookingModel booking = new BookingModel(
                UUID.randomUUID().toString(),
                bookerName,
                bookerPhone,
                bookingTime,
                totalAmount,
                selectedServiceIds,
                new ArrayList<>(payments)
        );
        booking.setBookingYards(bookingYards);

        DuLieu.getInstance().addBooking(booking);
        DuLieu.getInstance().saveBookingToFile("bookings.json");
        DuLieu.getInstance().saveYardToFile("yards.json");

        // Update tbl_payment with confirmed booking data
        if (tbl_payment != null) {
            tbl_payment.setItems(tbl_infoYard.getItems());
        }

        showAlert("Thành công", "Đặt sân thành công!");
        refresh();
    }

    private List<String> getBookedTimeStringsForYard(YardModel yard, LocalDate date) {
        List<String> booked = new ArrayList<>();
        if (yard == null || date == null) return booked;

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (BookingModel booking : DuLieu.getInstance().getBookings()) {
            for (BookingYardModel bym : booking.getBookingYards()) {
                if (!bym.getYardId().equals(yard.getYardId())) continue;
                for (TimeSlot slot : bym.getSlots()) {
                    if (slot.getStartTime().toLocalDate().equals(date)) {
                        LocalTime time = slot.getStartTime().toLocalTime();
                        LocalTime end = slot.getEndTime().toLocalTime();
                        while (!time.isAfter(end.minusMinutes(30))) {
                            booked.add(time.format(timeFormatter));
                            time = time.plusMinutes(30);
                        }
                    }
                }
            }
        }
        return booked;
    }

    public void setSelectedYards(List<YardModel> yards) {
        this.selectedYards = yards != null ? yards : new ArrayList<>();
        if (!this.selectedYards.isEmpty()) {
            javafx.collections.ObservableList<YardInfoRow> yardRows = FXCollections.observableArrayList();
            java.time.LocalDate startDate = dtp_startDay != null ? dtp_startDay.getValue() : null;
            java.time.LocalDate endDate = dtp_endDay != null ? dtp_endDay.getValue() : null;
            int stt = 1;
            for (YardModel yd : this.selectedYards) {
                if (yd == null) continue;
                String name = yd.getYardName();
                String startDateStr = startDate != null ? startDate.toString() : "";
                String endDateStr = endDate != null ? endDate.toString() : "";
                String pricePerHour = String.valueOf(yd.getYardPrice());
                List<String> slots = selectedSlotsPerYard.getOrDefault(yd, Collections.emptyList());
                String startTime = slots.isEmpty() ? "" : slots.get(0);
                String endTime = slots.isEmpty() ? "" : slots.get(slots.size() - 1);
                yardRows.add(new YardInfoRow(stt++, name, yd.getYardAddress(), startDateStr, endDateStr, "", startTime, endTime, pricePerHour, null, null));
            }
            if (tbl_infoYard != null) {
                tbl_infoYard.setItems(yardRows);
            } else {
                showAlert("Lỗi", "Bảng thông tin sân không được khởi tạo.");
            }
        }
    }

    @Override
    public void loadData() {
        DuLieu.getInstance().loadBookingsFromFile("bookings.json");
        DuLieu.getInstance().loadServicesFromFile("services.json");
        if (tbl_services != null && (tbl_services.getColumns().isEmpty() || (colServiceName != null && colServiceDesc != null && colServicePrice != null))) {
            tbl_services.setItems(FXCollections.observableArrayList(DuLieu.getInstance().getServices()));
        }
    }

    @Override
    public void refresh() {
        loadData();
        updateTimesTable();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Thêm phương thức để thêm khoản thanh toán
    private void addPayment(double amount, PaymentMethod method, PaymentStatus status, LocalDateTime time, String note) {
        payments.add(new PaymentRecord(amount, method, status, time, note));
    }
    // Thêm phương thức để xóa khoản thanh toán
    private void removePayment(int index) {
        if (index >= 0 && index < payments.size()) payments.remove(index);
    }
    // Thêm phương thức để sửa khoản thanh toán
    private void updatePayment(int index, double amount, PaymentMethod method, PaymentStatus status, LocalDateTime time, String note) {
        if (index >= 0 && index < payments.size()) {
            payments.set(index, new PaymentRecord(amount, method, status, time, note));
        }
    }
}

class YardInfoRow {
    public String stt;
    public String yardName;
    public String yardAddress;
    public String startDate;
    public String endDate;
    public String dayOfWeek;
    public String startTime;
    public String endTime;
    public String pricePerHour;
    public String selectedDate; // Thêm field để lưu ngày đã chọn
    public String serviceName; // Thêm field để lưu tên dịch vụ

    public YardInfoRow(int stt, String yardName, String yardAddress, String startDate, String endDate, String dayOfWeek, String startTime, String endTime, String pricePerHour, Runnable onEdit, Runnable onDelete) {
        this.stt = String.valueOf(stt);
        this.yardName = yardName != null ? yardName : "";
        this.yardAddress = yardAddress != null ? yardAddress : "";
        this.startDate = startDate != null ? startDate : "";
        this.endDate = endDate != null ? endDate : "";
        this.dayOfWeek = dayOfWeek != null ? dayOfWeek : "";
        this.startTime = startTime != null ? startTime : "";
        this.endTime = endTime != null ? endTime : "";
        this.pricePerHour = pricePerHour != null ? pricePerHour : "";
        this.selectedDate = ""; // Khởi tạo ngày đã chọn
        this.serviceName = ""; // Khởi tạo tên dịch vụ
    }
}