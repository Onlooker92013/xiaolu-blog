package com.xiaolu.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaolu.blog.common.PageResult;
import com.xiaolu.blog.common.Result;
import com.xiaolu.blog.entity.Article;
import com.xiaolu.blog.entity.Comment;
import com.xiaolu.blog.mapper.ArticleMapper;
import com.xiaolu.blog.mapper.CommentMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@io.swagger.v3.oas.annotations.tags.Tag(name = "评论管理")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentMapper commentMapper;
    private final ArticleMapper articleMapper;

    @Operation(summary = "所有评论列表(按文章分组)")
    @GetMapping("/comments")
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) Long articleId) {

        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .orderByDesc(Comment::getCreatedAt);
        if (articleId != null) {
            wrapper.eq(Comment::getArticleId, articleId);
        }

        Page<Comment> result = commentMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<Map<String, Object>> enriched = result.getRecords().stream().map(c -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId());
            m.put("articleId", c.getArticleId());
            m.put("guestName", c.getGuestName());
            m.put("content", c.getContent());
            m.put("parentId", c.getParentId());
            m.put("createdAt", c.getCreatedAt());
            Article article = articleMapper.selectById(c.getArticleId());
            m.put("articleTitle", article != null ? article.getTitle() : "(已删除)");
            return m;
        }).toList();

        return Result.success(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), enriched));
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/comments/{id}")
    public Result<?> delete(@PathVariable Long id) {
        // 同时删除子回复
        commentMapper.delete(new LambdaQueryWrapper<Comment>().eq(Comment::getParentId, id));
        commentMapper.deleteById(id);
        return Result.success();
    }
}
