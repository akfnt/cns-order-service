package com.akfnt.cnsorderservice.order.event;

// 주문 접수 이벤트를 나타내는 DTO
public record OrderAcceptedMessage(Long orderId) {
}
