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

import java.util.List;

/**
 * The {@link FreeboxHomeNode} is the Java class used to map the "HomeNode"
 * structure used by the Home Nodes API
 * https://mafreebox.free.fr/#Fbx.os.app.help.app > Home > Home API > Home Nodes
 *
 * @author Jordan Martin - Initial contribution
 */
public class FreeboxHomeNode {

    /**
     * Id of the HomeAdapter this node is connected to
     * Read-only
     */
    private int adapter;

    /**
     * Read-only
     */
    private String category;

    /**
     * Id of this node
     * Read-only
     */
    private int id;

    /**
     * Displayable name of this node
     * Read-only
     */
    private String label;

    /**
     * Technical name of this node
     * Read-only
     */
    private String name;

    /**
     * Endpoints exposed by this node
     */
    private List<FreeboxHomeNodeEndpoint> showEndpoints;

    /**
     * FIXME - array of HomeNodeLink
     * Links from other objects to this node signals
     */
    // private List signalLinks;

    /**
     * FIXME - array of HomeNodeLink
     * Links from other objects to this node slots(Read-only)
     */
    // private List slotLinks;

    /**
     * Status of this node
     */
    private FreeboxHomeNodeStatus status;

    /**
     * Node type info
     */
    private FreeboxHomeNodeType type;

    public int getAdapter() {
        return adapter;
    }

    public void setAdapter(int adapter) {
        this.adapter = adapter;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FreeboxHomeNodeEndpoint> getShowEndpoints() {
        return showEndpoints;
    }

    public void setShowEndpoints(List<FreeboxHomeNodeEndpoint> showEndpoints) {
        this.showEndpoints = showEndpoints;
    }

    public FreeboxHomeNodeStatus getStatus() {
        return status;
    }

    public void setStatus(FreeboxHomeNodeStatus status) {
        this.status = status;
    }

    public FreeboxHomeNodeType getType() {
        return type;
    }

    public void setType(FreeboxHomeNodeType type) {
        this.type = type;
    }
}
