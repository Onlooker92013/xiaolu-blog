package com.xiaolu.blog.controller;

import com.xiaolu.blog.common.Result;
import com.xiaolu.blog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@io.swagger.v3.oas.annotations.tags.Tag(name = "标签")
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(summary = "所有标签")
    @GetMapping
    public Result<List<com.xiaolu.blog.entity.Tag>> list() {
        return Result.success(tagService.listAll());
    }
}
