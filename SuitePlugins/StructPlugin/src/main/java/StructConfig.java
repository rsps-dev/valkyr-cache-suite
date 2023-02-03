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
    public int id;

    @OrderType(priority = 2)
    public Map<Integer, Object> params;

    @Override
    public void decode(int opcode, InputStream buffer) {
        if (opcode == 249) {
            int length = buffer.readUnsignedByte();

            params = new HashMap<>(length);

            for (int i = 0; i < length; i++) {
                boolean isString = buffer.readUnsignedByte() == 1;
                int key = buffer.read24BitInt();
                Object value;

                if (isString) {
                    value = buffer.readString();
                } else {
                    value = buffer.readInt();
                }

                params.put(key, value);
            }
        }
    }

    @Override
    public OutputStream encode(OutputStream buffer) {
        if (Objects.nonNull(params)) {
            buffer.writeByte(249);
            buffer.writeByte(params.size());
            for (int key : params.keySet()) {
                Object value = params.get(key);
                buffer.writeByte(value instanceof String ? 1 : 0);
                buffer.write24BitInt(key);
                if (value instanceof String) {
                    buffer.writeString((String) value);
                } else {
                    buffer.writeInt((Integer) value);
                }
            }
        }
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