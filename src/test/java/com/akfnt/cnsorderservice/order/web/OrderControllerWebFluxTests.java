package com.akfnt.cnsorderservice.order.web;

import com.akfnt.cnsorderservice.config.SecurityConfig;
import com.akfnt.cnsorderservice.order.domain.Order;
import com.akfnt.cnsorderservice.order.domain.OrderService;
import com.akfnt.cnsorderservice.order.domain.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebFluxTest(OrderController.class)
@Import(SecurityConfig.class)
public class OrderControllerWebFluxTests {
    @Autowired
    private WebTestClient webClient;    // 웹 클라이언트의 변형으로 Restful 서비스 테스트를 쉽게 하기 위한 기능을 추가로 가지고 있다

    @MockBean
    private OrderService orderService;

    @MockBean
    private ReactiveJwtDecoder reactiveJwtDecoder;      // 애플리케이션이 액세스 토큰 해독을 위한 공개 키를 받기 위해 키클록에 연결하지 않도록 한다

    @Test
    void whenBookNotAvailableThenRejectOrder() {
        var orderRequest = new OrderRequest("1234567890", 3);
        var expectedOrder = OrderService.buildRejectedOrder(orderRequest.isbn(), orderRequest.quantity());
        given(orderService.submitOrder(orderRequest.isbn(), orderRequest.quantity()))
                .willReturn(Mono.just(expectedOrder));

        webClient
                .mutateWith(SecurityMockServerConfigurers
                        .mockJwt()          // 'customer' 역할을 갖는 사용자에 대한 JWT 형식의 모의 액세스 토큰을 HTTP 요청에 추가한다
                        .authorities(new SimpleGrantedAuthority("ROLE_customer")))
                .post()
                .uri("/orders")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Order.class).value(actualOrder -> {
                   assertThat(actualOrder).isNotNull();
                   assertThat(actualOrder.status()).isEqualTo(OrderStatus.REJECTED);
                });
    }
}