package com.xiaolu.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaolu.blog.common.exception.BusinessException;
import com.xiaolu.blog.entity.FriendLink;
import com.xiaolu.blog.mapper.FriendLinkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendLinkService {

    private final FriendLinkMapper friendLinkMapper;

    public List<FriendLink> listApproved() {
        return friendLinkMapper.selectList(new LambdaQueryWrapper<FriendLink>()
                .eq(FriendLink::getStatus, "APPROVED")
                .orderByAsc(FriendLink::getCreatedAt));
    }

    public void apply(FriendLink link) {
        link.setStatus("PENDING");
        friendLinkMapper.insert(link);
    }

    public void approve(Long id) {
        FriendLink link = friendLinkMapper.selectById(id);
        if (link == null) throw new BusinessException(404, "友链不存在");
        link.setStatus("APPROVED");
        friendLinkMapper.updateById(link);
    }

    public void delete(Long id) {
        friendLinkMapper.deleteById(id);
    }

    public List<FriendLink> listAll() {
        return friendLinkMapper.selectList(new LambdaQueryWrapper<FriendLink>()
                .orderByDesc(FriendLink::getCreatedAt));
    }
}
