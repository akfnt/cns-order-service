package com.akfnt.cnsorderservice.order.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("orders")    // Order 객체와 order 테이블 사이의 매핑 설정 (order는 SQL 에서 예약어이기 때문에 매핑해줌)
public record Order(
        @Id
        Long id,

        String bookIsbn,
        String bookName,
        Double bookPrice,
        Integer quantity,
        OrderStatus status,

        @CreatedDate
        Instant createdDate,
        @LastModifiedDate
        Instant lastModifiedDate,

        @Version
        int version
) {
    public static Order of(String bookIbsn, String bookName, Double bookPrice, Integer quantity, OrderStatus status) {
        return new Order(null, bookIbsn, bookName, bookPrice, quantity, status,  null, null, 0);
    }
}
