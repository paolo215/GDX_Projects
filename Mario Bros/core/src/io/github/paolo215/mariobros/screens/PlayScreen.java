package io.github.paolo215.mariobros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.github.paolo215.mariobros.MarioBros;
import io.github.paolo215.mariobros.scenes.Hud;
import io.github.paolo215.mariobros.sprites.enemies.Enemy;
import io.github.paolo215.mariobros.sprites.Mario;
import io.github.paolo215.mariobros.sprites.items.Item;
import io.github.paolo215.mariobros.sprites.items.ItemDef;
import io.github.paolo215.mariobros.sprites.items.Mushroom;
import io.github.paolo215.mariobros.tools.B2WorldCreator;
import io.github.paolo215.mariobros.tools.WorldContactListener;

/**
 * Created by paolo on 8/26/2016.
 */

/**
 * world
 *      world
 *          bodies
 *              mass
 *              velocity
 *              location
 *              angles
 *              fixtures
 *                  shape
 *                  density
 *                  friction
 *                  restitution
 */

public class PlayScreen implements Screen {
    private MarioBros game;
    private TextureAtlas atlas;

    private Music music;

    //playscreen
    private OrthographicCamera gamecam;
    private Hud hud;
    private Viewport gamePort;


    //Tiled map
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    private Mario player;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;


    public PlayScreen(MarioBros game) {
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        this.game = game;
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM,
                MarioBros.V_HEIGHT / MarioBros.PPM, gamecam);
        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);
        gamecam.position.set(gamePort.getWorldWidth() / 2,
                gamePort.getWorldHeight() / 2, 0);

        //first param: gravity, 2nd param: move (false) or at rest (true)
        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        player = new Mario(this);

        world.setContactListener(new WorldContactListener());


        //set up music
        music = MarioBros.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.play();


        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }


    public void spawnItem(ItemDef idef) {
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems() {
        if(itemsToSpawn.isEmpty() == false) {
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Mushroom.class) {
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }


    public TextureAtlas getAtlas() {
        return atlas;
    }

    public void handleInput(float dt) {
        //force = gradual movement
        //impulse = immediate
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            player.b2body.applyLinearImpulse(new Vector2(0, 4f),
                    player.b2body.getWorldCenter(), true);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)
                && player.b2body.getLinearVelocity().x <= 2) {
            player.b2body.applyLinearImpulse(new Vector2(0.1f, 0),
                    player.b2body.getWorldCenter(), true);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)
                && player.b2body.getLinearVelocity().x >= -2) {
            player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0),
                    player.b2body.getWorldCenter(), true);
        }
    }

    public void update(float dt) {
        handleInput(dt);
        handleSpawningItems();
        world.step(1/60f, 6, 2);

        player.update(dt);
        for(Enemy enemy : creator.getGoombas()) {
            enemy.update(dt);

            //reactives
            if(enemy.getX() < player.getX() + 224 / MarioBros.PPM) {
                enemy.b2body.setActive(true);
            }
        }

        for(Item item: items) {
            item.update(dt);
        }


        hud.update(dt);
        gamecam.position.x = player.b2body.getPosition().x;
        gamecam.update();

        //tell our render to draw only what our camera can see in our game world
        renderer.setView(gamecam);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //separate our update logic from render
        update(delta);

        //Clear the game screen with black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //map reder
        renderer.render();

        //reder our Box2D
        b2dr.render(world, gamecam.combined);

        //main cam (set only what the game can see)
        game.batch.setProjectionMatrix(gamecam.combined);
        //begin batch
        game.batch.begin();
        //draw give game.batch to draw itself on
        player.draw(game.batch);
        for(Enemy enemy : creator.getGoombas()) {
            enemy.draw(game.batch);
        }

        for(Item item : items) {
            item.draw(game.batch);
        }

        //end batch
        game.batch.end();

        //Set our batch to now draw what the Hud camera sees
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

    }


    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
