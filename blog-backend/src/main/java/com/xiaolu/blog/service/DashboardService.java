package com.xiaolu.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaolu.blog.entity.Article;
import com.xiaolu.blog.entity.Category;
import com.xiaolu.blog.entity.Comment;
import com.xiaolu.blog.entity.FriendLink;
import com.xiaolu.blog.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final CommentMapper commentMapper;
    private final FriendLinkMapper friendLinkMapper;
    private final TagMapper tagMapper;
    private final UserMapper userMapper;

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // 文章统计
        Long totalArticles = articleMapper.selectCount(new LambdaQueryWrapper<Article>().eq(Article::getDeleted, 0));
        Long publishedArticles = articleMapper.selectCount(
                new LambdaQueryWrapper<Article>().eq(Article::getDeleted, 0).eq(Article::getStatus, "PUBLISHED"));
        Long draftArticles = articleMapper.selectCount(
                new LambdaQueryWrapper<Article>().eq(Article::getDeleted, 0).eq(Article::getStatus, "DRAFT"));

        // 总阅读量
        List<Article> allArticles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>().eq(Article::getDeleted, 0));
        long totalViews = allArticles.stream().mapToLong(a -> a.getViewCount() != null ? a.getViewCount() : 0).sum();

        // 分类/标签数
        Long categoryCount = categoryMapper.selectCount(null);
        Long tagCount = tagMapper.selectCount(null);

        // 评论数
        Long commentCount = commentMapper.selectCount(null);

        // 待审核友链
        Long pendingFriends = friendLinkMapper.selectCount(
                new LambdaQueryWrapper<FriendLink>().eq(FriendLink::getStatus, "PENDING"));

        // 用户数
        Long userCount = userMapper.selectCount(null);

        stats.put("totalArticles", totalArticles);
        stats.put("publishedArticles", publishedArticles);
        stats.put("draftArticles", draftArticles);
        stats.put("totalViews", totalViews);
        stats.put("categoryCount", categoryCount);
        stats.put("tagCount", tagCount);
        stats.put("commentCount", commentCount);
        stats.put("pendingFriends", pendingFriends);
        stats.put("userCount", userCount);

        // 最近7天新增文章数
        List<Map<String, Object>> recentTrend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            String date = java.time.LocalDate.now().minusDays(i).toString();
            Long count = articleMapper.selectCount(new LambdaQueryWrapper<Article>()
                    .eq(Article::getDeleted, 0)
                    .apply("DATE(created_at) = {0}", date));
            recentTrend.add(Map.of("date", date, "count", count));
        }
        stats.put("recentTrend", recentTrend);

        // 阅读排行 Top5
        List<Article> topRead = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getDeleted, 0).eq(Article::getStatus, "PUBLISHED")
                .orderByDesc(Article::getViewCount).last("LIMIT 5"));
        List<Map<String, Object>> topList = topRead.stream().map(a -> Map.<String, Object>of(
                "id", a.getId(), "title", a.getTitle(), "views", a.getViewCount() != null ? a.getViewCount() : 0
        )).toList();
        stats.put("topRead", topList);

        return stats;
    }
}
