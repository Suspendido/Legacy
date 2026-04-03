package me.comunidad.dev.legacy.utils.extra;

import lombok.Getter;

/*
Utility class used for easier and faster replacing.
 */
@Getter
public class FastReplaceString {

    private String string;

    public FastReplaceString(String string) {
        this.string = string;
    }

    public FastReplaceString replaceAll(String target, String replacement) {
        int targetLength = target.length();
        int idx2 = string.indexOf(target);

        if (targetLength == 0) return this;
        if (idx2 == -1) return this;

        StringBuilder buffer = new StringBuilder(targetLength > replacement.length() ? string.length() : string.length() * 2);
        int idx1 = 0;

        do {

            buffer.append(string, idx1, idx2);
            buffer.append(replacement);
            idx1 = idx2 + targetLength;
            idx2 = string.indexOf(target, idx1);

        } while (idx2 > 0);

        buffer.append(string, idx1, string.length());
        string = buffer.toString();
        return this;
    }

    public String endResult() {
        return string;
    }
}