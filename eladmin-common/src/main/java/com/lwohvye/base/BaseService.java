/*
 *  Copyright 2020-2022 lWoHvYe
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
package com.lwohvye.base;

import com.lwohvye.config.LocalCoreConfig;

/**
 * @author Hongyan Wang
 * @description Service层部分公共方法
 * @date 2021/6/17 5:07 下午
 */
public interface BaseService {

    //接口中可以有静态方法、默认方法、私有方法
    default String getSysName() {
        return LocalCoreConfig.getSysName();
    }
}
