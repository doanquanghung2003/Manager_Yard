package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.util.UUID;

import bean.TimeSlot;
import bean.YardModel;
import bean.BookingModel;
import bean.BookingYardModel;
import singleton.DuLieu;

public class ManagerYardController implements Initializable {
	@FXML
	private Label headerLabel;
	@FXML
	private DatePicker datePicker;
	@FXML
	private GridPane bookingGrid;
	@FXML
	private Label noteLabel;
	@FXML
	private Slider zoomSlider;
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private Button btn_maintenance, btn_event;
	@FXML private Button btn_booking, btn_viewBookings;
	private final Map<TimeSlot, YardModel> slotToYardMap = new HashMap<>();
		private final List<TimeSlot> selectedSlots = new ArrayList<>();
	
	// Thêm biến để lưu slot đầu tiên được chọn
	private TimeSlot firstSelectedSlot = null;
	private YardModel firstSelectedYard = null;
	

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

	private static final double MIN_CELL_WIDTH = 60, MAX_CELL_WIDTH = 100;
	private static final double MIN_CELL_HEIGHT = 40, MAX_CELL_HEIGHT = 60;
	private static final double MIN_FONT = 12, MAX_FONT = 18;
	private static final double HEADER_HEIGHT = 50;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		DuLieu.getInstance().loadBookingsFromFile("data/bookings.json");
		DuLieu.getInstance().loadYardsFromFile("data/yards.json");
		
		// Initialize UI components
		if (datePicker != null) {
			datePicker.setValue(LocalDate.now());
			datePicker.valueProperty()
					.addListener((obs, oldDate, newDate) -> drawHourView(zoomSlider != null ? zoomSlider.getValue() : 2.0));
		}
	
		if (scrollPane != null) {
			scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
			scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
			scrollPane.setFitToWidth(false);
			scrollPane.setFitToHeight(false);
			scrollPane.setPannable(true);
		}
		
		if (zoomSlider != null) {
			zoomSlider.setMin(1.0);
			zoomSlider.setMax(2.0);
			zoomSlider.setValue(2.0);
			zoomSlider.setMajorTickUnit(0.1);
			zoomSlider.setSnapToTicks(false);
			zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> drawHourView(newVal.doubleValue()));
		}
		
		// Configure bookingGrid
		if (bookingGrid != null) {
			bookingGrid.setGridLinesVisible(true);
			bookingGrid.setHgap(1);
			bookingGrid.setVgap(1);
			bookingGrid.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-width: 1px;");
		}
		
		// Set up button event handlers
		if (btn_maintenance != null) {
			btn_maintenance.setOnAction(e -> handleMaintenanceSlots());
		}
		if (btn_event != null) {
			btn_event.setOnAction(e -> {
				if (selectedSlots.isEmpty()) {
					showAlert("Bạn chưa chọn ô nào để gán sự kiện.");
					return;
				}
				// Kiểm tra các slot đã là sự kiện chưa
				boolean allAreEvent = true;
				for (TimeSlot slot : selectedSlots) {
					YardModel yard = slotToYardMap.get(slot);
					boolean isEvent = false;
					for (TimeSlot ts : yard.getEventSlots()) {
						if (ts.getStartTime().equals(slot.getStartTime()) && ts.getEndTime().equals(slot.getEndTime())) {
							isEvent = true;
							break;
						}
					}
					if (!isEvent) {
						allAreEvent = false;
						break;
					}
				}
				if (!allAreEvent) {
					// Nếu có slot chưa là sự kiện, hiện form nhập khách hàng và tạo booking
					showEventBookingFormForMultipleSlots();
				} else {
					// Nếu tất cả đã là sự kiện, giữ nguyên logic cũ
					int count = handleChooseEvent(new HashSet<>(selectedSlots), slotToYardMap);
					showAlert("Đã gán sự kiện cho " + count + " khung giờ.");
					selectedSlots.clear();
					slotToYardMap.clear();
					drawHourView(zoomSlider != null ? zoomSlider.getValue() : 2.0);
				}
			});
		}
		
		// Draw the initial view
		drawHourView(zoomSlider != null ? zoomSlider.getValue() : 2.0);
	}

	private void drawHourView(double scale) {
		
		
		if (bookingGrid == null) {
			System.out.println("ERROR: bookingGrid is null!");
			return;
		}
		
		bookingGrid.getChildren().clear();
		// selectedSlots.clear();
		
		// Đảm bảo bookingGrid được đặt trong ScrollPane
		if (scrollPane != null && scrollPane.getContent() != bookingGrid) {
			scrollPane.setContent(bookingGrid);
			System.out.println("DEBUG: Set bookingGrid as content of scrollPane");
		}
		
		// Debug: Kiểm tra scrollPane
		if (scrollPane != null) {
		
			System.out.println("DEBUG: scrollPane content: " + scrollPane.getContent());
			System.out.println("DEBUG: scrollPane visible: " + scrollPane.isVisible());
			System.out.println("DEBUG: scrollPane managed: " + scrollPane.isManaged());
		} else {
			System.out.println("ERROR: scrollPane is null!");
		}
		
		String[] currentTimeSlots = getCurrentTimeSlots();
	
		
		LocalDate selectedDate = datePicker.getValue();
		if (selectedDate == null) {
			
			return;
		}
		System.out.println("DEBUG: Selected date: " + selectedDate);
		double cellWidth = MIN_CELL_WIDTH + (MAX_CELL_WIDTH - MIN_CELL_WIDTH) * (scale - 1.0);
		double cellHeight = MIN_CELL_HEIGHT + (MAX_CELL_HEIGHT - MIN_CELL_HEIGHT) * (scale - 1.0);
		double fontSize = MIN_FONT + (MAX_FONT - MIN_FONT) * (scale - 1.0);
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
		System.out.println("DEBUG: Creating time header labels...");
		for (int i = 0; i < currentTimeSlots.length; i++) {
			Label timeLabel = new Label(currentTimeSlots[i]);
			timeLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-font-size: " + fontSize
					+ "px; -fx-background-color: #2196F3; -fx-text-fill: white; -fx-border-color: #1976D2; -fx-border-width: 1px;");
			timeLabel.setPrefSize(cellWidth, HEADER_HEIGHT);
			timeLabel.setMinSize(cellWidth, HEADER_HEIGHT);
			timeLabel.setAlignment(javafx.geometry.Pos.CENTER);
			bookingGrid.add(timeLabel, i + 1, 0);
			System.out.println("DEBUG: Added time label: " + currentTimeSlots[i] + " at position (" + (i + 1) + ", 0)");
		}
		List<YardModel> yards = DuLieu.getInstance().getYards();
		if (yards == null) {
			System.out.println("ERROR: yards list is null!");
			return;
		}
		System.out.println("DEBUG: Yards count: " + yards.size());
		System.out.println("DEBUG: Creating yard labels...");
		for (int i = 0; i < yards.size(); i++) {
			YardModel yard = yards.get(i);
			if (yard == null) {
				System.out.println("DEBUG: Yard at index " + i + " is null, skipping...");
				continue;
			}
			String yardName = yard.getYardName() != null ? yard.getYardName() : "Không tên sân";
			Label courtLabel = new Label(yardName);
			courtLabel.setStyle(
					"-fx-font-weight: bold; -fx-alignment: center; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-color: #388E3C; -fx-border-width: 1px; -fx-font-size: " + fontSize + "px;");
			courtLabel.setPrefSize(120, cellHeight);
			courtLabel.setMinSize(120, cellHeight);
			bookingGrid.add(courtLabel, 0, i + 1);
			System.out.println("DEBUG: Added yard label: " + yardName + " at position (0, " + (i + 1) + ")");

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
					if (!bym.getYardId().equals(yard.getYardId()))
						continue;
					for (TimeSlot bookingRange : bym.getSlots()) {
						if (!bookingRange.getStartTime().toLocalDate().equals(selectedDate))
							continue;

						int startIdx = -1, endIdx = -1;
						for (int j = 0; j < currentTimeSlots.length; j++) {
							LocalTime slotTime = LocalTime.parse(currentTimeSlots[j], timeFormatter);
							LocalDateTime slotStart = LocalDateTime.of(selectedDate, slotTime);

							long diffStart = Math
									.abs(Duration.between(slotStart, bookingRange.getStartTime()).toMinutes());
							long diffEnd = Math.abs(Duration.between(slotStart, bookingRange.getEndTime()).toMinutes());

							if (diffStart < 1)
								startIdx = j;
							if (diffEnd < 1)
								endIdx = j;
						}

						// Nếu không tìm thấy endIdx, tính toán dựa trên thời gian
						if (startIdx != -1 && endIdx == -1) {
							long minutes = Duration.between(bookingRange.getStartTime(), bookingRange.getEndTime())
									.toMinutes();
							int slotsNeeded = (int) Math.ceil((double) minutes / 30.0);
							endIdx = startIdx + slotsNeeded - 1;
						}

						if (startIdx == -1)
							continue;

						int span = (endIdx == -1 ? 1 : endIdx - startIdx + 1);
						String timeLabel = bookingRange.getStartTime().toLocalTime()
								.format(DateTimeFormatter.ofPattern("HH:mm")) + " - "
								+ bookingRange.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

						boolean isPastBooking = bookingRange.getEndTime().isBefore(LocalDateTime.now());

						Button btn = new Button(timeLabel);
						btn.setPrefSize(cellWidth * span, cellHeight);

						String style = "-fx-background-color: linear-gradient(#FF0000, #CC0000); -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #900;";
						btn.setStyle(style);
						btn.setDisable(isPastBooking);

						String tooltipText = "Khách: " + booking.getBookerName() + "\nSĐT: " + booking.getBookerPhone()
								+ "\nGiờ: " + timeLabel;
						if (isPastBooking) {
							tooltipText += "\n(Đã hoàn thành)";
						}
						Tooltip.install(btn, new Tooltip(tooltipText));

						if (!isPastBooking) {
							btn.setOnAction(e -> showBookingOptionsDialog(booking, bym, bookingRange));
						}

						bookingGrid.add(btn, startIdx + 1, i + 1, span, 1);
						for (int k = startIdx; k < startIdx + span; k++)
							slotDrawn[k] = true;
					}
				}
			}

			// 3. Vẽ các slot còn lại (bảo trì, sự kiện, slot trống)
			for (int j = 0; j < currentTimeSlots.length; j++) {
				if (slotDrawn[j])
					continue;
				LocalTime slotTime;
				try {
					slotTime = LocalTime.parse(currentTimeSlots[j], timeFormatter);
				} catch (Exception ex) {
					continue;
				}
				LocalDateTime slotStart = LocalDateTime.of(selectedDate, slotTime);
				LocalDateTime slotEnd = slotStart;
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

					// Tính toán span cho maintenance/event slot
					int startIdx = -1, endIdx = -1;
					for (int k = 0; k < currentTimeSlots.length; k++) {
						LocalTime checkSlotTime = LocalTime.parse(currentTimeSlots[k], timeFormatter);
						LocalDateTime checkSlotStart = LocalDateTime.of(selectedDate, checkSlotTime);
						
						long diffStart = Math.abs(Duration.between(checkSlotStart, finalSlot.getStartTime()).toMinutes());
						long diffEnd = Math.abs(Duration.between(checkSlotStart, finalSlot.getEndTime()).toMinutes());
						
						if (diffStart < 1)
							startIdx = k;
						if (diffEnd < 1)
							endIdx = k;
					}
					// Nếu không tìm thấy endIdx, tính toán dựa trên thời gian
					if (startIdx != -1 && endIdx == -1) {
						long minutes = Duration.between(finalSlot.getStartTime(), finalSlot.getEndTime())
								.toMinutes();
						int slotsNeeded = (int) Math.ceil((double) minutes / 30.0);
						endIdx = startIdx + slotsNeeded - 1;
					}

					int span = (startIdx == -1 || endIdx == -1) ? 1 : endIdx - startIdx + 1;
					String labelText = isMaintenance ? "Bảo trì" : "Sự kiện";
					String style = isMaintenance
							? "-fx-background-color: linear-gradient(#FFA500, #FF8C00); -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #CC6600;"
							: "-fx-background-color: #9932CC; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: black;";
					Button btn = new Button(labelText);
					btn.setPrefSize(cellWidth * span, cellHeight);
					btn.setStyle(style);
					// Kiểm tra đã qua thời gian chưa
					boolean isPastEvent = finalSlot.getEndTime().isBefore(LocalDateTime.now());
					btn.setDisable(isPastEvent);
					// Tooltip
					String tooltipText = (isMaintenance ? "Bảo trì" : "Sự kiện") + "\nGiờ: "
							+ finalSlot.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
							+ " - " + finalSlot.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
					if (isPastEvent)
						tooltipText += "\n(Đã hoàn thành)";
					Tooltip.install(btn, new Tooltip(tooltipText));
					// Khi click: nếu là sự kiện và chưa qua thời gian thì hiện form nhập khách hàng
					// và tạo booking
					if (!isMaintenance && !isPastEvent) {
						btn.setOnAction(e -> showEventBookingForm(yard, finalSlot));
					} else if (!isPastEvent) {
						btn.setOnAction(e -> showMaintenanceOrEventOptionsDialog(yard, finalSlot, isMaintenance));
					}
					if (startIdx != -1) {
						bookingGrid.add(btn, startIdx + 1, i + 1, span, 1);
						// Đánh dấu các slot đã được vẽ
						for (int k = startIdx; k < startIdx + span && k < currentTimeSlots.length; k++) {
							slotDrawn[k] = true;
						}
					} else {
						// Fallback: vẽ ở vị trí hiện tại nếu không tìm thấy startIdx
						bookingGrid.add(btn, j + 1, i + 1, 1, 1);
						slotDrawn[j] = true;
					}
					continue;
				}
				// Kiểm tra xem slot này có nằm trong selectedSlots không
				TimeSlot tempSlot = new TimeSlot(slotStart, slotEnd);
				boolean isSelected = false;

				for (TimeSlot selectedSlot : selectedSlots) {
					if (slotToYardMap.containsKey(selectedSlot)
							&& slotToYardMap.get(selectedSlot).getYardId().equals(yard.getYardId())) {
						// Kiểm tra xem slot hiện tại có trùng với selectedSlot không
						if (slotStart.equals(selectedSlot.getStartTime())
								&& slotEnd.equals(selectedSlot.getEndTime())) {
							isSelected = true;
							break;
						}
					}
				}

				// Nếu slot này đã được chọn, bỏ qua việc vẽ slot trống
				if (isSelected) {
					continue;
				}

				// Slot trống
				Button btn = new Button(" ");
				btn.setPrefSize(cellWidth, cellHeight);
				btn.setMinSize(cellWidth, cellHeight);
				btn.setStyle((isPast ? "-fx-background-color: #ccc;" : "-fx-background-color: white;")
						+ " -fx-border-color: #bbb; -fx-font-size: " + fontSize + "px; -fx-border-width: 1px;");
				btn.setDisable(isPast);
				System.out.println("DEBUG: Created empty slot button at position (" + (j + 1) + ", " + (i + 1) + ")");
				final LocalDateTime finalSlotStart = slotStart;
				final LocalDateTime finalSlotEnd = slotEnd;
				if (!isPast) {
					btn.setOnAction(e -> {
						TimeSlot temp = new TimeSlot(finalSlotStart, finalSlotEnd);
						if (selectedSlots.contains(temp)) {
							// Bỏ chọn slot này
							selectedSlots.remove(temp);
							slotToYardMap.remove(temp);
							btn.setStyle("-fx-background-color: white; -fx-border-color: #bbb; -fx-font-size: "
									+ fontSize + "px;");
							
						                        } else {
                            // Chọn slot này
                            if (firstSelectedSlot == null) {
                                // Đây là slot đầu tiên được chọn
                                firstSelectedSlot = temp;
                                firstSelectedYard = yard;
                                selectedSlots.add(temp);
                                slotToYardMap.put(temp, yard);
                                System.out.println(
                                        "Chọn slot đầu tiên: " + temp.getStartTime() + " - " + temp.getEndTime());
                                btn.setStyle(
                                        "-fx-background-color: #00cc66; -fx-border-color: black; -fx-font-weight: bold; -fx-font-size: "
                                                + fontSize + "px;");
                            } else {
                                System.out.println(
                                        "Chọn slot thứ hai: " + temp.getStartTime() + " - " + temp.getEndTime());
                                System.out.println("Slot đầu tiên: " + firstSelectedSlot.getStartTime() + " - "
                                        + firstSelectedSlot.getEndTime());
                                System.out
                                        .println("Cùng sân: " + firstSelectedYard.getYardId().equals(yard.getYardId()));
                                System.out.println("Cùng ngày: " + firstSelectedSlot.getStartTime().toLocalDate()
                                        .equals(temp.getStartTime().toLocalDate()));

                                // Đây là slot thứ hai, kiểm tra xem có cùng sân và cùng ngày không
                                if (firstSelectedYard.getYardId().equals(yard.getYardId()) && firstSelectedSlot
                                        .getStartTime().toLocalDate().equals(temp.getStartTime().toLocalDate())) {
                                    // Cùng sân và cùng ngày, chọn tất cả slot ở giữa
                                    selectRangeOfSlots(firstSelectedSlot, temp, yard);
                                    // Cập nhật giao diện sau khi chọn range
                                    drawHourView(zoomSlider.getValue());
                                    // Reset firstSelectedSlot sau khi chọn range
                                    firstSelectedSlot = null;
                                    firstSelectedYard = null;
                                } else {
                                    // Khác sân hoặc khác ngày, reset và chọn slot mới
                                    selectedSlots.clear();
                                    slotToYardMap.clear();
                                    firstSelectedSlot = temp;
                                    firstSelectedYard = yard;
                                    selectedSlots.add(temp);
                                    slotToYardMap.put(temp, yard);
                                    btn.setStyle(
                                            "-fx-background-color: #00cc66; -fx-border-color: black; -fx-font-weight: bold; -fx-font-size: "
                                                    + fontSize + "px;");
                                }
                            }
                        }
					});
				}
				bookingGrid.add(btn, j + 1, i + 1);
				System.out.println("DEBUG: Added empty slot button to grid at (" + (j + 1) + ", " + (i + 1) + ")");
			}

			// Vẽ các slot gộp đã chọn
			for (TimeSlot selectedSlot : selectedSlots) {
				if (slotToYardMap.containsKey(selectedSlot)
						&& slotToYardMap.get(selectedSlot).getYardId().equals(yard.getYardId())) {

					// Tính toán span cho slot gộp
					int startIdx = -1, endIdx = -1;
					for (int k = 0; k < currentTimeSlots.length; k++) {
						LocalTime checkSlotTime = LocalTime.parse(currentTimeSlots[k], timeFormatter);
						LocalDateTime checkSlotStart = LocalDateTime.of(selectedDate, checkSlotTime);
						
						long diffStart = Math.abs(Duration.between(checkSlotStart, selectedSlot.getStartTime()).toMinutes());
						long diffEnd = Math.abs(Duration.between(checkSlotStart, selectedSlot.getEndTime()).toMinutes());
						
						if (diffStart < 1)
							startIdx = k;
						if (diffEnd < 1)
							endIdx = k;
					}
					// Nếu không tìm thấy endIdx, tính toán dựa trên thời gian
					if (startIdx != -1 && endIdx == -1) {
						long minutes = Duration.between(selectedSlot.getStartTime(), selectedSlot.getEndTime())
								.toMinutes();
						int slotsNeeded = (int) Math.ceil((double) minutes / 30.0);
						endIdx = startIdx + slotsNeeded - 1;
					}

					if (startIdx != -1 && endIdx != -1) {
						int span = endIdx - startIdx + 1;
						String timeLabel = selectedSlot.getStartTime().toLocalTime()
								.format(DateTimeFormatter.ofPattern("HH:mm")) + " - "
								+ selectedSlot.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

						Button selectedBtn = new Button(timeLabel);
						selectedBtn.setPrefSize(cellWidth * span, cellHeight);
						selectedBtn.setStyle(
								"-fx-background-color: #00cc66; -fx-border-color: black; -fx-font-weight: bold; -fx-font-size: "
										+ fontSize + "px;");

						// Thêm action để bỏ chọn
						selectedBtn.setOnAction(e -> {
							selectedSlots.remove(selectedSlot);
							slotToYardMap.remove(selectedSlot);
							drawHourView(zoomSlider.getValue());
						});

						bookingGrid.add(selectedBtn, startIdx + 1, i + 1, span, 1);
					}
				}
			}
		}
		
		// Debug output
		if (bookingGrid != null) {
			System.out.println("DEBUG: Total components in bookingGrid: " + bookingGrid.getChildren().size());
			
			// Force bookingGrid to be visible and managed
			bookingGrid.setVisible(true);
			bookingGrid.setManaged(true);
			
			// Ensure bookingGrid is in the scene
			if (bookingGrid.getScene() == null) {
				System.out.println("WARNING: bookingGrid is not in scene, trying to add it...");
				// Try to add bookingGrid directly to the scene if possible
				if (datePicker != null && datePicker.getScene() != null) {
					javafx.scene.Node root = datePicker.getScene().getRoot();
					if (root instanceof AnchorPane) {
						AnchorPane anchorPane = (AnchorPane) root;
						// Find BorderPane and add bookingGrid to center
						for (javafx.scene.Node child : anchorPane.getChildren()) {
							if (child instanceof BorderPane) {
								BorderPane borderPane = (BorderPane) child;
								borderPane.setCenter(bookingGrid);
								System.out.println("DEBUG: Added bookingGrid directly to BorderPane center");
								break;
							}
						}
					}
				}
			} else {
				System.out.println("DEBUG: bookingGrid is in scene");
			}
		} else {
			System.out.println("ERROR: bookingGrid is null at end of drawHourView!");
		}
		System.out.println("=== DEBUG: drawHourView completed ===");
	}

	// Thêm hàm selectRangeOfSlots
	private void selectRangeOfSlots(TimeSlot startSlot, TimeSlot endSlot, YardModel yard) {
		// Xác định slot đầu và slot cuối dựa trên thời gian
		LocalDateTime rangeStart = startSlot.getStartTime().isBefore(endSlot.getStartTime()) ? startSlot.getStartTime()
				: endSlot.getStartTime();
		LocalDateTime rangeEnd = startSlot.getStartTime().isBefore(endSlot.getStartTime()) ? endSlot.getStartTime()
				: startSlot.getStartTime();

		// Kiểm tra cùng ngày
		if (!rangeStart.toLocalDate().equals(rangeEnd.toLocalDate())) {
			showAlert("Không thể chọn các slot thuộc các ngày khác nhau.");
			return;
		}

		// Kiểm tra xem các slot có thuộc lưới thời gian 30 phút
		String[] currentTimeSlots = getCurrentTimeSlots();
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
		int startIdx = -1, endIdx = -1;
		for (int i = 0; i < currentTimeSlots.length; i++) {
			LocalTime slotTime = LocalTime.parse(currentTimeSlots[i], timeFormatter);
			LocalDateTime slotStart = LocalDateTime.of(rangeStart.toLocalDate(), slotTime);
			
			long diffStart = Math.abs(Duration.between(slotStart, rangeStart).toMinutes());
			long diffEnd = Math.abs(Duration.between(slotStart, rangeEnd).toMinutes());
			
			if (diffStart < 1)
				startIdx = i;
			if (diffEnd < 1)
				endIdx = i;
		}

		// Nếu không tìm thấy endIdx, tính toán dựa trên thời gian
		if (startIdx != -1 && endIdx == -1) {
			long minutes = Duration.between(rangeStart, rangeEnd).toMinutes();
			int slotsNeeded = (int) Math.ceil((double) minutes / 30.0);
			endIdx = startIdx + slotsNeeded - 1;
		}

		if (startIdx == -1 || endIdx == -1 || endIdx < startIdx) {
			showAlert("Khoảng slot không hợp lệ hoặc không thuộc lưới thời gian.");
			return;
		}

		// Kiểm tra tính liên tục và tính khả dụng của các slot trong khoảng
		for (int i = startIdx; i <= endIdx; i++) {
			LocalTime slotTime = LocalTime.parse(currentTimeSlots[i], timeFormatter);
			LocalDateTime slotStart = LocalDateTime.of(rangeStart.toLocalDate(), slotTime);
			LocalDateTime slotEnd = slotStart;
			TimeSlot tempSlot = new TimeSlot(slotStart, slotEnd);

			// Kiểm tra xem slot có bị chiếm không
			if (isSlotOccupied(yard, tempSlot)) {
				showAlert("Khoảng slot đã chọn chứa slot đã được đặt, bảo trì hoặc sự kiện.");
				return;
			}
		}

		// Gộp slot và lưu
		selectedSlots.clear();
		slotToYardMap.clear();

		// Tạo slot gộp từ start đến end
		TimeSlot mergedSlot = new TimeSlot(rangeStart, rangeEnd);
		selectedSlots.add(mergedSlot);
		slotToYardMap.put(mergedSlot, yard);

		System.out.println("Đã gộp slot: " + mergedSlot.getStartTime().toLocalTime() + " - "
				+ mergedSlot.getEndTime().toLocalTime());
	}

	// Thêm method kiểm tra slot có bị chiếm không
	private boolean isSlotOccupied(YardModel yard, TimeSlot slot) {
		LocalDate selectedDate = datePicker.getValue();
		if (selectedDate == null)
			return false;

		// Kiểm tra booking
		List<BookingModel> allBookings = DuLieu.getInstance().getBookings();
		for (BookingModel booking : allBookings) {
			for (BookingYardModel bym : booking.getBookingYards()) {
				if (bym.getYardId().equals(yard.getYardId())) {
					for (TimeSlot bookingSlot : bym.getSlots()) {
						if (bookingSlot.getStartTime().toLocalDate().equals(selectedDate)) {
							// Kiểm tra overlap
							if (!(slot.getEndTime().isBefore(bookingSlot.getStartTime())
									|| slot.getStartTime().isAfter(bookingSlot.getEndTime()))) {
								return true;
							}
						}
					}
				}
			}
		}

		// Kiểm tra maintenance slots
		for (TimeSlot maintenanceSlot : yard.getMaintenanceSlots()) {
			if (maintenanceSlot.getStartTime().toLocalDate().equals(selectedDate)) {
				if (!(slot.getEndTime().isBefore(maintenanceSlot.getStartTime())
						|| slot.getStartTime().isAfter(maintenanceSlot.getEndTime()))) {
					return true;
				}
			}
		}

		// Kiểm tra event slots
		for (TimeSlot eventSlot : yard.getEventSlots()) {
			if (eventSlot.getStartTime().toLocalDate().equals(selectedDate)) {
				if (!(slot.getEndTime().isBefore(eventSlot.getStartTime())
						|| slot.getStartTime().isAfter(eventSlot.getEndTime()))) {
					return true;
				}
			}
		}

		return false;
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
				TimeSlot maintenanceSlot = new TimeSlot(slot.getStartTime(), slot.getEndTime());
				yard.getMaintenanceSlots().add(maintenanceSlot);
			}
		}
		DuLieu.getInstance().saveYardToFile("data/yards.json");
		showAlert("Đã chuyển các khung giờ sang trạng thái bảo trì.");
		selectedSlots.clear();
		slotToYardMap.clear();
		drawHourView(zoomSlider.getValue());
	}

	private int handleChooseEvent(Set<TimeSlot> selectedSlots, Map<TimeSlot, YardModel> slotToYardMap) {
		LocalDateTime start = null, end = null;
		YardModel yard = null;
		for (TimeSlot slot : selectedSlots) {
			if (start == null || slot.getStartTime().isBefore(start))
				start = slot.getStartTime();
			if (end == null || slot.getEndTime().isAfter(end))
				end = slot.getEndTime();
			yard = slotToYardMap.get(slot);
		}
		if (start == null || end == null || yard == null)
			return 0;
		TimeSlot eventSlot = new TimeSlot(start, end);
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
		DuLieu.getInstance().saveYardToFile("data/yards.json");
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
		content.append("Khách: ").append(booking.getBookerName()).append("\n");
		content.append("SĐT: ").append(booking.getBookerPhone()).append("\n");
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
		if (!result.isPresent() || result.get() == btnClose)
			return;
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
			DuLieu.getInstance().saveBookingToFile("data/bookings.json");
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
			if (!chooseResult.isPresent() || chooseResult.get() != ButtonType.OK)
				return;
			YardModel newYard = comboBoxYards.getValue();
			if (newYard == null)
				return;
			// Kiểm tra trùng lịch ở sân mới
			boolean conflict = false;
			for (BookingModel b : DuLieu.getInstance().getBookings()) {
				for (BookingYardModel by : b.getBookingYards()) {
					if (!by.getYardId().equals(newYard.getYardId()))
						continue;
					for (TimeSlot ts : by.getSlots()) {
						boolean overlap = !(bookingRange.getEndTime().isBefore(ts.getStartTime())
								|| bookingRange.getStartTime().isAfter(ts.getEndTime()));
						if (overlap) {
							conflict = true;
							break;
						}
					}
					if (conflict)
						break;
				}
				if (conflict)
					break;
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
			DuLieu.getInstance().saveBookingToFile("data/bookings.json");
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
		content.append("Giờ: ").append(slot.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")))
				.append(" - ").append(slot.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
		alert.setContentText(content.toString());
		ButtonType btnChange = new ButtonType("Chuyển sân");
		ButtonType btnCancel = new ButtonType("Huỷ " + (isMaintenance ? "bảo trì" : "sự kiện"));
		ButtonType btnClose = new ButtonType("Đóng", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(btnChange, btnCancel, btnClose);
		Optional<ButtonType> result = alert.showAndWait();
		if (!result.isPresent() || result.get() == btnClose)
			return;
		if (result.get() == btnCancel) {
			if (isMaintenance) {
				yard.getMaintenanceSlots().remove(slot);
			} else {
				yard.getEventSlots().remove(slot);
			}
			DuLieu.getInstance().saveYardToFile("data/yards.json");
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
			if (!chooseResult.isPresent() || chooseResult.get() != ButtonType.OK)
				return;
			YardModel newYard = comboBoxYards.getValue();
			if (newYard == null)
				return;
			// Kiểm tra trùng lịch ở sân mới
			boolean conflict = false;
			List<TimeSlot> targetSlots = isMaintenance ? newYard.getMaintenanceSlots() : newYard.getEventSlots();
			for (TimeSlot ts : targetSlots) {
				boolean overlap = !(slot.getEndTime().isBefore(ts.getStartTime())
						|| slot.getStartTime().isAfter(ts.getEndTime()));
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
			DuLieu.getInstance().saveYardToFile("data/yards.json");
			drawHourView(zoomSlider.getValue());
			showAlert("Chuyển sân thành công!");
		}
	}

	// Thêm hàm showEventBookingForm
	private void showEventBookingForm(YardModel yard, TimeSlot eventSlot) {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Đặt sân sự kiện");
		dialog.setHeaderText("Nhập thông tin khách hàng cho sự kiện");
		Label nameLabel = new Label("Tên khách hàng:");
		TextField nameField = new TextField();
		Label phoneLabel = new Label("SĐT:");
		TextField phoneField = new TextField();
		VBox vbox = new VBox(10, nameLabel, nameField, phoneLabel, phoneField);
		dialog.getDialogPane().setContent(vbox);

		// Thêm các nút: Xác nhận, Hủy đặt, Hủy
		ButtonType btnConfirm = new ButtonType("Xác nhận", ButtonBar.ButtonData.OK_DONE);
		ButtonType btnCancelBooking = new ButtonType("Hủy đặt", ButtonBar.ButtonData.NO);
		ButtonType btnCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().setAll(btnConfirm, btnCancelBooking, btnCancel);

		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent()) {
			if (result.get() == btnConfirm) {
				String name = nameField.getText().trim();
				String phone = phoneField.getText().trim();
				if (name.isEmpty() || phone.isEmpty()) {
					showAlert("Vui lòng nhập đầy đủ thông tin khách hàng!");
					return;
				}
				// Tạo booking mới
				BookingModel booking = new BookingModel();
				booking.setBookingId(UUID.randomUUID().toString());
				booking.setBookerName(name);
				booking.setBookerPhone(phone);
				booking.setBookingTime(LocalDateTime.now());
				BookingYardModel bym = new BookingYardModel(yard.getYardId(), new ArrayList<>());
				bym.getSlots().add(eventSlot);
				booking.setBookingYards(new ArrayList<>());
				booking.getBookingYards().add(bym);
				DuLieu.getInstance().getBookings().add(booking);
				DuLieu.getInstance().saveBookingToFile("data/bookings.json");
				showAlert("Đặt sân sự kiện thành công!");
				drawHourView(zoomSlider.getValue());
			} else if (result.get() == btnCancelBooking) {
				// Hủy đặt - xóa slot này khỏi eventSlots
				yard.getEventSlots().removeIf(ts -> ts.getStartTime().equals(eventSlot.getStartTime())
						&& ts.getEndTime().equals(eventSlot.getEndTime()));
				DuLieu.getInstance().saveYardToFile("data/yards.json");
				showAlert("Đã hủy đặt sự kiện cho slot này.");
				drawHourView(zoomSlider.getValue());
			}
			// Nếu chọn "Hủy" thì không làm gì cả
		}
	}

	// Thêm hàm showEventBookingFormForMultipleSlots
	private void showEventBookingFormForMultipleSlots() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Đặt sân sự kiện");
		dialog.setHeaderText("Nhập thông tin khách hàng cho các slot sự kiện đã chọn");
		Label nameLabel = new Label("Tên khách hàng:");
		TextField nameField = new TextField();
		Label phoneLabel = new Label("SĐT:");
		TextField phoneField = new TextField();
		VBox vbox = new VBox(10, nameLabel, nameField, phoneLabel, phoneField);
		dialog.getDialogPane().setContent(vbox);
		// Thêm các nút: Xác nhận, Hủy đặt, Hủy
		ButtonType btnConfirm = new ButtonType("Xác nhận", ButtonBar.ButtonData.OK_DONE);
		ButtonType btnCancelBooking = new ButtonType("Hủy đặt", ButtonBar.ButtonData.NO);
		ButtonType btnCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().setAll(btnConfirm, btnCancelBooking, btnCancel);
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent()) {
			if (result.get() == btnConfirm) {
				String name = nameField.getText().trim();
				String phone = phoneField.getText().trim();
				if (name.isEmpty() || phone.isEmpty()) {
					showAlert("Vui lòng nhập đầy đủ thông tin khách hàng!");
					return;
				}
				// Tạo booking mới cho từng slot
				for (TimeSlot slot : selectedSlots) {
					YardModel yard = slotToYardMap.get(slot);
					// Gán slot thành sự kiện nếu chưa có
					boolean exists = false;
					for (TimeSlot ts : yard.getEventSlots()) {
						if (ts.getStartTime().equals(slot.getStartTime())
								&& ts.getEndTime().equals(slot.getEndTime())) {
							exists = true;
							break;
						}
					}
					if (!exists) {
						TimeSlot eventSlot = new TimeSlot(slot.getStartTime(), slot.getEndTime());
						yard.getEventSlots().add(eventSlot);
					}
					// Tạo booking
					BookingModel booking = new BookingModel();
					booking.setBookingId(UUID.randomUUID().toString());
					booking.setBookerName(name);
					booking.setBookerPhone(phone);
					booking.setBookingTime(LocalDateTime.now());
					BookingYardModel bym = new BookingYardModel(yard.getYardId(), new ArrayList<>());
					bym.getSlots().add(slot);
					booking.setBookingYards(new ArrayList<>());
					booking.getBookingYards().add(bym);
					DuLieu.getInstance().getBookings().add(booking);
				}
				DuLieu.getInstance().saveYardToFile("data/yards.json");
				DuLieu.getInstance().saveBookingToFile("data/bookings.json");
				showAlert("Đặt sân sự kiện thành công!");
				selectedSlots.clear();
				slotToYardMap.clear();
				drawHourView(zoomSlider.getValue());
			} else if (result.get() == btnCancelBooking) {
				// Hủy đặt - xóa các slot đã chọn khỏi sự kiện
				for (TimeSlot slot : selectedSlots) {
					YardModel yard = slotToYardMap.get(slot);
					// Xóa slot khỏi eventSlots nếu có
					yard.getEventSlots().removeIf(ts -> ts.getStartTime().equals(slot.getStartTime())
							&& ts.getEndTime().equals(slot.getEndTime()));
				}
				DuLieu.getInstance().saveYardToFile("data/yards.json");
				showAlert("Đã hủy đặt sự kiện cho các slot đã chọn.");
				selectedSlots.clear();
				slotToYardMap.clear();
				drawHourView(zoomSlider.getValue());
			}
			// Nếu chọn "Hủy" thì không làm gì cả
		}
	}
}
