package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.OpenSession;
import rx.functions.Action0;
import rx.functions.Func1;

import static com.github.niqdev.openwebnet.message.Who.SOUND_SYSTEM_1;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * OpenWebNet Sound System.
 */
public class SoundSystem extends BaseOpenMessage {

    private static final int ON_SOURCE_BASE_BAND = 0;
    private static final int ON_SOURCE_STEREO_CHANNEL = 3;
    private static final int OFF_SOURCE_BASE_BAND = 10;
    private static final int OFF_SOURCE_STEREO_CHANNEL = 13;
    private static final int WHO = SOUND_SYSTEM_1.value();

    protected SoundSystem(String value) {
        super(value);
    }

    /**
     * TODO
     */
    public static SoundSystem requestTurnOn(String where) {
        checkAllowedWhere(where);
        return new SoundSystem(format(FORMAT_REQUEST, WHO, ON_SOURCE_STEREO_CHANNEL, where));
    }

    /**
     * TODO
     */
    public static SoundSystem requestTurnOff(String where) {
        checkAllowedWhere(where);
        return new SoundSystem(format(FORMAT_REQUEST, WHO, OFF_SOURCE_STEREO_CHANNEL, where));
    }

    /**
     * TODO
     */
    public static Func1<OpenSession, OpenSession> handleResponse(Action0 onSuccess, Action0 onFail) {
        return handleResponse(onSuccess, onFail, WHO);
    }

    /*
     * 0 amplifier general command
     * #0-#9 amplifiers environment command
     * 01-99 amplifiers point to point command
     * 101-109 sources point to point command
     */
    private static void checkAllowedWhere(String value) {
        final int AMPLIFIER_GENERAL_COMMAND = 0;
        final int AMPLIFIER_MIN_ENVIRONMENT_COMMAND = 0;
        final int AMPLIFIER_MAX_ENVIRONMENT_COMMAND = 9;
        final int AMPLIFIER_MIN_P2P_COMMAND = 1;
        final int AMPLIFIER_MAX_P2P_COMMAND = 99;
        final int SOURCE_MIN_P2P_COMMAND = 101;
        final int SOURCE_MAX_P2P_COMMAND = 109;

        checkNotNull(value, "invalid null value");
        checkArgument(value.length() <= 3, "invalid length");
        if (value.startsWith("#")) {
            checkArgument(value.length() == 2, "invalid length");
            checkRange(AMPLIFIER_MIN_ENVIRONMENT_COMMAND, AMPLIFIER_MAX_ENVIRONMENT_COMMAND, checkIsInteger(value.substring(1)));
        } else {
            int where = checkIsInteger(value);
            checkArgument(
                where == AMPLIFIER_GENERAL_COMMAND
                || isInRange(AMPLIFIER_MIN_P2P_COMMAND, AMPLIFIER_MAX_P2P_COMMAND, where)
                || isInRange(SOURCE_MIN_P2P_COMMAND, SOURCE_MAX_P2P_COMMAND, where),
                "invalid where range");
        }
    }



}
