package com.nytdacm.oa.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.nytdacm.oa.entity.Submission;
import com.nytdacm.oa.response.HttpResponse;
import com.nytdacm.oa.response.ListWrapper;
import com.nytdacm.oa.response.user.UserDto;
import com.nytdacm.oa.service.SubmissionService;
import com.nytdacm.oa.utils.ExcelUtil;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/admin/submission")
@SaCheckLogin
@SaCheckRole(value = {"admin", "super-admin"}, mode = SaMode.OR)
public class AdminSubmissionController {
    private final SubmissionService submissionService;

    public AdminSubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @GetMapping
    public ResponseEntity<HttpResponse<ListWrapper<SubmissionDto>>> getAllSubmissions(
        @RequestParam(required = false) String user,
        @RequestParam(required = false) Long group,
        @RequestParam(required = false) String oj,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "2147483647") Integer size
    ) {
        var submissions = submissionService.getAllSubmissions(user, group, oj, page, size)
            .parallelStream().map(SubmissionDto::fromEntity).toList();
        var count = submissionService.count(user, group, oj);
        return HttpResponse.success(200, "????????????",
            new ListWrapper<>(count, submissions));
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> download(
        @RequestParam(required = false) String user,
        @RequestParam(required = false) Long group,
        @RequestParam(required = false) String oj,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "2147483647") Integer size
    ) {
        String[] headers = {"?????????", "?????????", "????????????", "OJ", "OJ ????????????", "OJ ??????ID", "??????", "????????????", "????????????"};
        var submissions = submissionService.getAllSubmissions(user, group, oj, page, size).parallelStream()
            .map(submission -> new String[]{
                submission.getUser().getName(),
                submission.getUser().getUsername(),
                submission.getName(),
                submission.getOj(),
                submission.getRemoteProblemId(),
                submission.getRemoteSubmissionId(),
                switch (submission.getStatus()) {
                    case Submission.STATUS_SUCCESS -> "????????????";
                    case Submission.STATUS_WRONG_ANSWER -> "????????????";
                    case Submission.STATUS_TIME_LIMIT_EXCEEDED -> "????????????";
                    case Submission.STATUS_MEMORY_LIMIT_EXCEEDED -> "????????????";
                    case Submission.STATUS_COMPILATION_ERROR -> "????????????";
                    case Submission.STATUS_RUNTIME_ERROR -> "???????????????";
                    default -> submission.getStatus();
                },
                submission.getLanguage(),
                LocalDateTime
                    .ofInstant(submission.getSubmitTime(), ZoneId.of("Asia/Shanghai"))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            }).toList();
        var file = new InputStreamResource(ExcelUtil.createExcel("????????????", headers, submissions));
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" +
                    ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ".xlsx")
            .contentType(MediaType.parseMediaType(ExcelUtil.TYPE)).body(file);
    }
}

record SubmissionDto(
    long submissionId,
    UserDto user,
    String oj,
    String remoteProblemId,
    String name,
    String remoteSubmissionId,
    String contestId,
    String language,
    String status,
    Instant submitTime
) {
    public static SubmissionDto fromEntity(Submission submission) {
        return new SubmissionDto(
            submission.getSubmissionId(),
            UserDto.fromEntity(submission.getUser()),
            submission.getOj(),
            submission.getRemoteProblemId(),
            submission.getName(),
            submission.getRemoteSubmissionId(),
            submission.getContestId(),
            submission.getLanguage(),
            submission.getStatus(),
            submission.getSubmitTime()
        );
    }
}
