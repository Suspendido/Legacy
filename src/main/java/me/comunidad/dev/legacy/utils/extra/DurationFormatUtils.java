package me.comunidad.dev.legacy.utils.extra;

import me.comunidad.dev.legacy.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class DurationFormatUtils {

    public static String formatDuration(long durationMillis, String format) {
        return formatDuration(durationMillis, format, true);
    }

    public static String formatDuration(long durationMillis, String format, boolean padWithZeros) {
        inclusiveBetween(0L, Long.MAX_VALUE, durationMillis, "durationMillis must not be negative");
        Token[] tokens = lexx(format);
        long days = 0L;
        long hours = 0L;
        long minutes = 0L;
        long seconds = 0L;
        long milliseconds = durationMillis;
        if (Token.containsTokenWithValue(tokens, "d")) {
            days = durationMillis / 86400000L;
            milliseconds = durationMillis - days * 86400000L;
        }

        if (Token.containsTokenWithValue(tokens, "H")) {
            hours = milliseconds / 3600000L;
            milliseconds -= hours * 3600000L;
        }

        if (Token.containsTokenWithValue(tokens, "m")) {
            minutes = milliseconds / 60000L;
            milliseconds -= minutes * 60000L;
        }

        if (Token.containsTokenWithValue(tokens, "s")) {
            seconds = milliseconds / 1000L;
            milliseconds -= seconds * 1000L;
        }

        return format(tokens, days, hours, minutes, seconds, milliseconds, padWithZeros);
    }

    public static String formatDurationWords(long durationMillis, boolean suppressLeadingZeroElements, boolean suppressTrailingZeroElements) {
        String duration = formatDuration(durationMillis, "d' days 'H' hours 'm' minutes 's' seconds'");
        String tmp;
        if (suppressLeadingZeroElements) {
            duration = " " + duration;
            tmp = replaceOnce(duration, " 0 days", "");
            if (tmp.length() != duration.length()) {
                duration = tmp;
                tmp = replaceOnce(tmp, " 0 hours", "");
                if (tmp.length() != duration.length()) {
                    tmp = replaceOnce(tmp, " 0 minutes", "");
                    duration = tmp;
                }
            }

            if (!duration.isEmpty()) {
                duration = duration.substring(1);
            }
        }

        if (suppressTrailingZeroElements) {
            tmp = replaceOnce(duration, " 0 seconds", "");
            if (tmp.length() != duration.length()) {
                duration = tmp;
                tmp = replaceOnce(tmp, " 0 minutes", "");
                if (tmp.length() != duration.length()) {
                    duration = tmp;
                    tmp = replaceOnce(tmp, " 0 hours", "");
                    if (tmp.length() != duration.length()) {
                        duration = replaceOnce(tmp, " 0 days", "");
                    }
                }
            }
        }

        duration = " " + duration;
        duration = replaceOnce(duration, " 1 seconds", " 1 second");
        duration = replaceOnce(duration, " 1 minutes", " 1 minute");
        duration = replaceOnce(duration, " 1 hours", " 1 hour");
        duration = replaceOnce(duration, " 1 days", " 1 day");
        return duration.trim();
    }

    public static String replaceOnce(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, 1);
    }

    public static String replace(String text, String searchString, String replacement, int max) {
        return replace(text, searchString, replacement, max, false);
    }

    private static String replace(String text, String searchString, String replacement, int max, boolean ignoreCase) {
        if (!isEmpty(text) && !isEmpty(searchString) && replacement != null && max != 0) {
            if (ignoreCase) {
                searchString = searchString.toLowerCase();
            }

            int start = 0;
            int end = ignoreCase ? indexOfIgnoreCase(text, searchString, start) : indexOf(text, searchString, start);
            if (end == -1) {
                return text;
            } else {
                int replLength = searchString.length();
                int increase = Math.max(replacement.length() - replLength, 0);
                increase *= max < 0 ? 16 : Math.min(max, 64);

                StringBuilder buf;
                for (buf = new StringBuilder(text.length() + increase); end != -1; end = ignoreCase ? indexOfIgnoreCase(text, searchString, start) : indexOf(text, searchString, start)) {
                    buf.append(text, start, end).append(replacement);
                    start = end + replLength;
                    --max;
                    if (max == 0) {
                        break;
                    }
                }

                buf.append(text, start, text.length());
                return buf.toString();
            }
        } else {
            return text;
        }
    }

    public static int indexOfIgnoreCase(CharSequence str, CharSequence searchStr, int startPos) {
        if (str != null && searchStr != null) {
            if (startPos < 0) {
                startPos = 0;
            }

            int endLimit = str.length() - searchStr.length() + 1;
            if (startPos > endLimit) {
                return -1;
            } else if (searchStr.isEmpty()) {
                return startPos;
            } else {
                for (int i = startPos; i < endLimit; ++i) {
                    if (regionMatches(str, i, searchStr, searchStr.length())) {
                        return i;
                    }
                }

                return -1;
            }
        } else {
            return -1;
        }
    }

    public static int indexOf(CharSequence seq, CharSequence searchSeq, int startPos) {
        return seq != null && searchSeq != null ? ind(seq, searchSeq, startPos) : -1;
    }

    public static int ind(CharSequence cs, CharSequence searchChar, int start) {
        if (cs instanceof String) {
            return ((String) cs).indexOf(searchChar.toString(), start);
        } else if (cs instanceof StringBuilder) {
            return ((StringBuilder) cs).indexOf(searchChar.toString(), start);
        } else {
            return cs instanceof StringBuffer ? ((StringBuffer) cs).indexOf(searchChar.toString(), start) : cs.toString().indexOf(searchChar.toString(), start);
        }
    }


    public static String center(String str, int size) {
        return center(str, size, ' ');
    }

    public static String center(String str, int size, char padChar) {
        if (str != null && size > 0) {
            int strLen = str.length();
            int pads = size - strLen;
            if (pads > 0) {
                str = leftPad(str, strLen + pads / 2, padChar);
                str = rightPad(str, size, padChar);
            }
        }
        return str;
    }

    public static String center(String str, int size, String padStr) {
        if (str != null && size > 0) {
            if (isEmpty(padStr)) {
                padStr = " ";
            }

            int strLen = str.length();
            int pads = size - strLen;
            if (pads > 0) {
                str = leftPad(str, strLen + pads / 2, padStr);
                str = rightPad(str, size, padStr);
            }
        }
        return str;
    }

    private static boolean regionMatches(CharSequence cs, int thisStart, CharSequence substring, int length) {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(true, thisStart, (String) substring, 0, length);
        } else {
            int index1 = thisStart;
            int index2 = 0;
            int tmpLen = length;
            int srcLen = cs.length() - thisStart;
            int otherLen = substring.length();
            if (thisStart >= 0 && length >= 0) {
                if (srcLen >= length && otherLen >= length) {
                    while (tmpLen-- > 0) {
                        char c1 = cs.charAt(index1++);
                        char c2 = substring.charAt(index2++);
                        if (c1 != c2) {
                            char u1 = Character.toUpperCase(c1);
                            char u2 = Character.toUpperCase(c2);
                            if (u1 != u2 && Character.toLowerCase(u1) != Character.toLowerCase(u2)) {
                                return false;
                            }
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.isEmpty();
    }

    public static void inclusiveBetween(long start, long end, long value, String message) {
        if (value < start || value > end) {
            throw new IllegalArgumentException(message);
        }
    }

    static String format(Token[] tokens, long days, long hours, long minutes, long seconds, long milliseconds, boolean padWithZeros) {
        StringBuilder buffer = new StringBuilder();
        boolean lastOutputSeconds = false;

        for (Token token : tokens) {
            Object value = token.getValue();
            int count = token.getCount();
            if (value instanceof StringBuilder) {
                buffer.append(value);
            } else if (value.equals("y")) {
                buffer.append(paddedValue(0, padWithZeros, count));
                lastOutputSeconds = false;
            } else if (value.equals("M")) {
                buffer.append(paddedValue(0, padWithZeros, count));
                lastOutputSeconds = false;
            } else if (value.equals("d")) {
                buffer.append(paddedValue(days, padWithZeros, count));
                lastOutputSeconds = false;
            } else if (value.equals("H")) {
                buffer.append(paddedValue(hours, padWithZeros, count));
                lastOutputSeconds = false;
            } else if (value.equals("m")) {
                buffer.append(paddedValue(minutes, padWithZeros, count));
                lastOutputSeconds = false;
            } else if (value.equals("s")) {
                buffer.append(paddedValue(seconds, padWithZeros, count));
                lastOutputSeconds = true;
            } else if (value.equals("S")) {
                if (lastOutputSeconds) {
                    int width = padWithZeros ? Math.max(3, count) : 3;
                    buffer.append(paddedValue(milliseconds, true, width));
                } else {
                    buffer.append(paddedValue(milliseconds, padWithZeros, count));
                }

                lastOutputSeconds = false;
            }
        }

        return buffer.toString();
    }

    private static String paddedValue(long value, boolean padWithZeros, int count) {
        String longString = Long.toString(value);
        return padWithZeros ? leftPad(longString, count, '0') : longString;
    }

    public static String rightPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        } else {
            if (isEmpty(padStr)) {
                padStr = " ";
            }

            int padLen = padStr.length();
            int strLen = str.length();
            int pads = size - strLen;
            if (pads <= 0) {
                return str;
            } else if (padLen == 1 && pads <= 8192) {
                return rightPad(str, size, padStr.charAt(0));
            } else if (pads == padLen) {
                return str.concat(padStr);
            } else if (pads < padLen) {
                return str.concat(padStr.substring(0, pads));
            } else {
                char[] padding = new char[pads];
                char[] padChars = padStr.toCharArray();

                for(int i = 0; i < pads; ++i) {
                    padding[i] = padChars[i % padLen];
                }

                return str.concat(new String(padding));
            }
        }
    }

    public static String rightPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        } else {
            int pads = size - str.length();
            if (pads <= 0) {
                return str;
            } else {
                return pads > 8192 ? rightPad(str, size, String.valueOf(padChar)) : str.concat(padding(pads, padChar));
            }
        }
    }

    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        } else {
            int pads = size - str.length();
            if (pads <= 0) {
                return str;
            } else {
                return pads > 8192 ? leftPad(str, size, String.valueOf(padChar)) : Utils.repeat(String.valueOf(padChar), pads).concat(str);
            }
        }
    }

    public static String leftPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        } else {
            if (isEmpty(padStr)) {
                padStr = " ";
            }

            int padLen = padStr.length();
            int strLen = str.length();
            int pads = size - strLen;
            if (pads <= 0) {
                return str;
            } else if (padLen == 1 && pads <= 8192) {
                return leftPad(str, size, padStr.charAt(0));
            } else if (pads == padLen) {
                return padStr.concat(str);
            } else if (pads < padLen) {
                return padStr.substring(0, pads).concat(str);
            } else {
                char[] padding = new char[pads];
                char[] padChars = padStr.toCharArray();

                for(int i = 0; i < pads; ++i) {
                    padding[i] = padChars[i % padLen];
                }

                return (new String(padding)).concat(str);
            }
        }
    }

    private static String padding(int repeat, char padChar) throws IndexOutOfBoundsException {
        if (repeat < 0) {
            throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
        } else {
            char[] buf = new char[repeat];
            Arrays.fill(buf, padChar);
            return new String(buf);
        }
    }

    static Token[] lexx(String format) {
        ArrayList<Token> list = new ArrayList<>(format.length());
        boolean inLiteral = false;
        StringBuilder buffer = null;
        Token previous = null;

        for (int i = 0; i < format.length(); ++i) {
            char ch = format.charAt(i);
            if (inLiteral && ch != '\'') {
                buffer.append(ch);
            } else {
                String value = null;
                switch (ch) {
                    case '\'':
                        if (inLiteral) {
                            buffer = null;
                            inLiteral = false;
                        } else {
                            buffer = new StringBuilder();
                            list.add(new Token(buffer));
                            inLiteral = true;
                        }
                        break;
                    case 'H':
                        value = "H";
                        break;
                    case 'M':
                        value = "M";
                        break;
                    case 'S':
                        value = "S";
                        break;
                    case 'd':
                        value = "d";
                        break;
                    case 'm':
                        value = "m";
                        break;
                    case 's':
                        value = "s";
                        break;
                    case 'y':
                        value = "y";
                        break;
                    default:
                        if (buffer == null) {
                            buffer = new StringBuilder();
                            list.add(new Token(buffer));
                        }

                        buffer.append(ch);
                }

                if (value != null) {
                    if (previous != null && previous.getValue().equals(value)) {
                        previous.increment();
                    } else {
                        Token token = new Token(value);
                        list.add(token);
                        previous = token;
                    }

                    buffer = null;
                }
            }
        }

        if (inLiteral) {
            throw new IllegalArgumentException("Unmatched quote in format: " + format);
        } else {
            return list.toArray(Token.EMPTY_ARRAY);
        }
    }

    static class Token {
        private static final Token[] EMPTY_ARRAY = new Token[0];
        private final Object value;
        private int count;

        static boolean containsTokenWithValue(Token[] tokens, Object value) {
            for (Token token : tokens) {
                if (token.getValue() == value) {
                    return true;
                }
            }

            return false;
        }

        Token(Object value) {
            this.value = value;
            this.count = 1;
        }

        void increment() {
            ++this.count;
        }

        int getCount() {
            return this.count;
        }

        Object getValue() {
            return this.value;
        }

        public boolean equals(Object obj2) {
            if (obj2 instanceof Token tok2) {
                if (this.value.getClass() != tok2.value.getClass()) {
                    return false;
                } else if (this.count != tok2.count) {
                    return false;
                } else if (this.value instanceof StringBuilder) {
                    return this.value.toString().equals(tok2.value.toString());
                } else if (this.value instanceof Number) {
                    return this.value.equals(tok2.value);
                } else {
                    return this.value == tok2.value;
                }
            } else {
                return false;
            }
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return Utils.repeat(this.value.toString(), this.count);
        }
    }
}
