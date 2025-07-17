package bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class BookingModel implements Bean {

	private String bookingId;
	private String customerName;
	private String customerPhone;
	private List<String> serviceIds;
	private LocalDateTime bookingTime;
	private List<BookingYardModel> bookingYards;

	private double depositAmount;
	private double totalAmount;
	private PaymentStatus paymentStatus;
	private LocalDateTime paymentTime;

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	// Constructors
	public BookingModel() {
	}

	public BookingModel(String bookingId, String customerName, String customerPhone,
						LocalDateTime bookingTime, double depositAmount, double totalAmount,
						PaymentStatus paymentStatus, LocalDateTime paymentTime, List<String> serviceIds) {
		this.bookingId = bookingId;
		this.customerName = customerName;
		this.customerPhone = customerPhone;
		this.bookingTime = bookingTime;
		this.depositAmount = depositAmount;
		this.totalAmount = totalAmount;
		this.paymentStatus = paymentStatus;
		this.paymentTime = paymentTime;
		this.serviceIds = serviceIds;
		this.bookingYards = new ArrayList<>();
	}

	// Getters & Setters
	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public List<String> getServiceIds() {
		return serviceIds;
	}

	public void setServiceIds(List<String> serviceIds) {
		this.serviceIds = serviceIds;
	}

	public LocalDateTime getBookingTime() {
		return bookingTime;
	}

	public void setBookingTime(LocalDateTime bookingTime) {
		this.bookingTime = bookingTime;
	}

	public List<BookingYardModel> getBookingYards() {
		return bookingYards;
	}

	public void setBookingYards(List<BookingYardModel> bookingYards) {
		this.bookingYards = bookingYards;
	}

	public double getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(double depositAmount) {
		this.depositAmount = depositAmount;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public LocalDateTime getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(LocalDateTime paymentTime) {
		this.paymentTime = paymentTime;
	}

	// JSON convert
	@Override
	public JsonObject toJsonObject() {
		JsonObject json = new JsonObject();

		json.addProperty("bookingId", bookingId);
		json.addProperty("customerName", customerName);
		json.addProperty("customerPhone", customerPhone);
		json.addProperty("bookingTime", bookingTime != null ? bookingTime.format(formatter) : "");

		json.addProperty("depositAmount", depositAmount);
		json.addProperty("totalAmount", totalAmount);
		json.addProperty("paymentStatus", paymentStatus != null ? paymentStatus.name() : "");
		json.addProperty("paymentTime", paymentTime != null ? paymentTime.format(formatter) : "");

		// Danh sách mã dịch vụ
		JsonArray serviceIdArray = new JsonArray();
		if (serviceIds != null) {
			for (String id : serviceIds) {
				serviceIdArray.add(new JsonPrimitive(id));
			}
		}
		json.add("serviceIds", serviceIdArray);

		// Danh sách sân đặt và các khung giờ tương ứng
		JsonArray bookingYardsArray = new JsonArray();
		if (bookingYards != null) {
			for (BookingYardModel by : bookingYards) {
				JsonObject obj = new JsonObject();
				obj.addProperty("yardId", by.getYardId());

				JsonArray slotArr = new JsonArray();
				for (TimeSlot slot : by.getSlots()) {
					slotArr.add(slot.toJsonObject());
				}
				obj.add("slots", slotArr);
				bookingYardsArray.add(obj);
			}
		}
		json.add("bookingYards", bookingYardsArray);

		return json;
	}

	// JSON parse
	@Override
	public void parse(JsonObject obj) throws Exception {
		this.bookingId = obj.get("bookingId").getAsString();
		this.customerName = obj.get("customerName").getAsString();
		this.customerPhone = obj.get("customerPhone").getAsString();

		if (obj.has("bookingTime") && !obj.get("bookingTime").getAsString().isEmpty()) {
			this.bookingTime = LocalDateTime.parse(obj.get("bookingTime").getAsString(), formatter);
		}

		this.depositAmount = obj.get("depositAmount").getAsDouble();
		this.totalAmount = obj.get("totalAmount").getAsDouble();

		if (obj.has("paymentStatus")) {
			try {
				this.paymentStatus = PaymentStatus.valueOf(obj.get("paymentStatus").getAsString());
			} catch (Exception e) {
				this.paymentStatus = null;
			}
		}

		if (obj.has("paymentTime") && !obj.get("paymentTime").getAsString().isEmpty()) {
			this.paymentTime = LocalDateTime.parse(obj.get("paymentTime").getAsString(), formatter);
		}

		// Parse danh sách mã dịch vụ
		this.serviceIds = new ArrayList<>();
		if (obj.has("serviceIds") && obj.get("serviceIds").isJsonArray()) {
			JsonArray serviceArray = obj.getAsJsonArray("serviceIds");
			for (int i = 0; i < serviceArray.size(); i++) {
				serviceIds.add(serviceArray.get(i).getAsString());
			}
		}

		// Parse danh sách sân và khung giờ đặt
		this.bookingYards = new ArrayList<>();
		if (obj.has("bookingYards") && obj.get("bookingYards").isJsonArray()) {
			JsonArray array = obj.getAsJsonArray("bookingYards");
			for (int i = 0; i < array.size(); i++) {
				JsonObject byObj = array.get(i).getAsJsonObject();
				String yardId = byObj.get("yardId").getAsString();

				List<TimeSlot> slots = new ArrayList<>();
				JsonArray slotArr = byObj.getAsJsonArray("slots");
				for (int j = 0; j < slotArr.size(); j++) {
					TimeSlot slot = new TimeSlot();
					slot.parse(slotArr.get(j).getAsJsonObject());
					slots.add(slot);
				}

				bookingYards.add(new BookingYardModel(yardId, slots));
			}
		}
	}
}
