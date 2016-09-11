package io.github.paolo215.mariobros.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import io.github.paolo215.mariobros.MarioBros;
import io.github.paolo215.mariobros.screens.PlayScreen;

/**
 * Created by paolo on 8/27/2016.
 */
public class Mario extends Sprite {
    public World world;
    public Body b2body;
    private TextureRegion marioStand;

    public State currentState;
    public State previousState;
    private Animation marioRun;
    private Animation marioJump;
    private float stateTimer;
    private boolean runningRight;

    public enum State {
        FALLING,
        JUMPING,
        STANDING,
        RUNNING;
    }



    public Mario(PlayScreen screen) {
        super(screen.getAtlas().findRegion("turtle"));
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        //initialize running frames
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), i * 16, 0, 16, 16));
        }

        marioRun = new Animation(0.1f, frames);
        frames.clear();

        //initialize jump frames
        for(int i = 4; i < 6; i++) {
            frames.add(new TextureRegion(getTexture(), i * 16, 0, 16, 16));
        }
        marioJump = new Animation(0.1f, frames);



        marioStand = new TextureRegion(getTexture(), 0, 0, 16, 16);
        defineMario();
        setBounds(0, 0, 16 / MarioBros.PPM, 16 / MarioBros.PPM);

        //This is a textureRegion that is associated with the sprite
        setRegion(marioStand);
    }

    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth() / 2,
                b2body.getPosition().y - getHeight() / 2);
        //getFrame = returns approriate frame to display
        setRegion(getFrame(dt));
    }


    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case JUMPING:
                region = marioJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioStand;
                break;
        }

        if((b2body.getLinearVelocity().x < 0 || runningRight == false)
                && region.isFlipX() == false) {
            region.flip(true, false);
            runningRight = false;
        } else if((b2body.getLinearVelocity().x > 0 || runningRight)
                && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }


    public State getState() {
        if(b2body.getLinearVelocity().y > 0
                || (b2body.getLinearVelocity().y < 0
                    && previousState == State.JUMPING)) {
            return State.JUMPING;
        } else if(b2body.getLinearVelocity().y < 0) {
            return State.FALLING;
        } else if(b2body.getLinearVelocity().x != 0) {
            return State.RUNNING;
        } else {
            return State.STANDING;
        }
    }

    public void defineMario() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / MarioBros.PPM , 32 / MarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT
                | MarioBros.COIN_BIT
                | MarioBros.BRICK_BIT
                | MarioBros.ENEMY_BIT
                | MarioBros.OBJECT_BIT
                | MarioBros.ENEMY_HEAD_BIT
                | MarioBros.ITEM_BIT;


        fdef.shape = shape;
        b2body.createFixture(fdef);

        //sensor on mario's head
        EdgeShape head = new EdgeShape(); //linear between two points
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM),
                new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));

        fdef.shape = head;
        fdef.isSensor = true; //doesn't collide with any box2d now

        b2body.createFixture(fdef).setUserData("head");
    }


}
