package bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.google.gson.JsonObject;

public class DepositModel {
    private double amount;
    private String method;
    private LocalDateTime time;
    private String note;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public DepositModel() {}
    public DepositModel(double amount, String method, LocalDateTime time, String note) {
        this.amount = amount;
        this.method = method;
        this.time = time;
        this.note = note;
    }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public JsonObject toJsonObject() {
        JsonObject obj = new JsonObject();
        obj.addProperty("amount", amount);
        obj.addProperty("method", method);
        obj.addProperty("time", time != null ? time.format(formatter) : "");
        obj.addProperty("note", note);
        return obj;
    }
    public void parse(JsonObject obj) {
        this.amount = obj.get("amount").getAsDouble();
        this.method = obj.get("method").getAsString();
        this.note = obj.has("note") ? obj.get("note").getAsString() : null;
        if (obj.has("time") && !obj.get("time").getAsString().isEmpty()) {
            this.time = LocalDateTime.parse(obj.get("time").getAsString(), formatter);
        }
    }
} 