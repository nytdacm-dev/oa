package com.nytdacm.oa.controller.admin;

import com.nytdacm.oa.model.entity.Submission;
import com.nytdacm.oa.model.response.HttpResponse;
import com.nytdacm.oa.model.response.ListWrapper;
import com.nytdacm.oa.model.response.user.UserDto;
import com.nytdacm.oa.service.SubmissionService;
import jakarta.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/admin/submission")
public class AdminSubmissionController {
    private final SubmissionService submissionService;

    @Inject
    public AdminSubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @GetMapping
    public ResponseEntity<HttpResponse<ListWrapper<SubmissionDto>>> getAllSubmissions(
        @RequestParam(required = false) Long user,
        @RequestParam(required = false) Long group,
        @RequestParam(required = false) String oj,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "2147483647") Integer size
    ) {
        var submissions = submissionService.getAllSubmissions(user, group, oj, page, size)
            .stream().map(SubmissionDto::fromEntity).toList();
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
