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
package org.openhab.binding.freebox.internal.api.model.home;

/**
 * @author Jordan Martin - Initial contribution
 */
public class FreeboxHomeNodeEndpointValue {
    /**
     * The current value of the endpoint
     */
    private String value;

    /**
     * The displayable unit of the value
     */
    private String unit;

    /**
     * The period this value need to be refreshed
     */
    private int refresh;

    /**
     * The type of value this enpoint expose
     */
    private FreeboxHomeEndpointValueType valueType;
}
