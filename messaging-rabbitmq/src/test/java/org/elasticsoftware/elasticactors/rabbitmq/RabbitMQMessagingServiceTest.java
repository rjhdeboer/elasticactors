package org.elasticsoftware.elasticactors.rabbitmq;

import com.google.common.base.Charsets;
import org.elasticsoftware.elasticactors.ActorRef;
import org.elasticsoftware.elasticactors.PhysicalNode;
import org.elasticsoftware.elasticactors.cluster.ActorRefFactory;
import org.elasticsoftware.elasticactors.messaging.*;
import org.elasticsoftware.elasticactors.serialization.internal.ActorRefDeserializer;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Joost van de Wijgerd
 */
public class RabbitMQMessagingServiceTest {
    public final int NUM_PARTITIONS = 64;
    public final int NUM_MESSAGES = 1000;
    public final String CLUSTER_NAME = "test.vdwbv.com";
    public static final String QUEUENAME_FORMAT = "%s_%d";
    public static final String PAYLOAD_FORMAT = "This is message numero %d";
    public final Random random = new Random();
    private ActorRef senderRef;
    private ActorRef receiverRef;

    @BeforeTest(alwaysRun = true)
    public void setUp() {
        senderRef = mock(ActorRef.class);
        receiverRef = mock(ActorRef.class);

        ActorRefFactory actorRefFactory = mock(ActorRefFactory.class);

        when(receiverRef.toString()).thenReturn("actor://test.vdwbv.com/test/shards/1/testReceiver");
        when(senderRef.toString()).thenReturn("actor://test.vdwbv.com/test/shards/1/testSender");

        when(actorRefFactory.create("actor://test.vdwbv.com/test/shards/1/testReceiver")).thenReturn(receiverRef);
        when(actorRefFactory.create("actor://test.vdwbv.com/test/shards/1/testSender")).thenReturn(senderRef);

        // not a very nice construction, but alas
        ActorRefDeserializer.get().setActorRefFactory(actorRefFactory);
    }

    @Test
    public void testAllLocal() throws Exception {
        RabbitMQMessagingService messagingService = new RabbitMQMessagingService(CLUSTER_NAME,"mq001,mq002");
        messagingService.start();

        final CountDownLatch waitLatch = new CountDownLatch(NUM_MESSAGES);

        MessageHandler testHandler = new MessageHandler() {
            @Override
            public PhysicalNode getPhysicalNode() {
                return null;
            }

            @Override
            public void handleMessage(InternalMessage message, MessageHandlerEventListener messageHandlerEventListener) {
                /*byte[] buffer = new byte[message.getPayload().remaining()];
                message.getPayload().get(buffer);
                System.out.println(new String(buffer,Charsets.UTF_8));*/
                messageHandlerEventListener.onDone(message);
                waitLatch.countDown();
            }
        };

        List<MessageQueue> messageQueues = new LinkedList<>();
        // simulate 8 partitions
        MessageQueueFactory localMessageQueueFactory = messagingService.getLocalMessageQueueFactory();
        for (int i = 0; i < NUM_PARTITIONS; i++) {
            messageQueues.add(localMessageQueueFactory.create(String.format(QUEUENAME_FORMAT,CLUSTER_NAME,i),testHandler));
        }

        // send the messages
        for (int i = 0; i < NUM_MESSAGES; i++) {
            // select a random queue
            messageQueues.get(random.nextInt(NUM_PARTITIONS)).offer(createInternalMessage(i+1));
        }

        try {
            waitLatch.await(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore
        }

        for (MessageQueue messageQueue : messageQueues) {
            messageQueue.destroy();
        }

        messagingService.stop();
    }

    private InternalMessage createInternalMessage(int count) {
        ByteBuffer payload = ByteBuffer.wrap(String.format(PAYLOAD_FORMAT, count).getBytes(Charsets.UTF_8));
        return new InternalMessageImpl(senderRef,receiverRef,payload,String.class.getName());
    }
}