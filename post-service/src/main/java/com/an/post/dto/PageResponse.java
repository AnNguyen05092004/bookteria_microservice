package com.an.post.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PageResponse<T> {
    int currentPage;
    int totalPages;
    int pageSize;
    long totalElements;

    @Builder.Default // Để sử dụng giá trị mặc định emptylist thay vì null
    List<T> data = Collections.emptyList();  // dữ liệu của trang hiện tại
}

