package com.akfnt.cnsorderservice.book;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
public class BookClient {
    private static final String BOOKS_ROOT_URI = "/books/";
    private final WebClient webClient;

    public BookClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Book> getBookByIsbn(String isbn) {
        return webClient
                .get()
                .uri(BOOKS_ROOT_URI + isbn)
                .retrieve()
                .bodyToMono(Book.class)
                // GET 요청에 대해 3초의 타임아웃을 설정한다 (Read Timeout), 폴백으로 빈 모노 객체를 반환한다
                .timeout(Duration.ofSeconds(3), Mono.empty())
                // 404 응답을 받으면 빈 객체를 반환한다.
                .onErrorResume(WebClientResponseException.NotFound.class, exception -> Mono.empty())
                // timeout 뒤에 retryWhen 이 오면, 각 재시도에 대해 타임아웃이 적용된다는 것을 의미한다.
                // 지수 백오프를 재시도 전략으로 사용한다. 100 밀리초의 초기 백오프로 총 3회까지 시도한다.
                // 지연 시간의 최대 50%의 지터가 사용되도록 기본 설정되어 있어 주문 서비스가 여러 개 실행 중인 경우 지터 팩터로 인해
                // 모든 복제본에 동시에 요청을 재시도 하는 것을 피할 수 있다.
                .retryWhen(
                        Retry.backoff(3, Duration.ofMillis(100))
                )
                // 3회의 재시도 동안 오류가 발생하면 예외를 포착하고 빈 객체를 반환한다.
                .onErrorResume(Exception.class, exception -> Mono.empty());
    }
}
