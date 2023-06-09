# BearSimulator

- Designed using Object-Oriented Programming (OOP) principles and design pattern that improved reusability and maintainability of code.
- Created a hierarchy of different entities using inheritance and polymorphism, as well as, instance methods to enable them to interact with their environment and other entities.
- Mapped connecting classes and methods using a UML diagram for better readability.

Simulation Rules:
There are 8 entities, which act according to the following rules:
1. Bear:
    * Just one entity.
    * Controlled by the arrow keys.
    * Whenever the camera moves it follows bear entity so that it stays as centered as possible.
    * Whenever stomps on hive or flower whenever adjacent (hiveUP -> hiveDown, flowerFull -> flowerSapling)

2. Bee_Full
    * Spawns on whenMouseClicked
    * Is a flower harvesting entity who has already collected as much pollen as they can carry.
    * Utilizes A* to navigate to the nearest HiveUp to drop it off.
    * Transforms into a Bee_NotFull once task is completed (Bee_Full -> Bee_NotFull)

3. Bee_NotFull
    * Utilizes A* to navigate to nearest FlowerFull
    * will search until it has reached its resource limit,
      moving to multiple FlowerFulls if necessary.
    * transforms into a Bee_Full once it hits its resource limit (Bee_NotFull -> Bee_Full)

4. OBSTACLE
    * the water - entities cannot move through it

5. HiveDown
    * Spawns on whenMouseClicked
    * animates and grows into HiveUp entities once they hit their designated health limit
    * Bees will not navigate towards HiveDowns only HiveUp

6. HiveUp
    * animates and has health
    * Its health can be depleted if adjacent to bear entity,
     when health is depleted it will transform into a HiveDown entity

7. FlowerSapling
    * FlowerSaplings animate and grow into FlowerFull entities once they hit their designated health limit

8. FlowerFull
    * animates and has health
   * Its health can be depleted if adjacent to bear entity or bee entity,
      when health is depleted it will transform into a FlowerSapling entity
