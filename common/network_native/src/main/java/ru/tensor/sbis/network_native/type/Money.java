package ru.tensor.sbis.network_native.type;

/**
 * Тип Деньги
 */
@SuppressWarnings("unused")
public class Money {

    private double amount;

    /**
     * Конструктор
     */
    public Money() {
    }

    /**
     * Конструктор
     *
     * @param value количество денег
     */
    public Money(double value) {
        amount = value;
    }

    /**
     * Получить количество денег.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Выставить количество денег.
     *
     * @param value новое значение
     */
    public void setAmount(double value) {
        amount = value;
    }
}
