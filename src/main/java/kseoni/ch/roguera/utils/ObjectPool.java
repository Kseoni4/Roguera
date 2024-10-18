package kseoni.ch.roguera.utils;

import kseoni.ch.roguera.base.GameObject;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.HashMap;

public class ObjectPool {

    private final HashMap<Integer, GameObject> pool;

    private static ObjectPool INSTANCE;

    private ObjectPool(){
        pool = new HashMap<>();
    }

    public static ObjectPool get(){
        if(INSTANCE == null){
            INSTANCE = new ObjectPool();
        }
        return INSTANCE;
    }

    public void removeObjectFromPool(GameObject object){
        boolean flag = pool.remove(object.getId(), object);
        System.out.println("removed "+object+" - "+flag);
    }

    public void putObjectIntoPool(GameObject object){
        pool.put(object.getId(), object);
    }

    public <T> T getObjectFromPoolById(int objectId){
        return (T) pool.get(objectId);
    }

/*    public void setPool(HashMap<Integer, GameObject> pool) {
        ObjectPool.pool = pool;
    }*/

    @SneakyThrows
    public void dumpPoolIntoFile(){
        BufferedWriter writer = new BufferedWriter(new FileWriter(LocalDate.now()+"_objects.txt"));

        StringBuilder builder = new StringBuilder();

        builder.append("GameObject instance class --- ID --- Name --- Position --- Sprite \n");

        for(GameObject gameObject : pool.values()){
            builder.append("[")
                    .append(gameObject.getClass().getName())
                    .append("]")
                    .append(" - ")
                    .append(gameObject.getId())
                    .append(" - ")
                    .append(gameObject.getName())
                    .append(" - ")
                    .append(gameObject.getPosition())
                    .append(" - ")
                    .append(gameObject.getTextSprite().getSpriteChar())
                    .append("\n");
        }
        writer.write(builder.toString());
        writer.flush();
        writer.close();
    }
}
