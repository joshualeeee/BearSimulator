import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy
        implements PathingStrategy
{


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        // create openList hash map (key = Point)
        HashMap<Point, Node> openHash = new HashMap<>();

        // create priorityQueue of Nodes based on f-values
        PriorityQueue<Node> openQueue = new PriorityQueue<Node>(10, Comparator.comparing(Node::getF));

        // create closedList hash map
        HashMap<Point, Node> closedList = new HashMap<>();

        // create path
        List<Point> path = new LinkedList<>();

        // Add start node to open lists and mark as current node
        int startH = calculateH(end, start);
        Node startNode = new Node(start, 0, startH, startH, null);
        openHash.put(start, startNode);
        openQueue.add(startNode);
        Node currentNode = startNode;

        // keep looping while openList has grid points in it
        while(openQueue.size() != 0) {
            // set new current node to lowest f-value
            currentNode = openQueue.remove();

            // create tempList of valid neighbor points not in closed list
            List<Point> tempList = potentialNeighbors.apply(currentNode.position)
                                .filter(canPassThrough)
                                .filter(pt ->
                                    !pt.equals(start)
                                    && !pt.equals(end)
                                    && !closedList.containsKey(pt))
                                .collect(Collectors.toList());

            // for each neighbor, check if withinReach, if not, add to open list if not in it already
            for (Point neighbor : tempList) {

                // create node for neighbor
                int g = currentNode.g + 1;
                int h = calculateH(neighbor, end);
                int f = g + h;
                Node neighborNode = new Node(neighbor, g, h, f, currentNode);

                // if neighbor is next to goal, create path and return
                if (withinReach.test(end, neighbor)) {
                    // create path
                    return createPath(neighborNode);
                }

                // add to list if not in open yet
                if (!(openHash.containsKey(neighbor))) {
                    openHash.put(neighbor, neighborNode);
                    openQueue.add(neighborNode);
                // else if it already exists, check the g-values and compare
                } else {
                    // if new g-value better than old one, replace that Node with new neighbor Node
                    if (g < openHash.get(neighbor).g) {
                        openQueue.remove(openHash.get(neighbor));
                        openHash.remove(neighbor);
                        openHash.put(neighbor, neighborNode);
                        openQueue.add(neighborNode);
                    }
                }
            }

            // remove currentNode from openList and move to closedList
            closedList.put(currentNode.position, currentNode);
            openHash.remove(currentNode.position);
            openQueue.remove(currentNode);

//            // set new current node to lowest f-value
//            currentNode = openQueue.remove();

        }

        // create path and return
        return path;
    }

    // returns distance between neighbor and goal points
    public int calculateH(Point a, Point b) {
        int res = Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
        return res;
    }

    // create path
    public List<Point> createPath(Node last) {
        List<Point> path = new LinkedList<>();
        Node currentNode = last;

        while (currentNode.prior != null) {
            path.add(0, currentNode.position);
            currentNode = currentNode.prior;
        }

        return path;
    }


    public class Node {
        private Point position;
        private int g;
        private int h;
        private int f;
        private Node prior;

        // Node constructor
        public Node(Point position, int g, int h, int f, Node prior) {
            this.position = position;
            this.g = g;
            this.h = h;
            this.f = f;
            this.prior = prior;
        }

        // accessor for f
        public int getF() {
            return f;
        }

    }
}
