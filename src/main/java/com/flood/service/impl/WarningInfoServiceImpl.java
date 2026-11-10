package com.flood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flood.common.PageResult;
import com.flood.entity.WarningInfo;
import com.flood.mapper.WarningInfoMapper;
import com.flood.service.WarningInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;

/**
 * 预警信息服务实现类
 */
@Service
public class WarningInfoServiceImpl implements WarningInfoService {

    @Resource
    private WarningInfoMapper warningInfoMapper;

    @Override
    public PageResult<WarningInfo> getWarningPage(int pageNum, int pageSize, String keyword, Integer warningLevel) {
        Page<WarningInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<WarningInfo> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(WarningInfo::getTitle, keyword)
                    .or().like(WarningInfo::getAffectedArea, keyword)
                    .or().like(WarningInfo::getContent, keyword));
        }
        if (warningLevel != null) {
            wrapper.eq(WarningInfo::getWarningLevel, warningLevel);
        }
        wrapper.orderByDesc(WarningInfo::getCreateTime);
        warningInfoMapper.selectPage(page, wrapper);
        return new PageResult<>(page.getTotal(), page.getRecords(), pageNum, pageSize);
    }

    @Override
    public WarningInfo getWarningById(Long id) {
        return warningInfoMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishWarning(WarningInfo warningInfo) {
        warningInfo.setPublishTime(new Date());
        warningInfo.setCreateTime(new Date());
        warningInfo.setUpdateTime(new Date());
        if (warningInfo.getStatus() == null) {
            warningInfo.setStatus(1);
        }
        return warningInfoMapper.insert(warningInfo) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWarning(WarningInfo warningInfo) {
        warningInfo.setUpdateTime(new Date());
        return warningInfoMapper.updateById(warningInfo) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteWarning(Long id) {
        return warningInfoMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWarningStatus(Long id, int status) {
        WarningInfo warning = warningInfoMapper.selectById(id);
        if (warning == null) {
            throw new RuntimeException("预警信息不存在");
        }
        warning.setStatus(status);
        warning.setUpdateTime(new Date());
        return warningInfoMapper.updateById(warning) > 0;
    }
}
