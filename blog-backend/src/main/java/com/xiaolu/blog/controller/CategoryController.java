package com.xiaolu.blog.controller;

import com.xiaolu.blog.common.Result;
import com.xiaolu.blog.entity.Category;
import com.xiaolu.blog.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@io.swagger.v3.oas.annotations.tags.Tag(name = "分类")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "所有分类")
    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryService.listAll());
    }
}
