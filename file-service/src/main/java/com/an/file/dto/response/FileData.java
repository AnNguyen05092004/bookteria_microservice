package com.an.file.dto.response;

import org.springframework.core.io.Resource;

// record giống class nhưng 1 khi đã init thì ko thay dđổi được nx
public record FileData(String contentType, Resource resource) {

}
