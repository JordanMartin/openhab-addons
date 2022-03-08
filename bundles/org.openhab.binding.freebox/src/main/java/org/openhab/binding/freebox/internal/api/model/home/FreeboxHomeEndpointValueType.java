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

import com.google.gson.annotations.SerializedName;

/**
 * The {@link FreeboxHomeEndpointValueType} is the Java class used to map the "FreeboxHomeEndpointValueType"
 * structure used by the Home Nodes API
 * 
 * @author Jordan Martin - Initial contribution
 */
public enum FreeboxHomeEndpointValueType {
    @SerializedName("bool")
    BOOL,
    @SerializedName("int")
    INT,
    @SerializedName("float")
    FLOAT,
    @SerializedName("void")
    VOID;
}
