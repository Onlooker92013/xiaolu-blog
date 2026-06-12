package com.xiaolu.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTagMapper.ArticleTag> {

    @Insert("<script>" +
            "INSERT INTO article_tags (article_id, tag_id) VALUES " +
            "<foreach collection='tagIds' item='tagId' separator=','>" +
            "(#{articleId}, #{tagId})" +
            "</foreach>" +
            "</script>")
    void insertBatch(@Param("articleId") Long articleId, @Param("tagIds") List<Long> tagIds);

    @Delete("DELETE FROM article_tags WHERE article_id = #{articleId}")
    void deleteByArticleId(@Param("articleId") Long articleId);

    @com.baomidou.mybatisplus.annotation.TableName("article_tags")
    @lombok.Data
    class ArticleTag {
        private Long articleId;
        private Long tagId;
    }
}
