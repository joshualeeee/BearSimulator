import processing.core.PImage;

import java.util.List;

public class Obstacle extends Animates {
    public Obstacle(
            String id, List<PImage> images, Point position,
            int animationPeriod)
    {
        super(id, images, position, animationPeriod);
    }

    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore){
        scheduler.scheduleEvent(this,
                this.createAnimationAction(0),
                this.getAnimationPeriod());
    }

}
