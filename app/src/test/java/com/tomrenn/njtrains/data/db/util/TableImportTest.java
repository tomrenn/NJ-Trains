package com.tomrenn.njtrains.data.db.util;

import org.junit.Test;

import static com.tomrenn.njtrains.data.db.util.TableImportStrategy.StopTableImportStrategy;
import static junit.framework.Assert.*;
/**
 *
 */

public class TableImportTest {

    @Test
    public void testNameCleanup() {
        String lightRail = "Something LIGHT Rail Station";
        String dumbRail = "Dumb Light RAIL Sta";
        String secaucus = "FRANK R Lautenberg Secaucus";

        secaucus = StopTableImportStrategy.cleanSecaucus(secaucus);
        lightRail = StopTableImportStrategy.cleanLightRail(lightRail);
        dumbRail = StopTableImportStrategy.cleanLightRail(dumbRail);

        assertEquals("Secaucus", secaucus);
        assertEquals("Something", lightRail);
        assertEquals("Dumb", dumbRail);
    }
}
