package com.five.mapper;

import com.five.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OperationLogMapper {
//新增日志
    int insert(OperationLog log);
//日志列表
    List<OperationLog> list(@Param("offset") int offset,
                            @Param("size") int size,
                            @Param("module") String module,
                            @Param("operation") String operation);
//统计日志条数
    long count(@Param("module") String module,
               @Param("operation") String operation);
}
