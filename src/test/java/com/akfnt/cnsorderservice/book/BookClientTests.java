package com.akfnt.cnsorderservice.book;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

@TestMethodOrder(MethodOrderer.Random.class)
class BookClientTests {
    private MockWebServer mockWebServer;
    private BookClient bookClient;

    @BeforeEach
    void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        var webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").uri().toString())
                .build();
        this.bookClient = new BookClient(webClient);
    }

    @AfterEach
    void clean() throws IOException {
        this.mockWebServer.shutdown();
    }

    // 애플리케이션이 다른 서비스에 의존하는 경우 그 서비스의 API 사양에 대해 테스트 해야함
    // 아래 테스트는 카탈로그 서비스에서 제공하는 책 정보 반환 API 사양을 테스트하기 위해 BookClient 를 테스트 한다
    @Test
    void whenBookExistsThenReturnBook() {
        var bookIsbn = "1234567890";

        var mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
							{
								"isbn": %s,
								"title": "Title",
								"author": "Author",
								"price": 9.90,
								"publisher": "Polarsophia"
							}
						""".formatted(bookIsbn));

        mockWebServer.enqueue(mockResponse);

        Mono<Book> book = bookClient.getBookByIsbn(bookIsbn);

        StepVerifier.create(book)   // BookClient 가 반환하는 객체로 StepVerifier 객체를 초기화 한다
                .expectNextMatches(b -> b.isbn().equals(bookIsbn))
                .verifyComplete();
    }

    @Test
    void whenBookNotExistsThenReturnEmpty() {
        var bookIsbn = "1234567891";

        var mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(404);

        mockWebServer.enqueue(mockResponse);

        StepVerifier.create(bookClient.getBookByIsbn(bookIsbn))
                .expectNextCount(0)
                .verifyComplete();
    }
}