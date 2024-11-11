package kseoni.ch.roguera.base;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Event<T> {

   private final T value;

   public Event(T value) {
       this.value = value;
   }

   public static <T> Event<T> raise(T value) {
       return new Event<>(value);
   }
}
