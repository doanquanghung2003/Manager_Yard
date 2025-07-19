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
	@FXML
	private StackPane contentPane;
	@FXML
	private javafx.scene.control.SplitMenuButton btnMenuYards;
	@FXML
	private javafx.scene.control.MenuItem menuItemYardList, menuItemYardSchedule;
	@FXML
	private Button btnMenuBookings, btnMenuServices, btnMenuStats, btnRefresh, btnMenuUsers, btnMenuRoles;
	@FXML
	private DatePicker dpBookingDate;
	@FXML
	private Label lblTotalBookingAmount;

	public void initialize() {
		loadInitialData();
		setupEventHandlers();
		showYardsTableInContent();
	}

	private void loadInitialData() {
		DuLieu.getInstance().loadYardsFromFile("yards.json");
		DuLieu.getInstance().loadBookingsFromFile("bookings.json");
		DuLieu.getInstance().loadServicesFromFile("services.json");
		DuLieu.getInstance().loadUsersFromFile("users.json");
		DuLieu.getInstance().loadRolesFromFile("roles.json");
	}

	private void setupEventHandlers() {
		setOnAction();
		setupMenuButtons();
		setupRefreshButton();
	}

	private void setupMenuButtons() {
		if (btnMenuYards != null)
			btnMenuYards.setOnAction(e -> showYardsTableInContent());
		if (menuItemYardList != null)
			menuItemYardList.setOnAction(e -> showYardsTableInContent());
		if (menuItemYardSchedule != null)
			menuItemYardSchedule.setOnAction(e -> showYardScheduleInContent());
		if (btnMenuBookings != null)
			btnMenuBookings.setOnAction(e -> showBookingsTableInContent());
		if (btnMenuServices != null)
			btnMenuServices.setOnAction(e -> showServicesTableInContent());
		if (btnMenuUsers != null)
			btnMenuUsers.setOnAction(e -> showUsersTableInContent());
		if (btnMenuRoles != null)
			btnMenuRoles.setOnAction(e -> showRoleManagement());
		if (btnMenuStats != null)
			btnMenuStats.setOnAction(e -> showStatsInContent());
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

	private void showYardsTableInContent() {
		if (contentPane != null) {
			// Create header section
			HBox headerBox = new HBox(15);
			headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
			headerBox.setStyle("-fx-padding: 20px; -fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");

			Label titleLabel = new Label("Quản Lý Sân Bóng");
			titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

			Button btnAddYard = new Button("+ Thêm Sân Mới");
			btnAddYard.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 5px;");
			btnAddYard.setOnAction(e -> showAddYardDialog());

			headerBox.getChildren().addAll(titleLabel, btnAddYard);

			// Create table
			TableView<YardModel> table = createYardsTableView();
			table.setItems(FXCollections.observableArrayList(DuLieu.getInstance().getYards()));
			table.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 5px;");
			 
			// Create VBox to hold header and table
			VBox container = new VBox(10);
			container.getChildren().addAll(headerBox, table);
			VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS); // Table takes all remaining space

			contentPane.getChildren().setAll(container);
		}
	}

	private void showYardScheduleInContent() {
		if (contentPane != null) {
			try {
				// Load the YardMap FXML
				javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxmlManager/YardMap.fxml"));
				javafx.scene.Parent yardMapRoot = loader.load();
				
				// Create header section
				HBox headerBox = new HBox(15);
				headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
				headerBox.setStyle("-fx-padding: 20px; -fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");

				Label titleLabel = new Label("Lịch Đặt Sân");
				titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

				headerBox.getChildren().addAll(titleLabel);

				// Create VBox to hold header and yard map
				VBox container = new VBox(10);
				container.getChildren().addAll(headerBox, yardMapRoot);
				VBox.setVgrow(yardMapRoot, javafx.scene.layout.Priority.ALWAYS); // Yard map takes all remaining space

				contentPane.getChildren().setAll(container);
			} catch (Exception e) {
				e.printStackTrace();
				// Fallback to placeholder if loading fails
				showYardSchedulePlaceholder();
			}
		}
	}

	private void showYardSchedulePlaceholder() {
		if (contentPane != null) {
			// Create header section
			HBox headerBox = new HBox(15);
			headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
			headerBox.setStyle("-fx-padding: 20px; -fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");

			Label titleLabel = new Label("Lịch Đặt Sân");
			titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

			headerBox.getChildren().addAll(titleLabel);

			// Create placeholder
			VBox placeholder = new VBox(10);
			placeholder.setAlignment(javafx.geometry.Pos.CENTER);
			placeholder.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 5px; -fx-padding: 40px;");

			Label placeholderLabel = new Label("📅 Lịch Đặt Sân");
			placeholderLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #6c757d;");

			Label descriptionLabel = new Label("Hiển thị lịch đặt sân và quản lý thời gian");
			descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6c757d;");

			placeholder.getChildren().addAll(placeholderLabel, descriptionLabel);

			// Create VBox to hold header and placeholder
			VBox container = new VBox(10);
			container.getChildren().addAll(headerBox, placeholder);
			VBox.setVgrow(placeholder, javafx.scene.layout.Priority.ALWAYS);

			contentPane.getChildren().setAll(container);
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
			if (shouldSkipField(field))
				continue;

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
		return java.lang.reflect.Modifier.isStatic(field.getModifiers()) || field.getName().equals("formatter")
				|| field.getName().equals("yardId");
	}

	private void addThumbnailColumn(TableView<YardModel> table, Field field, String colName) {
		TableColumn<YardModel, Object> col = new TableColumn<>(colName);
		col.setCellValueFactory(cellData -> {
			String path = null;
			try {
				path = (String) field.get(cellData.getValue());
			} catch (IllegalAccessException e) {
			}
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
				if (empty || item == null || ((String) item).isEmpty()) {
					setGraphic(null);
				} else {
					try {
						imageView.setImage(new Image(new java.io.File((String) item).toURI().toString()));
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
			try {
				images = (java.util.List<String>) field.get(cellData.getValue());
			} catch (IllegalAccessException e) {
			}
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
						if (count++ >= 3)
							break;
						try {
							ImageView iv = new ImageView(new Image(new java.io.File(path).toURI().toString()));
							iv.setFitWidth(40);
							iv.setFitHeight(30);
							iv.setPreserveRatio(true);
							hbox.getChildren().add(iv);
						} catch (Exception e) {
						}
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
			try {
				price = (Double) field.get(cellData.getValue());
			} catch (IllegalAccessException e) {
			}
			return new SimpleObjectProperty<>(price);
		});
		col.setCellFactory(tc -> createPriceCell());
		table.getColumns().add(col);
	}

	private javafx.scene.control.TableCell<YardModel, Object> createPriceCell() {
		return new javafx.scene.control.TableCell<YardModel, Object>() {
			private final java.text.NumberFormat vnFormat = java.text.NumberFormat
					.getCurrencyInstance(new java.util.Locale("vi", "VN"));

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

	private SimpleObjectProperty<Object> createStatusCellValue(
			javafx.scene.control.TableColumn.CellDataFeatures<YardModel, Object> cellData, Field field) {
		try {
			Object value = field.get(cellData.getValue());
			String status = (value != null && value.toString().equals("active")) ? "Đang hoạt động" : value.toString();
			return new SimpleObjectProperty<>(status);
		} catch (IllegalAccessException e) {
			return new SimpleObjectProperty<>("");
		}
	}

	private SimpleObjectProperty<Object> createGenericCellValue(
			javafx.scene.control.TableColumn.CellDataFeatures<YardModel, Object> cellData, Field field) {
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
			return list.stream().map(ts -> {
				bean.TimeSlot slot = (bean.TimeSlot) ts;
				return slot.getStartTime().toLocalTime() + "-" + slot.getEndTime().toLocalTime();
			}).collect(java.util.stream.Collectors.joining(", "));
		}
		return "[" + list.size() + " items]";
	}

	private void addYardActionColumn(TableView<YardModel> table) {
		TableColumn<YardModel, Void> actionCol = new TableColumn<>("Thao tác");
		actionCol.setCellFactory(col -> createYardActionCell());
		table.getColumns().add(actionCol);
	}

	private javafx.scene.control.TableCell<YardModel, Void> createYardActionCell() {
		return new javafx.scene.control.TableCell<YardModel, Void>() {
			private final Button btnEdit = new Button("✏️ Sửa");
			private final Button btnDelete = new Button("🗑️ Xóa");
			private final HBox pane = new HBox(5, btnEdit, btnDelete);

			{
				setupYardActionButtons();
			}

			private void setupYardActionButtons() {
				btnEdit.setStyle("-fx-font-size: 12px; -fx-padding: 5px 10px; -fx-background-color: #ffc107; -fx-text-fill: #212529; -fx-background-radius: 3px;");
				btnDelete.setStyle("-fx-font-size: 12px; -fx-padding: 5px 10px; -fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 3px;");
				
				btnEdit.setOnAction(e -> {
					YardModel yard = getTableView().getItems().get(getIndex());
					showEditYardDialog(yard);
				});
				btnDelete.setOnAction(e -> {
					YardModel yard = getTableView().getItems().get(getIndex());
					getTableView().getItems().remove(yard);
					deleteYard(yard);
				});
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

	private void showBookingsTableInContent() {
		if (contentPane != null) {
			List<BookingModel> bookings = DuLieu.getInstance().getBookings();
			logBookingData(bookings);

			// Create header section
			HBox headerBox = new HBox(15);
			headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
			headerBox.setStyle("-fx-padding: 20px; -fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");

			Label titleLabel = new Label("Quản Lý Đặt Sân");
			titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

			headerBox.getChildren().addAll(titleLabel);

			// Create table
			TableView<BookingModel> table = new TableView<>();
			
			addBookingColumns(table, bookings);
			table.setItems(FXCollections.observableArrayList(bookings));
			table.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 5px;");

			// Calculate total amount
			double totalAmount = bookings.stream().mapToDouble(BookingModel::getTotalAmount).sum();
			Label totalLabel = new Label("Tổng tiền: " + String.format("%,.0f ₫", totalAmount));
			totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 10px;");

			// Create footer with total amount
			HBox footerBox = new HBox();
			footerBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
			footerBox.setStyle("-fx-padding: 10px; -fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1 0 0 0;");
			footerBox.getChildren().add(totalLabel);

			// Create VBox to hold header, table and footer
			VBox container = new VBox(10);
			container.getChildren().addAll(headerBox, table, footerBox);
			VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS); 

			contentPane.getChildren().setAll(container);
		}
	}

	private void logBookingData(List<BookingModel> bookings) {
		if (bookings != null && !bookings.isEmpty()) {

			for (int i = 0; i < bookings.size(); i++) {
				BookingModel booking = bookings.get(i);
			}
		} else {
			System.out.println("Không có booking nào được load!");
		}
	}

	private void addBookingColumns(TableView<BookingModel> table, List<BookingModel> bookings) {
		table.getColumns().clear();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.getColumns().addAll(createBookerNameColumn(), createBookerPhoneColumn(), createBookingTimeColumn(),
				createYardsColumn(), createServicesColumn(), createTotalAmountColumn(),
				createPaymentStatusColumn(), createBookingActionColumn());
	}

	private TableColumn<BookingModel, String> createBookerNameColumn() {
		TableColumn<BookingModel, String> col = new TableColumn<>("Khách hàng");
		col.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getBookerName()));
		col.setPrefWidth(120);
		return col;
	}

	private TableColumn<BookingModel, String> createBookerPhoneColumn() {
		TableColumn<BookingModel, String> col = new TableColumn<>("SĐT");
		col.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getBookerPhone()));
		col.setPrefWidth(100);
		return col;
	}

	private TableColumn<BookingModel, String> createBookingTimeColumn() {
		TableColumn<BookingModel, String> col = new TableColumn<>("Ngày đặt");
		col.setCellValueFactory(cell -> new SimpleStringProperty(
				cell.getValue().getBookingTime() != null ? 
				cell.getValue().getBookingTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""));
		col.setPrefWidth(100);
		return col;
	}

	private TableColumn<BookingModel, String> createYardsColumn() {
		TableColumn<BookingModel, String> col = new TableColumn<>("Sân & Giờ");
		col.setCellValueFactory(cell -> new SimpleStringProperty(formatYardsInfo(cell.getValue())));
		col.setPrefWidth(200);
		return col;
	}

	private String formatYardsInfo(BookingModel booking) {
		StringBuilder details = new StringBuilder();

		if (booking.getBookingYards() != null && !booking.getBookingYards().isEmpty()) {
			for (bean.BookingYardModel by : booking.getBookingYards()) {
				if (details.length() > 0)
					details.append("\n");

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

					details.append(yardName).append(" (").append(date).append(" ").append(startTime).append("-")
							.append(endTime).append(")");
				}
			}
		}

		return details.toString();
	}

	private TableColumn<BookingModel, String> createServicesColumn() {
		TableColumn<BookingModel, String> col = new TableColumn<>("Dịch vụ");
		col.setCellValueFactory(cell -> new SimpleStringProperty(formatServicesInfo(cell.getValue())));
		col.setPrefWidth(150);
		return col;
	}

	private String formatServicesInfo(BookingModel booking) {
		if (booking.getServiceIds() != null && !booking.getServiceIds().isEmpty()) {
			return booking.getServiceIds().stream().map(id -> {
				bean.ServicesModel service = singleton.DuLieu.getInstance().findServiceById(id);
				return service != null
						? service.getServiceName() + " (" + String.format("%,.0f", service.getPrice()) + "₫)"
						: "ID: " + id;
			}).collect(java.util.stream.Collectors.joining(", "));
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
			if (methods.length() > 0)
				methods.append(" | ");
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
					double totalPayments = booking.getPayments().stream().mapToDouble(PaymentRecord::getAmount).sum();
					result += " (" + String.format("%,.0f", totalPayments) + "₫)";
				}
				return result;
			case PAID:
				return "Thanh toán toàn bộ";
			case UNPAID:
				return "Chưa thanh toán";
			default:
				return status.toString();
			}
		} else {
			return "Chưa xác định";
		}
	}

	private TableColumn<BookingModel, String> createPaymentStatusColumn() {
		TableColumn<BookingModel, String> col = new TableColumn<>("Trạng thái");
		col.setCellValueFactory(cell -> new SimpleStringProperty(formatPaymentStatusLabel(cell.getValue())));
		col.setPrefWidth(100);
		return col;
	}

	private String formatPaymentStatusLabel(BookingModel booking) {
		PaymentStatus status = booking.getCalculatedPaymentStatus();
		if (status != null) {
			switch (status) {
			case DEPOSITED:
				return "Đã đặt cọc";
			case PAID:
				return "Đã thanh toán";
			case UNPAID:
				return "Chưa thanh toán";
			default:
				return status.toString();
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
					.map(p -> String.format("%,.0f₫ - %s - %s - %s%s", p.getAmount(),
							p.getMethod() != null ? p.getMethod().name() : "",
							p.getStatus() != null ? p.getStatus().name() : "",
							p.getTime() != null ? p.getTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
									: "",
							(p.getNote() != null && !p.getNote().isEmpty()) ? (" (" + p.getNote() + ")") : ""))
					.collect(Collectors.joining("\n"));
			return new SimpleStringProperty(paymentsInfo);
		});
		return col;
	}

	private String formatDepositAmount(BookingModel booking) {
		// Nếu thanh toán toàn bộ hoặc không có thanh toán, hiển thị rỗng
		if (booking.getCalculatedPaymentStatus() == bean.PaymentStatus.PAID || booking.getPayments() == null
				|| booking.getPayments().isEmpty()) {
			return "";
		}
		double totalPayments = booking.getPayments().stream().mapToDouble(PaymentRecord::getAmount).sum();
		return String.format("%,.0f ₫", totalPayments);
	}

	private TableColumn<BookingModel, String> createTotalAmountColumn() {
		TableColumn<BookingModel, String> col = new TableColumn<>("Tổng tiền");
		col.setCellValueFactory(cell -> new SimpleStringProperty(formatTotalAmount(cell.getValue())));
		col.setPrefWidth(120);
		return col;
	}

	private String formatTotalAmount(BookingModel booking) {
		StringBuilder totalInfo = new StringBuilder();
		totalInfo.append(String.format("%,.0f ₫", booking.getTotalAmount()));

		// Nếu có đặt cọc và chưa thanh toán toàn bộ, hiển thị thông tin còn lại
		if (booking.getCalculatedPaymentStatus() == bean.PaymentStatus.DEPOSITED && booking.getPayments() != null
				&& !booking.getPayments().isEmpty()) {
			double totalPayments = booking.getPayments().stream().mapToDouble(PaymentRecord::getAmount).sum();
			if (booking.getTotalAmount() > totalPayments) {
				double remaining = booking.getTotalAmount() - totalPayments;
				totalInfo.append(" (Còn: ").append(String.format("%,.0f", remaining)).append("₫)");
			}
		}

		return totalInfo.toString();
	}

	private TableColumn<BookingModel, Void> createBookingActionColumn() {
		TableColumn<BookingModel, Void> actionCol = new TableColumn<>("Thao tác");
		actionCol.setCellFactory(col -> createBookingActionCell());
		actionCol.setPrefWidth(100);
		return actionCol;
	}

	private javafx.scene.control.TableCell<BookingModel, Void> createBookingActionCell() {
		return new javafx.scene.control.TableCell<BookingModel, Void>() {
			private final Button btnEdit = new Button(" Sửa");
			private final Button btnDelete = new Button(" Xóa");
			private final HBox pane = new HBox(5, btnEdit, btnDelete);

			{
				setupBookingActionButtons();
			}

			private void setupBookingActionButtons() {
				btnEdit.setStyle("-fx-font-size: 12px; -fx-padding: 5px 10px; -fx-background-color: #ffc107; -fx-text-fill: #212529; -fx-background-radius: 3px;");
				btnDelete.setStyle("-fx-font-size: 12px; -fx-padding: 5px 10px; -fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 3px;");
				
				btnEdit.setOnAction(e -> {
					BookingModel booking = getTableView().getItems().get(getIndex());
					showEditBookingDialog(booking);
				});
				btnDelete.setOnAction(e -> {
					BookingModel booking = getTableView().getItems().get(getIndex());
					getTableView().getItems().remove(booking);
					deleteBooking(booking);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : pane);
			}
		};
	}

	private void deleteBooking(BookingModel booking) {
		singleton.DuLieu.getInstance().getBookings().remove(booking);
		singleton.DuLieu.getInstance().saveBookingToFile("bookings.json");
	}

	private boolean checkHasDeposits(List<BookingModel> bookings) {
		return bookings.stream().anyMatch(booking -> booking.getCalculatedPaymentStatus() != bean.PaymentStatus.PAID
				&& booking.getPayments() != null && !booking.getPayments().isEmpty());
	}

	private void showServicesTableInContent() {
		if (contentPane != null) {
			List<bean.ServicesModel> services = DuLieu.getInstance().getServices();
			logServicesData(services);

			// Create header section
			HBox headerBox = new HBox(15);
			headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
			headerBox.setStyle("-fx-padding: 20px; -fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");

			Label titleLabel = new Label("Quản Lý Dịch Vụ");
			titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

			Button btnAddService = new Button("+ Thêm Dịch Vụ Mới");
			btnAddService.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-color: #fd7e14; -fx-text-fill: white; -fx-background-radius: 5px;");
			btnAddService.setOnAction(e -> showAddServiceDialog());

			headerBox.getChildren().addAll(titleLabel, btnAddService);

			// Create table
			TableView<bean.ServicesModel> table = new TableView<>();
			
			addServicesColumns(table, services);
			table.setItems(FXCollections.observableArrayList(services));
			table.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 5px;");

			// Create VBox to hold header and table
			VBox container = new VBox(0);
			container.getChildren().addAll(headerBox, table);
			VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);

			contentPane.getChildren().setAll(container);
		}
	}

	private void logServicesData(List<bean.ServicesModel> services) {
		if (services != null && !services.isEmpty()) {
			System.out.println("Đã load " + services.size() + " dịch vụ:");
			for (int i = 0; i < services.size(); i++) {
				bean.ServicesModel service = services.get(i);
				System.out.println((i + 1) + ". " + service.getServiceName() + " - " + 
					String.format("%,.0f", service.getPrice()) + "₫");
			}
		} else {
			System.out.println("Không có dịch vụ nào được load!");
		}
	}

	private void addServicesColumns(TableView<bean.ServicesModel> table, List<bean.ServicesModel> services) {
		table.getColumns().clear();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.getColumns().addAll(createServiceNameColumn(), 
			createServiceDescriptionColumn(), createServicePriceColumn(), createServiceActionColumn());
	}



	private TableColumn<bean.ServicesModel, String> createServiceNameColumn() {
		TableColumn<bean.ServicesModel, String> col = new TableColumn<>("Tên Dịch vụ");
		col.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getServiceName()));
		return col;
	}

	private TableColumn<bean.ServicesModel, String> createServiceDescriptionColumn() {
		TableColumn<bean.ServicesModel, String> col = new TableColumn<>("Mô tả");
		col.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescription()));
		return col;
	}

	private TableColumn<bean.ServicesModel, String> createServicePriceColumn() {
		TableColumn<bean.ServicesModel, String> col = new TableColumn<>("Giá");
		col.setCellValueFactory(cell -> new SimpleStringProperty(
			String.format("%,.0f ₫", cell.getValue().getPrice())));
		return col;
	}

	private TableColumn<bean.ServicesModel, Void> createServiceActionColumn() {
		TableColumn<bean.ServicesModel, Void> actionCol = new TableColumn<>("Thao tác");
		actionCol.setCellFactory(col -> createServiceActionCell());
		return actionCol;
	}

	private javafx.scene.control.TableCell<bean.ServicesModel, Void> createServiceActionCell() {
		return new javafx.scene.control.TableCell<bean.ServicesModel, Void>() {
			private final Button btnEdit = new Button(" Sửa");
			private final Button btnDelete = new Button(" Xóa");
			private final HBox pane = new HBox(5, btnEdit, btnDelete);

			{
				setupServiceActionButtons();
			}

			private void setupServiceActionButtons() {
				btnEdit.setStyle("-fx-font-size: 12px; -fx-padding: 5px 10px; -fx-background-color: #ffc107; -fx-text-fill: #212529; -fx-background-radius: 3px;");
				btnDelete.setStyle("-fx-font-size: 12px; -fx-padding: 5px 10px; -fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 3px;");
				
				btnEdit.setOnAction(e -> {
					bean.ServicesModel service = getTableView().getItems().get(getIndex());
					showEditServiceDialog(service);
				});
				btnDelete.setOnAction(e -> {
					bean.ServicesModel service = getTableView().getItems().get(getIndex());
					getTableView().getItems().remove(service);
					deleteService(service);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : pane);
			}
		};
	}

	private void deleteService(bean.ServicesModel service) {
		singleton.DuLieu.getInstance().getServices().remove(service);
		singleton.DuLieu.getInstance().saveServiceToFile("services.json");
	}

	private void showAddBookingDialog() {
		Dialog<BookingModel> dialog = createBookingDialog("Thêm Đặt Sân Mới", null);
		dialog.showAndWait().ifPresent(newBooking -> {
			singleton.DuLieu.getInstance().getBookings().add(newBooking);
			singleton.DuLieu.getInstance().saveBookingToFile("bookings.json");
			showBookingsTableInContent();
		});
	}

	private void showEditBookingDialog(BookingModel booking) {
		Dialog<BookingModel> dialog = createBookingDialog("Sửa Đặt Sân", booking);
		dialog.showAndWait().ifPresent(updatedBooking -> {
			singleton.DuLieu.getInstance().saveBookingToFile("bookings.json");
			showBookingsTableInContent();
		});
	}

	private Dialog<BookingModel> createBookingDialog(String title, BookingModel existingBooking) {
		Dialog<BookingModel> dialog = new Dialog<>();
		dialog.setTitle(title);

		// Create form fields
		TextField bookerNameField = new TextField(existingBooking != null ? existingBooking.getBookerName() : "");
		TextField bookerPhoneField = new TextField(existingBooking != null ? existingBooking.getBookerPhone() : "");
		DatePicker bookingDatePicker = new DatePicker();
		if (existingBooking != null && existingBooking.getBookingTime() != null) {
			bookingDatePicker.setValue(existingBooking.getBookingTime().toLocalDate());
		}

		// Create grid layout
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.add(new Label("Tên người đặt:"), 0, 0);
		grid.add(bookerNameField, 1, 0);
		grid.add(new Label("Số điện thoại:"), 0, 1);
		grid.add(bookerPhoneField, 1, 1);
		grid.add(new Label("Ngày đặt:"), 0, 2);
		grid.add(bookingDatePicker, 1, 2);

		dialog.getDialogPane().setContent(grid);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// Set result converter
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.OK) {
				return createBookingFromForm(existingBooking, bookerNameField, bookerPhoneField, bookingDatePicker);
			}
			return null;
		});

		return dialog;
	}

	private BookingModel createBookingFromForm(BookingModel existingBooking, TextField bookerNameField, 
		TextField bookerPhoneField, DatePicker bookingDatePicker) {
		if (existingBooking != null) {
			// Update existing booking
			existingBooking.setBookerName(bookerNameField.getText());
			existingBooking.setBookerPhone(bookerPhoneField.getText());
			if (bookingDatePicker.getValue() != null) {
				existingBooking.setBookingTime(bookingDatePicker.getValue().atStartOfDay());
			}
			return existingBooking;
		} else {
			// Create new booking
			BookingModel newBooking = new BookingModel();
			newBooking.setBookingId(java.util.UUID.randomUUID().toString());
			newBooking.setBookerName(bookerNameField.getText());
			newBooking.setBookerPhone(bookerPhoneField.getText());
			if (bookingDatePicker.getValue() != null) {
				newBooking.setBookingTime(bookingDatePicker.getValue().atStartOfDay());
			}
			newBooking.setBookingYards(new ArrayList<>());
			newBooking.setServiceIds(new ArrayList<>());
			newBooking.setPayments(new ArrayList<>());
			return newBooking;
		}
	}

	private void showEditServiceDialog(bean.ServicesModel service) {
		Dialog<bean.ServicesModel> dialog = createServiceDialog("Sửa Dịch vụ", service);
		dialog.showAndWait().ifPresent(updatedService -> {
			singleton.DuLieu.getInstance().saveServiceToFile("services.json");
			showServicesTableInContent();
		});
	}

	private void showAddServiceDialog() {
		Dialog<bean.ServicesModel> dialog = createServiceDialog("Thêm Dịch vụ Mới", null);
		dialog.showAndWait().ifPresent(newService -> {
			singleton.DuLieu.getInstance().getServices().add(newService);
			singleton.DuLieu.getInstance().saveServiceToFile("services.json");
			showServicesTableInContent();
		});
	}

	private Dialog<bean.ServicesModel> createServiceDialog(String title, bean.ServicesModel existingService) {
		Dialog<bean.ServicesModel> dialog = new Dialog<>();
		dialog.setTitle(title);

		// Create form fields
		TextField nameField = new TextField(existingService != null ? existingService.getServiceName() : "");
		TextField descriptionField = new TextField(existingService != null ? existingService.getDescription() : "");
		TextField priceField = new TextField(existingService != null ? String.valueOf(existingService.getPrice()) : "");

		// Create grid layout
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.add(new Label("Tên dịch vụ:"), 0, 0);
		grid.add(nameField, 1, 0);
		grid.add(new Label("Mô tả:"), 0, 1);
		grid.add(descriptionField, 1, 1);
		grid.add(new Label("Giá:"), 0, 2);
		grid.add(priceField, 1, 2);

		dialog.getDialogPane().setContent(grid);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// Set result converter
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.OK) {
				return createServiceFromForm(existingService, nameField, descriptionField, priceField);
			}
			return null;
		});

		return dialog;
	}

	private bean.ServicesModel createServiceFromForm(bean.ServicesModel existingService, TextField nameField, 
		TextField descriptionField, TextField priceField) {
		if (existingService != null) {
			// Update existing service
			existingService.setServiceName(nameField.getText());
			existingService.setDescription(descriptionField.getText());
			try {
				existingService.setPrice(Double.parseDouble(priceField.getText()));
			} catch (Exception ex) {
				// Keep existing price if parsing fails
			}
			return existingService;
		} else {
			// Create new service
			bean.ServicesModel newService = new bean.ServicesModel();
			newService.setServiceId(java.util.UUID.randomUUID().toString());
			newService.setServiceName(nameField.getText());
			newService.setDescription(descriptionField.getText());
			try {
				newService.setPrice(Double.parseDouble(priceField.getText()));
			} catch (Exception ex) {
				newService.setPrice(0.0);
			}
			return newService;
		}
	}

	private void showStatsInContent() {
		if (contentPane != null) {
			// Create header section
			HBox headerBox = new HBox(15);
			headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
			headerBox.setStyle("-fx-padding: 20px; -fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");

			Label titleLabel = new Label("Thống Kê Tổng Quan");
			titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

			headerBox.getChildren().add(titleLabel);

			// Create stats content
			VBox statsContent = new VBox(20);
			statsContent.setStyle("-fx-padding: 40px; -fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 5px;");
			statsContent.setAlignment(javafx.geometry.Pos.CENTER);

			// Create stats cards
			HBox statsCards = new HBox(20);
			statsCards.setAlignment(javafx.geometry.Pos.CENTER);

			// Total Yards Card
			VBox yardsCard = createStatsCard("Tổng Số Sân", String.valueOf(DuLieu.getInstance().getYards().size()), "#28a745");
			// Total Bookings Card
			VBox bookingsCard = createStatsCard("Tổng Đặt Sân", String.valueOf(DuLieu.getInstance().getBookings().size()), "#007bff");
			// Total Services Card
			VBox servicesCard = createStatsCard("Tổng Dịch Vụ", String.valueOf(DuLieu.getInstance().getServices().size()), "#fd7e14");
			// Total Users Card
			VBox usersCard = createStatsCard("Tổng Người Dùng", String.valueOf(DuLieu.getInstance().getUsers().size()), "#6f42c1");

			statsCards.getChildren().addAll(yardsCard, bookingsCard, servicesCard, usersCard);
			statsContent.getChildren().add(statsCards);

			// Create VBox to hold header and content
			VBox container = new VBox(0);
			container.getChildren().addAll(headerBox, statsContent);
			VBox.setVgrow(statsContent, javafx.scene.layout.Priority.ALWAYS);

			contentPane.getChildren().setAll(container);
		}
	}

	private void showUsersTableInContent() {
		if (contentPane != null) {
			List<bean.UserModel> users = DuLieu.getInstance().getUsers();
			logUsersData(users);

			// Create header section
			HBox headerBox = new HBox(15);
			headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
			headerBox.setStyle("-fx-padding: 20px; -fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");

			Label titleLabel = new Label("Quản Lý Người Dùng");
			titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

			Button btnAddUser = new Button("+ Thêm Người Dùng Mới");
			btnAddUser.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-color: #6f42c1; -fx-text-fill: white; -fx-background-radius: 5px;");
			btnAddUser.setOnAction(e -> showAddUserDialog());

			headerBox.getChildren().addAll(titleLabel, btnAddUser);

			// Create table
			TableView<bean.UserModel> table = new TableView<>();
			
			addUsersColumns(table, users);
			table.setItems(FXCollections.observableArrayList(users));
			table.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 5px;");

			// Create VBox to hold header and table
			VBox container = new VBox(10);
			container.getChildren().addAll(headerBox, table);
			VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);

			contentPane.getChildren().setAll(container);
		}
	}

	private void logUsersData(List<bean.UserModel> users) {
		if (users != null && !users.isEmpty()) {
			System.out.println("Đã load " + users.size() + " người dùng:");
			for (int i = 0; i < users.size(); i++) {
				bean.UserModel user = users.get(i);
				System.out.println((i + 1) + ". " + user.getFullName() + " (" + user.getUserName() + ") - " + user.getEmail());
			}
		} else {
			System.out.println("Không có người dùng nào được load!");
		}
	}

	private void addUsersColumns(TableView<bean.UserModel> table, List<bean.UserModel> users) {
		table.getColumns().clear();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.getColumns().addAll(createUserNameColumn(), createFullNameColumn(), 
			createEmailColumn(), createCreateAtColumn(), createUserActionColumn());
	}

	private TableColumn<bean.UserModel, String> createUserNameColumn() {
		TableColumn<bean.UserModel, String> col = new TableColumn<>("Tên đăng nhập");
		col.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUserName()));
		col.setPrefWidth(150);
		return col;
	}

	private TableColumn<bean.UserModel, String> createFullNameColumn() {
		TableColumn<bean.UserModel, String> col = new TableColumn<>("Họ và tên");
		col.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFullName()));
		col.setPrefWidth(200);
		return col;
	}

	private TableColumn<bean.UserModel, String> createEmailColumn() {
		TableColumn<bean.UserModel, String> col = new TableColumn<>("Email");
		col.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
		col.setPrefWidth(200);
		return col;
	}

	private TableColumn<bean.UserModel, String> createCreateAtColumn() {
		TableColumn<bean.UserModel, String> col = new TableColumn<>("Ngày tạo");
		col.setCellValueFactory(cell -> new SimpleStringProperty(
			cell.getValue().getCreateAt() != null ? 
			cell.getValue().getCreateAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : ""));
		col.setPrefWidth(150);
		return col;
	}

	private TableColumn<bean.UserModel, Void> createUserActionColumn() {
		TableColumn<bean.UserModel, Void> actionCol = new TableColumn<>("Thao tác");
		actionCol.setCellFactory(col -> createUserActionCell());
		actionCol.setPrefWidth(120);
		return actionCol;
	}

	private javafx.scene.control.TableCell<bean.UserModel, Void> createUserActionCell() {
		return new javafx.scene.control.TableCell<bean.UserModel, Void>() {
			private final Button btnEdit = new Button("✏️ Sửa");
			private final Button btnDelete = new Button("🗑️ Xóa");
			private final HBox pane = new HBox(5, btnEdit, btnDelete);

			{
				setupUserActionButtons();
			}

			private void setupUserActionButtons() {
				btnEdit.setStyle("-fx-font-size: 12px; -fx-padding: 5px 10px; -fx-background-color: #ffc107; -fx-text-fill: #212529; -fx-background-radius: 3px;");
				btnDelete.setStyle("-fx-font-size: 12px; -fx-padding: 5px 10px; -fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 3px;");
				
				btnEdit.setOnAction(e -> {
					bean.UserModel user = getTableView().getItems().get(getIndex());
					showEditUserDialog(user);
				});
				btnDelete.setOnAction(e -> {
					bean.UserModel user = getTableView().getItems().get(getIndex());
					getTableView().getItems().remove(user);
					deleteUser(user);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : pane);
			}
		};
	}

	private void deleteUser(bean.UserModel user) {
		singleton.DuLieu.getInstance().getUsers().remove(user);
		singleton.DuLieu.getInstance().saveUserToFile("users.json");
	}

	private void showAddUserDialog() {
		Dialog<bean.UserModel> dialog = createUserDialog("Thêm Người Dùng Mới", null);
		dialog.showAndWait().ifPresent(newUser -> {
			singleton.DuLieu.getInstance().getUsers().add(newUser);
			singleton.DuLieu.getInstance().saveUserToFile("users.json");
			showUsersTableInContent();
		});
	}

	private void showEditUserDialog(bean.UserModel user) {
		Dialog<bean.UserModel> dialog = createUserDialog("Sửa Người Dùng", user);
		dialog.showAndWait().ifPresent(updatedUser -> {
			singleton.DuLieu.getInstance().saveUserToFile("users.json");
			showUsersTableInContent();
		});
	}

	private Dialog<bean.UserModel> createUserDialog(String title, bean.UserModel existingUser) {
		Dialog<bean.UserModel> dialog = new Dialog<>();
		dialog.setTitle(title);

		// Create form fields
		TextField userNameField = new TextField(existingUser != null ? existingUser.getUserName() : "");
		TextField passwordField = new TextField(existingUser != null ? existingUser.getPassword() : "");
		passwordField.setPromptText("Nhập mật khẩu mới nếu muốn thay đổi");
		TextField emailField = new TextField(existingUser != null ? existingUser.getEmail() : "");
		TextField fullNameField = new TextField(existingUser != null ? existingUser.getFullName() : "");

		// Create grid layout
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.add(new Label("Tên đăng nhập:"), 0, 0);
		grid.add(userNameField, 1, 0);
		grid.add(new Label("Mật khẩu:"), 0, 1);
		grid.add(passwordField, 1, 1);
		grid.add(new Label("Email:"), 0, 2);
		grid.add(emailField, 1, 2);
		grid.add(new Label("Họ và tên:"), 0, 3);
		grid.add(fullNameField, 1, 3);

		dialog.getDialogPane().setContent(grid);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// Set result converter
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.OK) {
				return createUserFromForm(existingUser, userNameField, passwordField, emailField, fullNameField);
			}
			return null;
		});

		return dialog;
	}

	private bean.UserModel createUserFromForm(bean.UserModel existingUser, TextField userNameField, 
		TextField passwordField, TextField emailField, TextField fullNameField) {
		if (existingUser != null) {
			// Update existing user
			existingUser.setUserName(userNameField.getText());
			// Only update password if new one is provided
			if (!passwordField.getText().isEmpty()) {
				existingUser.setPassword(passwordField.getText());
			}
			existingUser.setEmail(emailField.getText());
			existingUser.setFullName(fullNameField.getText());
			return existingUser;
		} else {
			// Create new user
			bean.UserModel newUser = new bean.UserModel();
			newUser.setUserId(java.util.UUID.randomUUID().toString());
			newUser.setUserName(userNameField.getText());
			newUser.setPassword(passwordField.getText());
			newUser.setEmail(emailField.getText());
			newUser.setFullName(fullNameField.getText());
			newUser.setCreateAt(java.time.LocalDateTime.now());
			return newUser;
		}
	}

	private void showRoleManagement() {
		try {
			javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxmlManager/RoleManagement.fxml"));
			javafx.scene.Parent roleManagementRoot = loader.load();
			
			if (contentPane != null) {
				contentPane.getChildren().setAll(roleManagementRoot);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Fallback to placeholder
			VBox placeholder = new VBox(10);
			placeholder.setAlignment(javafx.geometry.Pos.CENTER);
			placeholder.setStyle("-fx-padding: 40px; -fx-background-color: #f8f9fa;");
			
			Label titleLabel = new Label("Quản Lý Phân Quyền");
			titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
			
			Label descLabel = new Label("Giao diện quản lý phân quyền đang được phát triển");
			descLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #6c757d;");
			
			placeholder.getChildren().addAll(titleLabel, descLabel);
			contentPane.getChildren().setAll(placeholder);
		}
	}

	private VBox createStatsCard(String title, String value, String color) {
		VBox card = new VBox(10);
		card.setAlignment(javafx.geometry.Pos.CENTER);
		card.setStyle("-fx-padding: 30px; -fx-background-color: " + color + "; -fx-background-radius: 10px; -fx-min-width: 150px; -fx-min-height: 120px;");

		Label valueLabel = new Label(value);
		valueLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: white;");

		Label titleLabel = new Label(title);
		titleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-text-alignment: center;");

		card.getChildren().addAll(valueLabel, titleLabel);
		return card;
	}



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
		TextField priceField = new TextField(
				existingYard != null && existingYard.getYardPrice() != null ? existingYard.getYardPrice().toString()
						: "");
		TextField addressField = new TextField(existingYard != null ? existingYard.getYardAddress() : "");
		TextField thumbnailField = new TextField(existingYard != null ? existingYard.getYardThumbnail() : "");

		// Create image preview and buttons
		ImageView imgPreview = createImagePreview();
		Button btnChooseImg = createChooseImageButton(imgPreview, thumbnailField);

		// Create images list
		List<String> yardImages = new ArrayList<>(
				existingYard != null && existingYard.getYardImages() != null ? existingYard.getYardImages()
						: new ArrayList<>());
		HBox imagesPreview = createImagesPreview(yardImages);
		Button btnAddImages = createAddImagesButton(yardImages, imagesPreview);

		// Create grid layout
		GridPane grid = createYardFormGrid(nameField, priceField, addressField, thumbnailField, btnChooseImg,
				imgPreview, btnAddImages, imagesPreview);

		dialog.getDialogPane().setContent(grid);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// Set result converter
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.OK) {
				return createYardFromForm(existingYard, nameField, priceField, addressField, thumbnailField,
						yardImages);
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
			if (savedPath != null)
				thumbnailField.setText(savedPath);
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
			} catch (Exception ex) {
			}
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
		fileChooser.getExtensionFilters()
				.addAll(new javafx.stage.FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg", "*.gif"));
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
			java.nio.file.Path dest = java.nio.file.Paths.get("src/images",
					System.currentTimeMillis() + "_" + file.getName());
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
		} catch (Exception ex) {
		}
	}

	private GridPane createYardFormGrid(TextField nameField, TextField priceField, TextField addressField,
			TextField thumbnailField, Button btnChooseImg, ImageView imgPreview, Button btnAddImages,
			HBox imagesPreview) {
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
			try {
				existingYard.setYardPrice(Double.parseDouble(priceField.getText()));
			} catch (Exception ex) {
			}
			existingYard.setYardAddress(addressField.getText());
			existingYard.setYardThumbnail(thumbnailField.getText());
			existingYard.setYardImages(yardImages);
			return existingYard;
		} else {
			// Create new yard
			YardModel newYard = new YardModel();
			newYard.setYardId(java.util.UUID.randomUUID().toString());
			newYard.setYardName(nameField.getText());
			try {
				newYard.setYardPrice(Double.parseDouble(priceField.getText()));
			} catch (Exception ex) {
			}
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
		DuLieu.getInstance().loadUsersFromFile("users.json");
		DuLieu.getInstance().loadRolesFromFile("roles.json");
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