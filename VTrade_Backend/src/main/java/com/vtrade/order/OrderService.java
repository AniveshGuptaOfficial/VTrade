package com.vtrade.order;

import com.vtrade.order.OrderRequest;
import com.vtrade.order.Order;
import com.vtrade.order.OrderItem;
import com.vtrade.order.OrderRepository;
import com.vtrade.auth.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final double GST_RATE = 0.18;

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public Order placeOrder(Long userId, OrderRequest req) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (!req.getPhone().matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone must be a valid 10-digit number.");
        }

        double subtotal = req.getItems().stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum();
        double tax = subtotal * GST_RATE;
        double total = subtotal + tax;

        Order order = new Order();
        order.setUserId(userId);
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setTotalAmount(total);
        order.setHostelBlock(req.getHostelBlock());
        order.setRoomNumber(req.getRoomNumber());
        order.setPhone(req.getPhone());
        order.setLandmark(req.getLandmark());
        order.setPaymentMethod(req.getPaymentMethod());
        order.setStatus("placed");
        order.setDeliveryOtp(String.format("%06d", new Random().nextInt(1_000_000)));

        List<OrderItem> items = req.getItems().stream().map(i -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setName(i.getName());
            item.setUnitPrice(i.getUnitPrice());
            item.setQuantity(i.getQuantity());
            item.setImage(i.getImage());
            return item;
        }).collect(Collectors.toList());

        order.setItems(items);
        return orderRepository.save(order);
    }

    public List<Order> getMine(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void deleteOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own orders.");
        }
        orderRepository.delete(order);
    }

    /** Worker feed — all placed orders not yet assigned */
    public List<Order> getPending() {
        return orderRepository.findAll().stream()
                .filter(o -> "placed".equals(o.getStatus()))
                .collect(Collectors.toList());
    }

    /** Worker grabs an order */
    public Order acceptOrder(Long workerId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));
        if (!"placed".equals(order.getStatus())) {
            throw new IllegalArgumentException("This order has already been claimed.");
        }
        order.setStatus("assigned");
        return orderRepository.save(order);
    }
}
