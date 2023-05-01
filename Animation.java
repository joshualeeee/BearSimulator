public class Animation extends Action{
    public Animation(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount)
    {
        super(entity, world, imageStore, repeatCount);
    }

    public void executeAction(
            EventScheduler scheduler)
    {
        this.getEntity().nextImage();

        if (this.getRepeatCount() != 1) {
            scheduler.scheduleEvent(this.getEntity(),
                    this.getEntity().createAnimationAction(
                            Math.max(this.getRepeatCount() - 1,
                                    0)),
                    ((Animates)this.getEntity()).animationPeriod());
        }
    }

}
