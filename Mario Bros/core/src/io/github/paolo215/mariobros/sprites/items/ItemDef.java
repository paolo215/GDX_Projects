package io.github.paolo215.mariobros.sprites.items;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by paolo on 9/10/2016.
 */
public class ItemDef {
    public Vector2 position;
    public Class<?> type;

    public ItemDef(Vector2 position, Class<?> type) {
        this.position = position;
        this.type = type;
    }
}
