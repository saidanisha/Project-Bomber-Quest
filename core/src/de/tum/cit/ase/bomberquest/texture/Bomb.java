package de.tum.cit.ase.bomberquest.texture;

        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.audio.Sound;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;
        import com.badlogic.gdx.graphics.Texture;
        import com.badlogic.gdx.graphics.g2d.TextureRegion;
        import de.tum.cit.ase.bomberquest.texture.Drawable;
        import de.tum.cit.ase.bomberquest.screen.GameScreen;

public class Bomb implements Drawable {
    private static final float BOMB_TIMER = 3.0f; // 3 seconds before explosion
    private static final int EXPLOSION_RADIUS = 2; // How far the explosion spreads

    private float timer;  // Timer for how long the bomb will stay before exploding
    private boolean exploded;  // Whether the bomb has exploded
    private int x, y;  // Position of the bomb on the grid
    private final Texture bombTexture;
    private final Sound explosionSound;

    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
        this.timer = BOMB_TIMER;
        this.exploded = false;
        this.bombTexture = new Texture(Gdx.files.internal("bomb.png"));
        this.explosionSound = Gdx.audio.newSound(Gdx.files.internal("bomb_explosion.mp3"));
    }

    public void update(float deltaTime) {
        if (!exploded) {
            timer -= deltaTime;  // Decrease the timer by deltaTime each frame
            if (timer <= 0) {
                explode();  // Explode when the timer runs out
            }
        }
    }

    private void explode() {
        exploded = true;
        explosionSound.play();  // Play explosion sound
        // Implement the explosion effect (damaging enemies, destroying walls, etc.)
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        return new TextureRegion(bombTexture);  // Return bomb texture for rendering
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    public boolean isExploded() {
        return exploded;
    }

    // Add additional methods if needed (e.g., for explosion radius or interaction with objects)

    public void dispose() {
        bombTexture.dispose();
        explosionSound.dispose();
    }
}

