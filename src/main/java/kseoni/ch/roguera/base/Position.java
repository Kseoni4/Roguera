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
                this.x + position.x,
                this.y + position.y
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
