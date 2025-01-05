package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * Represents a wall or path in the game map.
 */
public class WallPath implements Drawable {

    private final Body body;
    private final boolean destructible;
    private final TextureRegion texture;
    private Exit exit;

    /**
     * Constructor to initialize the wall or path.
     * @param world The Box2D world where the body will be created.
     * @param x The x-coordinate of the wall or path.
     * @param y The y-coordinate of the wall or path.
     * @param destructible True if the wall is destructible, false otherwise.
     * @param texture The texture to render the wall or path.
     */
    public WallPath(World world, float x, float y, boolean destructible, TextureRegion texture) {
        this.destructible = destructible;
        this.texture = texture;
        this.body = createBody(world, x, y);
        this.exit = null;
    }

    public Exit getExit() {
        return exit;
    }

    /**
     * Creates a Box2D body for the wall or path.
     * @param world The Box2D world.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The created Box2D body.
     */
    private Body createBody(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f;

        body.createFixture(fixtureDef);
        body.setUserData(this);

        shape.dispose();
        return body;
    }

    /**
     * Destroys the wall if it is destructible.
     */
    public void destroy(GameMap gameMap) {
        if (destructible) {
            body.getWorld().destroyBody(body);

            // Reveal the exit if it was hidden under this wall
            if (exit != null && exit.getX() == getX() && exit.getY() == getY()) {
                exit.reveal();
            }
        }
    }



    /**
     * Checks if the wall is destructible.
     * @return True if destructible, false otherwise.
     */
    public boolean isDestructible() {
        return destructible;
    }
    public boolean isIndestructible() {
        return !destructible; // Wall is indestructible if it is not destructible
    }


    /**
     * Returns the current texture for rendering.
     * @return The texture region of the wall or path.
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        return texture;
    }

    /**
     * Gets the x-coordinate of the wall or path.
     * @return The x-coordinate.
     */
    @Override
    public float getX() {
        return body.getPosition().x;
    }

    /**
     * Gets the y-coordinate of the wall or path.
     * @return The y-coordinate.
     */
    @Override
    public float getY() {
        return body.getPosition().y;
    }
}
