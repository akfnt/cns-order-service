package com.akfnt.cnsorderservice.order.event;

import com.akfnt.cnsorderservice.order.domain.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Configuration
public class OrderFunctions {
    private static final Logger log = LoggerFactory.getLogger(OrderFunctions.class);

    // Rabbitmq 는 적어도 하나의 전달을 보증함 -> 중복으로 메시지를 받을 수 있음 -> 이벤트 소비자의 연산은 멱등성을 가져야 함
    @Bean
    public Consumer<Flux<OrderDispatchedMessage>> dispatchOrder(OrderService orderService) {
        return flux -> orderService.consumeOrderDispatchedEvent(flux)   // 각 발송된 메시지에 대해 데이터베이스에 해당 주문을 업데이트 한다
                .doOnNext(order -> log.info("The order with id {} is dispatched", order.id()))  // 데이터베이스에서 업데이트된 각 주문에 대해 로그를 기록한다
                .subscribe();
    }
}
