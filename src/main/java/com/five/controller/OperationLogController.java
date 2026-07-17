package com.five.controller;

import com.five.entity.OperationLog;
import com.five.mapper.OperationLogMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/admin", "/api/admin"})
@PreAuthorize("hasRole('ADMIN')") //定义管理员权限，只有管理员才可以访问
public class OperationLogController {
    //操作日志管理，为管理元提供日志查询和审计功能，配合operatiocontroller 构成完整的操作日志记录和查询闭环
    private final OperationLogMapper operationLogMapper;

    public OperationLogController(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @GetMapping("/logs")
    public Map<String, Object> listLogs(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "15") int size,
                                        @RequestParam(required = false) String module,
                                        @RequestParam(required = false) String operation) {
        int offset = (page - 1) * size;
        List<OperationLog> list = operationLogMapper.list(offset, size, module, operation);
        long total = operationLogMapper.count(module, operation);
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("total", total);
        map.put("page", page);
        map.put("size", size);
        return map;
    }
}
