import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Bee_Full extends Bee implements MoveTo {
    public Bee_Full(
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
        Optional<Entity> fullTarget =
                world.findNearest(this.position(), new ArrayList<>(Arrays.asList(HiveUp.class)));

        if (fullTarget.isPresent() && this.moveTo(world,
                fullTarget.get(), scheduler))
        {
            this.transformFull(world, scheduler, imageStore);
        }
        else {
            scheduler.scheduleEvent(this,
                    this.createActivityAction(world, imageStore),
                    this.actionPeriod());
        }
    }

    public void transformFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        Entity miner = WorldModel.createDudeNotFull(this.id(), this.position(),
                this.actionPeriod(),
                this.animationPeriod(),
                this.resourceLimit(),
                this.images());

        world.removeEntity(this);
        this.unscheduleAllEvents(scheduler);

        if(miner instanceof Bee_NotFull){
            world.addEntity(miner);
            ((Bee_NotFull)miner).scheduleActions(scheduler, world, imageStore);
        }
    }

    public boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (adjacent(this.position(), target.position())) {
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
