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

    public enum Type {
        // 0 amplifier general command
        AMPLIFIER_GENERAL,
        // #0-#9 amplifiers environment command
        AMPLIFIER_GROUP,
        // 01-99 amplifiers point to point command
        AMPLIFIER_P2P,
        // 100 source general command
        SOURCE_GENERAL,
        // 101-109 sources point to point command
        SOURCE_P2P;

        private static final String AMPLIFIER_GENERAL_COMMAND = "0";
        private static final int AMPLIFIER_GROUP_MIN_COMMAND = 0;
        private static final int AMPLIFIER_GROUP_MAX_COMMAND = 9;
        private static final int AMPLIFIER_P2P_MIN_COMMAND = 1;
        private static final int AMPLIFIER_P2P_MAX_COMMAND = 99;
        private static final String SOURCE_GENERAL_COMMAND = "100";
        private static final int SOURCE_P2P_MIN_COMMAND = 101;
        private static final int SOURCE_P2P_MAX_COMMAND = 109;

        /*
         * 0 amplifier general command
         * #0-#9 amplifiers environment command
         * 01-99 amplifiers point to point command
         * 100 source general command
         * 101-109 sources point to point command
         */
        public static boolean isValid(Type type, String value) {
            if (type == null || value == null || value.length() < 1 && value.length() > 3) {
                return false;
            }

            switch (type) {
                case AMPLIFIER_GENERAL:
                    return AMPLIFIER_GENERAL_COMMAND.equals(value);
                case AMPLIFIER_GROUP:
                    return value.length() == 1
                        && isInRange(AMPLIFIER_GROUP_MIN_COMMAND, AMPLIFIER_GROUP_MAX_COMMAND, checkIsInteger(value));
                case AMPLIFIER_P2P:
                    return value.length() == 2
                        && isInRange(AMPLIFIER_P2P_MIN_COMMAND, AMPLIFIER_P2P_MAX_COMMAND, checkIsInteger(value));
                case SOURCE_GENERAL:
                    return SOURCE_GENERAL_COMMAND.equals(value);
                case SOURCE_P2P:
                    return value.length() == 3
                        && isInRange(SOURCE_P2P_MIN_COMMAND, SOURCE_P2P_MAX_COMMAND, checkIsInteger(value));
            }
            throw new IllegalArgumentException("invalid type");
        }
    }

    private static final int ON_SOURCE_BASE_BAND = 0;
    private static final int ON_SOURCE_STEREO_CHANNEL = 3;
    private static final int OFF_SOURCE_BASE_BAND = 10;
    private static final int OFF_SOURCE_STEREO_CHANNEL = 13;
    private static final int STATUS = 5;
    private static final String WHERE_GROUP_PREFIX = "#";
    private static final int WHO_16 = SOUND_SYSTEM_1.value();
    private static final int WHO_22 = SOUND_SYSTEM_2.value();

    protected SoundSystem(String value) {
        super(value);
    }

    /**
     * TODO
     */
    public static SoundSystem requestTurnOn(String where, Type type, Source source) {
        return buildRequest(where, type, source, ON_SOURCE_BASE_BAND, ON_SOURCE_STEREO_CHANNEL);
    }

    /**
     * TODO
     */
    public static SoundSystem requestTurnOff(String where, Type type, Source source) {
        return buildRequest(where, type, source, OFF_SOURCE_BASE_BAND, OFF_SOURCE_STEREO_CHANNEL);
    }

    private static SoundSystem buildRequest(String where, Type type, Source source, int baseBandValue, int stereoValue) {
        checkArgument(Type.isValid(type, where), "invalid where|type");
        switch (source) {
            case BASE_BAND:
                return new SoundSystem(format(FORMAT_REQUEST, WHO_16, baseBandValue, buildWhereValue(where, type)));
            case STEREO_CHANNEL:
                return new SoundSystem(format(FORMAT_REQUEST, WHO_16, stereoValue, buildWhereValue(where, type)));
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
    public static SoundSystem requestStatus(String where, Type type) {
        checkArgument(Type.isValid(type, where), "invalid where|type");
        return new SoundSystem(format(FORMAT_DIMENSION, WHO_16, buildWhereValue(where, type), STATUS));
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
                && !request.getValue().equals(requestStatus("0", Type.AMPLIFIER_GENERAL).getValue())
            ).toList();
        return validResponses.size() >= 1;
    }

    private static String buildWhereValue(String where, Type type) {
        switch (type) {
            case AMPLIFIER_GENERAL:
            case AMPLIFIER_P2P:
            case SOURCE_GENERAL:
            case SOURCE_P2P:
                return where;
            case AMPLIFIER_GROUP:
                return WHERE_GROUP_PREFIX.concat(where);
        }
        throw new IllegalArgumentException("invalid type");
    }

}
