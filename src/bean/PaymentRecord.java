package bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import com.google.gson.JsonObject;

public class PaymentRecord {
    private String recordId;
    private double amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime time;
    private String note;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PaymentRecord() {
        this.recordId = UUID.randomUUID().toString();
    }

    public PaymentRecord(double amount, PaymentMethod method, PaymentStatus status, LocalDateTime time, String note) {
        this.recordId = UUID.randomUUID().toString();
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.time = time;
        this.note = note;
    }

    // Getters & Setters
    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    // JSON convert
    public JsonObject toJsonObject() {
        JsonObject obj = new JsonObject();
        obj.addProperty("recordId", recordId);
        obj.addProperty("amount", amount);
        obj.addProperty("method", method != null ? method.name() : "");
        obj.addProperty("status", status != null ? status.name() : "");
        obj.addProperty("time", time != null ? time.format(formatter) : "");
        obj.addProperty("note", note);
        return obj;
    }

    // JSON parse
    public void parse(JsonObject obj) {
        this.recordId = obj.get("recordId").getAsString();
        this.amount = obj.get("amount").getAsDouble();
        
        if (obj.has("method") && !obj.get("method").getAsString().isEmpty()) {
            try {
                this.method = PaymentMethod.valueOf(obj.get("method").getAsString());
            } catch (Exception e) {
                this.method = null;
            }
        }
        
        if (obj.has("status") && !obj.get("status").getAsString().isEmpty()) {
            try {
                this.status = PaymentStatus.valueOf(obj.get("status").getAsString());
            } catch (Exception e) {
                this.status = null;
            }
        }
        
        if (obj.has("time") && !obj.get("time").getAsString().isEmpty()) {
            this.time = LocalDateTime.parse(obj.get("time").getAsString(), formatter);
        }
        
        this.note = obj.has("note") ? obj.get("note").getAsString() : null;
    }
} 