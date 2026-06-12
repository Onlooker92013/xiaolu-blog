package com.xiaolu.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("articles")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String titleEn;         // 英文标题
    private String content;
    private String contentEn;       // 英文内容
    private String summary;
    private String summaryEn;       // 英文摘要
    private String coverImage;
    private Long categoryId;
    private Long authorId;
    private String status;          // DRAFT / PUBLISHED
    private Long viewCount;
    private Integer isTop;          // 0=普通 1=置顶
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
