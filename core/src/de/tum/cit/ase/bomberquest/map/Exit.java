
package de.tum.cit.ase.bomberquest.map;

        import com.badlogic.gdx.graphics.g2d.TextureRegion;
        import com.badlogic.gdx.physics.box2d.*;
        import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * Represents the exit in the game, hidden under a destructible wall.
 */
public class Exit implements Drawable {

    private final Body body;
    private boolean revealed;
    private final TextureRegion texture;

    public Exit(World world, float x, float y, TextureRegion texture) {
        this.texture = texture;
        this.revealed = false; // Start as hidden
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

    public void reveal() {
        this.revealed = true; // Mark exit as revealed
    }

    public boolean isRevealed() {
        return revealed;
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        return revealed ? texture : null; // Show only if revealed
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
