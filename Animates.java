import processing.core.PImage;

import java.util.List;

public abstract class Animates extends Entity{

    private int animationPeriod;
    public Animates(
            String id, List<PImage> images, Point position,
            int animationPeriod) {
        super(id, images, position);
        this.animationPeriod = animationPeriod;
    }

    public int animationPeriod(){
        return animationPeriod;
    }

    public int getAnimationPeriod(){
        return animationPeriod;
    }

    public abstract void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore);
}
