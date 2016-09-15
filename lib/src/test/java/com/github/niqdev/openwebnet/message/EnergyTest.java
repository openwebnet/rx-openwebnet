package com.github.niqdev.openwebnet.message;

import org.junit.Test;

import static com.github.niqdev.openwebnet.message.Energy.*;
import static org.junit.Assert.assertEquals;

public class EnergyTest {

    @Test
    public void testRequestInstantaneous() {
        assertEquals("invalid message", "*#18*51*113##", requestInstantaneous("1", Energy.Version.MODEL_F523).getValue());
        assertEquals("invalid message", "*#18*5255*113##", requestInstantaneous("255", Energy.Version.MODEL_F523).getValue());

        assertEquals("invalid message", "*#18*71#0*113##", requestInstantaneous("1", Energy.Version.MODEL_F523_A).getValue());
        assertEquals("invalid message", "*#18*7255#0*113##", requestInstantaneous("255", Energy.Version.MODEL_F523_A).getValue());
    }

    @Test
    public void testRequestDaily() {
        assertEquals("invalid message", "*#18*51*54##", requestDaily("1", Energy.Version.MODEL_F523).getValue());
        assertEquals("invalid message", "*#18*5255*54##", requestDaily("255", Energy.Version.MODEL_F523).getValue());

        assertEquals("invalid message", "*#18*71#0*54##", requestDaily("1", Energy.Version.MODEL_F523_A).getValue());
        assertEquals("invalid message", "*#18*7255#0*54##", requestDaily("255", Energy.Version.MODEL_F523_A).getValue());
    }

    @Test
    public void testRequestMonthly() {
        assertEquals("invalid message", "*#18*51*53##", requestMonthly("1", Energy.Version.MODEL_F523).getValue());
        assertEquals("invalid message", "*#18*5255*53##", requestMonthly("255", Energy.Version.MODEL_F523).getValue());

        assertEquals("invalid message", "*#18*71#0*53##", requestMonthly("1", Energy.Version.MODEL_F523_A).getValue());
        assertEquals("invalid message", "*#18*7255#0*53##", requestMonthly("255", Energy.Version.MODEL_F523_A).getValue());
    }

}
