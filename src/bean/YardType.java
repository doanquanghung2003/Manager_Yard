package bean;
public enum YardType {
    FIVE_A_SIDE("5 người"),
    SEVEN_A_SIDE("7 người"),
    ELEVEN_A_SIDE("11 người");

    private final String displayName;

    YardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static YardType fromString(String type) {
        switch (type.trim().toLowerCase()) {
            case "5":
            case "5 người":
            case "five":
            case "five-a-side":
                return FIVE_A_SIDE;
            case "7":
            case "7 người":
            case "seven":
            case "seven-a-side":
                return SEVEN_A_SIDE;
            case "11":
            case "11 người":
            case "eleven":
            case "eleven-a-side":
                return ELEVEN_A_SIDE;
            default:
                throw new IllegalArgumentException("Loại sân không hợp lệ: " + type);
        }
    }

    @Override
    public String toString() {
        return displayName; // Tự động in đẹp
    }
}
