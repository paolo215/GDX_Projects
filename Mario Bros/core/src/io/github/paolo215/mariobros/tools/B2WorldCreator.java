package io.github.paolo215.mariobros.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import io.github.paolo215.mariobros.MarioBros;
import io.github.paolo215.mariobros.screens.PlayScreen;
import io.github.paolo215.mariobros.sprites.tileobjects.Brick;
import io.github.paolo215.mariobros.sprites.tileobjects.Coin;
import io.github.paolo215.mariobros.sprites.enemies.Goomba;

/**
 * Created by paolo on 8/27/2016.
 */
public class B2WorldCreator {

    private Array<Goomba> goombas;

    public B2WorldCreator(PlayScreen screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();


        //need to define what body consists of
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //create ground bodies and fixtures
        for(MapObject object : map.getLayers().get(2).getObjects()
                .getByType(RectangleMapObject.class)) {

            //body
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM,
                    (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);
            body = world.createBody(bdef);

            //fixture
            shape.setAsBox((rect.getWidth() / 2) / MarioBros.PPM,
                    (rect.getHeight() / 2) / MarioBros.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        //create pipe bodies/fixtures
        for(MapObject object : map.getLayers().get(3).getObjects()
                .getByType(RectangleMapObject.class)) {

            //body
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM,
                    (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);
            body = world.createBody(bdef);

            //fixture
            shape.setAsBox((rect.getWidth() / 2) / MarioBros.PPM,
                    (rect.getHeight() / 2) / MarioBros.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MarioBros.OBJECT_BIT;
            body.createFixture(fdef);
        }


        //create brick bodies/fixtures
        for(MapObject object : map.getLayers().get(5).getObjects()
                .getByType(RectangleMapObject.class)) {


            new Brick(screen, object);
        }

        //create coin bodies/fixtures
        for(MapObject object : map.getLayers().get(4).getObjects()
                .getByType(RectangleMapObject.class)) {

            new Coin(screen, object);
        }

        //create all goombas
        goombas = new Array<Goomba>();
        for(MapObject object : map.getLayers().get(6).getObjects()
                .getByType(RectangleMapObject.class)) {

            //body
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            goombas.add(new Goomba(screen, rect.getX() / MarioBros.PPM,
                    rect.getY() / MarioBros.PPM));
        }

    }

    public Array<Goomba> getGoombas() {
        return goombas;
    }
}
