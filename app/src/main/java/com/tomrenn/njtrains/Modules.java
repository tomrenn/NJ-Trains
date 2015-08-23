package com.tomrenn.njtrains;

import android.app.Application;

/**
 *
 */
final class Modules {
    static Object[] list(Application app) {
        return new Object[] {
                new NJTModule(app),
        };
    }

    private Modules() {
        // No instances.
    }
}