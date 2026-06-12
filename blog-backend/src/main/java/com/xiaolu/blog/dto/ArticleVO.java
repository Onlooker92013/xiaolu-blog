package com.xiaolu.blog.dto;

import lombok.Data;
import java.util.List;

@Data
public class ArticleVO {
    private Long id;
    private String title;
    private String titleEn;
    private String content;
    private String contentEn;
    private String summary;
    private String summaryEn;
    private String coverImage;
    private Long categoryId;
    private String categoryName;
    private Long authorId;
    private String authorName;
    private String status;
    private Long viewCount;
    private Integer isTop;
    private List<String> tags;
    private String createdAt;
    private String updatedAt;
}
