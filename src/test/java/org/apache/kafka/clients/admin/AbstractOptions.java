/*
 * Copyright (c) 2023 VMware Inc. or its affiliates, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kafka.clients.admin;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractOptions<T extends AbstractOptions> {
    protected Integer timeoutMs = null;

    public T timeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
        return (T) this;
    }

    public Integer timeoutMs() {
        return this.timeoutMs;
    }
}
