package com.nytdacm.oa.controller

import cn.hutool.core.io.file.FileNameUtil
import com.nytdacm.oa.config.FileConfig
import com.nytdacm.oa.response.HttpResponse
import com.nytdacm.oa.utils.randomString
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import kotlin.io.path.exists

@RestController
@RequestMapping("/file")
class FileController(
    private val fileConfig: FileConfig,
) {
    @PostMapping
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<HttpResponse<FileUploadResponse>> {
        val filename =
            "${Instant.now().toEpochMilli()}-${randomString()}.${FileNameUtil.extName(file.originalFilename)}"
        // 判断目录是否存在
        val path = Paths.get(fileConfig.uploadDir)
        if (!path.exists()) {
            Files.createDirectories(path)
        }
        val filepath = path.resolve(filename)
        Files.copy(file.inputStream, filepath)
        return HttpResponse.success(
            200,
            "上传成功",
            FileUploadResponse(
                filename,
                "/file/$filename",
            ),
        )
    }
}

data class FileUploadResponse(
    val filename: String,
    val url: String,
)
