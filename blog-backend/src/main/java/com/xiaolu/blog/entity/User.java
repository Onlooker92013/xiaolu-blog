package com.xiaolu.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String avatar;
    private String role;        // ADMIN / USER
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
