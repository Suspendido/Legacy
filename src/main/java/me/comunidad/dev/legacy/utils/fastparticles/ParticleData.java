package me.comunidad.dev.legacy.utils.fastparticles;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public interface ParticleData {

    static ParticleData of(ItemStack item) {
        return new AbstractParticleData(Objects.requireNonNull(item, "item"));
    }

    static DustOptions createDustOptions(Color color, float size) {
        Objects.requireNonNull(color, "color");
        if (ParticleTypes.DUST_OPTIONS_CLASS == null) {
            return new DustOptions(color);
        }

        return new DustOptions(new Particle.DustOptions(color, size));
    }

    class AbstractParticleData implements ParticleData {
        final Object data;

        private AbstractParticleData(Object data) {
            this.data = data;
        }
    }

    class BlockData extends AbstractParticleData {
        private BlockData(Object data) {
            super(data);
        }
    }

    class DustOptions extends AbstractParticleData {
        private DustOptions(Object data) {
            super(data);
        }
    }

    class DustTransition extends DustOptions {

        private DustTransition(Object data) {
            super(data);
        }
    }
}
