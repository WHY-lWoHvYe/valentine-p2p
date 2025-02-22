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
package com.lwohvye.sys.modules.system.service;

import com.lwohvye.core.base.BaseService;
import com.lwohvye.api.modules.system.service.dto.DeptDto;
import com.lwohvye.api.modules.system.domain.Dept;
import com.lwohvye.api.modules.system.service.dto.DeptQueryCriteria;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2019-03-25
 */
public interface IDeptService extends BaseService {

    /**
     * 查询所有数据
     *
     * @param currentUserId
     * @param criteria      条件
     * @param isQuery       /
     * @return /
     * @throws Exception /
     */
    List<DeptDto> queryAll(Long currentUserId, DeptQueryCriteria criteria, Boolean isQuery) throws Exception;

    /**
     * 根据ID查询
     *
     * @param id /
     * @return /
     */
    DeptDto findById(Long id);

    /**
     * 创建
     *
     * @param resources /
     */
    void create(Dept resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void update(Dept resources);

    /**
     * 删除
     *
     * @param deptDtos /
     */
    void delete(Set<DeptDto> deptDtos);

    /**
     * 根据PID查询
     *
     * @param pid /
     * @return /
     */
    List<Dept> findByPid(long pid);

    /**
     * 导出数据
     *
     * @param queryAll 待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download(List<DeptDto> queryAll, HttpServletResponse response) throws IOException;

    /**
     * 获取待删除的部门
     *
     * @param deptList /
     * @param deptDtos /
     * @return /
     */
    Set<DeptDto> getDeleteDepts(List<Dept> deptList, Set<DeptDto> deptDtos);

    /**
     * fetch all subChild by Pid
     *
     * @param pid givenPid
     * @return java.util.List
     * @date 2023/7/9 8:18 AM
     */
    List<Long> fetchDeptChildByPid(Long pid);

    /**
     * 根据ID获取同级与上级数据
     *
     * @param deptDto /
     * @param depts   /
     * @return /
     */
    List<DeptDto> getSuperior(DeptDto deptDto, List<Dept> depts);

    /**
     * 构建树形数据
     *
     * @param deptDtos /
     * @return /
     */
    Map<String, Object> buildTree(List<DeptDto> deptDtos);

}
