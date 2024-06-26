/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.lwohvye.log.rest;

import cn.hutool.core.lang.Dict;
import com.lwohvye.core.annotation.RespResultBody;
import com.lwohvye.core.annotation.log.OprLog;
import com.lwohvye.core.utils.SecurityUtils;
import com.lwohvye.core.utils.result.ResultInfo;
import com.lwohvye.log.service.IBzLogService;
import com.lwohvye.log.service.dto.BzLogQueryCriteria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author Zheng Jie
 * @date 2018-11-24
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
@Tag(name = "LogController", description = "系统：日志管理")
public class LogController {

    private final IBzLogService bzLogService;

    @Operation(summary = "导出数据")
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response, BzLogQueryCriteria criteria) throws IOException {
        criteria.setLogType("INFO");
        bzLogService.download(bzLogService.queryAll(criteria), response);
    }

    @Operation(summary = "导出错误数据")
    @GetMapping(value = "/error/download")
    public void downloadErrorLog(HttpServletResponse response, BzLogQueryCriteria criteria) throws IOException {
        criteria.setLogType("ERROR");
        bzLogService.download(bzLogService.queryAll(criteria), response);
    }

    @GetMapping
    @RespResultBody
    @Operation(summary = "日志查询")
    public Map<String, Object> query(BzLogQueryCriteria criteria, Pageable pageable) {
        criteria.setLogType("INFO");
        return bzLogService.queryAll(criteria, pageable);
    }

    @GetMapping(value = "/user")
    @RespResultBody
    @Operation(summary = "用户日志查询")
    public Map<String, Object> queryUserLog(BzLogQueryCriteria criteria, Pageable pageable) {
        criteria.setLogType("INFO");
        criteria.setBlurry(SecurityUtils.getCurrentUsername());
        return bzLogService.queryAllByUser(criteria, pageable);
    }

    @GetMapping(value = "/error")
    @RespResultBody
    @Operation(summary = "错误日志查询")
    public Map<String, Object> queryErrorLog(BzLogQueryCriteria criteria, Pageable pageable) {
        criteria.setLogType("ERROR");
        return bzLogService.queryAll(criteria, pageable);
    }

    @GetMapping(value = "/error/{id}")
    @RespResultBody
    @Operation(summary = "日志异常详情查询")
    public Dict queryErrorLogs(@PathVariable Long id) {
        return bzLogService.findByErrDetail(id);
    }

    @DeleteMapping(value = "/del/error")
    @OprLog("删除所有ERROR日志")
    @Operation(summary = "删除所有ERROR日志")
    public ResultInfo<String> delAllErrorLog() {
        bzLogService.delAllByError();
        return ResultInfo.success();
    }

    @DeleteMapping(value = "/del/info")
    @OprLog("删除所有INFO日志")
    @Operation(summary = "删除所有INFO日志")
    public ResultInfo<String> delAllInfoLog() {
        bzLogService.delAllByInfo();
        return ResultInfo.success();
    }
}
