import java.util.*;

/**
 * Keeps track of events that have been scheduled.
 */
public final class EventScheduler
{
    private PriorityQueue<Event> eventQueue;
    private Map<Entity, List<Event>> pendingEvents;
    private double timeScale;

    public EventScheduler(double timeScale) {
        this.eventQueue = new PriorityQueue<>(new EventComparator());
        this.pendingEvents = new HashMap<>();
        this.timeScale = timeScale;
    }

    public Map<Entity, List<Event>> getPendingEvents(){
        return this.pendingEvents;
    }

    public PriorityQueue<Event> getEventQueue(){
        return this.eventQueue;
    }

    public void scheduleEvent(
            Entity entity,
            Action action,
            long afterPeriod)
    {
        long time = System.currentTimeMillis() + (long)(afterPeriod
                * this.timeScale);
        Event event = new Event(action, time, entity);

        this.eventQueue.add(event);

        // update list of pending events for the given entity
        List<Event> pending = this.pendingEvents.getOrDefault(entity,
                new LinkedList<>());
        pending.add(event);
        this.pendingEvents.put(entity, pending);
    }
//    public void unscheduleAllEvents(Entity entity)
//    {
//        List<Event> pending = this.pendingEvents.remove(entity);
//
//        if (pending != null) {
//            for (Event event : pending) {
//                this.eventQueue.remove(event);
//            }
//        }
//    }

//    public void scheduleActions(
//            Entity entity,
//            WorldModel world,
//            ImageStore imageStore)
//    {
//        switch (entity.getKind()) {
//            case DUDE_FULL:
//                this.scheduleEvent(entity,
//                        entity.createActivityAction(world, imageStore),
//                        entity.getActionPeriod());
//                this.scheduleEvent(entity,
//                        entity.createAnimationAction(0),
//                        entity.getAnimationPeriod());
//                break;
//
//            case DUDE_NOT_FULL:
//                this.scheduleEvent(entity,
//                        entity.createActivityAction(world, imageStore),
//                        entity.getActionPeriod());
//                this.scheduleEvent(entity,
//                        entity.createAnimationAction(0),
//                        entity.getAnimationPeriod());
//                break;
//
//            case OBSTACLE:
//                this.scheduleEvent(entity,
//                        entity.createAnimationAction(0),
//                        entity.getAnimationPeriod());
//                break;
//
//            case FAIRY:
//                this.scheduleEvent(entity,
//                        entity.createActivityAction(world, imageStore),
//                        entity.getActionPeriod());
//                this.scheduleEvent(entity,
//                        entity.createAnimationAction(0),
//                        entity.getAnimationPeriod());
//                break;
//
//            case SAPLING:
//                this.scheduleEvent(entity,
//                        entity.createActivityAction(world, imageStore),
//                        entity.getActionPeriod());
//                this.scheduleEvent(entity,
//                        entity.createAnimationAction(0),
//                        entity.getAnimationPeriod());
//                break;
//
//            case TREE:
//                this.scheduleEvent(entity,
//                        entity.createActivityAction(world, imageStore),
//                        entity.getActionPeriod());
//                this.scheduleEvent(entity,
//                        entity.createAnimationAction(0),
//                        entity.getAnimationPeriod());
//                break;
//
//            default:
//        }
//    }

    private void removePendingEvent(
            Event event)
    {
        List<Event> pending = this.pendingEvents.get(event.getEntity());

        if (pending != null) {
            pending.remove(event);
        }
    }

    public void updateOnTime(long time) {
        while (!this.eventQueue.isEmpty()
                && this.eventQueue.peek().getTime() < time) {
            Event next = this.eventQueue.poll();

            this.removePendingEvent(next);

            next.getAction().executeAction(this);
        }
    }

//    public void executeAction(Action action) {
//        switch (action.kind) {
//            case ACTIVITY:
//                action.executeActivityAction(this);
//                break;
//
//            case ANIMATION:
//                action.executeAnimationAction(this);
//                break;
//        }
//    }



}
