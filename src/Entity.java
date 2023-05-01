import processing.core.PImage;

import java.util.List;

public class Entity {
    private List<PImage> images;
    private int imageIndex;
    private Point position;
    private String id;
    private ImageStore imageStore;

    public Entity(
            String id, List<PImage> images, Point position)
    {
        this.id = id;
        this.images = images;
        this.imageIndex = 0;
        this.position = position;
    }

    public List<PImage> images() {
        return images;
    }

    public PImage getCurrentImage() {
        return (this).images.get((this).imageIndex);
    }

    public void nextImage() {
        this.imageIndex = (this.imageIndex + 1) % this.images.size();
    }

    public String id(){
        return this.id;
    }

    public Point position(){
        return this.position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Action createActivityAction(
            WorldModel world, ImageStore imageStore)
    {
        return new Activity( this, world, imageStore, 0);
    }

    public Action createAnimationAction(int repeatCount) {
        return new Animation(this, null, null,
                repeatCount);
    }

}
