package com.an.post.repository;

import com.an.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {

//    Page<Post>
//    Là kiểu trả về dữ liệu phân trang
//    Không chỉ có list data mà còn có: tổng số trang, tổng số phần tử, trang hiện tại
      Page<Post> findAllByUserId(String userId, Pageable pageable);
}
