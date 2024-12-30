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

    public WallPath(World world, float x, float y, boolean destructible, TextureRegion texture) {
        this.destructible = destructible;
        this.texture = texture;
        this.body = createBody(world, x, y);
    }


    private Body createBody(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);


        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);


        body.createFixture(shape, 0.0f);
        body.setUserData(this);

        shape.dispose();
        return body;
    }


    public void destroy() {
        if (destructible) {
            body.getWorld().destroyBody(body);
        }
    }
    public boolean isDestructible() {
        return destructible;
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        return texture;
    }

    @Override
    public float getX() {
        return body.getPosition().x;
    }

    @Override
    public float getY() {
        return body.getPosition().y;
    }
}

