package com.akfnt.cnsorderservice.order.event;

// 발송 주문 이벤트를 나타내는 DTO
public record OrderDispatchedMessage(
        Long orderId
) {
}
