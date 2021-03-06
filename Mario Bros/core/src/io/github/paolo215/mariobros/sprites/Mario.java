package io.github.paolo215.mariobros.sprites;

import com.badlogic.gdx.audio.Sound;
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

    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation bigMarioRun;
    private Animation growMario;

    public State currentState;
    public State previousState;
    private Animation marioRun;
    private TextureRegion marioJump;
    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefinteBigMario;
    private boolean timeToRedefineMario;

    public enum State {
        FALLING,
        JUMPING,
        STANDING,
        RUNNING,
        GROWING;
    }



    public Mario(PlayScreen screen) {
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        //initialize running frames
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"),
                    i * 16, 0, 16, 16));
        }

        marioRun = new Animation(0.1f, frames);
        frames.clear();

        //initialize jump frames
        for(int i = 4; i < 6; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"),
                    i * 16, 0, 16, 16));
        }
        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"),
                80, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"),
                80, 0, 16, 32);

        frames.clear();

        //initialize big running frames
        for(int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"),
                    i * 16, 0, 16, 16));
        }
        bigMarioRun = new Animation(0.1f, frames);

        frames.clear();
        //Mario grow animation
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),
                240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),
                0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),
                240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),
                0, 0, 16, 32));
        growMario = new Animation(0.2f, frames);

        frames.clear();

        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"),
                0, 0, 16, 16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"),
                0, 0, 16, 32);

        defineMario();
        setBounds(0, 0, 16 / MarioBros.PPM, 16 / MarioBros.PPM);

        //This is a textureRegion that is associated with the sprite
        setRegion(marioStand);
    }

    public void update(float dt) {
        if(marioIsBig == true) {
            setPosition(b2body.getPosition().x - getWidth() / 2,
                    b2body.getPosition().y - getHeight() / 2  - 6 / MarioBros.PPM);
        } else {
            setPosition(b2body.getPosition().x - getWidth() / 2,
                    b2body.getPosition().y - getHeight() / 2);
        }
        //getFrame = returns approriate frame to display
        setRegion(getFrame(dt));

        if(timeToDefinteBigMario) {
            defineBigMario();
        }

        if (timeToRedefineMario) {
            redefineMario();
        }
    }


    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer)) {
                    runGrowAnimation = false;
                }
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ?  bigMarioRun.getKeyFrame(stateTimer, true) : marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig ? bigMarioStand : marioStand;
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

    public void grow() {
        runGrowAnimation = true;
        marioIsBig = true;
        timeToDefinteBigMario = true;
        setBounds(getX(), getY(), getWidth(), getHeight() * 2);
        MarioBros.manager.get("audio/sounds/powerup.wav", Sound.class).play();
    }


    public State getState() {
        if(runGrowAnimation) {
            return State.GROWING;
        } else if(b2body.getLinearVelocity().y > 0
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

    public void redefineMario() {
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

        timeToRedefineMario = false;
    }

    public void defineBigMario() {
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);


        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition);
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
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / MarioBros.PPM));
        b2body.createFixture(fdef).setUserData(this);


        //sensor on mario's head
        EdgeShape head = new EdgeShape(); //linear between two points
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM),
                new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));

        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true; //doesn't collide with any box2d now

        b2body.createFixture(fdef).setUserData(this);
        timeToDefinteBigMario = false;
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
        b2body.createFixture(fdef).setUserData(this);

        //sensor on mario's head
        EdgeShape head = new EdgeShape(); //linear between two points
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM),
                new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));

        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true; //doesn't collide with any box2d now

        b2body.createFixture(fdef).setUserData(this);
    }

    public boolean isBig() {
        return marioIsBig;
    }

    public void hit() {
        if(marioIsBig == true) {
            marioIsBig = false;
            timeToRedefineMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() / 2);
            MarioBros.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
        } else {
            MarioBros.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
        }
    }

}
