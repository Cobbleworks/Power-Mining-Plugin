package de.andidoescode.powermining.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DungeonLocatorManagerTest {
    @Test
    void sphereIncludesBoundaryAndRejectsOutsideBlocks() {
        assertTrue(DungeonLocatorManager.withinRadius(32, 0, 0, 32));
        assertTrue(DungeonLocatorManager.withinRadius(16, 16, 16, 32));
        assertFalse(DungeonLocatorManager.withinRadius(32, 1, 0, 32));
        assertFalse(DungeonLocatorManager.withinRadius(24, 24, 0, 32));
    }
}
