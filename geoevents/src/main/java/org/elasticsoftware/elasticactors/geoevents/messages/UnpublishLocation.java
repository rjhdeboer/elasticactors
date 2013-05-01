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

package org.elasticsoftware.elasticactors.geoevents.messages;

import org.elasterix.elasticactors.ActorRef;
import org.elasticsoftware.elasticactors.geoevents.Coordinate;

/**
 * @author Joost van de Wijgerd
 */
public final class UnpublishLocation {
    private final ActorRef ref;
    private final Coordinate location;

    public UnpublishLocation(ActorRef ref, Coordinate location) {
        this.ref = ref;
        this.location = location;
    }
}