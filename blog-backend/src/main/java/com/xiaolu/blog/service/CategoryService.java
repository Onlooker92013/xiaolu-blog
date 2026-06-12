package com.xiaolu.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaolu.blog.common.exception.BusinessException;
import com.xiaolu.blog.entity.Category;
import com.xiaolu.blog.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public List<Category> listAll() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getCreatedAt));
    }

    public Category create(Category category) {
        categoryMapper.insert(category);
        return category;
    }

    public Category update(Long id, Category category) {
        Category exist = categoryMapper.selectById(id);
        if (exist == null) throw new BusinessException(404, "分类不存在");
        category.setId(id);
        categoryMapper.updateById(category);
        return category;
    }

    public void delete(Long id) {
        if (categoryMapper.selectById(id) == null) throw new BusinessException(404, "分类不存在");
        categoryMapper.deleteById(id);
    }
}
