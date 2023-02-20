package com.nytdacm.oa.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.nytdacm.oa.entity.Submission;
import com.nytdacm.oa.response.HttpResponse;
import com.nytdacm.oa.response.ListWrapper;
import com.nytdacm.oa.response.user.UserDto;
import com.nytdacm.oa.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

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
        return HttpResponse.success(200, "获取成功",
            new ListWrapper<>(count, submissions));
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