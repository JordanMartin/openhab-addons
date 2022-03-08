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
package org.openhab.binding.freebox.internal.handler;

import static org.openhab.binding.freebox.internal.FreeboxBindingConstants.FREEBOX_THING_TYPE_SHUTTER_HOME_NODE;

import org.openhab.binding.freebox.internal.FreeboxBindingConstants;
import org.openhab.binding.freebox.internal.api.FreeboxException;
import org.openhab.binding.freebox.internal.config.FreeboxHomeNodeConfiguration;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.ThingStatusInfo;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FreeboxShutterThingHandler} is responsible for handling shutter home node thing
 *
 * @author Jordan Martin - Initial contribution
 */
public class FreeboxShutterThingHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(FreeboxShutterThingHandler.class);

    private FreeboxHandler bridgeHandler;
    private int homeNodeId;

    public FreeboxShutterThingHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            return;
        }
        if (getThing().getStatus() == ThingStatus.UNKNOWN || (getThing().getStatus() == ThingStatus.OFFLINE
                && (getThing().getStatusInfo().getStatusDetail() == ThingStatusDetail.BRIDGE_OFFLINE
                        || getThing().getStatusInfo().getStatusDetail() == ThingStatusDetail.BRIDGE_UNINITIALIZED
                        || getThing().getStatusInfo().getStatusDetail() == ThingStatusDetail.CONFIGURATION_ERROR))) {
            return;
        }
        if (bridgeHandler == null) {
            return;
        }

        if (FreeboxBindingConstants.SHUTTER_POSITION.equals(channelUID.getId())) {
            if (!(command instanceof DecimalType)) {
                logger.debug("Thing {}: invalid command {} from channel {}", getThing().getUID(), command,
                        channelUID.getId());
                return;
            }

            try {
                bridgeHandler.getApiManager().setShutterPosition(homeNodeId,
                        (int) (((DecimalType) command).doubleValue() * 100));
            } catch (FreeboxException e) {
                bridgeHandler.logCommandException(e, channelUID, command);
            }
        }
    }

    @Override
    public void initialize() {
        logger.debug("initializing handler for thing {}", getThing().getUID());
        Bridge bridge = getBridge();
        if (bridge == null) {
            initializeThing(null, null);
        } else {
            initializeThing(bridge.getHandler(), bridge.getStatus());
        }
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        logger.debug("bridgeStatusChanged {}", bridgeStatusInfo);
        Bridge bridge = getBridge();
        if (bridge == null) {
            initializeThing(null, bridgeStatusInfo.getStatus());
        } else {
            initializeThing(bridge.getHandler(), bridgeStatusInfo.getStatus());
        }
    }

    private void initializeThing(ThingHandler bridgeHandler, ThingStatus bridgeStatus) {
        if (bridgeHandler == null || bridgeStatus == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_UNINITIALIZED);
            return;
        }
        if (bridgeStatus != ThingStatus.ONLINE) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
            return;
        }

        this.bridgeHandler = (FreeboxHandler) bridgeHandler;

        if (getThing().getThingTypeUID().equals(FREEBOX_THING_TYPE_SHUTTER_HOME_NODE)) {
            updateStatus(ThingStatus.ONLINE);
            homeNodeId = getConfigAs(FreeboxHomeNodeConfiguration.class).id;
        }
    }

    @Override
    public void dispose() {
        logger.debug("Disposing handler for thing {}", getThing().getUID());
        super.dispose();
    }
}
