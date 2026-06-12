package com.xiaolu.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("friend_links")
public class FriendLink {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String url;
    private String avatar;
    private String description;
    private String status;      // PENDING / APPROVED
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
