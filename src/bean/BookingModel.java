package bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import bean.DepositModel;
import bean.PaymentRecord;
import bean.PaymentMethod;

public class BookingModel implements Bean {

	private String bookingId;
	private String bookerName;
	private String bookerPhone;
	private List<String> serviceIds;
	private LocalDateTime bookingTime;
	private List<BookingYardModel> bookingYards;

	private double totalAmount;
	private List<PaymentRecord> payments;
	

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	// Constructors
	public BookingModel() {
	}

	public BookingModel(String bookingId, String bookerName, String bookerPhone,
						LocalDateTime bookingTime, double totalAmount, List<String> serviceIds, List<PaymentRecord> payments) {
		this.bookingId = bookingId;
		this.bookerName = bookerName;
		this.bookerPhone = bookerPhone;
		this.bookingTime = bookingTime;
		this.totalAmount = totalAmount;
		this.serviceIds = serviceIds;
		this.bookingYards = new ArrayList<>();
		this.payments = payments;
	}

	// Getters & Setters
	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getBookerName() {
		return bookerName;
	}

	public void setBookerName(String bookerName) {
		this.bookerName = bookerName;
	}

	public String getBookerPhone() {
		return bookerPhone;
	}

	public void setBookerPhone(String bookerPhone) {
		this.bookerPhone = bookerPhone;
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

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public List<PaymentRecord> getPayments() { return payments; }
	public void setPayments(List<PaymentRecord> payments) { this.payments = payments; }

	// Thêm method để tính PaymentStatus từ payments
	public PaymentStatus getCalculatedPaymentStatus() {
		if (payments == null || payments.isEmpty()) {
			return PaymentStatus.UNPAID;
		}
		
		double totalPaid = payments.stream()
			.mapToDouble(PaymentRecord::getAmount)
			.sum();
			
		if (totalPaid >= totalAmount) {
			return PaymentStatus.PAID;
		} else if (totalPaid > 0) {
			return PaymentStatus.DEPOSITED;
		} else {
			return PaymentStatus.UNPAID;
		}
	}

	// Thêm method để lấy thời gian thanh toán cuối cùng
	public LocalDateTime getLastPaymentTime() {
		if (payments == null || payments.isEmpty()) {
			return null;
		}
		return payments.stream()
			.map(PaymentRecord::getTime)
			.filter(time -> time != null)
			.max(LocalDateTime::compareTo)
			.orElse(null);
	}

	// JSON convert
	@Override
	public JsonObject toJsonObject() {
		JsonObject json = new JsonObject();

		json.addProperty("bookingId", bookingId);
		json.addProperty("bookerName", bookerName);
		json.addProperty("bookerPhone", bookerPhone);
		json.addProperty("bookingTime", bookingTime != null ? bookingTime.format(formatter) : "");

		json.addProperty("totalAmount", totalAmount);
		
		// Danh sách dịch vụ
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
		
		// Danh sách các khoản thanh toán (payments)
		JsonArray paymentArray = new JsonArray();
		if (payments != null) {
			for (PaymentRecord p : payments) {
				paymentArray.add(p.toJsonObject());
			}
		}
		json.add("payments", paymentArray);
		
		return json;
	}

	// JSON parse
	@Override
	public void parse(JsonObject obj) throws Exception {
		this.bookingId = obj.get("bookingId").getAsString();
		this.bookerName = obj.get("bookerName").getAsString();
		this.bookerPhone = obj.get("bookerPhone").getAsString();
		
		if (obj.has("bookingTime") && !obj.get("bookingTime").getAsString().isEmpty()) {
			this.bookingTime = LocalDateTime.parse(obj.get("bookingTime").getAsString(), formatter);
		}
		
		this.totalAmount = obj.get("totalAmount").getAsDouble();
		
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
		
		// Parse danh sách các khoản thanh toán (payments)
		this.payments = new ArrayList<>();
		if (obj.has("payments") && obj.get("payments").isJsonArray()) {
			JsonArray paymentArr = obj.getAsJsonArray("payments");
			for (int i = 0; i < paymentArr.size(); i++) {
				PaymentRecord p = new PaymentRecord();
				p.parse(paymentArr.get(i).getAsJsonObject());
				payments.add(p);
			}
		}
		
		// Backward compatibility: nếu có deposits cũ, chuyển đổi sang payments
		if (obj.has("deposits") && obj.get("deposits").isJsonArray()) {
			JsonArray depositArr = obj.getAsJsonArray("deposits");
			for (int i = 0; i < depositArr.size(); i++) {
				JsonObject depositObj = depositArr.get(i).getAsJsonObject();
				PaymentRecord p = new PaymentRecord();
				p.setAmount(depositObj.get("amount").getAsDouble());
				p.setMethod(PaymentMethod.valueOf(depositObj.get("method").getAsString()));
				p.setStatus(PaymentStatus.PAID); // Mặc định là đã thanh toán
				if (depositObj.has("time") && !depositObj.get("time").getAsString().isEmpty()) {
					p.setTime(LocalDateTime.parse(depositObj.get("time").getAsString(), formatter));
				}
				p.setNote(depositObj.has("note") ? depositObj.get("note").getAsString() : null);
				payments.add(p);
			}
		}
	}
}
