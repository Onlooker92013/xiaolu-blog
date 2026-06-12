package com.xiaolu.blog.controller;

import com.xiaolu.blog.common.PageResult;
import com.xiaolu.blog.common.Result;
import com.xiaolu.blog.entity.Comment;
import com.xiaolu.blog.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@io.swagger.v3.oas.annotations.tags.Tag(name = "评论")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "文章评论列表")
    @GetMapping("/article/{articleId}")
    public Result<PageResult<Comment>> listByArticle(@PathVariable Long articleId,
                                                      @RequestParam(defaultValue = "1") Integer page,
                                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(commentService.pageByArticle(articleId, page, pageSize));
    }

    @Operation(summary = "评论回复列表")
    @GetMapping("/replies/{parentId}")
    public Result<List<Comment>> replies(@PathVariable Long parentId) {
        return Result.success(commentService.getReplies(parentId));
    }

    @Operation(summary = "发表评论")
    @PostMapping
    public Result<Comment> create(@RequestBody Comment comment,
                                  @RequestAttribute(required = false) Long userId) {
        if (userId == null) {
            // 允许匿名，userId 设为 0
            userId = 0L;
        }
        return Result.success(commentService.create(comment, userId));
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id,
                            @RequestAttribute(required = false) Long userId) {
        if (userId == null) userId = 0L;
        commentService.delete(id, userId);
        return Result.success();
    }

    @Operation(summary = "验证评论用户")
    @GetMapping("/verify")
    public Result<?> verify(@RequestParam Long userId, @RequestParam Long commentId) {
        return Result.success(Map.of("valid", true));
    }
}
