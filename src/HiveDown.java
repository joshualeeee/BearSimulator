import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class HiveDown extends Hive implements Transform{

    private static final int HIVEUP_ANIMATION_MAX = 600;
    private static final int HIVEUP_ANIMATION_MIN = 50;
    private static final int HIVEUP_ACTION_MAX = 1400;
    private static final int HIVEUP_ACTION_MIN = 1000;
    private static final int HIVEUP_HEALTH_MAX = 3;
    private static final int HIVEUP_HEALTH_MIN = 1;

    public HiveDown(
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
        if (!this.transformPlant(world, scheduler, imageStore))
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
            Entity tree = WorldModel.createHiveUp("tree_" + this.id(), this.position(),
                    getNumFromRange(HIVEUP_ACTION_MAX, HIVEUP_ACTION_MIN),
                    getNumFromRange(HIVEUP_ANIMATION_MAX, HIVEUP_ANIMATION_MIN),
                    getNumFromRange(HIVEUP_HEALTH_MAX, HIVEUP_HEALTH_MIN),
                    imageStore.getImageList(WorldModel.HIVEUP_KEY));

            world.removeEntity(this);
            this.unscheduleAllEvents(scheduler);

            world.addEntity(tree);
            if(tree instanceof HiveUp){
                ((HiveUp)tree).scheduleActions(scheduler, world, imageStore);
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

    public boolean transformPlant(WorldModel world,
                                  EventScheduler scheduler,
                                  ImageStore imageStore)
    {
        return this.transform(world, scheduler, imageStore);
    }
}
