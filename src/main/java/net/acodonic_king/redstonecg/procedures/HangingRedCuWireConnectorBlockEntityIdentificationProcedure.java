package net.acodonic_king.redstonecg.procedures;

import java.util.ArrayList;
import java.util.List;

public class HangingRedCuWireConnectorBlockEntityIdentificationProcedure {
    public static List<Integer> IDENTIFIERS = new ArrayList<>();
    public static List<Integer> FREED = new ArrayList<>();
    public static int add(){
        int value = 0;
        if (FREED.isEmpty()) {
            value = IDENTIFIERS.size();
            IDENTIFIERS.add(value);
        } else {
            value = FREED.remove(0);
            IDENTIFIERS.add(value, value);
        }
        return value;
    }
    public static boolean remove(int value){
        if(!IDENTIFIERS.contains(value))
            return false;
        FREED.add(
                IDENTIFIERS.remove(
                        IDENTIFIERS.indexOf(value)
                )
        );
        return true;
    }
}
