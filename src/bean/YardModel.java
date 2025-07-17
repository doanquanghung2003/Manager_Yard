package bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class YardModel implements Bean {
    private String yardId;
    private String yardName;
    private YardType yardType;
    private Double yardPrice;
    private String yardDescription;
    private String yardThumbnail;
    private List<String> yardImages;
    private String yardAddress;
    private String yardStatus = "active";
    private Boolean isAvailable;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private List<TimeSlot> maintenanceSlots = new ArrayList<>();
    private List<TimeSlot> eventSlots = new ArrayList<>();

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public YardModel() {}

    public YardModel(String yardId, String yardName, YardType yardType, Double yardPrice, String yardDescription,
                     String yardThumbnail, List<String> yardImages, String yardAddress, String yardStatus,
                     Boolean isAvailable, LocalDateTime createAt, LocalDateTime updateAt) {
        this.yardId = yardId;
        this.yardName = yardName;
        this.yardType = yardType;
        this.yardPrice = yardPrice;
        this.yardDescription = yardDescription;
        this.yardThumbnail = yardThumbnail;
        this.yardImages = yardImages;
        this.yardAddress = yardAddress;
        this.yardStatus = yardStatus;
        this.isAvailable = isAvailable;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    // Getters & Setters
    public String getYardId() { return yardId; }
    public void setYardId(String yardId) { this.yardId = yardId; }

    public String getYardName() { return yardName; }
    public void setYardName(String yardName) { this.yardName = yardName; }

    public YardType getYardType() { return yardType; }
    public void setYardType(YardType yardType) { this.yardType = yardType; }

    public Double getYardPrice() { return yardPrice; }
    public void setYardPrice(Double yardPrice) { this.yardPrice = yardPrice; }

    public String getYardDescription() { return yardDescription; }
    public void setYardDescription(String yardDescription) { this.yardDescription = yardDescription; }

    public String getYardThumbnail() { return yardThumbnail; }
    public void setYardThumbnail(String yardThumbnail) { this.yardThumbnail = yardThumbnail; }

    public List<String> getYardImages() { return yardImages; }
    public void setYardImages(List<String> yardImages) { this.yardImages = yardImages; }

    public String getYardAddress() { return yardAddress; }
    public void setYardAddress(String yardAddress) { this.yardAddress = yardAddress; }

    public String getYardStatus() { return yardStatus; }
    public void setYardStatus(String yardStatus) { this.yardStatus = yardStatus; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public LocalDateTime getCreateAt() { return createAt; }
    public void setCreateAt(LocalDateTime createAt) { this.createAt = createAt; }

    public LocalDateTime getUpdateAt() { return updateAt; }
    public void setUpdateAt(LocalDateTime updateAt) { this.updateAt = updateAt; }

    public List<TimeSlot> getMaintenanceSlots() { return maintenanceSlots; }
    public void setMaintenanceSlots(List<TimeSlot> maintenanceSlots) {
        this.maintenanceSlots = (maintenanceSlots != null) ? maintenanceSlots : new ArrayList<>();
    }

    public List<TimeSlot> getEventSlots() { return eventSlots; }
    public void setEventSlots(List<TimeSlot> eventSlots) {
        this.eventSlots = (eventSlots != null) ? eventSlots : new ArrayList<>();
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("yardId", yardId);
        json.addProperty("yardName", yardName);
        json.addProperty("yardType", yardType != null ? yardType.name() : null);
        json.addProperty("yardPrice", yardPrice);
        json.addProperty("yardDescription", yardDescription);
        json.addProperty("yardThumbnail", yardThumbnail);
        JsonArray imagesArray = new JsonArray();
        if (yardImages != null) {
            for (String img : yardImages) {
                imagesArray.add(new JsonPrimitive(img));
            }
        }
        json.add("yardImages", imagesArray);
        json.addProperty("yardAddress", yardAddress);
        json.addProperty("yardStatus", yardStatus);
        json.addProperty("isAvailable", isAvailable);
        json.addProperty("createAt", createAt != null ? createAt.format(formatter) : null);
        json.addProperty("updateAt", updateAt != null ? updateAt.format(formatter) : null);
        JsonArray maintenanceArray = new JsonArray();
        for (TimeSlot ts : maintenanceSlots) {
            maintenanceArray.add(ts.toJsonObject());
        }
        json.add("maintenanceSlots", maintenanceArray);
        JsonArray eventArray = new JsonArray();
        for (TimeSlot ts : eventSlots) {
            eventArray.add(ts.toJsonObject());
        }
        json.add("eventSlots", eventArray);
        return json;
    }

    @Override
    public void parse(JsonObject obj) throws Exception {
        yardId = obj.get("yardId").getAsString();
        yardName = obj.get("yardName").getAsString();
        yardType = obj.has("yardType") && !obj.get("yardType").isJsonNull()
                ? YardType.valueOf(obj.get("yardType").getAsString()) : null;
        yardPrice = obj.get("yardPrice").getAsDouble();
        yardDescription = obj.get("yardDescription").getAsString();
        yardThumbnail = obj.get("yardThumbnail").getAsString();
        yardImages = new ArrayList<>();
        if (obj.has("yardImages") && obj.get("yardImages").isJsonArray()) {
            JsonArray imagesArray = obj.getAsJsonArray("yardImages");
            for (int i = 0; i < imagesArray.size(); i++) {
                yardImages.add(imagesArray.get(i).getAsString());
            }
        }
        yardAddress = obj.get("yardAddress").getAsString();
        yardStatus = obj.get("yardStatus").getAsString();
        isAvailable = obj.get("isAvailable").getAsBoolean();
        if (obj.has("createAt") && !obj.get("createAt").isJsonNull()) {
            createAt = LocalDateTime.parse(obj.get("createAt").getAsString(), formatter);
        }
        if (obj.has("updateAt") && !obj.get("updateAt").isJsonNull()) {
            updateAt = LocalDateTime.parse(obj.get("updateAt").getAsString(), formatter);
        }
        maintenanceSlots = new ArrayList<>();
        if (obj.has("maintenanceSlots") && obj.get("maintenanceSlots").isJsonArray()) {
            for (JsonElement el : obj.getAsJsonArray("maintenanceSlots")) {
                TimeSlot ts = new TimeSlot();
                ts.parse(el.getAsJsonObject());
                maintenanceSlots.add(ts);
            }
        }
        eventSlots = new ArrayList<>();
        if (obj.has("eventSlots") && obj.get("eventSlots").isJsonArray()) {
            for (JsonElement el : obj.getAsJsonArray("eventSlots")) {
                TimeSlot ts = new TimeSlot();
                ts.parse(el.getAsJsonObject());
                eventSlots.add(ts);
            }
        }
    }

    @Override
    public String toString() {
        return yardName;
    }
}
