package com.xiaolu.blog.controller;

import com.xiaolu.blog.common.Result;
import com.xiaolu.blog.entity.User;
import com.xiaolu.blog.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@io.swagger.v3.oas.annotations.tags.Tag(name = "用户")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "获取个人信息")
    @GetMapping("/profile")
    public Result<User> profile(@RequestAttribute Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) user.setPassword(null); // 不返回密码
        return Result.success(user);
    }

    @Operation(summary = "更新个人信息")
    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestAttribute Long userId, @RequestBody User form) {
        User user = userMapper.selectById(userId);
        if (user == null) return Result.error(404, "用户不存在");
        if (form.getEmail() != null) user.setEmail(form.getEmail());
        if (form.getAvatar() != null) user.setAvatar(form.getAvatar());
        userMapper.updateById(user);
        return Result.success();
    }

    @Operation(summary = "修改密码")
    @PostMapping("/change-password")
    public Result<?> changePassword(@RequestAttribute Long userId, @RequestBody Map<String, String> body) {
        String oldPwd = body.get("oldPassword");
        String newPwd = body.get("newPassword");

        if (oldPwd == null || newPwd == null || newPwd.length() < 6) {
            return Result.error(400, "密码不能为空且新密码至少6位");
        }

        User user = userMapper.selectById(userId);
        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            return Result.error(400, "原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPwd));
        userMapper.updateById(user);
        return Result.success();
    }
}
