package de.tum.cit.ase.bomberquest.map;

        import com.badlogic.gdx.math.Vector2;
        import com.badlogic.gdx.physics.box2d.World;
        import de.tum.cit.ase.bomberquest.map.WallPath;
        import de.tum.cit.ase.bomberquest.BomberQuestGame;

public class Enemy {

    private Vector2 position;
    private int health;
    private World world;
    private BomberQuestGame game;
    private GameMap gameMap; // Assuming you have a GameMap class to manage bombs and enemies.

    // Movement behavior (can be random or more intelligent)
    private float moveSpeed = 1f;

    public Enemy(World world, BomberQuestGame game, float startX, float startY) {
        this.world = world;
        this.game = game;
        this.position = new Vector2(startX, startY);
        this.health = 1; // For simplicity, let's assume the enemy dies in one hit
        this.gameMap = game.getMap();
    }

    // Update the enemy's state (position, behavior)
    public void tick(float deltaTime) {
        moveRandomly();
        checkCollisionsWithPlayer();
    }

    // Move the enemy randomly, avoiding walls
    private void moveRandomly() {
        // Random movement logic
        float directionX = (float) (Math.random() * 2 - 1); // Random -1 to 1
        float directionY = (float) (Math.random() * 2 - 1); // Random -1 to 1
        Vector2 newPosition = position.cpy().add(directionX * moveSpeed, directionY * moveSpeed);

        // Check if the new position is valid (not colliding with walls)
        if (!isCollidingWithWall(newPosition)) {
            position.set(newPosition);
        }
    }

    // Check if the enemy's new position would cause a collision with walls
    private boolean isCollidingWithWall(Vector2 newPosition) {
        // Iterate through all WallPath tiles in the map
        for (WallPath tile : this.game.getMap().getTiles()){
            // Compare the distance between the enemy's new position and the wall's position
            float distance = newPosition.dst(tile.getX(), tile.getY());

            // Check if the enemy is close enough to the wall and if the wall is indestructible
            if (distance < 0.5f && tile.isDestructible() == false) {
                return true; // There is a collision with an indestructible wall
            }
        }
        return false; // No collision with walls
    }


    // Check if the enemy collides with the player
    private void checkCollisionsWithPlayer() {
        // Check if the enemy collides with the player
            Player player = game.getMap().getPlayer();
            // Use the getX() and getY() methods from Player class to get position
            if (position.dst(player.getX(), player.getY()) < 0.5f) {


                 // Player dies when touched by an enemy
            }
        }



    // Check if the enemy has been killed
    public boolean isDead() {
        return health <= 0;
    }

    // Reduce health (e.g., when hit by an explosion)
    public void takeDamage(int damage) {
            health -= damage;
            if (isDead()) {
                game.getMap().removeEnemy(this); // Remove enemy from the map when dead
            }
        }


    // Getters and setters
    public Vector2 getPosition() {
        return position;
    }
}
