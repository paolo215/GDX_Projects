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

import io.github.paolo215.mariobros.MarioBros;
import io.github.paolo215.mariobros.sprites.Brick;
import io.github.paolo215.mariobros.sprites.Coin;

/**
 * Created by paolo on 8/27/2016.
 */
public class B2WorldCreator {
    public B2WorldCreator(World world, TiledMap map) {
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
            body.createFixture(fdef);
        }


        //create brick bodies/fixtures
        for(MapObject object : map.getLayers().get(5).getObjects()
                .getByType(RectangleMapObject.class)) {

            //body
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new Brick(world, map, rect);
        }

        //create coin bodies/fixtures
        for(MapObject object : map.getLayers().get(4).getObjects()
                .getByType(RectangleMapObject.class)) {

            //body
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new Coin(world, map, rect);
        }

    }
}