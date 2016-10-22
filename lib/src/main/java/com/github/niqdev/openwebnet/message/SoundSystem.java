package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.OpenSession;
import com.google.common.collect.FluentIterable;
import rx.functions.Action0;
import rx.functions.Func1;

import java.util.List;

import static com.github.niqdev.openwebnet.message.Who.SOUND_SYSTEM_1;
import static com.github.niqdev.openwebnet.message.Who.SOUND_SYSTEM_2;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * OpenWebNet Sound System.
 */
public class SoundSystem extends BaseOpenMessage {

    public enum Source {
        BASE_BAND,
        STEREO_CHANNEL
    }

    private static final int ON_SOURCE_BASE_BAND = 0;
    private static final int ON_SOURCE_STEREO_CHANNEL = 3;
    private static final int OFF_SOURCE_BASE_BAND = 10;
    private static final int OFF_SOURCE_STEREO_CHANNEL = 13;
    private static final int STATUS = 5;
    private static final int WHO_16 = SOUND_SYSTEM_1.value();
    private static final int WHO_22 = SOUND_SYSTEM_2.value();

    protected SoundSystem(String value) {
        super(value);
    }

    /**
     * TODO
     */
    public static SoundSystem requestTurnOn(String where, Source source) {
        return buildRequest(where, source, ON_SOURCE_BASE_BAND, ON_SOURCE_STEREO_CHANNEL);
    }

    /**
     * TODO
     */
    public static SoundSystem requestTurnOff(String where, Source source) {
        return buildRequest(where, source, OFF_SOURCE_BASE_BAND, OFF_SOURCE_STEREO_CHANNEL);
    }

    private static SoundSystem buildRequest(String where, Source source, int baseBandValue, int stereoValue) {
        checkAllowedWhere(where);
        switch (source) {
            case BASE_BAND:
                return new SoundSystem(format(FORMAT_REQUEST, WHO_16, baseBandValue, where));
            case STEREO_CHANNEL:
                return new SoundSystem(format(FORMAT_REQUEST, WHO_16, stereoValue, where));
        }
        throw new IllegalArgumentException("invalid source");
    }

    /**
     * TODO
     */
    public static Func1<OpenSession, OpenSession> handleResponse(Action0 onSuccess, Action0 onFail) {
        return handleResponse(onSuccess, onFail, WHO_16);
    }

    /**
     * TODO
     */
    public static SoundSystem requestStatus(String where) {
        checkAllowedWhere(where);
        return new SoundSystem(format(FORMAT_DIMENSION, WHO_16, where, STATUS));
    }

    /**
     * TODO
     */
    public static Func1<OpenSession, OpenSession> handleStatus(Action0 onStatus, Action0 offStatus) {
        return openSession -> {
            isValidPrefixType(openSession.getRequest(), FORMAT_PREFIX_DIMENSION_WHO, WHO_16);
            List<OpenMessage> response = openSession.getResponse();
            checkNotNull(response, "response is null");
            checkArgument(response.size() >= 1, "invalid response");

            if (isOn(openSession.getRequest(), response)) {
                onStatus.call();
            } else {
                offStatus.call();
            }
            return openSession;
        };
    }

    /*
     * 0 amplifier general command
     * #0-#9 amplifiers environment command
     * 01-99 amplifiers point to point command
     * 100 source general command
     * 101-109 sources point to point command
     */
    private static void checkAllowedWhere(String value) {
        final int AMPLIFIER_GENERAL_COMMAND = 0;
        final int AMPLIFIER_MIN_ENVIRONMENT_COMMAND = 0;
        final int AMPLIFIER_MAX_ENVIRONMENT_COMMAND = 9;
        final int AMPLIFIER_MIN_P2P_COMMAND = 1;
        final int AMPLIFIER_MAX_P2P_COMMAND = 99;
        final int SOURCE_GENERAL_COMMAND = 100;
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
                || where == SOURCE_GENERAL_COMMAND
                || isInRange(SOURCE_MIN_P2P_COMMAND, SOURCE_MAX_P2P_COMMAND, where),
                "invalid where range");
        }
    }

    /*
     * Verify OpenWebNet message response if amplifier/source is on.
     *
     * From documentation:
     * "if environment or general request, we get as a lot of frames as the active amplifiers are available"
     * what = 0 (Base band ON) | 3 (Stereo channel ON) | 13 (OFF)
     * where = [01 – 99] and [101 – 109]
     *
     * NOTE: this is a best effort, answer is NOT deterministic!
     */
    static boolean isOn(OpenMessage request, List<OpenMessage> responses) {
        final String responseOnBaseBand = format(FORMAT_PREFIX_RESPONSE, WHO_16, ON_SOURCE_BASE_BAND);
        final String responseOnStereo = format(FORMAT_PREFIX_RESPONSE, WHO_16, ON_SOURCE_STEREO_CHANNEL);

        // filter only response ON and exclude request "where=0" because response is always ON
        List<OpenMessage> validResponses = FluentIterable.from(responses)
            .filter(response ->
                (response.getValue().startsWith(responseOnBaseBand) || response.getValue().startsWith(responseOnStereo))
                && !request.getValue().equals(requestStatus("0").getValue())
            ).toList();
        return validResponses.size() >= 1;
    }

}
