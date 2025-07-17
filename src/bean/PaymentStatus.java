package bean;

public enum PaymentStatus {
    UNPAID(0, "Chưa thanh toán"),
    DEPOSITED(1, "Đã đặt cọc"),
    PAID(2, "Đã thanh toán"),
    CANCELLED(3, "Đã hủy");

    private final int code;
    private final String description;

    PaymentStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

  
    public static PaymentStatus fromCode(int code) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Mã trạng thái không hợp lệ: " + code);
    }

    @Override
    public String toString() {
        return description;
    }
}
