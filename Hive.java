import processing.core.PImage;

import java.util.List;

public abstract class Hive extends ActivityEnt {

    private static final String STUMP_KEY = "stump";
    public Hive(
            String id,
            Point position,
            List<PImage> images,
            int resourceLimit,
            int resourceCount,
            int actionPeriod,
            int animationPeriod,
            int health,
            int healthLimit)
    {
        super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod, health, healthLimit);
    }


    public abstract boolean transformPlant(WorldModel world,
                                           EventScheduler scheduler,
                                           ImageStore imageStore);

}
