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

public class InvConfig extends ConfigExtensionBase {

    private static Map<Field, Integer> fieldPriorities;

    @OrderType(priority = 1)
    public int[] getId;

    @OrderType(priority = 2)
    public int getSize;

    @Override
    public void decode(int opcode, InputStream buffer) {
        InventoryDefinition def = new InventoryDefinition();
        def.id = id;
        InputStream is = new InputStream();
        while (true)
        {
            int getId = buffer.readUnsignedByte();
            if (opcode == 0)
            {
                break;
            }

            if (opcode == 2)
            {
                getSize = buffer.readUnsignedShort();
            }
        }
    }

    @Override
    public OutputStream encode(OutputStream buffer) {
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
