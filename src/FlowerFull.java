import processing.core.PImage;

import java.util.List;

public class FlowerFull extends Flower implements Transform{
    public FlowerFull(
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

        if (!this.transformFlower(world, scheduler, imageStore)) {
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
            Entity flower = WorldModel.createFlowerSapling("FlowerSapling_" + this.id(), this.position(),
                    imageStore.getImageList(WorldModel.FLOWERSAPLING_KEY));

            world.removeEntity(this);
            this.unscheduleAllEvents(scheduler);

            if(flower instanceof FlowerSapling){
                world.addEntity(flower);
                ((FlowerSapling)flower).scheduleActions(scheduler, world, imageStore);
            }

            return true;
        }

        return false;
    }

    public boolean transformFlower(WorldModel world,
                                           EventScheduler scheduler,
                                           ImageStore imageStore)
    {
        return this.transform(world, scheduler, imageStore);
    }
}
