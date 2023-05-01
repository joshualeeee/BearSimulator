import processing.core.PImage;

import java.util.List;

public abstract class Bee extends ActivityEnt implements NextPosition{
    public Bee(
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

    public Point nextPosition(
            WorldModel world, Point destPos)
    {
        PathingStrategy strategy = new AStarPathingStrategy();
        List<Point> points;

        points = strategy.computePath(this.position(), destPos,
                p -> !world.isOccupied(p),
                (p1, p2) -> neighbors(p1,p2),
                PathingStrategy.CARDINAL_NEIGHBORS);

        if (points.size() == 0)
        {
            System.out.println("No path found");
            return this.position();
        }

        Point newPos = points.get(0);
        return newPos;
    }

    private static boolean neighbors(Point p1, Point p2)
    {
        return p1.x+1 == p2.x && p1.y == p2.y ||
                p1.x-1 == p2.x && p1.y == p2.y ||
                p1.x == p2.x && p1.y+1 == p2.y ||
                p1.x == p2.x && p1.y-1 == p2.y;
    }

}
