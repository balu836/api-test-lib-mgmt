package uk.gov.dwp.utils;

import java.util.function.Function;

public class LambdaUtil {
    /**
     * This helps convert checked exceptions thrown by lambdas into Runtime exceptions.
     * This prevents us from having ugly try/catch blocks in lambda when using them.
     * Note: It is not always we'd want to convert a checked exception to a runtime exception, so use wisely!
     *
     * @param lambda the lambda that throws a checked exception.
     * @param <T>    Input type of the lamnda
     * @param <R>    Output type of the lamnda
     * @param <E>    Checked exception it throws
     * @return A new handled lambda that only throws runtime exceptions.
     */
    public static <T, R, E extends Exception> Function<T, R> toHandledLambda(FunctionWithException<T, R, E> lambda) {
        return arg -> {
            try {
                return lambda.apply(arg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @FunctionalInterface
    public interface FunctionWithException<T, R, E extends Exception> {
        R apply(T t) throws E;
    }
}
