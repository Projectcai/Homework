package com.flood.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果类
 */
@Data
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    /** 总记录数 */
    private Long total;

    /** 当前页数据列表 */
    private List<T> rows;

    /** 当前页码 */
    private Integer pageNum;

    /** 每页条数 */
    private Integer pageSize;

    /**
     * 全参构造方法
     */
    public PageResult(Long total, List<T> rows, Integer pageNum, Integer pageSize) {
        this.total = total;
        this.rows = rows;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    /**
     * 两参构造方法
     */
    public PageResult(Long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> of(Long total, List<T> rows) {
        return new PageResult<>(total, rows);
    }
}
