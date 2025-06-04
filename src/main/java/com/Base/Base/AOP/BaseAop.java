package com.Base.Base.AOP;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.util.Arrays;
import java.util.Optional;


@Aspect
@Component
@Log4j2
public class BaseAop {

    @Autowired
    private Tracer tracer;



    @Around("@annotation(io.awspring.cloud.sqs.annotation.SqsListener)")
    @Before("execution(* com.Tracer.*.Listeners.*(..)) || within(com.Tracer.*.Controller.*(..))")
    public Object traceSqsMessage(ProceedingJoinPoint joinPoint) throws Throwable {
        Message message = extractMessageFromArgs(joinPoint.getArgs());

        log.info("inside AOP");

        // Get trace context from SQS attributes
        String traceId = Optional.ofNullable(message.messageAttributes().get("traceId"))
                .map(MessageAttributeValue::stringValue).orElse(null);
        String spanId = Optional.ofNullable(message.messageAttributes().get("spanId"))
                .map(MessageAttributeValue::stringValue).orElse(null);

        Span span;

        if (traceId != null && spanId != null) {
            TraceContext parentContext = tracer.traceContextBuilder()
                    .traceId(traceId)
                    .spanId(spanId)
                    .build();

            span = tracer.spanBuilder().setParent(parentContext).name("sqs-receive").start();
        } else {
            span = tracer.nextSpan().name("sqs-receive").start();
        }

        try (Tracer.SpanInScope scope = tracer.withSpan(span)) {
            return joinPoint.proceed();
        } finally {
            span.end();
        }
    }

    public Message extractMessageFromArgs(Object[] args) {
        return Arrays.stream(args)
                .filter(arg -> arg instanceof Message)
                .map(arg -> (Message) arg)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No SQS Message parameter found"));
    }
}
