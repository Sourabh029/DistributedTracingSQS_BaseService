package AOP;
import com.Base.Base.AOP.TracingService;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

public class TracingServiceTest {

    @Mock
    private Tracer tracer;

    @Mock
    private TraceContext.Builder traceContextBuilder;  // ✅ Correct type

    @Mock
    private TraceContext traceContext;

    private TracingService tracingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tracingService = new TracingService(tracer);
    }

    @Test
    void testBuildTraceContext() {
        String traceId = "abc123";
        String spanId = "def456";

        // ✅ Correct mocks for Micrometer
        when(tracer.traceContextBuilder()).thenReturn(traceContextBuilder);
        when(traceContextBuilder.traceId(traceId)).thenReturn(traceContextBuilder);
        when(traceContextBuilder.spanId(spanId)).thenReturn(traceContextBuilder);
        when(traceContextBuilder.build()).thenReturn(traceContext);

        TraceContext result = tracingService.buildTraceContext(traceId, spanId);

        verify(tracer).traceContextBuilder();
        verify(traceContextBuilder).traceId(traceId);
        verify(traceContextBuilder).spanId(spanId);
        verify(traceContextBuilder).build();
        assertSame(traceContext, result);
    }
}
