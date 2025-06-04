package com.Base.Base.AOP;

import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;

public class TracingService {

    private final Tracer tracer;

    public TracingService(Tracer tracer) {
        this.tracer = tracer;
    }

    public TraceContext buildTraceContext(String traceId, String spanId) {
        return tracer.traceContextBuilder()
                .traceId(traceId)
                .spanId(spanId)
                .build();
    }
}
