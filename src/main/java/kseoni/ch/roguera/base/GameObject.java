
package kseoni.ch.roguera.base;

import kseoni.ch.roguera.rendering.TextSprite;
import kseoni.ch.roguera.utils.ObjectPool;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
@Getter
public abstract class GameObject implements Serializable {

    private static EmptyObject emptyObject;

    private final int id;

    @Setter
    private String name;

    private static int idCounter = 0;

    @Setter
    private Position position;

    @Setter
    private TextSprite textSprite;

    public GameObject(String name) {
        this.name = name;
        this.id = ++idCounter;
        ObjectPool.putObjectIntoPool(this);
    }

    public GameObject() {
        this("Unnamed Object");
        this.name = this.name.concat("-").concat(String.valueOf(id));
    }

    public static EmptyObject getEmpty(){
        if(emptyObject == null){
            emptyObject = new EmptyObject("Empty object");
        }
        return emptyObject;
    }

    private static class EmptyObject extends GameObject {
        public EmptyObject(String name) {
            super(name);
            setTextSprite(TextSprite.DEFAULT_SPRITE);
        }
    }

}
