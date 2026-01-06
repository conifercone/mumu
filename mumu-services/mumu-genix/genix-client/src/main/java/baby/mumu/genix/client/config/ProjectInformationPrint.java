/*
 * Copyright (c) 2024-2026, the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package baby.mumu.genix.client.config;

import baby.mumu.basis.annotations.Metamodel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

/**
 * 项目信息打印
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
@Metamodel(projectVersion = true, projectName = true)
public class ProjectInformationPrint {

    private final Logger log = LoggerFactory.getLogger(ProjectInformationPrint.class);

    @PostConstruct
    public void run() {
        log.info(":: {} :: {}", ProjectInformationPrintMetamodel.PROJECT_NAME,
            ProjectInformationPrintMetamodel.PROJECT_VERSION);
    }
}
