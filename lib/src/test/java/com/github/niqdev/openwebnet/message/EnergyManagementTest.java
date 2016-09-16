package com.github.niqdev.openwebnet.message;

import org.junit.Test;

import static com.github.niqdev.openwebnet.message.EnergyManagement.*;
import static org.junit.Assert.assertEquals;

public class EnergyManagementTest {

    @Test
    public void testRequestInstantaneous() {
        assertEquals("invalid message", "*#18*51*113##", requestInstantaneousPower("1", EnergyManagement.Version.MODEL_F523).getValue());
        assertEquals("invalid message", "*#18*5255*113##", requestInstantaneousPower("255", EnergyManagement.Version.MODEL_F523).getValue());

        assertEquals("invalid message", "*#18*71#0*113##", requestInstantaneousPower("1", EnergyManagement.Version.MODEL_F523_A).getValue());
        assertEquals("invalid message", "*#18*7255#0*113##", requestInstantaneousPower("255", EnergyManagement.Version.MODEL_F523_A).getValue());
    }

    @Test
    public void testRequestDaily() {
        assertEquals("invalid message", "*#18*51*54##", requestDailyPower("1", EnergyManagement.Version.MODEL_F523).getValue());
        assertEquals("invalid message", "*#18*5255*54##", requestDailyPower("255", EnergyManagement.Version.MODEL_F523).getValue());

        assertEquals("invalid message", "*#18*71#0*54##", requestDailyPower("1", EnergyManagement.Version.MODEL_F523_A).getValue());
        assertEquals("invalid message", "*#18*7255#0*54##", requestDailyPower("255", EnergyManagement.Version.MODEL_F523_A).getValue());
    }

    @Test
    public void testRequestMonthly() {
        assertEquals("invalid message", "*#18*51*53##", requestMonthlyPower("1", EnergyManagement.Version.MODEL_F523).getValue());
        assertEquals("invalid message", "*#18*5255*53##", requestMonthlyPower("255", EnergyManagement.Version.MODEL_F523).getValue());

        assertEquals("invalid message", "*#18*71#0*53##", requestMonthlyPower("1", EnergyManagement.Version.MODEL_F523_A).getValue());
        assertEquals("invalid message", "*#18*7255#0*53##", requestMonthlyPower("255", EnergyManagement.Version.MODEL_F523_A).getValue());
    }

}
