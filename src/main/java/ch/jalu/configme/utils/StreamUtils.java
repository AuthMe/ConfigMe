package ch.jalu.configme.utils;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Stream utils.
 */
public final class StreamUtils {

    private StreamUtils() {
    }

    /**
     * Creates a stream with the requested size. Every element in the stream is the given {@code element}.
     *
     * @param element the element to repeat
     * @param numberOfTimes number of times the stream should have the element
     * @param <T> element type
     * @return stream with the element the requested number of times
     */
    public static <T> Stream<T> repeat(T element, int numberOfTimes) {
        return IntStream.range(0, numberOfTimes)
            .mapToObj(i -> element);
    }
}
