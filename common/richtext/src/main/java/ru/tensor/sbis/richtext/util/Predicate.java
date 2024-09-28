package ru.tensor.sbis.richtext.util;

/**
 * Копия класса com.android.internal.util.Predicate который был до build tools 28
 * <p>
 * A Predicate can determine a true or false value for any input of its
 * parameterized type. For example, a `RegexPredicate` might implement
 * `Predicate<String>`, and return true for any String that matches its
 * given regular expression.
 * <p>
 * <p>
 * <p>
 * <p>
 * Implementors of Predicate which may cause side effects upon evaluation are
 * strongly encouraged to state this fact clearly in their API documentation.
 * <p>
 * @author am.boldinov
 */
public interface Predicate<T> {

    /**
     * Проверяет условие
     */
    boolean apply(T t);
}
