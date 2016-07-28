package com.github.niqdev.openwebnet.message;

import static com.github.niqdev.openwebnet.message.Who.SOUND_SYSTEM_1;

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

}
