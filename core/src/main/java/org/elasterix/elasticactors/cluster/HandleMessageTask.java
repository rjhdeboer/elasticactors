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

package org.elasterix.elasticactors.cluster;

import com.google.common.cache.Cache;
import org.apache.log4j.Logger;
import org.elasterix.elasticactors.ActorRef;
import org.elasterix.elasticactors.ActorState;
import org.elasterix.elasticactors.ActorSystem;
import org.elasterix.elasticactors.ElasticActor;
import org.elasterix.elasticactors.messaging.InternalMessage;
import org.elasterix.elasticactors.serialization.MessageDeserializer;
import org.elasterix.elasticactors.util.concurrent.ElasticActorRunnable;

/**
 * Task that is responsible for internalMessage deserialization, error handling and state updates
 *
 * @author Joost van de Wijged
 */
public final class HandleMessageTask implements ElasticActorRunnable<String> {
    private static final Logger log = Logger.getLogger(HandleMessageTask.class);
    private final ActorRef receiverRef;
    private final ElasticActor receiver;
    private final ActorSystem actorSystem;
    private final InternalMessage internalMessage;
    private final Cache<String,ActorState> stateCache;

    public HandleMessageTask(ActorSystem actorSystem, ElasticActor receiver, InternalMessage internalMessage, Cache<String, ActorState> stateCache) {
        this.actorSystem = actorSystem;
        this.receiver = receiver;
        this.internalMessage = internalMessage;
        this.stateCache = stateCache;
        this.receiverRef = internalMessage.getReceiver();
    }



    @Override
    public String getKey() {
        return receiverRef.toString();
    }

    @Override
    public void run() {
        try {
            // first see if we can deserialize the actual internalMessage
            MessageDeserializer<Object> deserializer = actorSystem.getDeserializer(internalMessage.getPayloadClass());
            if(deserializer != null) {
                Object message = deserializer.deserialize(internalMessage.getPayload());
                handleMessage(message);
            } else {
                log.error(String.format("No Deserializer found for Message class %s in ActorSystem [%s]",
                                        internalMessage.getPayloadClass().getName(),actorSystem.getName()));
            }
        } catch(Exception e) {
            log.error(String.format("Exception while Deserializing Message class %s in ActorSystem [%s]",
                                    internalMessage.getPayloadClass().getName(),actorSystem.getName()), e);
        }
    }

    private void handleMessage(Object message) {
        // setup the state
        final ActorState stateBefore = stateCache.getIfPresent(receiverRef.toString());
        ActorStateContext.setState(stateBefore);
        try {
            receiver.onMessage(message,internalMessage.getSender());
        } catch(Exception e) {
            // @todo: handle by sending back a message (if possible)
            log.error(String.format("Exception while handling message for actor [%s]",receiverRef.toString()),e);
        } finally {
            // clear the state
            ActorState stateAfter = ActorStateContext.getAndClearState();
            // check if we have state now that needs to be put in the cache
            if(stateBefore == null && stateAfter != null) {
                stateCache.put(receiverRef.toString(),stateAfter);
            }
            // flush state if it was changed
            if(stateAfter != null) {
                // @todo: implement persistent state
            }
        }
    }
}