import processing.core.PImage;

import java.util.List;

public abstract class ActivityEnt extends Animates{

    private String id;
    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int health;
    private int healthLimit;
    public ActivityEnt(
            String id,
            Point position,
            List<PImage> images,
            int resourceLimit,
            int resourceCount,
            int actionPeriod,
            int animationPeriod,
            int health,
            int healthLimit) {
        super(id, images, position, animationPeriod);
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.health = health;
        this.healthLimit = healthLimit;
    }


    public int actionPeriod(){
        return this.actionPeriod;
    }

    public int health(){
        return this.health;
    }

    public int healthLimit() {
        return healthLimit;
    }

    public int resourceCount() {
        return resourceCount;
    }

    public int resourceLimit() {
        return resourceLimit;
    }

    public void setResourceCount(int resourceCount) {
        this.resourceCount = resourceCount;
    }

    public void setHealth(int health) {
        this.health = health;
    }


    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore){
        scheduler.scheduleEvent(this,
                this.createActivityAction(world, imageStore),
                this.actionPeriod);
        scheduler.scheduleEvent(this,
                this.createAnimationAction(0),
                this.getAnimationPeriod());
    }

    public void unscheduleAllEvents(EventScheduler scheduler)
    {
        List<Event> pending = scheduler.getPendingEvents().remove(this);

        if (pending != null) {
            for (Event event : pending) {
                scheduler.getEventQueue().remove(event);
            }
        }
    }
    public abstract void executeActivity(WorldModel world,
                                         ImageStore imageStore,
                                         EventScheduler scheduler);

    public static boolean adjacent(Point p1, Point p2) {
        return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) || (p1.y == p2.y
                && Math.abs(p1.x - p2.x) == 1);
    }

}
