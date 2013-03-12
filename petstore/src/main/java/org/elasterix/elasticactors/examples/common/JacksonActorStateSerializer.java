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

package org.elasterix.elasticactors.examples.common;

import org.codehaus.jackson.map.ObjectMapper;
import org.elasterix.elasticactors.ActorState;
import org.elasterix.elasticactors.serialization.Serializer;

import java.io.IOException;

/**
 *
 */
public final class JacksonActorStateSerializer implements Serializer<ActorState,byte[]> {
    private final ObjectMapper objectMapper;

    public JacksonActorStateSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(ActorState object) throws IOException {
        return objectMapper.writeValueAsBytes(object.getAsMap());
    }
}
