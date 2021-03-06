/*
 * Copyright 2013 - 2017 The Original Authors
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

package org.elasticsoftware.elasticactors.test.messaging;

import org.elasticsoftware.elasticactors.PhysicalNode;
import org.elasticsoftware.elasticactors.messaging.MessageHandler;
import org.elasticsoftware.elasticactors.messaging.MessageQueue;
import org.elasticsoftware.elasticactors.messaging.MessageQueueFactory;
import org.elasticsoftware.elasticactors.messaging.MessagingService;
import org.elasticsoftware.elasticactors.util.concurrent.ThreadBoundExecutor;

import java.io.IOException;

/**
 * @author Joost van de Wijgerd
 */
public final class TestMessagingService implements MessagingService, MessageQueueFactory {
    private final ThreadBoundExecutor queueExecutor;

    public TestMessagingService(ThreadBoundExecutor queueExecutor) {
        this.queueExecutor = queueExecutor;
    }

    @Override
    public void sendWireMessage(String queueName, byte[] serializedMessage, PhysicalNode receiver) throws IOException {
        // not used, there is no remote connection
    }

    @Override
    public MessageQueue create(String name, MessageHandler messageHandler) throws Exception {
        return new LocalMessageQueue(queueExecutor,name,messageHandler);
    }
}
