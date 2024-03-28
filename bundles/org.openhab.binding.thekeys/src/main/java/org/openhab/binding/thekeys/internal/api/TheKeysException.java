/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
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
package org.openhab.binding.thekeys.internal.api;

import java.io.Serial;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Generic exception for this binding
 *
 * @author Jordan Martin - Initial contribution
 */
@NonNullByDefault
public class TheKeysException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public TheKeysException(String message) {
        super(message);
    }

    public TheKeysException(String message, Throwable cause) {
        super(message, cause);
    }

    @Nullable
    @Override
    public String getMessage() {
        Throwable cause = getCause();
        if (cause != null) {
            return super.getMessage() + " : " + cause.getMessage();
        }
        return super.getMessage();
    }
}