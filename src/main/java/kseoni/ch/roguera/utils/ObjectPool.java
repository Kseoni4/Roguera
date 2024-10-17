package kseoni.ch.roguera.utils;

import kseoni.ch.roguera.base.GameObject;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.HashMap;

public class ObjectPool {

    private static HashMap<Integer, GameObject> pool;

    static {
        pool = new HashMap<>();
    }

    public static void putObjectIntoPool(GameObject object){
        pool.put(object.getId(), object);
    }

    public static <T> T getObjectFromPoolById(int objectId){
        return (T) pool.get(objectId);
    }

    public static void setPool(HashMap<Integer, GameObject> pool) {
        ObjectPool.pool = pool;
    }

    @SneakyThrows
    public static void dumpPoolIntoFile(){
        BufferedWriter writer = new BufferedWriter(new FileWriter(LocalDate.now()+"_objects.txt"));

        StringBuilder builder = new StringBuilder();

        builder.append("GameObject instance class --- ID --- Name --- Position \n");

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
                    .append("\n");
        }
        writer.write(builder.toString());
        writer.flush();
        writer.close();
    }
}
