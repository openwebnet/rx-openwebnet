package com.github.niqdev.openwebnet.message;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * OpenWebNet WHO types.
 */
public enum Who {

    SCENARIO(0),
    LIGHTING(1),
    AUTOMATION(2),
    LOAD_CONTROL(3),
    // heating
    TEMPERATURE_CONTROL(4),
    // intrusion
    BURGLAR_ALARM(5),
    DOOR_ENTRY_SYSTEM(6),
    // multimedia
    VIDEO_DOOR_ENTRY_SYSTEM(7),
    AUXILIARY(9),
    GATEWAY_MANAGEMENT(13),
    LIGHT_SHUTTER_ACTUATORS_LOCK(14),
    CEN_SCENARIO_SCHEDULER_SWITCH(15),
    // audio
    SOUND_SYSTEM_1(16),
    // MH200N
    SCENARIO_PROGRAMMING(17),
    ENERGY_MANAGEMENT(18),
    // audio
    SOUND_SYSTEM_2(22),
    LIGHTING_MANAGEMENT(24),
    CEN_SCENARIO_SCHEDULER_BUTTON(25),
    DIAGNOSTIC(1000),
    AUTOMATIC_DIAGNOSTIC(1001),
    THERMOREGULATION_DIAGNOSTIC(1004),
    DEVICE_DIAGNOSTIC(1013);

    private final Integer value;

    Who(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }

    public static boolean isValidName(String name) {
        return name != null && findWho(isEqualName(name)).isPresent();
    }

    public static boolean isValidValue(Integer value) {
        return value != null && findWho(isEqualValue(value)).isPresent();
    }

    public static Who fromName(String name) {
        checkArgument(isValidName(name), "invalid name");
        return findWho(isEqualName(name)).get();
    }

    public static Who fromValue(Integer value) {
        checkArgument(isValidValue(value), "invalid value");
        return findWho(isEqualValue(value)).get();
    }

    private static Predicate<Who> isEqualName(String name) {
        return who -> who.name().equals(name);
    }

    private static Predicate<Who> isEqualValue(Integer value) {
        return who -> who.value().intValue() == value.intValue();
    }

    private static Optional<Who> findWho(Predicate<Who> isEqual) {
        return EnumSet.allOf(Who.class).stream().filter(isEqual).findFirst();
    }

}
