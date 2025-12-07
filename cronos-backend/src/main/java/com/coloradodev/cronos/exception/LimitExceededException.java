package com.coloradodev.cronos.exception;

public class LimitExceededException extends RuntimeException {

    private final String limitType;
    private final int currentValue;
    private final int maxValue;

    public LimitExceededException(String limitType, int currentValue, int maxValue) {
        super(String.format("Subscription limit exceeded for %s: %d/%d", limitType, currentValue, maxValue));
        this.limitType = limitType;
        this.currentValue = currentValue;
        this.maxValue = maxValue;
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
