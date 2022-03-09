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
package org.openhab.binding.thekeys.internal.gateway;

import static org.openhab.binding.thekeys.internal.TheKeysBindingConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.thekeys.internal.api.GatewayInfo;
import org.openhab.binding.thekeys.internal.api.GatewayApi;
import org.openhab.binding.thekeys.internal.discovery.TheKeysDiscoveryService;
import org.openhab.binding.thekeys.internal.smartlock.TheKeysSmartlockConfiguration;
import org.openhab.binding.thekeys.internal.smartlock.TheKeysSmartlockHandler;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.*;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The {@link TheKeysGatewayHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Jordan Martin - Initial contribution
 */
public class TheKeysGatewayHandler extends BaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(TheKeysGatewayHandler.class);
    private @Nullable GatewayApi gatewayApi;
    private @Nullable ScheduledFuture<?> gwPollingJob;

    public TheKeysGatewayHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void initialize() {
        TheKeysGatewayConfiguration config = getConfigAs(TheKeysGatewayConfiguration.class);
        gatewayApi = new GatewayApi(config);
        updateStatus(ThingStatus.UNKNOWN);
        gwPollingJob = scheduler.scheduleAtFixedRate(this::fetchBridgeData, 0, config.refreshInterval,
                TimeUnit.SECONDS);
    }

    @Nullable
    private Thing getLockThing(int lockId) {
        return getThing().getThings().stream().filter(thing -> {
            var config = thing.getConfiguration().as(TheKeysSmartlockConfiguration.class);
            return lockId == config.lockId;
        }).findFirst().orElse(null);
    }

    private void fetchBridgeData() {
        try {
            gatewayApi.getLocks().forEach(lock -> {
                Thing lockThing = getLockThing(lock.getIdentifier());
                if (lockThing != null && lockThing.getHandler() != null
                        && lockThing.getStatus() == ThingStatus.ONLINE) {
                    TheKeysSmartlockHandler lockHandler = (TheKeysSmartlockHandler) lockThing.getHandler();
                    lockHandler.updateLastLog(lock.getLastLog());
                }
            });

            GatewayInfo gwInfos = gatewayApi.getGwInfos();
            updateState(CHANNEL_VERSION, new DecimalType(gwInfos.getVersion()));
            updateState(CHANNEL_STATUS, new StringType(gwInfos.getCurrentStatus()));

            updateProperty(PROPERTY_VERSION, String.valueOf(gwInfos.getVersion()));
            updateStatus(ThingStatus.ONLINE);
        } catch (IOException e) {
            logger.debug("Failed to fetch data from gateway", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
        }
    }

    @Override
    public void dispose() {
        if (gwPollingJob != null && !gwPollingJob.isCancelled()) {
            gwPollingJob.cancel(true);
            gwPollingJob = null;
        }
        super.dispose();
    }

    @Nullable
    public GatewayApi getGatewayApi() {
        return gatewayApi;
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Collections.singleton(TheKeysDiscoveryService.class);
    }
}
