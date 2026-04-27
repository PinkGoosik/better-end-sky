package better_end_sky.util;

import net.minecraft.util.RandomSource;

public class MHelper {
    public static float randRange(float min, float max, RandomSource random) {
        return min + random.nextFloat() * (max - min);
    }
}