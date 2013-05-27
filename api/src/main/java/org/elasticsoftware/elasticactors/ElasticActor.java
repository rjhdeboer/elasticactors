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

package org.elasticsoftware.elasticactors;

/**
 * @author Joost van de Wijgerd
 */
public interface ElasticActor<T> {
    void postCreate(ActorRef creator) throws Exception;

    void postActivate(String previousVersion) throws Exception;

    void onReceive(ActorRef sender, T message) throws Exception;

    void onUndeliverable(ActorRef receiver, Object message) throws Exception;

    void prePassivate() throws Exception;

    void preDestroy(ActorRef destroyer) throws Exception;
}