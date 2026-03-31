package com.an.post.service;

import com.an.post.dto.request.PostRequest;
import com.an.post.dto.response.PostResponse;
import com.an.post.entity.Post;
import com.an.post.mapper.PostMapper;
import com.an.post.repository.PostRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {

    PostRepository postRepository;
    PostMapper postMapper;

    public PostResponse createPost(PostRequest postRequest) {
        // lấy ttin user trong token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Post post = Post.builder()
                .content(postRequest.getContent())
                .userId(authentication.getName())  // là get sub trong token
                .createdDate(Instant.now())
                .modifiedDate(Instant.now())
                .build();

        return postMapper.toPostResponse(postRepository.save(post));
    }

    public List<PostResponse> getMyPosts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return postRepository.findAllByUserId(userId).stream()
                .map(postMapper::toPostResponse)
                .toList();
    }
}
