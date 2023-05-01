/**
 * An action that can be taken by an entity
 */
public abstract class Action
{
    private Entity entity;
    private WorldModel world;
    private ImageStore imageStore;
    private int repeatCount;

    public Action(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    public Entity getEntity() {
        return entity;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public WorldModel getWorld() {
        return world;
    }

    public ImageStore getImageStore() {
        return imageStore;
    }

    public abstract void executeAction(EventScheduler scheduler);


}
