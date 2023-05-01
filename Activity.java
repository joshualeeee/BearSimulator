public class Activity extends Action{
    public Activity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount)
    {
        super(entity, world, imageStore, repeatCount);
    }

    public void executeAction(EventScheduler scheduler)
    {
        Entity ent = this.getEntity();
        if(ent instanceof HiveDown){
            ((HiveDown)ent).executeActivity(this.getWorld(),
                    this.getImageStore(), scheduler);
            return;
        }
        if(ent instanceof HiveUp){
            ((HiveUp)ent).executeActivity(this.getWorld(),
                    this.getImageStore(), scheduler);
            return;
        }
        if(ent instanceof FlowerSapling){
            ((FlowerSapling)ent).executeActivity(this.getWorld(),
                    this.getImageStore(), scheduler);
            return;
        }
        if(ent instanceof FlowerFull){
            ((FlowerFull)ent).executeActivity(this.getWorld(),
                    this.getImageStore(), scheduler);
            return;
        }
        if(ent instanceof Bear){
            ((Bear)ent).executeActivity(this.getWorld(),
                    this.getImageStore(), scheduler);
            return;
        }
        if(ent instanceof Bee_NotFull){
            ((Bee_NotFull)ent).executeActivity(this.getWorld(),
                    this.getImageStore(), scheduler);
            return;
        }
        if(ent instanceof Bee_Full){
            ((Bee_Full)ent).executeActivity(this.getWorld(),
                    this.getImageStore(), scheduler);
        }
    }

}
