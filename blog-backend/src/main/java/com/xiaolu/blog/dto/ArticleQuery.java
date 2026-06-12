package com.xiaolu.blog.dto;

import lombok.Data;

@Data
public class ArticleQuery {
    private Integer page = 1;
    private Integer pageSize = 10;
    private Long categoryId;
    private Long tagId;
    private String tag;     // tag name for convenience
    private String keyword;
    private String status;
    private String lang;  // zh / en
}
