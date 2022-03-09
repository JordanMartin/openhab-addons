/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.thekeys.internal.discovery;

import static org.openhab.binding.thekeys.internal.TheKeysBindingConstants.BINDING_ID;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.thekeys.internal.TheKeysBindingConstants;
import org.openhab.binding.thekeys.internal.api.Locker;
import org.openhab.binding.thekeys.internal.gateway.TheKeysGatewayHandler;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Discovery service which uses TheKeys API to find all lock connected.
 *
 * @author Jordan Martin - Initial contribution
 */
@Component(service = DiscoveryService.class, immediate = true, configurationPid = "discovery." + BINDING_ID)
public class TheKeysDiscoveryService extends AbstractDiscoveryService implements ThingHandlerService {

    private final Logger logger = LoggerFactory.getLogger(TheKeysDiscoveryService.class);

    @Nullable
    private TheKeysGatewayHandler gatewayHandler;

    public TheKeysDiscoveryService() {
        super(Set.of(TheKeysBindingConstants.THING_TYPE_SMARTLOCK), 5, false);
    }

    @Override
    protected void startScan() {
        if (gatewayHandler == null) {
            return;
        }

        for (Locker lock : getLocks()) {
            DiscoveryResult smartLock = DiscoveryResultBuilder.create(getUid(lock.getIdentifier(), gatewayHandler))
                    .withBridge(gatewayHandler.getThing().getUID())
                    .withLabel("TheKeys Smartlock " + lock.getIdentifier())
                    .withRepresentationProperty(TheKeysBindingConstants.CONF_SMARTLOCK_LOCKID)
                    .withProperty(TheKeysBindingConstants.CONF_SMARTLOCK_LOCKID, lock.getIdentifier()).build();
            thingDiscovered(smartLock);
        }
    }

    private List<Locker> getLocks() {
        try {
            return gatewayHandler.getGatewayApi().getLocks();
        } catch (IOException e) {
            logger.warn("Cannot start TheKeys discovery : {}", e.getMessage(), e);
            return List.of();
        }
    }

    private ThingUID getUid(int lockId, TheKeysGatewayHandler bridgeHandler) {
        return new ThingUID(TheKeysBindingConstants.THING_TYPE_SMARTLOCK, bridgeHandler.getThing().getUID(),
                String.valueOf(lockId));
    }

    @Override
    protected synchronized void stopScan() {
        super.stopScan();
        removeOlderResults(getTimestampOfLastScan());
    }

    @Override
    public void setThingHandler(@Nullable ThingHandler handler) {
        if (handler instanceof TheKeysGatewayHandler) {
            gatewayHandler = (TheKeysGatewayHandler) handler;
        }
    }

    @Override
    public @Nullable ThingHandler getThingHandler() {
        return gatewayHandler;
    }

    @Override
    public void deactivate() {
    }
}
