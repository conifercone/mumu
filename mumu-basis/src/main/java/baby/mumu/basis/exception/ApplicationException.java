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

package baby.mumu.basis.exception;

import baby.mumu.basis.response.ResponseCode;
import lombok.Data;
import org.jspecify.annotations.NonNull;

import java.io.Serial;

/**
 * 顶级异常
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.15.0
 */
@Data
public class ApplicationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5646742276995362775L;

    private final ResponseCode responseCode;

    private final transient Object data;

    private final transient Throwable throwable;

    public ApplicationException(@NonNull ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
        this.data = null;
        this.throwable = null;
    }

    public ApplicationException(@NonNull ResponseCode responseCode, Throwable throwable) {
        super(throwable);
        this.responseCode = responseCode;
        this.data = null;
        this.throwable = throwable;
    }

    public ApplicationException(@NonNull ResponseCode responseCode, Object data) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
        this.data = data;
        this.throwable = null;
    }
}
