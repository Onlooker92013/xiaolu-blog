package com.xiaolu.blog.controller;

import com.xiaolu.blog.common.PageResult;
import com.xiaolu.blog.common.Result;
import com.xiaolu.blog.dto.ArticleQuery;
import com.xiaolu.blog.dto.ArticleVO;
import com.xiaolu.blog.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@io.swagger.v3.oas.annotations.tags.Tag(name = "文章")
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "文章列表")
    @GetMapping
    public Result<PageResult<ArticleVO>> list(ArticleQuery query) {
        return Result.success(articleService.page(query));
    }

    @Operation(summary = "文章详情")
    @GetMapping("/{id}")
    public Result<ArticleVO> detail(@PathVariable Long id) {
        return Result.success(articleService.getById(id));
    }

    @Operation(summary = "置顶文章")
    @GetMapping("/top")
    public Result<List<ArticleVO>> top() {
        return Result.success(articleService.listTop());
    }

    @Operation(summary = "相关文章推荐")
    @GetMapping("/{id}/related")
    public Result<List<ArticleVO>> related(@PathVariable Long id) {
        return Result.success(articleService.listRelated(id));
    }

    @Operation(summary = "上一篇/下一篇")
    @GetMapping("/{id}/navigation")
    public Result<Map<String, Object>> navigation(@PathVariable Long id) {
        return Result.success(articleService.getNavigation(id));
    }

    @Operation(summary = "阅读排行")
    @GetMapping("/top-read")
    public Result<List<Map<String, Object>>> topRead() {
        return Result.success(articleService.getTopRead(5));
    }
}
