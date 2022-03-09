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
package org.openhab.binding.thekeys.internal.smartlock;

import static org.openhab.binding.thekeys.internal.TheKeysBindingConstants.*;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledFuture;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.thekeys.internal.api.LockerStatus;
import org.openhab.binding.thekeys.internal.gateway.TheKeysGatewayHandler;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.*;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link TheKeysSmartlockHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Jordan Martin - Initial contribution
 */
@NonNullByDefault
public class TheKeysSmartlockHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(TheKeysSmartlockHandler.class);
    private static final int BATTERY_MIN_LEVEL_MV = 6200;
    private static final int BATTERY_MAX_LEVEL_MV = 8000;
    private static final int BATTERY_SECURITY_MV = 200;

    private @Nullable TheKeysSmartlockConfiguration config;
    private @Nullable ScheduledFuture<?> pollingJob;
    private @Nullable TheKeysGatewayHandler gateway;

    /**
     * Identifier of the last event of the lock
     */
    private int lastLog = -1;

    public TheKeysSmartlockHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (CHANNEL_LOCK.equals(channelUID.getId())) {
            lock((OnOffType) command);
        }
    }

    private void lock(OnOffType open) {
        try {
            gateway.getGatewayApi().sendLockCommand(config.lockId, open);
        } catch (IOException e) {
            logger.error("Lock command failed", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
        }
    }

    @Override
    public void initialize() {
        config = getConfigAs(TheKeysSmartlockConfiguration.class);
        gateway = (TheKeysGatewayHandler) getBridge().getHandler();

        if (!isBridgeReady()) {
            return;
        }

        updateStatus(ThingStatus.UNKNOWN);
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        super.bridgeStatusChanged(bridgeStatusInfo);
    }

    private boolean isBridgeReady() {
        Bridge bridge = getBridge();
        if (bridge == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_UNINITIALIZED);
            return false;
        }
        if (bridge.getStatus() != ThingStatus.ONLINE) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
            return false;
        }

        return true;
    }

    private void fetchLockStatus() throws IOException {
        LockerStatus lockStatus = gateway.getGatewayApi().getLockStatus(config.lockId);
        if ("ko".equals(lockStatus.getStatus())) {
            logger.warn("Request failed to lock={} (code {})", config.lockId, lockStatus.getCode());
            return;
        }
        updateState(CHANNEL_STATUS, new StringType(lockStatus.getStatus()));
        updateState(CHANNEL_LOCK, OnOffType.from(!lockStatus.isClosed()));
        updateState(CHANNEL_BATTERY_LEVEL, new DecimalType(getBatteryLevel(lockStatus.getBattery())));
        updateState(CHANNEL_LOW_BATTERY, OnOffType.from(isLowBattery(lockStatus.getBattery())));
        updateState(CHANNEL_POSITION, new DecimalType(lockStatus.getPosition()));
        updateState(CHANNEL_RSSI, new DecimalType(lockStatus.getRssi()));
        updateState(CHANNEL_VERSION, new DecimalType(lockStatus.getVersion()));
        updateState(CHANNEL_LAST_SYNC, new DateTimeType(ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS)));

        updateProperty(PROPERTY_VERSION, String.valueOf(lockStatus.getVersion()));
    }

    private boolean fetchData() {
        try {
            fetchLockStatus();
            if (getThing().getStatus() != ThingStatus.ONLINE) {
                updateStatus(ThingStatus.ONLINE);
            }
            return true;
        } catch (Exception e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
            return false;
        }
    }

    @Override
    public void dispose() {
        if (pollingJob != null && !pollingJob.isCancelled()) {
            pollingJob.cancel(true);
            pollingJob = null;
        }
        lastLog = -1;
        super.dispose();
    }

    private int getBatteryLevel(int batteryMv) {
        double level = (double) (batteryMv - BATTERY_SECURITY_MV - BATTERY_MIN_LEVEL_MV)
                / (BATTERY_MAX_LEVEL_MV - BATTERY_MIN_LEVEL_MV);
        int batteryPercent = (int) Math.floor(level * 100);
        return Math.max(0, Math.min(100, batteryPercent));
    }

    public void updateLastLog(int lastLogUpdated) {
        if (lastLog != lastLogUpdated) {
            if (fetchData()) {
                lastLog = lastLogUpdated;
            }
        }
    }

    private boolean isLowBattery(int batteryLevel) {
        return batteryLevel <= 20;
    }
}
