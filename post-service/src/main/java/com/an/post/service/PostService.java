package com.an.post.service;

import com.an.post.dto.PageResponse;
import com.an.post.dto.request.PostRequest;
import com.an.post.dto.response.PostResponse;
import com.an.post.dto.response.UserProfileResponse;
import com.an.post.entity.Post;
import com.an.post.mapper.PostMapper;
import com.an.post.repository.PostRepository;
import com.an.post.repository.httpClient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostService {

    PostRepository postRepository;
    PostMapper postMapper;
    DateTimeFormatter dateTimeFormatter;
    ProfileClient profileClient;

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

    public PageResponse<PostResponse> getMyPosts(int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        UserProfileResponse userProfileResponse = null;

        try {
            userProfileResponse = profileClient.getProfileByUserId(userId).getResult();
        } catch (Exception e) {
            log.error("Error while calling profile service: {}", e.getMessage());
        }


        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var pageData = postRepository.findAllByUserId(userId, pageable);

        String userName = userProfileResponse != null ? userProfileResponse.getFirstName() : "Unknown User";
        var postList = pageData.getContent().stream().map(post -> {
            var postResponse = postMapper.toPostResponse(post);
            postResponse.setCreatedAt(dateTimeFormatter.format(post.getCreatedDate()));
            postResponse.setUserName(userName);
            return postResponse;
        }).toList();

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(postList)
                .build();
    }
}
