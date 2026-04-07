package com.an.chat.service;

import com.an.chat.dto.request.IntrospectRequest;
import com.an.chat.dto.response.IntrospectResponse;
import com.an.chat.repository.httpclient.IdentityClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;

    public IntrospectResponse introspect (IntrospectRequest request) {
        try {
            var result = identityClient.introspect(request).getResult();
            if (Objects.isNull(result)) {
                return IntrospectResponse.builder().valid(false).build();
            }
            return result;
        } catch (FeignException e) {
            log.error("FeignException introspect token: {}", request.getToken(), e);
            return IntrospectResponse.builder().valid(false).build();
        }
    }
}
