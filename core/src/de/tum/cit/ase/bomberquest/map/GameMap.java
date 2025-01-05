package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.texture.Bomb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the game map.
 * Holds all the objects and entities in the game.
 */
public class GameMap {

    // Box2D physics simulation parameters
    private static final float TIME_STEP = 1f / 60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private float physicsTime = 0;

    private final BomberQuestGame game;
    private final World world;

    // Game objects
    private final Player player;
    private final Chest chest;
    private final Flowers[][] flowers;
    private WallPath[][] tiles;
    private static final int MAP_WIDTH = 10;
    private static final int MAP_HEIGHT = 10;
    private final Vector2 entrance;
    private Exit exit;
    private float countdownTime;
    private boolean isGameOver = false;  // Track if the game is over
    private float initialTime = 300f;
    private List<Enemy> enemies;

    // List to store bombs in the game world
    private List<Bomb> bombs = new ArrayList<>();


    public GameMap(BomberQuestGame game) {
        this.game = game;
        this.world = new World(Vector2.Zero, true);
        this.enemies = new ArrayList<>();
        this.enemies.add(new Enemy(world, game, 3, 4));
        this.enemies.add(new Enemy(world, game, 5, 5));

        // Create a player with initial position (1, 3)
        //this.player = new Player(this.world, 1, 3, this);

        // Create a chest in the middle of the map
        this.chest = new Chest(world, 3, 3);
        this.entrance = new Vector2(1, 1); // Example spawn point
        //this.exit = new Exit(this.world, entrance.x, entrance.y);
        this.countdownTime = initialTime;
        this.player = new Player(world, game.getMap(), entrance.x, entrance.y);

        // Create flowers in a 7x7 grid
        this.flowers = new Flowers[7][7];
        for (int i = 0; i < flowers.length; i++) {
            for (int j = 0; j < flowers[i].length; j++) {
                this.flowers[i][j] = new Flowers(i, j);
            }
        }
        initializeTiles();
        initializeExit();
    }


    // Updates the game state. This is called once per frame.
    // Every dynamic object in the game should update its state here.
    public void tick(float frameTime) {
        if (!isGameOver) {
            countdownTime -= frameTime;
            if (countdownTime <= 0) {
                countdownTime = 0;
                isGameOver = true;
            }
        }

        this.player.tick(frameTime);
        for (Enemy enemy : enemies) {
            enemy.tick(frameTime);
        }
        doPhysicsStep(frameTime);
    }

    /**
     * Performs as many physics steps as necessary to catch up to the given frame time.
     * This will update the Box2D world by the given time step.
     *
     * @param frameTime Time since last frame in seconds
     */
    private void doPhysicsStep(float frameTime) {
        this.physicsTime += frameTime;
        while (this.physicsTime >= TIME_STEP) {
            this.world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            this.physicsTime -= TIME_STEP;
        }
    }

    /**
     * Returns the player on the map.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the chest on the map.
     */
    public Chest getChest() {
        return chest;
    }

    /**
     * Returns the flowers on the map.
     */
    public List<Flowers> getFlowers() {
        return Arrays.stream(flowers).flatMap(Arrays::stream).toList();
    }

    private void initializeTiles() {
        int rows = 7;
        int cols = 7;
        tiles = new WallPath[rows][cols];

        TextureRegion destructibleTexture = game.getAssetManager().get("destructible.png", TextureRegion.class);
        TextureRegion indestructibleTexture = game.getAssetManager().get("indestructible.png", TextureRegion.class);
        TextureRegion pathTexture = game.getAssetManager().get("path.png", TextureRegion.class);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // Indestructible walls at borders
                if (row == 0 || col == 0 || row == rows - 1 || col == cols - 1) {
                    tiles[row][col] = new WallPath(world, row, col, false, indestructibleTexture);
                }
                // Destructible walls at alternate positions
                else if ((row + col) % 2 == 0) {
                    tiles[row][col] = new WallPath(world, row, col, true, destructibleTexture);
                }
                // Paths
                else {
                    tiles[row][col] = new WallPath(world, row, col, false, pathTexture);
                }
            }
        }
    }

    private void initializeExit() {
        // Randomly place the exit under a destructible wall
        int x, y;
        TextureRegion exitTexture = game.getAssetManager().get("exit.png", TextureRegion.class);

        do {
            x = (int) (Math.random() * (MAP_WIDTH - 2)) + 1; // Avoid edges
            y = (int) (Math.random() * (MAP_HEIGHT - 2)) + 1;
        } while (!(tiles[x][y].isDestructible())); // Place exit under a destructible wall

        this.exit = new Exit(world, x, y, exitTexture);
    }

    // Add a bomb to the game world
    public void addBomb(Bomb bomb) {
        bombs.add(bomb);
    }

    // Get all bombs in the game world
    public List<Bomb> getBombs() {
        return bombs;
    }

    // Get all tiles
    public List<WallPath> getTiles() {
        List<WallPath> allTiles = new ArrayList<>();
        for (WallPath[] row : tiles) {
            for (WallPath tile : row) {
                allTiles.add(tile);
            }
        }
        return allTiles;
    }

    public float getRemainingTime() {
        return countdownTime;
    }
    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
    }
    public Exit getExit() {
        return exit;
    }

    public float getPhysicsTime() {
        return physicsTime;
    }

    public BomberQuestGame getGame() {
        return game;
    }

    public World getWorld() {
        return world;
    }

    public Vector2 getEntrance() {
        return entrance;
    }

    public float getCountdownTime() {
        return countdownTime;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public float getInitialTime() {
        return initialTime;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }
}
