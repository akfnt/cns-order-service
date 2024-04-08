package com.akfnt.cnsorderservice.book;

// 책 정보를 저장하는 DTO
public record Book(
        String isbn,
        String title,
        String author,
        Double price
) {
}
