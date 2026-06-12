package com.xiaolu.blog.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaolu.blog.common.PageResult;
import com.xiaolu.blog.common.exception.BusinessException;
import com.xiaolu.blog.dto.ArticleDTO;
import com.xiaolu.blog.dto.ArticleQuery;
import com.xiaolu.blog.dto.ArticleVO;
import com.xiaolu.blog.entity.Article;
import com.xiaolu.blog.entity.Category;
import com.xiaolu.blog.entity.Tag;
import com.xiaolu.blog.entity.User;
import com.xiaolu.blog.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;
    private final UserMapper userMapper;

    public PageResult<ArticleVO> page(ArticleQuery query) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, "PUBLISHED")
                .orderByDesc(Article::getIsTop)
                .orderByDesc(Article::getCreatedAt);

        if (query.getCategoryId() != null) {
            wrapper.eq(Article::getCategoryId, query.getCategoryId());
        }
        // Resolve tag name to ID if provided
        Long filterTagId = query.getTagId();
        if (filterTagId == null && query.getTag() != null && !query.getTag().isBlank()) {
            Tag tagByName = tagMapper.selectOne(new LambdaQueryWrapper<Tag>().eq(Tag::getName, query.getTag()));
            if (tagByName != null) filterTagId = tagByName.getId();
        }
        if (filterTagId != null) {
            List<Long> articleIds = articleTagMapper.selectList(
                new LambdaQueryWrapper<ArticleTagMapper.ArticleTag>()
                    .eq(ArticleTagMapper.ArticleTag::getTagId, filterTagId)
            ).stream().map(ArticleTagMapper.ArticleTag::getArticleId).toList();
            if (articleIds.isEmpty()) {
                return PageResult.of(0L, (long) query.getPage(), (long) query.getPageSize(), List.of());
            }
            wrapper.in(Article::getId, articleIds);
        }
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.and(w -> w.like(Article::getTitle, query.getKeyword())
                    .or().like(Article::getTitleEn, query.getKeyword())
                    .or().like(Article::getSummary, query.getKeyword()));
        }

        Page<Article> page = new Page<>(query.getPage(), query.getPageSize());
        Page<Article> result = articleMapper.selectPage(page, wrapper);

        List<ArticleVO> vos = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), vos);
    }

    public ArticleVO getById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null || article.getDeleted() == 1) {
            throw new BusinessException(404, "文章不存在");
        }
        // 阅读量+1
        article.setViewCount(article.getViewCount() + 1);
        articleMapper.updateById(article);
        return toVO(article);
    }

    public List<ArticleVO> listTop() {
        List<Article> articles = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, "PUBLISHED")
                .eq(Article::getIsTop, 1)
                .orderByDesc(Article::getCreatedAt)
                .last("LIMIT 5"));
        return articles.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Transactional
    public ArticleVO create(ArticleDTO dto, Long authorId) {
        Article article = new Article();
        BeanUtil.copyProperties(dto, article);
        article.setAuthorId(authorId);
        article.setViewCount(0L);
        articleMapper.insert(article);
        // 保存标签关联
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            articleTagMapper.insertBatch(article.getId(), dto.getTagIds());
        }
        return toVO(article);
    }

    @Transactional
    public ArticleVO update(Long id, ArticleDTO dto) {
        Article article = articleMapper.selectById(id);
        if (article == null) throw new BusinessException(404, "文章不存在");
        BeanUtil.copyProperties(dto, article, "id", "authorId", "viewCount");
        articleMapper.updateById(article);
        // 更新标签关联
        articleTagMapper.deleteByArticleId(id);
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            articleTagMapper.insertBatch(id, dto.getTagIds());
        }
        return toVO(article);
    }

    @Transactional
    public void delete(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) throw new BusinessException(404, "文章不存在");
        articleTagMapper.deleteByArticleId(id);
        articleMapper.deleteById(id);
    }

    public PageResult<ArticleVO> adminPage(ArticleQuery query) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .orderByDesc(Article::getCreatedAt);
        if (query.getStatus() != null) {
            wrapper.eq(Article::getStatus, query.getStatus());
        }
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.like(Article::getTitle, query.getKeyword());
        }
        Page<Article> page = new Page<>(query.getPage(), query.getPageSize());
        Page<Article> result = articleMapper.selectPage(page, wrapper);
        List<ArticleVO> vos = result.getRecords().stream()
                .map(this::toVO).collect(Collectors.toList());
        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), vos);
    }

    public List<ArticleVO> listRelated(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) return List.of();

        // 基于相同标签推荐文章
        List<Tag> tags = tagMapper.selectByArticleId(articleId);
        if (tags.isEmpty()) {
            // 无标签时返回最新文章
            List<Article> latest = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                    .eq(Article::getStatus, "PUBLISHED").eq(Article::getDeleted, 0)
                    .ne(Article::getId, articleId)
                    .orderByDesc(Article::getCreatedAt).last("LIMIT 4"));
            return latest.stream().map(this::toVO).collect(Collectors.toList());
        }

        // 查找有共同标签的文章
        List<Long> tagIds = tags.stream().map(Tag::getId).toList();
        List<Long> relatedIds = articleTagMapper.selectList(null).stream()
                .filter(at -> tagIds.contains(at.getTagId()) && !at.getArticleId().equals(articleId))
                .map(ArticleTagMapper.ArticleTag::getArticleId)
                .distinct().limit(4).toList();

        if (relatedIds.isEmpty()) return List.of();

        List<Article> related = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .in(Article::getId, relatedIds)
                .eq(Article::getStatus, "PUBLISHED").eq(Article::getDeleted, 0)
                .orderByDesc(Article::getCreatedAt));
        return related.stream().map(this::toVO).collect(Collectors.toList());
    }

    public Map<String, Object> getNavigation(Long articleId) {
        // 上一篇：同分类下创建时间比当前早的最新1篇
        Article current = articleMapper.selectById(articleId);
        if (current == null) return Map.of();

        ArticleVO prev = null, next = null;

        Article prevArticle = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
                .eq(Article::getDeleted, 0).eq(Article::getStatus, "PUBLISHED")
                .lt(Article::getCreatedAt, current.getCreatedAt())
                .orderByDesc(Article::getCreatedAt).last("LIMIT 1"));
        if (prevArticle != null) prev = toVO(prevArticle);

        Article nextArticle = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
                .eq(Article::getDeleted, 0).eq(Article::getStatus, "PUBLISHED")
                .gt(Article::getCreatedAt, current.getCreatedAt())
                .orderByAsc(Article::getCreatedAt).last("LIMIT 1"));
        if (nextArticle != null) next = toVO(nextArticle);

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("prev", prev);
        result.put("next", next);
        return result;
    }

    public List<Map<String, Object>> getTopRead(int limit) {
        List<Article> articles = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getDeleted, 0).eq(Article::getStatus, "PUBLISHED")
                .orderByDesc(Article::getViewCount).last("LIMIT " + limit));
        return articles.stream().map(a -> Map.<String, Object>of(
                "id", a.getId(), "title", a.getTitle(), "views", a.getViewCount() != null ? a.getViewCount() : 0
        )).toList();
    }

    private ArticleVO toVO(Article article) {
        ArticleVO vo = new ArticleVO();
        BeanUtil.copyProperties(article, vo);
        vo.setCreatedAt(article.getCreatedAt() != null ? article.getCreatedAt().toString() : null);
        vo.setUpdatedAt(article.getUpdatedAt() != null ? article.getUpdatedAt().toString() : null);
        // 分类名
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            vo.setCategoryName(category != null ? category.getName() : null);
        }
        // 作者名
        if (article.getAuthorId() != null) {
            User user = userMapper.selectById(article.getAuthorId());
            vo.setAuthorName(user != null ? user.getUsername() : null);
        }
        // 标签
        List<Tag> tags = tagMapper.selectByArticleId(article.getId());
        vo.setTags(tags.stream().map(Tag::getName).collect(Collectors.toList()));
        return vo;
    }
}
