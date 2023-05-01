import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Bear extends ActivityEnt {
    public Bear(
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
        Optional<Entity> target = world.findNearest(this.position(), new ArrayList<>(Arrays.asList(HiveUp.class, FlowerFull.class)));
        if (target.isPresent()) {
            Point tgtPos = target.get().position();
            if (adjacent(this.position(), tgtPos)) {
                if(target.get() instanceof FlowerFull){
                    ((FlowerFull)target.get()).setHealth(((FlowerFull)target.get()).health() - 1);
                }
                if(target.get() instanceof HiveUp){
                    ((HiveUp)target.get()).setHealth(((HiveUp)target.get()).health() - 1);
                }
            }
        }

        scheduler.scheduleEvent(this,
                this.createActivityAction(world, imageStore),
                this.actionPeriod());
    }

//    public Point nextPosition(
//            WorldModel world, Point destPos)
//    {
//        int horiz = Integer.signum(destPos.x - this.position().x);
//        Point newPos = new Point(this.position().x + horiz, this.position().y);
//
//        if (horiz == 0 || world.isOccupied(newPos)) {
//            int vert = Integer.signum(destPos.y - this.position().y);
//            newPos = new Point(this.position().x, this.position().y + vert);
//
//            if (vert == 0 || world.isOccupied(newPos)) {
//                newPos = this.position();
//            }
//        }
//
//        return newPos;
//    }

//    public boolean moveTo(
//            WorldModel world,
//            Point target,
//            EventScheduler scheduler) {
//        {
//            if (this.position().equals(target)) {
//                return true;
//            } else {
//                Optional<Entity> occupant = world.getOccupant(target);
//                if (occupant.isPresent()) {
//                    unscheduleAllEvents(scheduler);
//                }
//                world.moveEntity(this, target);
//                return false;
//            }
//        }
//    }
}
