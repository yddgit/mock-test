package com.my.project.matcher;

import java.util.function.Function;

import org.mockito.ArgumentMatcher;

/**
 * 匹配参数对象属性值的Matcher
 * @author yang.dongdong
 *
 * @param <T>
 */
public class FieldMatcher<T> extends ArgumentMatcher<T> {
    private Object value;
    private Function<T, Object> function;

    public FieldMatcher(Function<T, Object> getProperty, Object value) {
        this.value = value;
        this.function = getProperty;
    }

    public static <F> FieldMatcher<F> fieldMatcher(Function<F, Object> getProperty, Object value) {
        return new FieldMatcher<F>(getProperty, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(Object o) {
        return function.apply((T)o).equals(value);
    }
}
