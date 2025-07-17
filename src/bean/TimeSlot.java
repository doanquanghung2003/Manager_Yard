package bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.google.gson.JsonObject;

public class TimeSlot implements Bean {

    private String slotId; 
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isBooked = false;
    private String slotType = "booking";
    private String yardId;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  
    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        this.slotId = java.util.UUID.randomUUID().toString();
        this.startTime = startTime;
        this.endTime = endTime;
        this.isBooked = true;
    }


    // ========== GETTERS / SETTERS ==========

    public TimeSlot() {
		// TODO Auto-generated constructor stub
	}

	public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }
    public String getYardId() {
        return yardId;
    }

    public void setYardId(String yardId) {
        this.yardId = yardId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean isBooked) {
        this.isBooked = isBooked;
    }

    public String getFormattedStartTime() {
        return startTime != null ? startTime.format(formatter) : "";
    }

    public String getFormattedEndTime() {
        return endTime != null ? endTime.format(formatter) : "";
    }
    public String getSlotType() {
        return slotType;
    }

    public void setSlotType(String slotType) {
        this.slotType = slotType;
    }


    @Override
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("slotId", slotId);
        json.addProperty("startTime", getFormattedStartTime());
        json.addProperty("endTime", getFormattedEndTime());
        json.addProperty("isBooked", isBooked);
        json.addProperty("slotType", slotType);
        json.addProperty("yardId", yardId);
        return json;
    }

    @Override
    public void parse(JsonObject obj) throws Exception {
        this.slotId = obj.has("slotId") ? obj.get("slotId").getAsString() : "";

        String start = obj.has("startTime") ? obj.get("startTime").getAsString() : "";
        String end = obj.has("endTime") ? obj.get("endTime").getAsString() : "";

        if (!start.isEmpty()) {
            this.startTime = LocalDateTime.parse(start, formatter);
        }

        if (!end.isEmpty()) {
            this.endTime = LocalDateTime.parse(end, formatter);
        }

        this.isBooked = obj.has("isBooked") && obj.get("isBooked").getAsBoolean();
        this.slotType = obj.has("slotType") ? obj.get("slotType").getAsString() : "booking";
        this.yardId = obj.has("yardId") ? obj.get("yardId").getAsString() : null; 
    }

	@Override
	public String toString() {
		return "TimeSlot [slotId=" + slotId + ", startTime=" + startTime + ", endTime=" + endTime + ", isBooked="
				+ isBooked + "]";
	}
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null || getClass() != obj.getClass()) return false;
	    TimeSlot other = (TimeSlot) obj;
	    return startTime.equals(other.startTime) && endTime.equals(other.endTime);
	}

	@Override
	public int hashCode() {
	    return java.util.Objects.hash(startTime, endTime);
	}

}
