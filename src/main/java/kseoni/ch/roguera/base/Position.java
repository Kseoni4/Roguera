package kseoni.ch.roguera.base;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Position {

    private int x;

    private int y;

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
    public static final Position[] AROUND_POSITIONS = {
            NORTH,
            WEST,
            EAST,
            SOUTH,
            NORTH_WEST,
            NORTH_EAST,
            SOUTH_WEST,
            SOUTH_EAST
    };

    public static final Position[] CARDINAL_POSITIONS = {
            NORTH,
            EAST,
            SOUTH,
            WEST
    };

    public static final Position FRONT = new Position(0,1);
    public static final Position BACK = new Position(0,-1);
    public static final Position LEFT = new Position(-1,0);
    public static final Position RIGHT = new Position(1,0);

    public static final Position ZERO = new Position(0,0);

    public boolean isNegative(){
        return x < 0 || y < 0;
    }

    public Position set(Position position){
        return set(position.x, position.y);
    }

    public Position set(int x, int y){
//        this.x = x;
//        this.y = y;
        return new Position(x, y);
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

    public boolean isInBetweenX(Position point1, Position  point2){
        return isInBetweenX(point1.x, point2.x);
    }

    public boolean isInBetweenY(Position point1, Position  point2){
        return isInBetweenY(point1.y, point2.y);
    }

    public boolean isInBetweenX(int x1, int x2){
        System.out.printf("""
                        x0 = %s ; x1 = %s
                        x0 = %s ; x2 = %s
                        
                        %s > %s && %s < %s
                        
                        %n""", this.x, x1,
        this.x, x2,
        this.x, x1, this.x, x2
);
        return (this.x > x1 && this.x < x2);
    }

    public boolean isInBetweenY(int y1, int y2){
        System.out.printf("""
                        y0 = %s ; y1 = %s
                        y0 = %s ; y2 = %s
                        
                        %s >= %s && %s <= %s
                        
                        %n""", this.y, y1,
                this.y, y2,
                this.y, y1, this.y, y2
        );
        return (this.y >= y1 && this.y <= y2);
    }

    public double getDistance(Position position){
        return getDistance(position.x, position.y);
    }

    public double getDistance(int x, int y){
        return Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2));
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

    @Override
    public String toString() {
        return "{"+this.x +";"+this.y+"}";
    }
}
