package me.comunidad.dev.legacy.utils;

import me.comunidad.dev.legacy.Core;
import me.comunidad.dev.legacy.framework.extra.Configs;
import me.comunidad.dev.legacy.utils.extra.DecimalFormat;
import me.comunidad.dev.legacy.utils.extra.DurationFormatUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Formatter {

    private static final long MINUTE = TimeUnit.MINUTES.toMillis(1L);
    private static final long HOUR = TimeUnit.HOURS.toMillis(1L);
    private static final long DAY = TimeUnit.DAYS.toMillis(1L);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM hh:mm");
    private static final ThreadLocal<java.text.DecimalFormat> REMAINING_SECONDS_TRAILING = ThreadLocal.withInitial(() -> new java.text.DecimalFormat("0.0"));
    private static DecimalFormat HEALTH_FORMATTER;

    public static void loadFormats(Core instance, Configs configs) {
        HEALTH_FORMATTER = new DecimalFormat(instance, "#.#");
    }

    public static Long parse(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        if (input.equals("0") || input.equalsIgnoreCase("0s")) {
            return 0L;
        }

        long result = 0L;
        StringBuilder number = new StringBuilder();

        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            if (Character.isDigit(c)) {
                number.append(c);
            } else {
                String str;
                if (Character.isLetter(c) && !(str = number.toString()).isEmpty()) {
                    result += convert(Integer.parseInt(str), c);
                    number = new StringBuilder();
                }
            }
        }

        return (result == 0L || result == -1L ? null : result);
    }

    public static Integer parseInt(String input) {
        if (input == null || input.isEmpty()) return -1;

        int result = 0;
        StringBuilder number = new StringBuilder();

        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);

            if (Character.isDigit(c)) {
                number.append(c);
            }
            else {
                String str;
                if (Character.isLetter(c) && !(str = number.toString()).isEmpty()) {
                    result += convertInt(Integer.parseInt(str), c);
                    number = new StringBuilder();
                }
            }
        }
        return result;
    }

    private static int convertInt(int value, char unit) {
        return switch (unit) {
            case 'd' -> value * 60 * 60 * 24;
            case 'h' -> value * 60 * 60;
            case 'm' -> value * 60;
            case 's' -> value;
            default -> -1;
        };
    }

    private static long convert(int value, char unit) {
        return switch (unit) {
            case 'y' -> value * TimeUnit.DAYS.toMillis(365L);
            case 'M' -> value * TimeUnit.DAYS.toMillis(30L);
            case 'd' -> value * TimeUnit.DAYS.toMillis(1L);
            case 'h' -> value * TimeUnit.HOURS.toMillis(1L);
            case 'm' -> value * TimeUnit.MINUTES.toMillis(1L);
            case 's' -> value * TimeUnit.SECONDS.toMillis(1L);
            default -> -1L;
        };
    }

    public static String getRemaining(long duration, boolean milliseconds) {
        if (milliseconds && duration < MINUTE) {
            return REMAINING_SECONDS_TRAILING.get().format(duration * 0.001) + 's';
        } else {
            return (duration <= 0 ? "00:00" : DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? "HH:" : "") + "mm:ss"));
        }
    }

    public static String formatDetailed(long time) {
        if (time == 0L) {
            return "0s";
        }

        return DurationFormatUtils.formatDurationWords(time, true, true);
    }

    public static String formatPlaytime(long millis) {
        long seconds = millis / 1000;

        if (seconds < 60) {
            return "<1m";
        }

        long years = seconds / 31536000;
        seconds %= 31536000;

        long months = seconds / 2592000;
        seconds %= 2592000;

        long days = seconds / 86400;
        seconds %= 86400;

        long hours = seconds / 3600;
        seconds %= 3600;

        long minutes = seconds / 60;

        StringBuilder sb = new StringBuilder();
        if (years > 0) sb.append(years).append("y ");
        if (months > 0) sb.append(months).append("mo ");
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m");

        return sb.toString().trim();
    }

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String formatHealth(double health) {
        return HEALTH_FORMATTER.format(health);
    }
}