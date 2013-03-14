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

import java.util.Map;

/**
 * @author Joost van de Wijgerd
 */
public final class JacksonActorState implements ActorState {
    private final ObjectMapper objectMapper;
    private final Map<String, Object> stateMap;
    private volatile Object stateObject;

    public JacksonActorState(ObjectMapper objectMapper, Map<String, Object> stateMap) {
        this.objectMapper = objectMapper;
        this.stateMap = stateMap;
    }

    public JacksonActorState(ObjectMapper objectMapper, Object stateObject) {
        this.objectMapper = objectMapper;
        this.stateObject = stateObject;
        this.stateMap = null;
    }

    // @todo: this setup is a bit dangerous and we might loose state updates when used the wrong way

    @Override
    public Map<String, Object> getAsMap() {
        if(stateObject != null) {
            return objectMapper.convertValue(stateObject,JacksonActorStateDeserializer.MAP_TYPE);
        }
        return stateMap;
    }

    @Override
    public <T> T getAsObject(Class<T> objectClass) {
        if (stateObject == null) {
            stateObject = objectMapper.convertValue(stateMap, objectClass);
        }
        return (T) stateObject;
    }
}
