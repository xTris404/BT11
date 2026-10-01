package com.bookstore.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Order_24162132 {
    private int orderId, userId;
    private String receiverName, phone, address, note, paymentMethod, status;
    private BigDecimal total;
    private Timestamp createdAt;
    private List<OrderItem_24162132> items = new ArrayList<>();

    public int getOrderId() { return orderId; }
    public void setOrderId(int v) { orderId = v; }
    public int getUserId() { return userId; }
    public void setUserId(int v) { userId = v; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String v) { receiverName = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { phone = v; }
    public String getAddress() { return address; }
    public void setAddress(String v) { address = v; }
    public String getNote() { return note; }
    public void setNote(String v) { note = v; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String v) { paymentMethod = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { status = v; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal v) { total = v; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp v) { createdAt = v; }
    public List<OrderItem_24162132> getItems() { return items; }
    public void setItems(List<OrderItem_24162132> v) { items = v; }

    public String getStatusLabel() { return "PENDING".equals(status) ? "Chờ xác nhận" : status; }
    public String getPaymentLabel() { return "COD".equals(paymentMethod) ? "Thanh toán khi nhận hàng (COD)" : paymentMethod; }
}
