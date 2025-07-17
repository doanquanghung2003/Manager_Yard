package bean;

public enum PaymentMethod {
    DEPOSIT("Đặt cọc", PaymentStatus.DEPOSITED),
    FULL_PAYMENT("Thanh toán toàn bộ", PaymentStatus.PAID);

    private final String label;
    private final PaymentStatus status;

    PaymentMethod(String label, PaymentStatus status) {
        this.label = label;
        this.status = status;
    }

    public String getLabel() {
        return label;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return label;
    }
}
