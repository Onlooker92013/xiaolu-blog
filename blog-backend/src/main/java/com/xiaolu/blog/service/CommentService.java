package com.xiaolu.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaolu.blog.common.PageResult;
import com.xiaolu.blog.common.exception.BusinessException;
import com.xiaolu.blog.entity.Comment;
import com.xiaolu.blog.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;

    public PageResult<Comment> pageByArticle(Long articleId, Integer page, Integer pageSize) {
        var wrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getArticleId, articleId)
                .isNull(Comment::getParentId)
                .orderByDesc(Comment::getCreatedAt);
        var result = commentMapper.selectPage(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize), wrapper);
        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    public List<Comment> getReplies(Long parentId) {
        return commentMapper.selectList(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getParentId, parentId)
                .orderByAsc(Comment::getCreatedAt));
    }

    public Comment create(Comment comment, Long userId) {
        comment.setUserId(userId);
        commentMapper.insert(comment);
        return comment;
    }

    public void delete(Long id, Long userId) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) throw new BusinessException(404, "评论不存在");
        if (!comment.getUserId().equals(userId)) throw new BusinessException(403, "无权删除");
        commentMapper.deleteById(id);
    }
}
