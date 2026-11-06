package com.sky.controller.admin;

import com.sky.properties.AliOssProperties;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 通用接口：文件上传（支持阿里云 OSS 与本地存储自动降级）
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    private final AliOssUtil aliOssUtil;
    private final AliOssProperties aliOssProperties;

    public CommonController(AliOssUtil aliOssUtil, AliOssProperties aliOssProperties) {
        this.aliOssUtil = aliOssUtil;
        this.aliOssProperties = aliOssProperties;
    }

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 如果配置了真实的阿里云 OSS 密钥，优先使用 OSS 上传
        boolean hasValidOss = aliOssProperties != null
                && aliOssProperties.getAccessKeyId() != null
                && !aliOssProperties.getAccessKeyId().contains("你的")
                && !aliOssProperties.getAccessKeyId().trim().isEmpty();

        if (hasValidOss) {
            try {
                String filePath = aliOssUtil.upload(file.getBytes(), originalFilename);
                return Result.success(filePath);
            } catch (Exception e) {
                log.warn("阿里云 OSS 上传失败，自动降级为本地文件保存: {}", e.getMessage());
            }
        }

        // 降级为本地保存，确保无 OSS 账号也能平滑体验
        try {
            File uploadDir = new File("uploads");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            String newFileName = UUID.randomUUID().toString().replace("-", "") + extension;
            Path targetPath = Paths.get(uploadDir.getAbsolutePath(), newFileName);
            Files.write(targetPath, file.getBytes());

            String localUrl = "/uploads/" + newFileName;
            log.info("文件已成功保存到本地: {}", localUrl);
            return Result.success(localUrl);
        } catch (IOException e) {
            log.error("本地文件上传失败", e);
            return Result.error("文件上传失败");
        }
    }
}
