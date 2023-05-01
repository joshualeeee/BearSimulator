import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class FlowerSapling extends Flower implements Transform{

    private static final int FLOWER_ANIMATION_MAX = 600;
    private static final int FLOWER_ANIMATION_MIN = 50;
    private static final int FLOWER_ACTION_MAX = 1400;
    private static final int FLOWER_ACTION_MIN = 1000;
    private static final int FLOWER_HEALTH_MAX = 3;
    private static final int FLOWER_HEALTH_MIN = 1;

    public FlowerSapling(
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

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        setHealth(this.health() + 1);
        if (!this.transformFlower(world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(this,
                    this.createActivityAction(world, imageStore),
                    this.actionPeriod());
        }
    }
    public boolean transform(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.health() >= this.healthLimit())
        {
            Entity flower = WorldModel.createFlowerFull("FlowerFull_" + this.id(), this.position(),
                    getNumFromRange(FLOWER_ACTION_MAX, FLOWER_ACTION_MIN),
                    FLOWER_ANIMATION_MIN,
                    FLOWER_HEALTH_MIN,
                    imageStore.getImageList(WorldModel.FLOWERFULL_KEY));

            world.removeEntity(this);
            this.unscheduleAllEvents(scheduler);

            world.addEntity(flower);
            if(flower instanceof FlowerFull){
                ((FlowerFull)flower).scheduleActions(scheduler, world, imageStore);
            }
            return true;
        }
        return false;
    }

    private static int getNumFromRange(int max, int min)
    {
        Random rand = new Random();
        return min + rand.nextInt(
                max - min);
    }

    public boolean transformFlower(WorldModel world,
                                  EventScheduler scheduler,
                                  ImageStore imageStore)
    {
        return this.transform(world, scheduler, imageStore);
    }
}
