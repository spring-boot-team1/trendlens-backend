package com.test.trend.domain.s3presigned.controller;

import com.test.trend.domain.account.dto.CustomAccountDetails;
import com.test.trend.domain.account.service.AccountService;
import com.test.trend.domain.s3presigned.dto.FileRequest;
import com.test.trend.domain.s3presigned.dto.PresignedURLResponse;
import com.test.trend.domain.s3presigned.service.S3Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "S3 Presigned URL", description = "AWS S3 Presigned URL 관련")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class S3Controller {
    private final S3Service s3Service;
    private final AccountService accountService;

    @PostMapping("/v1/presigned/{category}")
    public PresignedURLResponse getPresignedPutUrl(@PathVariable String category, @RequestBody FileRequest request) {
        String fileName = UUID.randomUUID() + "." + request.getExt();
        return s3Service.createPresignedPutUrl(category, fileName, request.getContentType());
    }

    @GetMapping("/v1/user/profile-image")
    public String getMyProfileImage(Authentication auth) {
        CustomAccountDetails user = (CustomAccountDetails) auth.getPrincipal();
        String key = accountService.getProfileKey(user.getSeqAccount());
        return s3Service.createGetPresignedUrl(key);
    }


}
