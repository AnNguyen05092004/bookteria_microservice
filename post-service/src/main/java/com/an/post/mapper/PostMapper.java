package com.an.post.mapper;

import com.an.post.dto.response.PostResponse;
import com.an.post.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponse toPostResponse(Post post);
}
