package com.billmyservices.paypal.ipn;

/**
 * The currently unique possible action is to add certain amount to the current counter value.
 * <p>
 * To add new ones you must to decide what action you will be when the user pay.
 * <p>
 * Is not possible return an empty action, since the payment is done, you must to add the value. Use exception for failure cases.
 */
public class CounterAction {
    private String counterTypeCode;
    private String counterCode;
    private Integer valueToAdd;

    public CounterAction counterTypeCode(String counterTypeCode) {
        this.counterTypeCode = counterTypeCode;
        return this;
    }

    public CounterAction counterCode(String counterCode) {
        this.counterCode = counterCode;
        return this;
    }

    public CounterAction valueToAdd(Integer valueToAdd) {
        this.valueToAdd = valueToAdd;
        return this;
    }

    public String getCounterTypeCode() {
        if (counterTypeCode == null)
            throw new IllegalStateException("the counterTypeCode can not be null");
        return counterTypeCode;
    }

    public String getCounterCode() {
        if (counterCode == null)
            throw new IllegalStateException("the counterCode can not be null");
        return counterCode;
    }

    public Integer getValueToAdd() {
        if (valueToAdd == null)
            throw new IllegalStateException("the valueToAdd can not be null");
        return valueToAdd;
    }

}
