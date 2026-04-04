package com.an.file.controller;

import com.an.file.dto.ApiResponse;
import com.an.file.dto.response.FileData;
import com.an.file.dto.response.FileResponse;
import com.an.file.service.FileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileController {
    FileService fileService;

    @PostMapping("/media/upload")
    ApiResponse<FileResponse> uploadMedia(@RequestParam("file") MultipartFile file) throws IOException {
        return ApiResponse.<FileResponse>builder()
                .message("File uploaded successfully")
                .result(fileService.uploadFile(file))
                .build();
    }

    // api xem ảnh, ai cung có thể xem được, nên ko cần check quyền
    @GetMapping("/media/download/{fileName}")
    ResponseEntity<Resource>  downloadMedia(@PathVariable String fileName) throws IOException {
        var fileData = fileService.dowloadFile(fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, fileData.contentType())
                .body(fileData.resource());
    }

}
