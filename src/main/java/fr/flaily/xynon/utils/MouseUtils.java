package fr.flaily.xynon.utils;

import fr.flaily.xynon.events.game.EventOverrideInput;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.entity.Entity;
import java.util.Random;

public class MouseUtils {
    
    private final EventOverrideInput inputEvent;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Random random = new Random();

    // State tracking
    private Object lastTarget = null;
    private double offX, offY, offZ;       // Initial random snap point
    private double driftX, driftY, driftZ; // Stable pseudo-random drift point
    private float driftProgress = 0.0f;

    public MouseUtils(EventOverrideInput input) {
        this.inputEvent = input;
    }

    public void moveTo(BlockPos pos, float jerkiness, float driftSpeed) {
        updateTargetOffsets(pos);
        double curX = pos.getX() + interpolate(offX, driftX, driftProgress);
        double curY = pos.getY() + interpolate(offY, driftY, driftProgress);
        double curZ = pos.getZ() + interpolate(offZ, driftZ, driftProgress);
        
        performMove(curX, curY, curZ, jerkiness);
        advanceDrift(driftSpeed);
    }

    public void moveTo(Entity entity, float jerkiness, float driftSpeed) {
        updateTargetOffsets(entity);
        double curX = entity.posX + interpolate(offX, driftX, driftProgress);
        // Target slightly above the feet (waist/chest area)
        double curY = (entity.getEntityBoundingBox().minY + entity.getEyeHeight() * 0.5) + interpolate(offY, driftY, driftProgress);
        double curZ = entity.posZ + interpolate(offZ, driftZ, driftProgress);

        performMove(curX, curY, curZ, jerkiness);
        advanceDrift(driftSpeed);
    }
    private float remainderX = 0;
    private float remainderY = 0;

    private void performMove(double tx, double ty, double tz, float jerkiness) {
        if (mc.thePlayer == null) return;

        double ex = mc.thePlayer.posX;
        double ey = mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight();
        double ez = mc.thePlayer.posZ;

        float[] targetRot = getRotationTo(tx, ty, tz, ex, ey, ez);
        
        float yawError = MathHelper.wrapAngleTo180_float(targetRot[0] - mc.thePlayer.rotationYaw);
        float pitchError = targetRot[1] - mc.thePlayer.rotationPitch;

        // Apply Jerkiness
        if (jerkiness > 0) {
            yawError += (random.nextFloat() - 0.5f) * jerkiness;
            pitchError += (random.nextFloat() - 0.5f) * jerkiness;
        }

        // Distance-based Gain (Optional: increase gain slightly when far away)
        double dist = mc.thePlayer.getDistance(tx, ty, tz);
        float dynamicGain = 0.00878035f;
        
        // If we are very far, we can afford a slightly higher gain to overcome the tiny angles
        if (dist > 4.5) dynamicGain *= 2.2f;

        float maxStep = 1.0f;
        float yawStep = MathHelper.clamp_float(yawError * dynamicGain, -maxStep, maxStep);
        float pitchStep = MathHelper.clamp_float(pitchError * dynamicGain, -maxStep, maxStep);

        float sens = mc.gameSettings.mouseSensitivity;
        float f = sens * 0.6f + 0.2f;
        float gcd = f * f * f * 8.0f * 0.15f; 

        // 1. Calculate raw mouse units
        float rawDx = yawStep / gcd;
        float rawDy = (pitchStep / gcd) * -1; 

        // 2. Add remainders
        float totalDx = rawDx + remainderX;
        float totalDy = rawDy + remainderY;

        // 3. Round to actual pixels
        int dx = Math.round(totalDx);
        int dy = Math.round(totalDy);

        // 4. Update remainders (Crucial: keeps the precision alive)
        remainderX = totalDx - dx;
        remainderY = totalDy - dy;

        // 5. REMOVED totalDirection > 2
        // We only check if dx or dy is non-zero. 
        // The remainder system handles the "slow crawl" to the center.
        if (dx != 0 || dy != 0) {
            inputEvent.moveMouse(dx, dy);
        }
    }
    private void updateTargetOffsets(Object newTarget) {
        if (newTarget != lastTarget) {
            lastTarget = newTarget;
            driftProgress = 0.0f;

            // 1. Initial hit: Completely random for every new block/entity
            offX = random.nextDouble();
            offY = random.nextDouble();
            offZ = random.nextDouble();

            // 2. Drift target: Pseudo-random based on position/ID
            // This stays the same for a specific block every time you look at it
            long seed = 0;
            if (newTarget instanceof BlockPos) {
                BlockPos bp = (BlockPos) newTarget;
                // Using % 20 and prime multipliers to prevent overflow and clear patterns
                seed = (bp.getX() % 20) * 31L + (bp.getY() % 20) * 17L + (bp.getZ() % 20);
            } else if (newTarget instanceof Entity) {
                seed = ((Entity) newTarget).getEntityId();
            }

            Random stableRand = new Random(seed);
            driftX = stableRand.nextDouble();
            driftY = stableRand.nextDouble();
            driftZ = stableRand.nextDouble();
        }
    }

    private void advanceDrift(float speed) {
        if (driftProgress < 1.0f) {
            driftProgress = Math.min(1.0f, driftProgress + speed);
        }
    }

    private double interpolate(double start, double end, float p) {
        return start + (end - start) * p;
    }

    private float[] getRotationTo(double tx, double ty, double tz, double ex, double ey, double ez) {
        double dx = tx - ex;
        double dy = ty - ey;
        double dz = tz - ez;
        double distXZ = MathHelper.sqrt_double(dx * dx + dz * dz);
        return new float[]{
            (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0),
            (float) -(Math.toDegrees(Math.atan2(dy, distXZ)))
        };
    }
}