/*
 * Copyright 2013 - 2014 The Original Authors
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

package org.elasticsoftware.elasticactors.messaging;

import org.elasticsoftware.elasticactors.ActorRef;
import org.elasticsoftware.elasticactors.serialization.MessageDeliveryMode;
import org.elasticsoftware.elasticactors.serialization.MessageDeserializer;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

/**
 * @author Joost van de Wijgerd
 */
public interface InternalMessage {
    UUID getId();

    ActorRef getSender();

    List<ActorRef> getReceivers();

    ByteBuffer getPayload();

    String getPayloadClass();

    boolean isDurable();

    byte[] toByteArray();

    <T> T getPayload(MessageDeserializer<T> deserializer) throws IOException;

    boolean isUndeliverable();

    InternalMessage copyOf();

    MessageDeliveryMode getDeliveryMode();
}
