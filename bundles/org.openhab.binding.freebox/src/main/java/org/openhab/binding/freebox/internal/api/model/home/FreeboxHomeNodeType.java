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
 * The {@link FreeboxHomeNodeType} is the Java class used to map the "HomeNodeType"
 * structure used by the Home Nodes API
 * https://mafreebox.free.fr/#Fbx.os.app.help.app > Home > Home API > Home Nodes
 *
 * @author Jordan Martin - Initial contribution
 */
public class FreeboxHomeNodeType {
    /**
     * The node icon name or url
     */
    private String icon;

    /**
     * The node displayable type
     */
    private String label;

    /**
     * True when the node is an actual connected object, false when it’s a virtual node
     */
    boolean physical;
}
