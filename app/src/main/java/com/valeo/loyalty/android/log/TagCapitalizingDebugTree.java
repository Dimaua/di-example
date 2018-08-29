package com.valeo.loyalty.android.log;

import android.util.Log;

import java.util.regex.Pattern;

import java8.util.stream.Collectors;
import java8.util.stream.RefStreams;
import timber.log.Timber;

/**
 * A subclass of Timber {@link Timber.DebugTree} that takes default tag derived
 * from class name and turns it to UPPER_CASE notation to improve log visibility.
 */
public class TagCapitalizingDebugTree extends Timber.DebugTree {

    private static final Pattern LOG_TAG_PATTERN = Pattern.compile("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");

    private final int minPriority;

    public TagCapitalizingDebugTree(int minPriority) {
        this.minPriority = minPriority;
    }

    public TagCapitalizingDebugTree() {
        this(Log.DEBUG);
    }

    @Override
    protected boolean isLoggable(String tag, int priority) {
        return priority >= minPriority;
    }

    @Override
    protected String createStackElementTag(StackTraceElement element) {
        String previous = super.createStackElementTag(element);

        return RefStreams.of(LOG_TAG_PATTERN.split(previous))
            .map(String::toUpperCase)
            .collect(Collectors.joining("_"));
    }
}
