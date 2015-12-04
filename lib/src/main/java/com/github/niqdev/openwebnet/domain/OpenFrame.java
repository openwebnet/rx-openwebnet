package com.github.niqdev.openwebnet.domain;

import com.google.common.collect.Lists;

import java.util.List;

/**
 *
 */
public class OpenFrame {

    private final String value;

    private OpenFrame(String value) {
        this.value = value;
    }

    public static class Builder {

        private Who who;
        private Integer what;
        private Where where;
        private Integer dimension;
        private List<Integer> values;

        public Builder who(Who who) {
            this.who = who;
            return this;
        }

        public Builder who(String name) {
            this.who = Who.fromName(name);
            return this;
        }

        public Builder who(Integer value) {
            this.who = Who.fromValue(value);
            return this;
        }

    }

    public static class BuilderWriteCommand {

    }

    public static class BuilderReadStatus {

    }

    public static class BuilderReadDimension {

    }

    public static class BuilderWriteDimension {

    }
}
