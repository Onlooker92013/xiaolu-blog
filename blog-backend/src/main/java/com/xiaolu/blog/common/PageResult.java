package com.xiaolu.blog.common;

import lombok.Data;

@Data
public class PageResult<T> {
    private Long total;
    private Long page;
    private Long pageSize;
    private java.util.List<T> list;

    public static <T> PageResult<T> of(Long total, Long page, Long pageSize, java.util.List<T> list) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setList(list);
        return result;
    }
}
