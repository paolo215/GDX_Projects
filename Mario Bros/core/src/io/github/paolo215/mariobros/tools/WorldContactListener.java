package io.github.paolo215.mariobros.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import io.github.paolo215.mariobros.MarioBros;
import io.github.paolo215.mariobros.sprites.Mario;
import io.github.paolo215.mariobros.sprites.enemies.Enemy;
import io.github.paolo215.mariobros.sprites.items.Item;
import io.github.paolo215.mariobros.sprites.tileobjects.InteractiveTileObject;

/**
 * Created by paolo on 8/28/2016.
 */

//ContactListener gets called when two fixtures in box2d collide with each other
public class WorldContactListener implements ContactListener{

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int collisionDef = fixA.getFilterData().categoryBits
                | fixB.getFilterData().categoryBits;


        switch(collisionDef) {
            case MarioBros.MARIO_HEAD_BIT | MarioBros.BRICK_BIT:
            case MarioBros.MARIO_HEAD_BIT | MarioBros.COIN_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.MARIO_HEAD_BIT) {
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                } else {
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                }
                break;

            case MarioBros.ENEMY_HEAD_BIT | MarioBros.MARIO_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT) {
                    ((Enemy) fixA.getUserData()).hitOnHead();
                } else {
                    ((Enemy) fixB.getUserData()).hitOnHead();
                }
                break;

            case MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT) {
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                } else {
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                }
                break;

            case MarioBros.MARIO_BIT | MarioBros.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.MARIO_BIT) {
                    ((Mario) fixA.getUserData()).hit();
                } else {
                    ((Mario) fixB.getUserData()).hit();
                }
                break;
            case MarioBros.ENEMY_BIT | MarioBros.ENEMY_BIT:
                ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                break;

            case MarioBros.ITEM_BIT | MarioBros.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT) {
                    ((Item) fixA.getUserData()).reverseVelocity(true, false);
                } else {
                    ((Item) fixB.getUserData()).reverseVelocity(true, false);
                }
                break;

            case MarioBros.ITEM_BIT | MarioBros.MARIO_BIT:
                if(fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT) {
                    ((Item) fixA.getUserData()).use((Mario) fixB.getUserData());
                } else {
                    ((Item) fixB.getUserData()).use((Mario) fixA.getUserData());
                }
                break;

        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
