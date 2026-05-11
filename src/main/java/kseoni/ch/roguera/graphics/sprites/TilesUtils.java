package kseoni.ch.roguera.graphics.sprites;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TilesUtils {

    private static final Map<Integer, Character> WALL_TILES = new LinkedHashMap<>();

    private static final Map<Character, Integer> GLYPH_TO_MASK = new LinkedHashMap<>();

    static {
        WALL_TILES.putAll(Map.of(
                //WSEN
                0b0000, AssetPool.get().getAsset("wall_point"),
                0b0001, AssetPool.get().getAsset("wall_end_up"),
                0b0010, AssetPool.get().getAsset("wall_end_right"),
                0b0011, AssetPool.get().getAsset("wall_corner_bottom_l"),
                0b0100, AssetPool.get().getAsset("wall_end_down"),
                0b0101, AssetPool.get().getAsset("wall_v"),
                0b0110, AssetPool.get().getAsset("wall_corner_top_l"),
                0b0111, AssetPool.get().getAsset("wall_tsharp_left"),
                0b1000, AssetPool.get().getAsset("wall_end_left"),
                0b1001, AssetPool.get().getAsset("wall_corner_bottom_r")
        ));
        WALL_TILES.putAll(Map.of(
                0b1010, AssetPool.get().getAsset("wall_h"),
                0b1011, AssetPool.get().getAsset("wall_tsharp_up"),
                0b1100, AssetPool.get().getAsset("wall_corner_top_r"),
                0b1101, AssetPool.get().getAsset("wall_tsharp_right"),
                0b1110, AssetPool.get().getAsset("wall_tsharp_down"),
                0b1111, AssetPool.get().getAsset("wall_tsharp")
        ));

        for (Map.Entry<Integer, Character> entry : WALL_TILES.entrySet()) {
            GLYPH_TO_MASK.put(entry.getValue(), entry.getKey());
        }
    }

    public static int getMaskByGlyph(Character glyph){
        if(glyph == null){
            return 0;
        }
        return GLYPH_TO_MASK.get(glyph) == null ? 0 : GLYPH_TO_MASK.get(glyph);
    }

    public static boolean isCorner(char glyph){
        int maskByGlyph = getMaskByGlyph(glyph);

        if(maskByGlyph == 0){
            return false;
        }

        return      (maskByGlyph & 0b0011) != 0 // is bottom left corner
                ||  (maskByGlyph & 0b0110) != 0 // is top left corner
                ||  (maskByGlyph & 0b1100) != 0 // is top right corner
                ||  (maskByGlyph & 0b1001) != 0; // is bottom right corner
    }

    public static char getByMask(int mask){
        return WALL_TILES.getOrDefault(mask, '?');
    }

    public static boolean continuesEast(char glyph) {
        Integer mask = GLYPH_TO_MASK.get(glyph);
        return mask != null && (mask & 0b0010) != 0;  // bit1 = E
    }

    public static char horizontalGlyph() {
        return WALL_TILES.get(0b1010);
    }

}
