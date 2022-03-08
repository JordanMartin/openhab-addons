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
public class FreeboxHomeNodeEndpoint {
    /**
     *
     */
    private String category;

    /**
     * The endpoint type
     */
    private FreeboxHomeEndpointEpType epType;

    /**
     * The endpoint id
     */
    private int id;

    /**
     * Visibility level of this endpoint
     */
    private FreeboxHomeEndpointVisibility visibility;

    /**
     * Access mode of this endpoint
     */
    private FreeboxHomeEndpointAccess access;
}
