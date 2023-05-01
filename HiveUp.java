import processing.core.PImage;

import java.util.List;

public class HiveUp extends Hive implements Transform{
    public HiveUp(
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

        if (!this.transformPlant(world, scheduler, imageStore)) {
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
        if (this.health() <= 0) {
            Entity sapling = WorldModel.createHiveDown("sapling_" + this.id(), this.position(),
                    imageStore.getImageList(WorldModel.HIVEDOWN_KEY));

            world.removeEntity(this);
            this.unscheduleAllEvents(scheduler);

            if(sapling instanceof HiveDown){
                world.addEntity(sapling);
                ((HiveDown)sapling).scheduleActions(scheduler, world, imageStore);
            }

            return true;
        }

        return false;
    }

    public boolean transformPlant(WorldModel world,
                                           EventScheduler scheduler,
                                           ImageStore imageStore)
    {
        return this.transform(world, scheduler, imageStore);
    }
}
