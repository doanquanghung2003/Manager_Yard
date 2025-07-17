package bean;

import java.util.List;

public class BookingYardModel {
    private String yardId;
    private List<TimeSlot> slots;

    public BookingYardModel() {}

    public BookingYardModel(String yardId, List<TimeSlot> slots) {
        this.yardId = yardId;
        this.slots = slots;
    }

    public String getYardId() {
        return yardId;
    }

    public void setYardId(String yardId) {
        this.yardId = yardId;
    }

    public List<TimeSlot> getSlots() {
        return slots;
    }

    public void setSlots(List<TimeSlot> slots) {
        this.slots = slots;
    }
}
