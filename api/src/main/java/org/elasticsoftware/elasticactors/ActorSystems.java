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

package org.elasticsoftware.elasticactors;

import org.elasticsoftware.elasticactors.cluster.RebalancingEventListener;

import javax.annotation.Nullable;

/**
 * Parent class for the local {@link ActorSystem} instance. Can be used to obtain references to remote
 * {@link ActorSystem}s
 *
 * @author Joost van de Wijgerd
 */
public interface ActorSystems {
    /**
     * Retrieve the name of the local ElasticActors cluster
     *
     * @return
     */
    String getClusterName();

    /**
     * Get an {@link ActorSystem} by name. Normally there will only be one ActorSystem per cluster
     *
     * @param actorSystemName   the name of the {@link ActorSystem} or null to get the default
     * @return                  the local {@link ActorSystem} instance
     */
    ActorSystem get(@Nullable String actorSystemName);

    /**
     * Obtain a reference to a remote {@link ActorSystem} instance. Currently only remote ActorSystems that
     * use the same Message Bus as the local ElasticActors cluster instance can be addressed.
     *
     * @param clusterName       the name of the remote cluster
     * @param actorSystemName   the {@link ActorSystem} name in the remote cluster or null to get the default
     * @return                  the remote {@link ActorSystem} instance
     */
    ActorSystem getRemote(String clusterName,@Nullable String actorSystemName);

    /**
     * Register a {@link RebalancingEventListener} that can be used to listen to the rebalancing events (pre and post)
     *
     * @param eventListener
     */
    void registerRebalancingEventListener(RebalancingEventListener eventListener);
}
