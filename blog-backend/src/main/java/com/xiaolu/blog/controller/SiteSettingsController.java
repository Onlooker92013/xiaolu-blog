package com.xiaolu.blog.controller;

import com.xiaolu.blog.common.Result;
import com.xiaolu.blog.entity.SiteSetting;
import com.xiaolu.blog.mapper.SiteSettingMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@io.swagger.v3.oas.annotations.tags.Tag(name = "网站设置")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SiteSettingsController {

    private final SiteSettingMapper siteSettingMapper;

    @Operation(summary = "获取所有网站设置（公开）")
    @GetMapping("/settings")
    public Result<Map<String, String>> getAll() {
        List<SiteSetting> list = siteSettingMapper.selectList(null);
        Map<String, String> map = new HashMap<>();
        for (SiteSetting s : list) {
            map.put(s.getSettingKey(), s.getSettingValue());
        }
        return Result.success(map);
    }

    @Operation(summary = "更新网站设置（管理）")
    @PutMapping("/admin/settings")
    public Result<?> update(@RequestBody Map<String, String> body) {
        for (Map.Entry<String, String> entry : body.entrySet()) {
            SiteSetting setting = siteSettingMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SiteSetting>()
                    .eq(SiteSetting::getSettingKey, entry.getKey())
            );
            if (setting != null) {
                setting.setSettingValue(entry.getValue());
                siteSettingMapper.updateById(setting);
            }
        }
        return Result.success();
    }
}
