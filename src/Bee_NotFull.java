import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Bee_NotFull extends Bee implements Transform, MoveTo{
    public Bee_NotFull(
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
        Optional<Entity> target =
                world.findNearest(this.position(), new ArrayList<>(Arrays.asList(FlowerFull.class)));

        if (!target.isPresent() || !this.moveTo(world,
                target.get(),
                scheduler)
                || !this.transform(world, scheduler, imageStore))
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
        if (this.resourceCount() >= this.resourceLimit()) {
            Entity miner = WorldModel.createDudeFull(this.id(), this.position(),
                    this.actionPeriod(),
                    this.animationPeriod(),
                    this.resourceLimit(),
                    this.images());

            world.removeEntity(this);
            this.unscheduleAllEvents(scheduler);

            if(miner instanceof Bee_Full){
                world.addEntity(miner);
                ((Bee_Full)miner).scheduleActions(scheduler, world, imageStore);
            }

            return true;
        }

        return false;
    }

    public boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (adjacent(this.position(), target.position())) {
            setResourceCount(this.resourceCount() + 1);
            if(target instanceof FlowerFull){
                ((FlowerFull)target).setHealth(((FlowerFull)target).health() - 1);
            }
            return true;
        }
        else {
            Point nextPos = this.nextPosition(world, target.position());

            if (!this.position().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    unscheduleAllEvents(scheduler);
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }
}
