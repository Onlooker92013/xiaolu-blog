package com.xiaolu.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaolu.blog.common.exception.BusinessException;
import com.xiaolu.blog.entity.Tag;
import com.xiaolu.blog.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagMapper tagMapper;

    public List<Tag> listAll() {
        return tagMapper.selectList(new LambdaQueryWrapper<Tag>()
                .orderByAsc(Tag::getCreatedAt));
    }

    public Tag create(Tag tag) {
        tagMapper.insert(tag);
        return tag;
    }

    public Tag update(Long id, Tag tag) {
        if (tagMapper.selectById(id) == null) throw new BusinessException(404, "标签不存在");
        tag.setId(id);
        tagMapper.updateById(tag);
        return tag;
    }

    public void delete(Long id) {
        if (tagMapper.selectById(id) == null) throw new BusinessException(404, "标签不存在");
        tagMapper.deleteById(id);
    }
}
