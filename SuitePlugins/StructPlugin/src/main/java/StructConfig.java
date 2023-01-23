import com.google.common.collect.Maps;
import javafx.util.Pair;
import store.io.impl.InputStream;
import store.io.impl.OutputStream;
import store.plugin.extension.ConfigExtensionBase;
import store.utilities.ReflectionUtils;
import suite.annotation.OrderType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StructConfig extends ConfigExtensionBase {

    private static Map<Field, Integer> fieldPriorities;

    @OrderType(priority = 1)
    public int[] keyType;

    @OrderType(priority = 2)
    public String[] stringValues;

    @OrderType(priority = 3)
    public int[] intValues;
    @Override
    public void decode(int opcode, InputStream buffer) {
        if (opcode == 249)
        {
            int length = buffer.readUnsignedByte();
            keyType = new int[length];
            stringValues = new String[length];
            intValues = new int[length];

            for (int i = 0; i < length; i++)
            {
                boolean isString = buffer.readUnsignedByte() == 1;
                keyType[i] = buffer.read24BitInt();

                if (isString)
                {
                    stringValues[i] = buffer.readString();
                }
                else
                {
                    intValues[i] = buffer.readInt();
                }
            }
        }
    }

    @Override
    public OutputStream encode(OutputStream buffer) {
        System.out.println("This is not even called");
        buffer.writeByte(249);
        int length = keyType.length;
        buffer.writeByte(length);
        for (int i = 0; i < length; i++) {
            buffer.writeByte(stringValues[i] != null ? 1 : 0);
            buffer.write24BitInt(keyType[i]);
            if (stringValues[i] != null) {
                buffer.writeString(stringValues[i]);
            } else {
                buffer.writeInt(intValues[i]);
            }
        }
        buffer.writeByte(0);
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
