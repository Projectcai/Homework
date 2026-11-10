package com.flood.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flood.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 站内通知Mapper (K)
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    @Update("UPDATE biz_notification SET status = 1 WHERE user_id = #{userId} AND status = 0")
    int markReadByUserId(Long userId);
}
