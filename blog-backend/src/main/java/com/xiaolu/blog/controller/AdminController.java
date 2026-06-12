package com.xiaolu.blog.controller;

import com.xiaolu.blog.common.PageResult;
import com.xiaolu.blog.common.Result;
import com.xiaolu.blog.dto.ArticleDTO;
import com.xiaolu.blog.dto.ArticleQuery;
import com.xiaolu.blog.dto.ArticleVO;
import com.xiaolu.blog.entity.Category;
import com.xiaolu.blog.entity.FriendLink;
import com.xiaolu.blog.service.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@io.swagger.v3.oas.annotations.tags.Tag(name = "后台管理")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final FriendLinkService friendLinkService;
    private final FileService fileService;

    // ===== 文章管理 =====
    @Operation(summary = "文章列表(含草稿)")
    @GetMapping("/articles")
    public Result<PageResult<ArticleVO>> articles(ArticleQuery query) {
        return Result.success(articleService.adminPage(query));
    }

    @Operation(summary = "新建文章")
    @PostMapping("/articles")
    public Result<ArticleVO> createArticle(@Valid @RequestBody ArticleDTO dto,
                                           @RequestAttribute Long userId) {
        return Result.success(articleService.create(dto, userId));
    }

    @Operation(summary = "编辑文章")
    @PutMapping("/articles/{id}")
    public Result<ArticleVO> updateArticle(@PathVariable Long id,
                                           @Valid @RequestBody ArticleDTO dto) {
        return Result.success(articleService.update(id, dto));
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/articles/{id}")
    public Result<?> deleteArticle(@PathVariable Long id) {
        articleService.delete(id);
        return Result.success();
    }

    // ===== 分类管理 =====
    @Operation(summary = "所有分类")
    @GetMapping("/categories")
    public Result<List<Category>> categories() {
        return Result.success(categoryService.listAll());
    }

    @Operation(summary = "新建分类")
    @PostMapping("/categories")
    public Result<Category> createCategory(@Valid @RequestBody Category category) {
        return Result.success(categoryService.create(category));
    }

    @Operation(summary = "编辑分类")
    @PutMapping("/categories/{id}")
    public Result<Category> updateCategory(@PathVariable Long id,
                                           @Valid @RequestBody Category category) {
        return Result.success(categoryService.update(id, category));
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/categories/{id}")
    public Result<?> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }

    // ===== 标签管理 =====
    @Operation(summary = "所有标签")
    @GetMapping("/tags")
    public Result<java.util.List<com.xiaolu.blog.entity.Tag>> tags() {
        return Result.success(tagService.listAll());
    }

    @Operation(summary = "新建标签")
    @PostMapping("/tags")
    public Result<com.xiaolu.blog.entity.Tag> createTag(@Valid @RequestBody com.xiaolu.blog.entity.Tag tag) {
        return Result.success(tagService.create(tag));
    }

    @Operation(summary = "编辑标签")
    @PutMapping("/tags/{id}")
    public Result<com.xiaolu.blog.entity.Tag> updateTag(@PathVariable Long id, @Valid @RequestBody com.xiaolu.blog.entity.Tag tag) {
        return Result.success(tagService.update(id, tag));
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/tags/{id}")
    public Result<?> deleteTag(@PathVariable Long id) {
        tagService.delete(id);
        return Result.success();
    }

    // ===== 友链管理 =====
    @Operation(summary = "所有友链")
    @GetMapping("/friends")
    public Result<List<FriendLink>> friends() {
        return Result.success(friendLinkService.listAll());
    }

    @Operation(summary = "审核友链")
    @PutMapping("/friends/{id}/approve")
    public Result<?> approveFriend(@PathVariable Long id) {
        friendLinkService.approve(id);
        return Result.success();
    }

    @Operation(summary = "删除友链")
    @DeleteMapping("/friends/{id}")
    public Result<?> deleteFriend(@PathVariable Long id) {
        friendLinkService.delete(id);
        return Result.success();
    }

    // ===== 文件上传 =====
    @Operation(summary = "上传图片")
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        return Result.success(fileService.uploadImage(file));
    }
}
