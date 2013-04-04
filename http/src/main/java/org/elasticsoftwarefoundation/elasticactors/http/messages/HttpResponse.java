/*
 * Copyright 2013 Joost van de Wijgerd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elasticsoftwarefoundation.elasticactors.http.messages;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jboss.netty.handler.codec.http.HttpHeaders;

import java.util.List;
import java.util.Map;

/**
 * @author Joost van de Wijgerd
 */
public final class HttpResponse extends HttpMessage {
    private final int statusCode;

    @JsonCreator
    public HttpResponse(@JsonProperty("statusCode") int statusCode,
                        @JsonProperty("headers") Map<String,List<String>> headers,
                        @JsonProperty("content") byte[] content) {
        super(headers, content);
        this.statusCode = statusCode;
    }

    @JsonProperty("statusCode")
    public int getStatusCode() {
        return statusCode;
    }

}
