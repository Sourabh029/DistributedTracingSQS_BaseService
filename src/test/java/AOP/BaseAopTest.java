package AOP;

import com.Base.Base.AOP.BaseAop;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class BaseAopTest {

    @InjectMocks
    private BaseAop tracingAspect;

    @Mock
    private Tracer tracer;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private Span span;

    @Mock
    private Tracer.SpanInScope spanInScope;

    @Mock
    private TraceContext traceContext;

    @Mock
    private TraceContext.Builder traceContextBuilder;

    @Mock
    private Span.Builder spanBuilder;





    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    public void testTraceSqsMessage_withTraceIdAndSpanId() throws Throwable {

        String traceId = "abc123";
        String spanId = "def456";
        // Arrange
        Map<String, MessageAttributeValue> attributes = new HashMap<>();
        attributes.put("traceId", MessageAttributeValue.builder().stringValue("abc123").build());
        attributes.put("spanId", MessageAttributeValue.builder().stringValue("def456").build());

        Message message = Message.builder().messageAttributes(attributes).build();
        when(joinPoint.getArgs()).thenReturn(new Object[]{message});

        // Mock chain: tracer.traceContextBuilder() → traceContextBuilder
        when(tracer.traceContextBuilder()).thenReturn(traceContextBuilder);

// Mock method chaining on builder
        when(traceContextBuilder.traceId(traceId)).thenReturn(traceContextBuilder);
        when(traceContextBuilder.spanId(spanId)).thenReturn(traceContextBuilder);
        when(traceContextBuilder.build()).thenReturn(traceContext);

        when(tracer.spanBuilder()).thenReturn(spanBuilder);
        when(spanBuilder.setParent(traceContext)).thenReturn(spanBuilder);
        when(spanBuilder.name(Mockito.any())).thenReturn(spanBuilder);
        when(spanBuilder.start()).thenReturn(span);

        tracingAspect.traceSqsMessage(joinPoint);

    }

    static class DummyHandler {
        public void handle(Message message) {}
    }
}
