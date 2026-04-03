package com.an.file.service;

import com.an.file.dto.FileInfo;
import com.an.file.dto.response.FileResponse;
import com.an.file.entity.FileMgmt;
import com.an.file.mapper.FileMgmtMapper;
import com.an.file.repository.FileMgmtRepository;
import com.an.file.repository.FileRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class FileService {
    FileRepository fileRepository;
    FileMgmtRepository fileMgmtRepository;
    FileMgmtMapper fileMgmtMapper;

    public FileResponse uploadFile(MultipartFile file) throws IOException {
        // store file in local storage
        FileInfo fileInfo = fileRepository.store(file);

        // create file management ifo
        FileMgmt  fileMgmt = fileMgmtMapper.toFileMgmt(fileInfo);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        fileMgmt.setOwnerId(userId);

        // save to mongo
        fileMgmtRepository.save(fileMgmt);

        return FileResponse.builder()
                .originalFileName(file.getOriginalFilename())
                .url(fileInfo.getUrl())
                .build();
    }
}
