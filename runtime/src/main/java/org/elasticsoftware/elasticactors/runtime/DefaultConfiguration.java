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

package org.elasticsoftware.elasticactors.runtime;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.elasticsoftware.elasticactors.ElasticActor;
import org.elasticsoftware.elasticactors.InternalActorSystemConfiguration;
import org.elasticsoftware.elasticactors.ServiceActor;
import org.elasticsoftware.elasticactors.serialization.MessageDeliveryMode;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.*;

import static org.elasticsoftware.elasticactors.serialization.MessageDeliveryMode.STRICT_ORDER;
import static org.elasticsoftware.elasticactors.serialization.MessageDeliveryMode.SYSTEM_DEFAULT;

/**
 * @author Joost van de Wijgerd
 */
public final class DefaultConfiguration implements InternalActorSystemConfiguration, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private final String name;
    private final int numberOfShards;
    private final List<DefaultRemoteConfiguration> remoteConfigurations;
    private final Map<String,Object> properties = new LinkedHashMap<>();
    private final ConversionService conversionService = new DefaultConversionService();
    private final Map<String,ElasticActor> serviceActors = new HashMap<>();
    private final MessageDeliveryMode defaultMessageDeliveryMode;

    @JsonCreator
    public DefaultConfiguration(@JsonProperty("name") String name,
                                @JsonProperty("shards") int numberOfShards,
                                @JsonProperty("remoteActorSystems") List<DefaultRemoteConfiguration> remoteConfigurations,
                                @JsonProperty("defaultMessageDeliveryMode") MessageDeliveryMode defaultMessageDeliveryMode) {
        this.name = name;
        this.numberOfShards = numberOfShards;
        this.defaultMessageDeliveryMode = (defaultMessageDeliveryMode == null || defaultMessageDeliveryMode == SYSTEM_DEFAULT) ? STRICT_ORDER : defaultMessageDeliveryMode;
        this.remoteConfigurations = (remoteConfigurations != null) ? remoteConfigurations : Collections.<DefaultRemoteConfiguration>emptyList();
    }

    @JsonProperty("name")
    @Override
    public String getName() {
        return name;
    }

    @JsonProperty("shards")
    @Override
    public int getNumberOfShards() {
        return numberOfShards;
    }

    @JsonAnySetter
    public void setProperty(String name,Object value) {
        this.properties.put(name,value);
    }

    @Override
    public String getVersion() {
        // @todo: fix this
        return "1.0.0";
    }

    @Override
    public ElasticActor<?> getService(final String serviceId) {
        // cache it locally as the lookup in the application context is really slow
        ElasticActor serviceActor = this.serviceActors.get(serviceId);
        if(serviceActor == null) {
            serviceActor = applicationContext.getBean(serviceId, ElasticActor.class);
            this.serviceActors.put(serviceId, serviceActor);
        }
        return serviceActor;
    }

    @Override
    public Set<String> getServices() {
        return applicationContext.getBeansWithAnnotation(ServiceActor.class).keySet();
    }

    @Override
    public <T> T getProperty(Class component, String key, Class<T> targetType) {
        Map<String,Object> componentProperties = (Map<String, Object>) properties.get(generateComponentName(component));
        if(componentProperties != null) {
            Object value = componentProperties.get(key);
            if(value != null) {
                if(conversionService.canConvert(value.getClass(),targetType)) {
                    return conversionService.convert(value,targetType);
                }
            }
        }
        return null;
    }

    @Override
    public <T> T getProperty(Class component, String key, Class<T> targetType, T defaultValue) {
        T value = getProperty(component,key,targetType);
        return (value != null) ? value : defaultValue;
    }

    @Override
    public <T> T getRequiredProperty(Class component, String key, Class<T> targetType) throws IllegalStateException {
        T value = getProperty(component,key,targetType);
        if (value == null) {
            throw new IllegalStateException(String.format("required key [%s] not found for component[%s]", key, component.getName()));
        }
        return value;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Strip out the first two packages from {@link Class#getName()}
     *
     * @param component
     * @return
     */
    private String generateComponentName(Class component) {
        String componentName = component.getName();
        int idx = componentName.indexOf('.',componentName.indexOf('.')+1);
        if(idx != -1) {
            componentName = componentName.substring(idx+1);
        }
        return componentName;
    }

    @JsonProperty("remoteActorSystems")
    @Override
    public List<DefaultRemoteConfiguration> getRemoteConfigurations() {
        return remoteConfigurations;
    }

    @JsonProperty("defaultMessageDeliveryMode")
    @Override
    public MessageDeliveryMode getMessageDeliveryMode() {
        return defaultMessageDeliveryMode;
    }
}
