import com.google.common.collect.Maps;
import javafx.util.Pair;
import store.io.impl.InputStream;
import store.io.impl.OutputStream;
import store.plugin.extension.ConfigExtensionBase;
import store.utilities.ReflectionUtils;
import suite.annotation.OrderType;

import java.lang.reflect.Field;
import java.util.Map;

public class BitConfig extends ConfigExtensionBase {

    private static Map<Field, Integer> fieldPriorities;

    @OrderType(priority = 1)
    public int varpId;
    @OrderType(priority = 2)
    public int leastSigBit;
    @OrderType(priority = 3)
    public int mostSigBit;


//
//
//    @OrderType(priority = 1)
//    public int length;
//    @OrderType(priority = 2)
//    public int isString;
//    @OrderType(priority = 3)
//    public int key;
//    @OrderType(priority = 4)
//    public String value;

    public static int highestValue = 0;

    @Override
    public void decode(int opcode, InputStream buffer) {
        if (opcode == 1) {
            varpId = buffer.readUnsignedShort();
            leastSigBit = buffer.readUnsignedByte();
            mostSigBit = buffer.readUnsignedByte();
//            if (varpId == 403) {
//                System.out.println("varp: "+ opcode + " Bits: " + leastSigBit + " MOST:"  + mostSigBit);
//            }
            if (varpId > highestValue) {
                highestValue = varpId;
                System.out.println("Highest value: " + highestValue);
            }
//            buffer.readUnsignedByte()
        }
    }

    @Override
    public OutputStream encode(OutputStream buffer) {
//        buffer.writeByte(249);
//        buffer.writeByte(length);
//        System.out.println("Packing " + length);
//        for (int i = 0; i < length; i++) {
//            if (i > 13800) {
//                System.out.println("Packing " + i);
//            }
//            int isInt = -1;
//            try {
//                isInt = Integer.parseInt(value);
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//            }
//            if (isInt == -1) {
//                isString = 1;
//            } else {
//                isString = 0;
//            }
//            buffer.writeByte(isString);
//            buffer.write24BitInt(key);
//            if (isInt == -1) {
//                buffer.writeString(value);
//            } else {
//                buffer.writeInt(isInt);
//            }
//        }
//        buffer.writeByte(0);
//




        buffer.writeByte(1);
        buffer.writeShort(varpId);
        buffer.writeByte(leastSigBit);
        buffer.writeByte(mostSigBit);
        buffer.writeByte(0);
//        buffer.flip();
        return buffer;
    }

    @Override
    public String toString() {
        return "" + id;
    }

    @Override
    public Map<Field, Integer> getPriority() {
        if (fieldPriorities != null)
            return fieldPriorities;
        Map<String, Pair<Field, Object>> values = ReflectionUtils.getValues(this);
        fieldPriorities = Maps.newHashMap();
        values.values().stream().forEach(pair -> {
            Field field = pair.getKey();
            int priority = field.isAnnotationPresent(OrderType.class) ? field.getAnnotation(OrderType.class).priority() : 1000;
            fieldPriorities.put(field, priority);
        });
        return fieldPriorities;
    }
}
