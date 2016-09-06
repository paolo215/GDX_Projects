package io.github.paolo215.mariobros.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import io.github.paolo215.mariobros.screens.PlayScreen;

/**
 * Created by paolo on 8/29/2016.
 */
public abstract class Enemy extends Sprite {
    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    public Vector2 velocity;

    public Enemy(PlayScreen screen, float x, float y) {
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(1, 0);

        //puts b2body to sleep until it gets woken up
        b2body.setActive(false);
    }

    protected  abstract void defineEnemy();

    public abstract void hitOnHead();

    public void reverseVelocity(boolean x, boolean y) {
        if(x == true) {
            velocity.x = -velocity.x;
        }

        if(y == true) {
            velocity.y = -velocity.y;
        }
    }

    public abstract void update(float dt);
}
