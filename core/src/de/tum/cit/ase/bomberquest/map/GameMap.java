package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.BomberQuestGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the game map.
 * Holds all the objects and entities in the game.
 */
public class GameMap {

    // Box2D physics simulation parameters
    private static final float TIME_STEP = 1f / Gdx.graphics.getDisplayMode().refreshRate;
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

    public GameMap(BomberQuestGame game) {
        this.game = game;
        this.world = new World(Vector2.Zero, true);

        // Create a player with initial position (1, 3)
        this.player = new Player(this.world, 1, 3);

        // Create a chest in the middle of the map
        this.chest = new Chest(world, 3, 3);

        // Create flowers in a 7x7 grid
        this.flowers = new Flowers[7][7];
        for (int i = 0; i < flowers.length; i++) {
            for (int j = 0; j < flowers[i].length; j++) {
                this.flowers[i][j] = new Flowers(i, j);
            }
        }
        initializeTiles();
    }


    // * Updates the game state. This is called once per frame.
    // * Every dynamic object in the game should update its state here.
     //* @param frameTime the time that has passed since the last update

    public void tick(float frameTime) {
        this.player.tick(frameTime);
        doPhysicsStep(frameTime);
    }

    /**
     * Performs as many physics steps as necessary to catch up to the given frame time.
     * This will update the Box2D world by the given time step.
     * @param frameTime Time since last frame in seconds
     */
    private void doPhysicsStep(float frameTime) {
        this.physicsTime += frameTime;
        while (this.physicsTime >= TIME_STEP) {
            this.world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            this.physicsTime -= TIME_STEP;
        }
    }

    /** Returns the player on the map. */
    public Player getPlayer() {
        return player;
    }

    /** Returns the chest on the map. */
    public Chest getChest() {
        return chest;
    }

    /** Returns the flowers on the map. */
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

    public List<WallPath> getTiles() {
        List<WallPath> allTiles = new ArrayList<>();
        for (WallPath[] row : tiles) {
            for (WallPath tile : row) {
                allTiles.add(tile);
            }
        }
        return allTiles;
    }
}
