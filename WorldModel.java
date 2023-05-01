import processing.core.PImage;

import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel
{

    private int numRows;
    private int numCols;
    private Background background[][];
    private Entity occupancy[][];
    private Set<Entity> entities;
    private static final int PROPERTY_KEY = 0;

    public static final String HIVEDOWN_KEY = "hivedown";
    public static final int HIVEDOWN_ACTION_ANIMATION_PERIOD = 1000;
    public static final int HIVEDOWN_HEALTH_LIMIT = 5;

    private static final String BGND_KEY = "background";
    private static final int BGND_NUM_PROPERTIES = 4;
    private static final int BGND_ID = 1;
    private static final int BGND_COL = 2;
    private static final int BGND_ROW = 3;

    public static final String HIVEUP_KEY = "hiveup";

    public static final String FLOWERFULL_KEY = "flowerFull";
    private static final int FLOWERFULL_NUM_PROPERTIES = 7;
    private static final int FLOWERFULL_ID = 1;
    private static final int FLOWERFULL_COL = 2;
    private static final int FLOWERFULL_ROW = 3;
    private static final int FLOWERFULL_ANIMATION_PERIOD = 4;
    private static final int FLOWERFULL_ACTION_PERIOD = 5;
    private static final int FLOWERFULL_HEALTH = 6;

    public static final String FLOWERSAPLING_KEY = "flowerSapling";
    public static final int FLOWERSAPLING_ACTION_ANIMATION_PERIOD = 1000;
    public static final int FLOWERSAPLING_HEALTH_LIMIT = 5;
    private static final int FLOWERSAPLING_NUM_PROPERTIES = 4;
    private static final int FLOWERSAPLING_ID = 1;
    private static final int FLOWERSAPLING_COL = 2;
    private static final int FLOWERSAPLING_ROW = 3;
    private static final int FLOWERSAPLING_HEALTH = 4;

    private static final String OBSTACLE_KEY = "obstacle";
    private static final int OBSTACLE_NUM_PROPERTIES = 5;
    private static final int OBSTACLE_ID = 1;
    private static final int OBSTACLE_COL = 2;
    private static final int OBSTACLE_ROW = 3;
    private static final int OBSTACLE_ANIMATION_PERIOD = 4;

    public static final String BEE_KEY = "bee";

    private static final String BEAR_KEY = "bear";
    private static final int BEAR_NUM_PROPERTIES = 6;
    private static final int BEAR_ID = 1;
    private static final int BEAR_COL = 2;
    private static final int BEAR_ROW = 3;
    private static final int BEAR_ANIMATION_PERIOD = 4;
    private static final int BEAR_ACTION_PERIOD = 5;

    public WorldModel(int numRows, int numCols, Background defaultBackground) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.background = new Background[numRows][numCols];
        this.occupancy = new Entity[numRows][numCols];
        this.entities = new HashSet<>();

        for (int row = 0; row < numRows; row++) {
            Arrays.fill(this.background[row], defaultBackground);
        }
    }

    public int getNumRows(){
        return this.numRows;
    }

    public int getNumCols(){
        return this.numCols;
    }

    public Set<Entity> getEntities(){
        return this.entities;
    }

    public void load(
            Scanner in, ImageStore imageStore)
    {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                if (!this.processLine(in.nextLine(), imageStore)) {
                    System.err.println(String.format("invalid entry on line %d",
                            lineNumber));
                }
            }
            catch (NumberFormatException e) {
                System.err.println(
                        String.format("invalid entry on line %d", lineNumber));
            }
            catch (IllegalArgumentException e) {
                System.err.println(
                        String.format("issue on line %d: %s", lineNumber,
                                e.getMessage()));
            }
            lineNumber++;
        }
    }

    private boolean processLine(
            String line, ImageStore imageStore)
    {
        String[] properties = line.split("\\s");
        if (properties.length > 0) {
            switch (properties[PROPERTY_KEY]) {
                case BGND_KEY:
                    return this.parseBackground(properties, imageStore);
                case OBSTACLE_KEY:
                    return this.parseObstacle(properties, imageStore);
                case FLOWERFULL_KEY:
                    return this.parseFlowerFull(properties, imageStore);
            }
        }
        return false;
    }

    private boolean parseBackground(
            String[] properties, ImageStore imageStore)
    {
        if (properties.length == BGND_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[BGND_COL]),
                    Integer.parseInt(properties[BGND_ROW]));
            String id = properties[BGND_ID];
            this.setBackground(pt,
                    new Background(id, imageStore.getImageList(id)));
        }

        return properties.length == BGND_NUM_PROPERTIES;
    }

    private boolean parseFlowerFull(
            String[] properties, ImageStore imageStore)
    {
        if (properties.length == FLOWERFULL_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[FLOWERFULL_COL]),
                    Integer.parseInt(properties[FLOWERFULL_ROW]));
            Entity entity = createFlowerFull(properties[FLOWERFULL_ID], pt,
                    Integer.parseInt(properties[FLOWERFULL_ACTION_PERIOD]),
                    Integer.parseInt(properties[FLOWERFULL_ANIMATION_PERIOD]),
                    Integer.parseInt(properties[FLOWERFULL_HEALTH]),
                    imageStore.getImageList(FLOWERFULL_KEY));
            this.tryAddEntity(entity);
        }

        return properties.length == FLOWERFULL_NUM_PROPERTIES;
    }

    private boolean parseObstacle(
            String[] properties, ImageStore imageStore)
    {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[OBSTACLE_COL]),
                    Integer.parseInt(properties[OBSTACLE_ROW]));
            Entity entity = createObstacle(properties[OBSTACLE_ID], pt,
                    Integer.parseInt(properties[OBSTACLE_ANIMATION_PERIOD]),
                    imageStore.getImageList(
                            OBSTACLE_KEY));
            this.tryAddEntity(entity);
        }

        return properties.length == OBSTACLE_NUM_PROPERTIES;
    }

    public void tryAddEntity(Entity entity) {
        if (this.isOccupied(entity.position())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }
        this.addEntity(entity);
    }

    public boolean isOccupied(Point pos) {
        return this.withinBounds(pos) && this.getOccupancyCell(pos) != null;
    }

    public boolean withinBounds(Point pos) {
        return pos.y >= 0 && pos.y < this.numRows && pos.x >= 0
                && pos.x < this.numCols;
    }

    public Optional<Entity> findNearest(
            Point pos, List<Class> kinds)
    {
        List<Entity> ofType = new LinkedList<>();
        for (Class kind: kinds)
        {
            for (Entity entity : this.getEntities()) {
                if (entity.getClass() == kind) {
                    ofType.add(entity);
                }
            }
        }

        return nearestEntity(ofType, pos);
    }

    private static Optional<Entity> nearestEntity(
            List<Entity> entities, Point pos)
    {
        if (entities.isEmpty()) {
            return Optional.empty();
        }
        else {
            Entity nearest = entities.get(0);
            int nearestDistance = distanceSquared(nearest.position(), pos);

            for (Entity other : entities) {
                int otherDistance = distanceSquared(other.position(), pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }

    private static int distanceSquared(Point p1, Point p2) {
        int deltaX = p1.x - p2.x;
        int deltaY = p1.y - p2.y;

        return deltaX * deltaX + deltaY * deltaY;
    }

    public void addEntity(Entity entity) {
        if (this.withinBounds(entity.position())) {
            this.setOccupancyCell(entity.position(), entity);
            this.entities.add(entity);
        }
    }

    public void moveEntity(Entity entity, Point pos) {
        Point oldPos = entity.position();
        if (this.withinBounds(pos) && !pos.equals(oldPos)) {
            this.setOccupancyCell(oldPos, null);
            this.removeEntityAt(pos);
            this.setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }
    public void removeEntity(Entity entity) {
        this.removeEntityAt(entity.position());
    }

    public void removeEntityAt(Point pos) {
        if (this.withinBounds(pos) && this.getOccupancyCell(pos) != null) {
            Entity entity = this.getOccupancyCell(pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.setPosition(new Point(-1, -1));
            this.entities.remove(entity);
            this.setOccupancyCell(pos, null);
        }
    }

    public void setBackground(
            Point pos, Background background)
    {
        if (this.withinBounds(pos)) {
            this.setBackgroundCell(pos, background);
        }
    }

    public Optional<Entity> getOccupant(Point pos) {
        if (this.isOccupied(pos)) {
            return Optional.of(this.getOccupancyCell(pos));
        }
        else {
            return Optional.empty();
        }
    }

    public Entity getOccupancyCell(Point pos) {
        return this.occupancy[pos.y][pos.x];
    }

    public void setOccupancyCell(
            Point pos, Entity entity)
    {
        this.occupancy[pos.y][pos.x] = entity;
    }

    public Background getBackgroundCell(Point pos) {
        return this.background[pos.y][pos.x];
    }

    public void setBackgroundCell(
           Point pos, Background background)
    {
        this.background[pos.y][pos.x] = background;
    }

    public Entity createObstacle(
            String id, Point position, int animationPeriod, List<PImage> images)
    {
        return new Obstacle(id, images, position,
                animationPeriod);
    }

    public static Entity createHiveUp(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            int health,
            List<PImage> images)
    {
        return new HiveUp(id, position, images, 0, 0,
                actionPeriod, animationPeriod, health, 0);
    }

    public static Entity createHiveDown(
            String id,
            Point position,
            List<PImage> images)
    {
        return new HiveDown(id, position, images, 0, 0,
                WorldModel.HIVEDOWN_ACTION_ANIMATION_PERIOD, WorldModel.HIVEDOWN_ACTION_ANIMATION_PERIOD, 0, WorldModel.HIVEDOWN_HEALTH_LIMIT);
    }

    public Entity createBear(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new Bear(id, position, images, 0, 0,
                actionPeriod, animationPeriod, 0, 0);
    }

    // need resource count, though it always starts at 0
    public static Entity createDudeNotFull(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            int resourceLimit,
            List<PImage> images)
    {
        return new Bee_NotFull(id, position, images, resourceLimit, 0,
                actionPeriod, animationPeriod, 0, 0);
    }

    // don't technically need resource count ... full
    public static Entity createDudeFull(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            int resourceLimit,
            List<PImage> images) {
        return new Bee_Full(id, position, images, resourceLimit, 0,
                actionPeriod, animationPeriod, 0, 0);
    }

    public static Entity createFlowerFull(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            int health,
            List<PImage> images)
    {
        return new FlowerFull(id, position, images, 0, 0,
                actionPeriod, animationPeriod, health, 0);
    }

    public static Entity createFlowerSapling(
            String id,
            Point position,
            List<PImage> images)
    {
        return new FlowerSapling(id, position, images, 0, 0,
                WorldModel.FLOWERSAPLING_ACTION_ANIMATION_PERIOD, WorldModel.FLOWERSAPLING_ACTION_ANIMATION_PERIOD, 0, WorldModel.FLOWERSAPLING_HEALTH_LIMIT);
    }


}
