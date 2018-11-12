package com.bhegstam.measurement.domain;

public class InstrumentationId extends Identifier {
    public InstrumentationId() {
    }

    private InstrumentationId(String id) {
        super(id);
    }

    public static InstrumentationId parse(String id) {
        return new InstrumentationId(id);
    }
}
