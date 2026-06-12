package com.xiaolu.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleDTO {
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题最多200字")
    private String title;

    @Size(max = 200, message = "英文标题最多200字")
    private String titleEn;

    private String content;
    private String contentEn;

    @Size(max = 500, message = "摘要最多500字")
    private String summary;

    @Size(max = 500, message = "英文摘要最多500字")
    private String summaryEn;

    private String coverImage;
    private Long categoryId;
    private String status;
    private Integer isTop;
    private java.util.List<Long> tagIds;
}
