package com.an.file.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "file_mgmt")
public class FileMgmt {
    @MongoId
    String id;
    String ownerId;
    String contentType;
    long size;
    String md5Checksum;
    String path;
}
