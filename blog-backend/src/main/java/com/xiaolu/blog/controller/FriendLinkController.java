package com.xiaolu.blog.controller;

import com.xiaolu.blog.common.Result;
import com.xiaolu.blog.entity.FriendLink;
import com.xiaolu.blog.service.FriendLinkService;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@io.swagger.v3.oas.annotations.tags.Tag(name = "友链")
@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendLinkController {

    private final FriendLinkService friendLinkService;

    @Operation(summary = "已审核友链")
    @GetMapping
    public Result<List<FriendLink>> list() {
        return Result.success(friendLinkService.listApproved());
    }

    @Operation(summary = "申请友链")
    @PostMapping("/apply")
    public Result<?> apply(@Valid @RequestBody FriendLink link) {
        friendLinkService.apply(link);
        return Result.success();
    }
}
