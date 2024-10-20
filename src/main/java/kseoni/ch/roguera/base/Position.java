package kseoni.ch.roguera.base;

import kseoni.ch.roguera.graphics.render.Window;
import lombok.*;

import java.util.Objects;
import java.util.Random;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Position{

    /**
     * Статическая константа, описывающая точку расположенную по "розе ветров" на одну относительно текущей.
     */
    private static final Position NORTH = new Position(0,-1);
    /**
     * Статическая константа, описывающая точку расположенную по "розе ветров" на одну относительно текущей.
     */
    private static final Position WEST = new Position(-1,0);
    /**
     * Статическая константа, описывающая точку расположенную по "розе ветров" на одну относительно текущей.
     */
    private static final Position EAST = new Position(1,0);
    /**
     * Статическая константа, описывающая точку расположенную по "розе ветров" на одну относительно текущей.
     */
    private static final Position SOUTH = new Position(0,1);
    /**
     * Статическая константа, описывающая точку расположенную по "розе ветров" на одну относительно текущей.
     */
    private static final Position NORTH_WEST = new Position(-1,-1);
    /**
     * Статическая константа, описывающая точку расположенную по "розе ветров" на одну относительно текущей.
     */
    private static final Position NORTH_EAST = new Position(1,-1);
    /**
     * Статическая константа, описывающая точку расположенную по "розе ветров" на одну относительно текущей.
     */
    private static final Position SOUTH_WEST = new Position(-1,1);
    /**
     * Статическая константа, описывающая точку расположенную по "розе ветров" на одну относительно текущей.
     */
    private static final Position SOUTH_EAST = new Position(1,1);

    /**
     * Статический массив точек вокруг данной.
     */
    public static final Position[] AroundPositions = {
            NORTH,
            WEST,
            EAST,
            SOUTH,
            NORTH_WEST,
            NORTH_EAST,
            SOUTH_WEST,
            SOUTH_EAST
    };

    public static final Position FRONT = new Position(0,1);
    public static final Position BACK = new Position(0,-1);
    public static final Position LEFT = new Position(-1,0);
    public static final Position RIGHT = new Position(1,0);

    public static final Position ZERO = new Position(0,0);

    public static Position getRandomPosition(){
        return getRandomPosition(Window.get().getWight(), Window.get().getHeight());
    }

    public static Position getRandomPosition(int boundX, int boundY){
        Random random = new Random();
        return new Position(random.nextInt(0,boundX), random.nextInt(0,boundY));
    }

    private int x;

    private int y;

    public boolean isNegative(){
        return x < 0 || y < 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Position position)){
            return false;
        }
        if(obj == this){
            return true;
        }

        return this.x == position.x && this.y == position.y;
    }

    public Position set(Position position){
        return set(position.x, position.y);
    }

    public Position set(int x, int y){
        this.x = x;
        this.y = y;
        return this;
    }

    public Position getRelativePosition(Position position){
        return getRelativePosition(
                position.x,
                position.y
        );
    }

    public Position getRelativePosition(int x, int y){
        return new Position(
                this.x + x,
                this.y + y
        );
    }

    @Override
    public String toString() {
        return "{"+this.x +";"+this.y+"}";
    }
}
