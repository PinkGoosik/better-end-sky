package better_end_sky.render;

import better_end_sky.util.BackgroundInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;

public class CustomFogRenderer {

    public static boolean applyFogDensity(Camera camera, float viewDistance) {
        FogType fogType = camera.getFluidInCamera();
        if (fogType != FogType.NONE) {
            BackgroundInfo.fogDensity = 1;
            return false;
        }
        Entity entity = camera.getEntity();

        if (!isForcedDimension(entity.getLevel())) {
            BackgroundInfo.fogDensity = 1;
            return false;
        }

        float fog = 0.75F;
        BackgroundInfo.fogDensity = fog;

        float fogStart = viewDistance * 0.25F / fog; // In vanilla - 0
        float fogEnd = viewDistance / fog;

        if (entity instanceof LivingEntity living) {
            MobEffectInstance effect = living.getEffect(MobEffects.BLINDNESS);
            if (effect != null) {
                int duration = effect.getDuration();
                if (duration > 20) {
                    fogStart = 0;
                    fogEnd *= 0.03F;
                    BackgroundInfo.blindness = 1;
                }
                else {
                    float delta = (float) duration / 20F;
                    BackgroundInfo.blindness = delta;
                    fogStart = Mth.lerp(delta, fogStart, 0);
                    fogEnd = Mth.lerp(delta, fogEnd, fogEnd * 0.03F);
                }
            }
            else {
                BackgroundInfo.blindness = 0;
            }
        }
        RenderSystem.setShaderFogStart(fogStart);
        RenderSystem.setShaderFogEnd(fogEnd);

        return true;
    }

    private static boolean isForcedDimension(Level level) {
        return level.dimension() == Level.END;
    }
}
