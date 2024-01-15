package io.github.blankbro.lzss.util;

import java.time.Duration;

public class TimeUtil {

    public static String formatDuration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        long millis = duration.toMillis() % 1000;
        long micros = duration.toNanos() / 1_000 % 1_000;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(" ").append(days).append("d");
        }
        if (hours > 0) {
            sb.append(" ").append(hours).append("h");
        }
        if (minutes > 0) {
            sb.append(" ").append(minutes).append("m");
        }
        if (seconds > 0) {
            sb.append(" ").append(seconds).append("s");
        }
        if (millis > 0) {
            sb.append(" ").append(millis).append("ms");
        }
        if (micros > 0) {
            sb.append(" ").append(micros).append("μs");
        }
        return sb.toString();
    }

    private TimeUtil() {
    }
}
