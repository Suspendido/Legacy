package me.comunidad.dev.legacy.utils.extra;

import lombok.Getter;
import me.comunidad.dev.legacy.Core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class DecimalFormat {

    private final java.text.DecimalFormat decimalFormat;
    private final Map<Double, String> cache;

    public DecimalFormat(Core instance, String pattern) {
        this.decimalFormat = new java.text.DecimalFormat(pattern);
        this.cache = new ConcurrentHashMap<>();
        instance.getDecimalFormats().add(this);
    }

    public String format(double d) {
        String cached = cache.get(d);

        if (cached != null) {
            return cached;
        }

        String formatted = decimalFormat.format(d);
        cache.put(d, formatted);
        return formatted;
    }

    public void clean() {
        cache.clear();
    }
}