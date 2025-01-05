package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Bomb;
import de.tum.cit.ase.bomberquest.texture.Drawable;

import java.util.List;

/**
 * Represents the player character in the game.
 * The player has a hitbox, so it can collide with other objects in the game.
 */
public class Player implements Drawable {

    /** Total time elapsed since the game started. We use this for calculating the player movement and animating it. */
    private float elapsedTime;

    /** The Box2D hitbox of the player, used for position and collision detection. */
    private final Body hitbox;

    /** Player health (1 life). */
    private boolean isDead = false;

    private final World world;
    private final GameMap gameMap;  // Assuming you have a GameMap class to manage bombs and enemies.

    public Player(World world, GameMap gameMap, int x, int y) {
        this.world = world;
        this.gameMap = gameMap;
        this.hitbox = createHitbox(world, x, y);
    }

    /**
     * Creates a Box2D body for the player.
     * @param world The Box2D world to add the body to.
     * @param startX The initial X position.
     * @param startY The initial Y position.
     * @return The created body.
     */
    private Body createHitbox(World world, float startX, float startY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startX, startY);

        Body body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(0.3f);  // Adjust radius as needed
        body.createFixture(circle, 1.0f);
        circle.dispose();

        body.setUserData(this);
        return body;
    }

    /**
     * Move the player around in response to keyboard input.
     * This method will prevent movement through walls.
     * @param frameTime the time since the last frame.
     */
    public void tick(float frameTime) {
        this.elapsedTime += frameTime;

        // Handle movement based on key input
        float xVelocity = 0;
        float yVelocity = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            yVelocity = 2;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            yVelocity = -2;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            xVelocity = -2;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            xVelocity = 2;
        }

        // Set the player's linear velocity to move it around
        this.hitbox.setLinearVelocity(xVelocity, yVelocity);

        // Drop bombs if space bar is pressed
        dropBomb();

        // Check if the player is dead (bomb explosion or enemy collision)
        checkDeath(gameMap.getBombs(), gameMap.getEnemies());


    }

    /**
     * Drops a bomb at the player's current position if the space bar is pressed.
     */
    private void dropBomb() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Bomb bomb = new Bomb(this.getX(), this.getY());
            gameMap.addBomb(bomb);  // Assuming you have a method to add bombs to your game world
        }
    }

    /**
     * Checks if the player is dead (collided with bomb explosion or enemy).
     * @param bombs The list of bombs in the game.
     * @param enemies The list of enemies in the game.
     */
    private void checkDeath(List<Bomb> bombs, List<Enemy> enemies) {
        // Check bomb explosions
        for (Bomb bomb : bombs) {
            if (bomb.explodesAt(this.getX(), this.getY())) {
                isDead = true;
                gameOver();
                break;
            }
        }

        // Check for collision with enemies
        for (Enemy enemy : enemies) {
            if (enemy.getX() == this.getX() && enemy.getY() == this.getY()) {
                isDead = true;
                gameOver();
                break;
            }
        }
    }

    /**
     * Trigger the game over logic (e.g., pause the game or display a game over screen).
     */
    private void gameOver() {
        System.out.println("Game Over!");
        // Add game over handling here, like stopping the game or showing a screen
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        // Get the frame of the walk down animation that corresponds to the current time.
        return Animations.CHARACTER_WALK_DOWN.getKeyFrame(this.elapsedTime, true);
    }

    @Override
    public int getX() {
        return hitbox.getPosition().x;
    }

    @Override
    public int getY() {
        return hitbox.getPosition().y;
    }

    public boolean isDead() {
        return isDead;
    }
}
