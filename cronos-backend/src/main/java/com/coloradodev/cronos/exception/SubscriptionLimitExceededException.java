package com.coloradodev.cronos.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a subscription limit is exceeded.
 * HTTP 402 (Payment Required)
 */
public class SubscriptionLimitExceededException extends CronosException {

    private final String limitType;
    private final int currentValue;
    private final int maxValue;

    public SubscriptionLimitExceededException(String limitType, int currentValue, int maxValue) {
        super(
                String.format("Subscription limit exceeded for %s: %d/%d", limitType, currentValue, maxValue),
                "SUBSCRIPTION_LIMIT_EXCEEDED",
                HttpStatus.PAYMENT_REQUIRED);
        this.limitType = limitType;
        this.currentValue = currentValue;
        this.maxValue = maxValue;
        this.withDetail("limitType", limitType);
        this.withDetail("currentValue", String.valueOf(currentValue));
        this.withDetail("maxValue", String.valueOf(maxValue));
    }

    public String getLimitType() {
        return limitType;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public int getMaxValue() {
        return maxValue;
    }
}
