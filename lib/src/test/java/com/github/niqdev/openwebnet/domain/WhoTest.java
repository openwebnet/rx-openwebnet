package com.github.niqdev.openwebnet.domain;

import org.junit.Test;

import static com.github.niqdev.openwebnet.domain.Who.*;
import static org.junit.Assert.*;

public class WhoTest {

    @Test
    public void testIsValidName() {
        assertTrue("should be a valid name", isValidName("LIGHTING"));
        assertFalse("should not be a valid name", isValidName("lighting"));
        assertFalse("should not be a valid name", isValidName(null));
    }

    @Test
    public void testIsValidValue() {
        assertTrue("should be a valid value", isValidValue(1));
        assertFalse("should not be a valid value", isValidValue(-1));
        assertFalse("should not be a valid value", isValidValue(100));
        assertFalse("should not be a valid value", isValidValue(null));
    }

    @Test
    public void testFromName() {
        assertEquals("should retrieve element by name", LIGHTING, fromName("LIGHTING"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFromName() {
        fromName("lighting");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullFromName() {
        fromName(null);
    }

    @Test
    public void testFromValue() {
        assertEquals("should retrieve element by value", LIGHTING, fromValue(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFromValue() {
        fromValue(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullFromValue() {
        fromValue(null);
    }

}
