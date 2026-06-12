package com.xiaolu.blog.controller;

import com.xiaolu.blog.common.PageResult;
import com.xiaolu.blog.common.Result;
import com.xiaolu.blog.dto.ArticleQuery;
import com.xiaolu.blog.dto.ArticleVO;
import com.xiaolu.blog.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@io.swagger.v3.oas.annotations.tags.Tag(name = "搜索")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final ArticleService articleService;

    @Operation(summary = "全文搜索")
    @GetMapping
    public Result<PageResult<ArticleVO>> search(@RequestParam String keyword,
                                                 @RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        ArticleQuery query = new ArticleQuery();
        query.setKeyword(keyword);
        query.setPage(page);
        query.setPageSize(pageSize);
        return Result.success(articleService.page(query));
    }
}
