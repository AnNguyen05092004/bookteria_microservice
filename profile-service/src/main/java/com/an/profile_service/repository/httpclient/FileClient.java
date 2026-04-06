package com.an.profile_service.repository.httpclient;

import java.io.IOException;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.an.profile_service.configuration.AuthenticationRequestInterceptor;
import com.an.profile_service.dto.response.ApiResponse;
import com.an.profile_service.dto.response.FileResponse;

@FeignClient(
        name = "file-service",
        url = "${app.service.file}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface FileClient {
    // dùng requestPart vì gửi file lên, nếu dùng requestParam sẽ ko nhận được file
    @PostMapping(value = "/file/media/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<FileResponse> uploadMedia(@RequestPart("file") MultipartFile file) throws IOException;
}
