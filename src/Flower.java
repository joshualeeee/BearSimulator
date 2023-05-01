import processing.core.PImage;

import java.util.List;

public abstract class Flower extends ActivityEnt {

    public Flower(
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


    public abstract boolean transformFlower(WorldModel world,
                                           EventScheduler scheduler,
                                           ImageStore imageStore);

}
