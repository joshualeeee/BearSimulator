import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Optional;

import processing.core.*;

public final class VirtualWorld extends PApplet
{
    public static final int TIMER_ACTION_PERIOD = 100;

    public static final int VIEW_WIDTH = 640;
    public static final int VIEW_HEIGHT = 480;
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;
    public static final int WORLD_WIDTH_SCALE = 2;
    public static final int WORLD_HEIGHT_SCALE = 2;

    public static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    public static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
    public static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
    public static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

    public static final String IMAGE_LIST_FILE_NAME = "imagelist";
    public static final String DEFAULT_IMAGE_NAME = "background_default";
    public static final int DEFAULT_IMAGE_COLOR = 0x808080;

    public static String LOAD_FILE_NAME = "world.sav";

    public static final String FAST_FLAG = "-fast";
    public static final String FASTER_FLAG = "-faster";
    public static final String FASTEST_FLAG = "-fastest";
    public static final double FAST_SCALE = 0.5;
    public static final double FASTER_SCALE = 0.25;
    public static final double FASTEST_SCALE = 0.10;

    private static double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;
    private Bear bear;

    private long nextTime;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        this.imageStore = new ImageStore(
                createImageColored(TILE_WIDTH, TILE_HEIGHT,
                                   DEFAULT_IMAGE_COLOR));
        this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
                                    createDefaultBackground(imageStore));
        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH,
                                  TILE_HEIGHT);
        this.scheduler = new EventScheduler(timeScale);

        loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
        loadWorld(world, LOAD_FILE_NAME, imageStore);

        Entity entity = world.createBear("bear_2_12", new Point(12, 2), 51, 51, imageStore.getImageList("bear"));

        world.tryAddEntity(entity);

        if (entity instanceof Bear){
            this.bear = ((Bear)entity);
        }

        scheduleActions(world, scheduler, imageStore);

        nextTime = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
    }

    public void draw() {
        long time = System.currentTimeMillis();
        if (time >= nextTime) {
            this.scheduler.updateOnTime(time);
            nextTime = time + TIMER_ACTION_PERIOD;
        }
        view.drawViewport();
    }



    // Just for debugging and for P5
    // Be sure to refactor this method as appropriate
    public void mousePressed() {
        Point pressed = mouseToPoint(mouseX, mouseY);
        System.out.println("CLICK! " + pressed.x + ", " + pressed.y);

        Optional<Entity> entityOptional = world.getOccupant(pressed);
        if (entityOptional.isPresent())
        {
            Entity entity = entityOptional.get();
            int h = 0;
            if(entity instanceof ActivityEnt){
                h = ((ActivityEnt)entity).health();
            }
            System.out.println(entity.id() + ": " + entity.getClass() + " : " + h);
        }

        Entity hive = WorldModel.createHiveDown("hivedown_" + pressed, pressed,
                imageStore.getImageList(WorldModel.HIVEDOWN_KEY));

        Optional<Entity> occupant = world.getOccupant(pressed);
        if(occupant.isPresent()){
            Entity ent = occupant.get();
            if(ent instanceof ActivityEnt && !(ent instanceof Bear)){
                world.removeEntity(ent);
                ((ActivityEnt)ent).unscheduleAllEvents(scheduler);
            }
        }
        if(hive instanceof HiveDown){
            world.addEntity(hive);
            ((HiveDown)hive).scheduleActions(scheduler, world, imageStore);
        }

        Point bee_pos = new Point(pressed.x, pressed.y + 1);
        Point bee_pos2 = new Point(pressed.x, pressed.y - 1);

        Entity bee = WorldModel.createDudeNotFull("bee_" + bee_pos, bee_pos,
                400,
                100,
                2,
                imageStore.getImageList(WorldModel.BEE_KEY));

        Entity bee_backup = WorldModel.createDudeNotFull("bee_" + bee_pos2, bee_pos2,
                400,
                100,
                2,
                imageStore.getImageList(WorldModel.BEE_KEY));

        occupant = world.getOccupant(bee_pos);
        Optional<Entity> occupant2 = world.getOccupant(bee_pos2);
        boolean bearblock = false;
        if(occupant.isPresent()){
            Entity ent = occupant.get();
            if(ent instanceof ActivityEnt && !(ent instanceof Bear) && !(ent instanceof Obstacle)){
                world.removeEntity(ent);
                ((ActivityEnt)ent).unscheduleAllEvents(scheduler);
                bearblock = true;
            }
            else{
                if (occupant2.isPresent()){
                    ent = occupant2.get();
                    if(ent instanceof ActivityEnt && !(ent instanceof Bear)){
                        world.removeEntity(ent);
                        ((ActivityEnt)ent).unscheduleAllEvents(scheduler);
                        bearblock = false;
                    }
                }
            }
        }
        if(bee instanceof Bee_NotFull && !bearblock){
            world.addEntity(bee);
            ((Bee_NotFull)bee).scheduleActions(scheduler, world, imageStore);
        }
        if(bee_backup instanceof Bee_NotFull && bearblock){
            world.addEntity(bee_backup);
            ((Bee_NotFull)bee_backup).scheduleActions(scheduler, world, imageStore);
        }




        Point pt = new Point(pressed.x, pressed.y);
        String id = "butter";
        world.setBackground(pt, new Background(id, imageStore.getImageList(id)));
        Point pt1 = new Point(pressed.x + 1, pressed.y);
        world.setBackground(pt1, new Background(id, imageStore.getImageList(id)));
        Point pt2 = new Point(pressed.x - 1, pressed.y);
        world.setBackground(pt2, new Background(id, imageStore.getImageList(id)));
        Point pt3 = new Point(pressed.x, pressed.y + 1);
        world.setBackground(pt3, new Background(id, imageStore.getImageList(id)));
        Point pt4 = new Point(pressed.x, pressed.y - 1);
        world.setBackground(pt4, new Background(id, imageStore.getImageList(id)));
//        Point pt5 = new Point(pressed.x + 1, pressed.y + 1);
//        world.setBackground(pt5, new Background(id, imageStore.getImageList(id)));
//        Point pt6 = new Point(pressed.x - 1, pressed.y + 1);
//        world.setBackground(pt6, new Background(id, imageStore.getImageList(id)));
//        Point pt7 = new Point(pressed.x + 1, pressed.y - 1);
//        world.setBackground(pt7, new Background(id, imageStore.getImageList(id)));
//        Point pt8 = new Point(pressed.x - 1, pressed.y - 1);
//        world.setBackground(pt8, new Background(id, imageStore.getImageList(id)));

    }

    private Point mouseToPoint(int x, int y)
    {
        return view.getViewport().viewportToWorld(mouseX/TILE_WIDTH, mouseY/TILE_HEIGHT);
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;
            int ex = 0;
            int ey = 0;

            switch (keyCode) {
                case UP:
                    dy = -1;
                    ey = -1;
                    break;
                case DOWN:
                    dy = 1;
                    ey = 1;
                    break;
                case LEFT:
                    dx = -1;
                    ex = -1;
                    break;
                case RIGHT:
                    dx = 1;
                    ex = 1;
                    break;
            }
            Point pos = bear.position();
            Point newpos = new Point(pos.x + ex, pos.y + ey);
            if (world.withinBounds(newpos) && !world.isOccupied(newpos)){
                world.moveEntity(bear, newpos);
                if(ex == 0 && pos.y > 6 && pos.y < 22){
                    view.shiftView(dx, dy);
                }
                if(ey == 0 && pos.x > 8 && pos.x < 31){
                    view.shiftView(dx, dy);
                }
            }
        }
    }

    private static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME,
                imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    private static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            img.pixels[i] = color;
        }
        img.updatePixels();
        return img;
    }

    private void loadImages(
            String filename, ImageStore imageStore, PApplet screen)
    {
        try {
            Scanner in = new Scanner(new File(filename));
            imageStore.loadImages(in, screen);
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void loadWorld(
            WorldModel world, String filename, ImageStore imageStore)
    {
        try {
            Scanner in = new Scanner(new File(filename));
            world.load(in, imageStore);
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void scheduleActions(
            WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        for (Entity entity : world.getEntities()) {
            if(entity instanceof Animates){
                ((Animates)entity).scheduleActions(scheduler, world, imageStore);
            }
        }
    }

    private static void parseCommandLine(String[] args) {
        if (args.length > 1)
        {
            if (args[0].equals("file"))
            {

            }
        }
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG:
                    timeScale = Math.min(FAST_SCALE, timeScale);
                    break;
                case FASTER_FLAG:
                    timeScale = Math.min(FASTER_SCALE, timeScale);
                    break;
                case FASTEST_FLAG:
                    timeScale = Math.min(FASTEST_SCALE, timeScale);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        parseCommandLine(args);
        PApplet.main(VirtualWorld.class);
    }
}
