package controller;

import bean.YardModel;
import bean.BookingModel;
import bean.DepositModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.TextField;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import singleton.DuLieu;
import utils.ImageUtil;
import java.util.*;
import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import bean.PaymentRecord;
import bean.PaymentStatus;

public class DashboardController implements BaseController {
    @FXML private StackPane contentPane;
    @FXML private Button btnMenuYards, btnMenuBookings, btnMenuStats, btnRefresh;
    @FXML private DatePicker dpBookingDate;
    @FXML private Label lblTotalBookingAmount;

    public void initialize() {
        loadInitialData();
        setupEventHandlers();
        showYardsTableInContent();
    }

    // ==================== INITIALIZATION & DATA LOADING ====================
    
    private void loadInitialData() {
        DuLieu.getInstance().loadYardsFromFile("yards.json");
        DuLieu.getInstance().loadBookingsFromFile("bookings.json");
        DuLieu.getInstance().loadServicesFromFile("services.json");
    }

    private void setupEventHandlers() {
        setOnAction();
        setupMenuButtons();
        setupRefreshButton();
    }

    private void setupMenuButtons() {
        if (btnMenuYards != null) btnMenuYards.setOnAction(e -> showYardsTableInContent());
        if (btnMenuBookings != null) btnMenuBookings.setOnAction(e -> showBookingsTableInContent());
        if (btnMenuStats != null) btnMenuStats.setOnAction(e -> showStatsInContent());
    }

    private void setupRefreshButton() {
        if (btnRefresh != null) {
            btnRefresh.setOnAction(e -> {
                System.out.println("=== REFRESHING DATA ===");
                refresh();
                System.out.println("=== REFRESH COMPLETE ===");
            });
        }
    }

    // ==================== YARDS MANAGEMENT ====================

    private void showYardsTableInContent() {
        if (contentPane != null) {
            TableView<YardModel> table = createYardsTableView();
            table.setItems(FXCollections.observableArrayList(DuLieu.getInstance().getYards()));
            contentPane.getChildren().setAll(table);
        }
    }

    private TableView<YardModel> createYardsTableView() {
            TableView<YardModel> table = new TableView<>();
        addYardColumns(table);
        addYardActionColumn(table);
        return table;
    }

    private void addYardColumns(TableView<YardModel> table) {
        Map<String, String> columnNames = getYardColumnNames();
        Field[] fields = YardModel.class.getDeclaredFields();
        
        for (Field field : fields) {
            if (shouldSkipField(field)) continue;
            
            field.setAccessible(true);
            String colName = columnNames.getOrDefault(field.getName(), field.getName());
            
            if (field.getName().equals("yardThumbnail")) {
                addThumbnailColumn(table, field, colName);
            } else if (field.getName().equals("yardImages")) {
                addImagesColumn(table, field, colName);
            } else if (field.getName().equals("yardPrice")) {
                addPriceColumn(table, field, colName);
            } else {
                addGenericColumn(table, field, colName);
            }
        }
    }

    private Map<String, String> getYardColumnNames() {
            Map<String, String> columnNames = new HashMap<>();
            columnNames.put("yardName", "Tên Sân");
            columnNames.put("yardType", "Loại Sân");
            columnNames.put("yardPrice", "Giá");
            columnNames.put("yardDescription", "Mô tả");
            columnNames.put("yardThumbnail", "Ảnh đại diện");
            columnNames.put("yardImages", "Ảnh khác");
            columnNames.put("yardAddress", "Địa chỉ");
            columnNames.put("yardStatus", "Trạng thái");
            columnNames.put("isAvailable", "Sẵn sàng");
            columnNames.put("createAt", "Ngày tạo");
            columnNames.put("updateAt", "Ngày cập nhật");
            columnNames.put("maintenanceSlots", "Bảo trì");
            columnNames.put("eventSlots", "Sự kiện");
        return columnNames;
    }

    private boolean shouldSkipField(Field field) {
        return java.lang.reflect.Modifier.isStatic(field.getModifiers()) || 
               field.getName().equals("formatter") || 
               field.getName().equals("yardId");
    }

    private void addThumbnailColumn(TableView<YardModel> table, Field field, String colName) {
                    TableColumn<YardModel, Object> col = new TableColumn<>(colName);
                    col.setCellValueFactory(cellData -> {
                        String path = null;
                        try { path = (String) field.get(cellData.getValue()); } catch (IllegalAccessException e) {}
                        return new SimpleObjectProperty<>(path);
                    });
        col.setCellFactory(tc -> createThumbnailCell());
        table.getColumns().add(col);
    }

    private javafx.scene.control.TableCell<YardModel, Object> createThumbnailCell() {
        return new javafx.scene.control.TableCell<YardModel, Object>() {
                        private final ImageView imageView = new ImageView();
            { 
                imageView.setFitWidth(60); 
                imageView.setFitHeight(40); 
                imageView.setPreserveRatio(true); 
            }
                        @Override
                        protected void updateItem(Object item, boolean empty) {
                            super.updateItem(item, empty);
                if (empty || item == null || ((String)item).isEmpty()) { 
                    setGraphic(null); 
                } else {
                    try { 
                        imageView.setImage(new Image(new java.io.File((String)item).toURI().toString())); 
                        setGraphic(imageView); 
                    } catch (Exception e) { 
                        setGraphic(null); 
                    }
                }
            }
        };
    }

    private void addImagesColumn(TableView<YardModel> table, Field field, String colName) {
                    TableColumn<YardModel, Object> col = new TableColumn<>(colName);
                    col.setCellValueFactory(cellData -> {
                        java.util.List<String> images = null;
                        try { images = (java.util.List<String>) field.get(cellData.getValue()); } catch (IllegalAccessException e) {}
                        return new SimpleObjectProperty<>(images);
                    });
        col.setCellFactory(tc -> createImagesCell());
        table.getColumns().add(col);
    }

    private javafx.scene.control.TableCell<YardModel, Object> createImagesCell() {
        return new javafx.scene.control.TableCell<YardModel, Object>() {
                        @Override
                        protected void updateItem(Object item, boolean empty) {
                            super.updateItem(item, empty);
                if (empty || item == null) { 
                    setGraphic(null); 
                } else {
                                java.util.List<String> images = (java.util.List<String>) item;
                                HBox hbox = new HBox(2);
                                int count = 0;
                                for (String path : images) {
                                    if (count++ >= 3) break;
                                    try {
                                        ImageView iv = new ImageView(new Image(new java.io.File(path).toURI().toString()));
                            iv.setFitWidth(40); 
                            iv.setFitHeight(30); 
                            iv.setPreserveRatio(true);
                                        hbox.getChildren().add(iv);
                                    } catch (Exception e) {}
                                }
                                setGraphic(hbox.getChildren().isEmpty() ? null : hbox);
                            }
                        }
        };
                }

    private void addPriceColumn(TableView<YardModel> table, Field field, String colName) {
                    TableColumn<YardModel, Object> col = new TableColumn<>(colName);
                    col.setCellValueFactory(cellData -> {
                        Double price = null;
                        try { price = (Double) field.get(cellData.getValue()); } catch (IllegalAccessException e) {}
                        return new SimpleObjectProperty<>(price);
                    });
        col.setCellFactory(tc -> createPriceCell());
        table.getColumns().add(col);
    }

    private javafx.scene.control.TableCell<YardModel, Object> createPriceCell() {
        return new javafx.scene.control.TableCell<YardModel, Object>() {
                        private final java.text.NumberFormat vnFormat = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
                        @Override
                        protected void updateItem(Object item, boolean empty) {
                            super.updateItem(item, empty);
                if (empty || item == null) { 
                    setText(""); 
                } else { 
                    try { 
                        setText(vnFormat.format(item)); 
                    } catch (Exception e) { 
                        setText(item.toString()); 
                    } 
                }
            }
        };
    }

    private void addGenericColumn(TableView<YardModel> table, Field field, String colName) {
                TableColumn<YardModel, Object> col = new TableColumn<>(colName);
                if (field.getName().equals("yardStatus")) {
            col.setCellValueFactory(cellData -> createStatusCellValue(cellData, field));
        } else {
            col.setCellValueFactory(cellData -> createGenericCellValue(cellData, field));
        }
        table.getColumns().add(col);
    }

    private SimpleObjectProperty<Object> createStatusCellValue(javafx.scene.control.TableColumn.CellDataFeatures<YardModel, Object> cellData, Field field) {
                        try {
                            Object value = field.get(cellData.getValue());
                            String status = (value != null && value.toString().equals("active")) ? "Đang hoạt động" : value.toString();
                            return new SimpleObjectProperty<>(status);
                        } catch (IllegalAccessException e) {
                            return new SimpleObjectProperty<>("");
                        }
    }

    private SimpleObjectProperty<Object> createGenericCellValue(javafx.scene.control.TableColumn.CellDataFeatures<YardModel, Object> cellData, Field field) {
                        try {
                            Object value = field.get(cellData.getValue());
                            if (value instanceof java.util.List) {
                return new SimpleObjectProperty<>(formatListValue((java.util.List<?>) value));
                            }
                            return new SimpleObjectProperty<>(value != null ? value.toString() : "");
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            return new SimpleObjectProperty<>("");
                        }
    }

    private String formatListValue(java.util.List<?> list) {
        if (!list.isEmpty() && list.get(0) instanceof bean.TimeSlot) {
            return list.stream()
                .map(ts -> {
                    bean.TimeSlot slot = (bean.TimeSlot) ts;
                    return slot.getStartTime().toLocalTime() + "-" + slot.getEndTime().toLocalTime();
                })
                .collect(java.util.stream.Collectors.joining(", "));
        }
        return "[" + list.size() + " items]";
    }

    private void addYardActionColumn(TableView<YardModel> table) {
            TableColumn<YardModel, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> createYardActionCell());
        table.getColumns().add(actionCol);
    }

    private javafx.scene.control.TableCell<YardModel, Void> createYardActionCell() {
        return new javafx.scene.control.TableCell<YardModel, Void>() {
                private final Button btnEdit = new Button("Sửa");
                private final Button btnDelete = new Button("Xóa");
                private final Button btnAdd = new Button("Thêm");
                private final HBox pane = new HBox(5, btnEdit, btnDelete, btnAdd);
            
                {
                setupYardActionButtons();
            }
            
            private void setupYardActionButtons() {
                    btnEdit.setOnAction(e -> {
                        YardModel yard = getTableView().getItems().get(getIndex());
                        showEditYardDialog(yard);
                    });
                    btnDelete.setOnAction(e -> {
                        YardModel yard = getTableView().getItems().get(getIndex());
                        getTableView().getItems().remove(yard);
                    deleteYard(yard);
                    });
                btnAdd.setOnAction(e -> showAddYardDialog());
                }
            
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : pane);
                }
        };
    }

    private void deleteYard(YardModel yard) {
        singleton.DuLieu.getInstance().getYards().remove(yard);
        singleton.DuLieu.getInstance().saveYardToFile("yards.json");
    }

    // ==================== BOOKINGS MANAGEMENT ====================

    private void showBookingsTableInContent() {
        if (contentPane != null) {
            List<BookingModel> bookings = DuLieu.getInstance().getBookings();
            logBookingData(bookings);
            
            TableView<BookingModel> table = new TableView<>();
            addBookingColumns(table, bookings);
            table.setItems(FXCollections.observableArrayList(bookings));
            contentPane.getChildren().setAll(table);
        }
    }

    private void logBookingData(List<BookingModel> bookings) {
        if (bookings != null && !bookings.isEmpty()) {
            System.out.println("=== DEBUG: Tổng số booking: " + bookings.size() + " ===");
            for (int i = 0; i < bookings.size(); i++) {
                BookingModel booking = bookings.get(i);
                System.out.println("Booking " + (i+1) + ": " + booking.getBookerName() + " - " + booking.getBookingId());
                System.out.println("  - Payment Status: " + booking.getCalculatedPaymentStatus());
                System.out.println("  - Payments count: " + (booking.getPayments() != null ? booking.getPayments().size() : 0));
            }
        } else {
            System.out.println("Không có booking nào được load!");
        }
    }

    private void addBookingColumns(TableView<BookingModel> table, List<BookingModel> bookings) {
        table.getColumns().clear();
        table.getColumns().addAll(
            createBookerNameColumn(),
            createBookerPhoneColumn(),
            createBookingTimeColumn(),
            createYardsColumn(),
            createServicesColumn(),
            createDepositColumn(), // Sử dụng cột deposit mới
            createTotalAmountColumn(),
            createPaymentStatusColumn(),
            createPaymentTimeColumn()
        );
    }

    private TableColumn<BookingModel, String> createBookerNameColumn() {
        TableColumn<BookingModel, String> col = new TableColumn<>("Người đặt");
        col.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getBookerName()));
        return col;
    }

    private TableColumn<BookingModel, String> createBookerPhoneColumn() {
        TableColumn<BookingModel, String> col = new TableColumn<>("SĐT");
        col.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getBookerPhone()));
        return col;
    }

    private TableColumn<BookingModel, String> createBookingTimeColumn() {
        TableColumn<BookingModel, String> col = new TableColumn<>("Thời gian");
        col.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getBookingTime() != null ? cell.getValue().getBookingTime().toString() : ""
        ));
        return col;
    }

    private TableColumn<BookingModel, String> createYardsColumn() {
        TableColumn<BookingModel, String> col = new TableColumn<>("Sân đã đặt");
        col.setCellValueFactory(cell -> new SimpleStringProperty(formatYardsInfo(cell.getValue())));
        return col;
    }

    private String formatYardsInfo(BookingModel booking) {
        StringBuilder details = new StringBuilder();
        
        if (booking.getBookingYards() != null && !booking.getBookingYards().isEmpty()) {
            for (bean.BookingYardModel by : booking.getBookingYards()) {
                if (details.length() > 0) details.append(" | ");
                
                // Tìm tên sân
                bean.YardModel yard = singleton.DuLieu.getInstance().findYardById(by.getYardId());
                String yardName = yard != null ? yard.getYardName() : "Sân " + by.getYardId();
                
                // Thông tin khung giờ
                if (by.getSlots() != null && !by.getSlots().isEmpty()) {
                    bean.TimeSlot firstSlot = by.getSlots().get(0);
                    bean.TimeSlot lastSlot = by.getSlots().get(by.getSlots().size() - 1);
                    
                    DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
                    String startTime = firstSlot.getStartTime().toLocalTime().format(timeFmt);
                    String endTime = lastSlot.getEndTime().toLocalTime().format(timeFmt);
                    String date = firstSlot.getStartTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM"));
                    
                    details.append(yardName).append(" (").append(date).append(" ").append(startTime).append("-").append(endTime).append(")");
                }
            }
        }
        
        return details.toString();
    }

    private TableColumn<BookingModel, String> createServicesColumn() {
        TableColumn<BookingModel, String> col = new TableColumn<>("Dịch vụ");
        col.setCellValueFactory(cell -> new SimpleStringProperty(formatServicesInfo(cell.getValue())));
        return col;
    }

    private String formatServicesInfo(BookingModel booking) {
        if (booking.getServiceIds() != null && !booking.getServiceIds().isEmpty()) {
            return booking.getServiceIds().stream()
                .map(id -> {
                    bean.ServicesModel service = singleton.DuLieu.getInstance().findServiceById(id);
                    return service != null ? service.getServiceName() + " (" + String.format("%,.0f", service.getPrice()) + "₫)" : "ID: " + id;
                })
                .collect(java.util.stream.Collectors.joining(", "));
        } else {
            return "Không có";
        }
    }

    private TableColumn<BookingModel, String> createPaymentTimeColumn() {
        TableColumn<BookingModel, String> col = new TableColumn<>("Thời gian thanh toán");
        col.setCellValueFactory(cell -> new SimpleStringProperty(formatPaymentTime(cell.getValue())));
        return col;
    }

    private String formatPaymentTime(BookingModel booking) {
        LocalDateTime lastPaymentTime = booking.getLastPaymentTime();
        if (lastPaymentTime != null) {
            return lastPaymentTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        return "";
    }

    private TableColumn<BookingModel, String> createPaymentMethodColumn() {
        TableColumn<BookingModel, String> col = new TableColumn<>("Phương thức thanh toán");
        col.setCellValueFactory(cell -> new SimpleStringProperty(formatPaymentMethods(cell.getValue())));
        return col;
    }

    private String formatPaymentMethods(BookingModel booking) {
        StringBuilder methods = new StringBuilder();
        if (booking.getServiceIds() != null && !booking.getServiceIds().isEmpty()) {
            if (methods.length() > 0) methods.append(" | ");
            methods.append("Có dịch vụ");
        }
        if (methods.length() == 0) {
            methods.append(formatPaymentStatus(booking));
        }
        return methods.toString();
    }

    private String formatPaymentStatus(BookingModel booking) {
        PaymentStatus status = booking.getCalculatedPaymentStatus();
        if (status != null) {
            switch (status) {
                case DEPOSITED: 
                    String result = "Đặt cọc";
                    if (booking.getPayments() != null && !booking.getPayments().isEmpty()) {
                        double totalPayments = booking.getPayments().stream()
                            .mapToDouble(PaymentRecord::getAmount)
                            .sum();
                        result += " (" + String.format("%,.0f", totalPayments) + "₫)";
                    }
                    return result;
                case PAID: return "Thanh toán toàn bộ";
                case UNPAID: return "Chưa thanh toán";
                default: return status.toString();
            }
        } else {
            return "Chưa xác định";
        }
    }

    private TableColumn<BookingModel, String> createPaymentStatusColumn() {
        TableColumn<BookingModel, String> col = new TableColumn<>("Trạng thái thanh toán");
        col.setCellValueFactory(cell -> new SimpleStringProperty(formatPaymentStatusLabel(cell.getValue())));
        return col;
    }

    private String formatPaymentStatusLabel(BookingModel booking) {
        PaymentStatus status = booking.getCalculatedPaymentStatus();
        if (status != null) {
            switch (status) {
                case DEPOSITED: return "Đã đặt cọc";
                case PAID: return "Đã thanh toán";
                case UNPAID: return "Chưa thanh toán";
                default: return status.toString();
            }
        }
        return "";
    }

    private TableColumn<BookingModel, String> createDepositColumn() {
        TableColumn<BookingModel, String> col = new TableColumn<>("Thanh toán/Đặt cọc");
        col.setCellValueFactory(cellData -> {
            BookingModel booking = cellData.getValue();
            if (booking.getPayments() == null || booking.getPayments().isEmpty()) {
                return new SimpleStringProperty("");
            }
            String paymentsInfo = booking.getPayments().stream()
                .map(p -> String.format("%,.0f₫ - %s - %s - %s%s",
                    p.getAmount(),
                    p.getMethod() != null ? p.getMethod().name() : "",
                    p.getStatus() != null ? p.getStatus().name() : "",
                    p.getTime() != null ? p.getTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "",
                    (p.getNote() != null && !p.getNote().isEmpty()) ? (" (" + p.getNote() + ")") : ""
                ))
                .collect(Collectors.joining("\n"));
            return new SimpleStringProperty(paymentsInfo);
        });
        return col;
    }

    private String formatDepositAmount(BookingModel booking) {
        // Nếu thanh toán toàn bộ hoặc không có thanh toán, hiển thị rỗng
        if (booking.getCalculatedPaymentStatus() == bean.PaymentStatus.PAID || 
            booking.getPayments() == null || booking.getPayments().isEmpty()) {
            return "";
        }
        double totalPayments = booking.getPayments().stream()
            .mapToDouble(PaymentRecord::getAmount)
            .sum();
        return String.format("%,.0f ₫", totalPayments);
    }

    private TableColumn<BookingModel, String> createTotalAmountColumn() {
        TableColumn<BookingModel, String> col = new TableColumn<>("Tổng tiền");
        col.setCellValueFactory(cell -> new SimpleStringProperty(formatTotalAmount(cell.getValue())));
        return col;
    }

    private String formatTotalAmount(BookingModel booking) {
        StringBuilder totalInfo = new StringBuilder();
        totalInfo.append(String.format("%,.0f ₫", booking.getTotalAmount()));
        
        // Nếu có đặt cọc và chưa thanh toán toàn bộ, hiển thị thông tin còn lại
        if (booking.getCalculatedPaymentStatus() == bean.PaymentStatus.DEPOSITED && 
            booking.getPayments() != null && !booking.getPayments().isEmpty()) {
            double totalPayments = booking.getPayments().stream()
                .mapToDouble(PaymentRecord::getAmount)
                .sum();
            if (booking.getTotalAmount() > totalPayments) {
                double remaining = booking.getTotalAmount() - totalPayments;
                totalInfo.append(" (Còn: ").append(String.format("%,.0f", remaining)).append("₫)");
            }
        }
        
        return totalInfo.toString();
    }

    private boolean checkHasDeposits(List<BookingModel> bookings) {
        return bookings.stream().anyMatch(booking -> 
            booking.getCalculatedPaymentStatus() != bean.PaymentStatus.PAID && 
            booking.getPayments() != null && !booking.getPayments().isEmpty()
        );
    }

    // ==================== STATS & OTHER VIEWS ====================

    private void showStatsInContent() {
        VBox statsBox = new VBox(10);
        statsBox.setStyle("-fx-padding: 40;");
        Label lbl = new Label("Thống kê sẽ hiển thị ở đây.");
        lbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        statsBox.getChildren().add(lbl);
        if (contentPane != null) {
            contentPane.getChildren().setAll(statsBox);
        }
    }

    // ==================== YARD DIALOG MANAGEMENT ====================

    private void showEditYardDialog(YardModel yard) {
        Dialog<YardModel> dialog = createYardDialog("Sửa Sân", yard);
        dialog.showAndWait().ifPresent(updatedYard -> {
            singleton.DuLieu.getInstance().saveYardToFile("yards.json");
            showYardsTableInContent();
        });
    }

    private void showAddYardDialog() {
        Dialog<YardModel> dialog = createYardDialog("Thêm Sân Mới", null);
        dialog.showAndWait().ifPresent(newYard -> {
            singleton.DuLieu.getInstance().getYards().add(newYard);
            singleton.DuLieu.getInstance().saveYardToFile("yards.json");
            showYardsTableInContent();
        });
    }

    private Dialog<YardModel> createYardDialog(String title, YardModel existingYard) {
        Dialog<YardModel> dialog = new Dialog<>();
        dialog.setTitle(title);
        
        // Create form fields
        TextField nameField = new TextField(existingYard != null ? existingYard.getYardName() : "");
        TextField priceField = new TextField(existingYard != null && existingYard.getYardPrice() != null ? existingYard.getYardPrice().toString() : "");
        TextField addressField = new TextField(existingYard != null ? existingYard.getYardAddress() : "");
        TextField thumbnailField = new TextField(existingYard != null ? existingYard.getYardThumbnail() : "");
        
        // Create image preview and buttons
        ImageView imgPreview = createImagePreview();
        Button btnChooseImg = createChooseImageButton(imgPreview, thumbnailField);
        
        // Create images list
        List<String> yardImages = new ArrayList<>(existingYard != null && existingYard.getYardImages() != null ? existingYard.getYardImages() : new ArrayList<>());
        HBox imagesPreview = createImagesPreview(yardImages);
        Button btnAddImages = createAddImagesButton(yardImages, imagesPreview);
        
        // Create grid layout
        GridPane grid = createYardFormGrid(nameField, priceField, addressField, thumbnailField, 
                                         btnChooseImg, imgPreview, btnAddImages, imagesPreview);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return createYardFromForm(existingYard, nameField, priceField, addressField, thumbnailField, yardImages);
            }
            return null;
        });
        
        return dialog;
    }

    private ImageView createImagePreview() {
        ImageView imgPreview = new ImageView();
        imgPreview.setFitWidth(80);
        imgPreview.setFitHeight(80);
        return imgPreview;
        }

    private Button createChooseImageButton(ImageView imgPreview, TextField thumbnailField) {
        Button btnChooseImg = new Button("Chọn ảnh");
        btnChooseImg.setOnAction(ev -> {
            String savedPath = ImageUtil.chooseImageAndSave(imgPreview, "src/images");
            if (savedPath != null) thumbnailField.setText(savedPath);
        });
        return btnChooseImg;
    }

    private HBox createImagesPreview(List<String> yardImages) {
        HBox imagesPreview = new HBox(5);
        for (String imgPath : yardImages) {
            try {
                ImageView iv = new ImageView(new Image(new java.io.File(imgPath).toURI().toString()));
                iv.setFitWidth(40); 
                iv.setFitHeight(30);
                imagesPreview.getChildren().add(iv);
            } catch (Exception ex) {}
        }
        return imagesPreview;
    }

    private Button createAddImagesButton(List<String> yardImages, HBox imagesPreview) {
        Button btnAddImages = new Button("Thêm ảnh con");
        btnAddImages.setOnAction(ev -> addImagesToPreview(yardImages, imagesPreview));
        return btnAddImages;
    }

    private void addImagesToPreview(List<String> yardImages, HBox imagesPreview) {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Chọn ảnh phụ");
            fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            List<java.io.File> files = fileChooser.showOpenMultipleDialog(null);
            if (files != null) {
                for (java.io.File file : files) {
                String savedPath = saveImageFile(file);
                    if (savedPath != null) {
                        yardImages.add(savedPath);
                    addImageToPreview(savedPath, imagesPreview);
                }
            }
        }
    }

    private String saveImageFile(java.io.File file) {
                    try {
                        java.nio.file.Path dest = java.nio.file.Paths.get("src/images", System.currentTimeMillis() + "_" + file.getName());
                        java.nio.file.Files.copy(file.toPath(), dest);
            return dest.toString();
        } catch (Exception ex) {
            return null;
        }
    }

    private void addImageToPreview(String savedPath, HBox imagesPreview) {
                        try {
                            ImageView iv = new ImageView(new Image(new java.io.File(savedPath).toURI().toString()));
            iv.setFitWidth(40); 
            iv.setFitHeight(30);
                            imagesPreview.getChildren().add(iv);
                        } catch (Exception ex) {}
                    }

    private GridPane createYardFormGrid(TextField nameField, TextField priceField, TextField addressField, 
                                      TextField thumbnailField, Button btnChooseImg, ImageView imgPreview, 
                                      Button btnAddImages, HBox imagesPreview) {
        GridPane grid = new GridPane();
        grid.setHgap(10); 
        grid.setVgap(10);
        grid.add(new Label("Tên sân:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Giá:"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Địa chỉ:"), 0, 2);
        grid.add(addressField, 1, 2);
        grid.add(new Label("Ảnh đại diện:"), 0, 3);
        grid.add(thumbnailField, 1, 3);
        grid.add(btnChooseImg, 2, 3);
        grid.add(imgPreview, 1, 4);
        grid.add(new Label("Ảnh con:"), 0, 5);
        grid.add(btnAddImages, 1, 5);
        grid.add(imagesPreview, 1, 6);
        return grid;
    }

    private YardModel createYardFromForm(YardModel existingYard, TextField nameField, TextField priceField, 
                                       TextField addressField, TextField thumbnailField, List<String> yardImages) {
        if (existingYard != null) {
            // Update existing yard
            existingYard.setYardName(nameField.getText());
            try { existingYard.setYardPrice(Double.parseDouble(priceField.getText())); } catch (Exception ex) {}
            existingYard.setYardAddress(addressField.getText());
            existingYard.setYardThumbnail(thumbnailField.getText());
            existingYard.setYardImages(yardImages);
            return existingYard;
        } else {
            // Create new yard
                YardModel newYard = new YardModel();
                newYard.setYardId(java.util.UUID.randomUUID().toString());
                newYard.setYardName(nameField.getText());
                try { newYard.setYardPrice(Double.parseDouble(priceField.getText())); } catch (Exception ex) {}
                newYard.setYardAddress(addressField.getText());
                newYard.setYardThumbnail(thumbnailField.getText());
                newYard.setYardImages(yardImages);
                newYard.setYardStatus("active");
                newYard.setIsAvailable(true);
                newYard.setCreateAt(java.time.LocalDateTime.now());
                newYard.setUpdateAt(java.time.LocalDateTime.now());
                return newYard;
            }
    }

    // ==================== BASE CONTROLLER METHODS ====================
    
    @Override
    public void setOnAction() {
        // Implementation for base controller interface
    }

	@Override
	public void constructorView() {
        // Implementation for base controller interface
	}

	@Override
	public void loadData() {
        DuLieu.getInstance().loadYardsFromFile("yards.json");
        // Clear existing bookings and reload
        DuLieu.getInstance().getBookings().clear();
        DuLieu.getInstance().loadBookingsFromFile("bookings.json");
        DuLieu.getInstance().loadServicesFromFile("services.json");
	}

	@Override
	public void refresh() {
        loadData();
        // Refresh current view
        if (contentPane != null && !contentPane.getChildren().isEmpty()) {
            Object currentContent = contentPane.getChildren().get(0);
            if (currentContent instanceof TableView) {
                TableView<?> table = (TableView<?>) currentContent;
                if (table.getItems() != null) {
                    table.refresh();
                }
            }
        }
	}
} 