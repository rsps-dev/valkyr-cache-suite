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

public class MapElementConfig extends ConfigExtensionBase {

    private static Map<Field, Integer> fieldPriorities;

    public int sprite1 = -1;
    public int sprite2 = -1;
    public String displayName = "";
    public int textSize = 0;
    public int colour = 0;
    public String menuName = "";
    public String[] actions = new String[5];
    public int horizontalAlignment = 0;
    public int verticalAlignment = 0;
    public int category = -1;
    public boolean showOnWorldMap = true;
    public boolean showOnMinimap = false;


    public Map<Integer, Object> params = null;

    @Override
    public void decode(int opcode, InputStream buffer) {
        {
            if (opcode == 1) {
                sprite1 = buffer.readBigSmart();
            } else if (opcode == 2) {
                sprite2 = buffer.readBigSmart();
            } else if (opcode == 3) {
                displayName = buffer.readString();
            } else if (opcode == 4) {
                colour = buffer.read24BitInt();
            } else if (opcode == 5) {
                buffer.read24BitInt();
            } else if (opcode == 6) {
                textSize = buffer.readUnsignedByte();
            } else if (opcode == 7) {
                int var3 = buffer.readUnsignedByte();
                if ((var3 & 1) == 0) {
                    showOnWorldMap = false;
                }

                if ((var3 & 2) == 2) {
                    showOnMinimap = true;
                }
            } else if (opcode == 8) {
                buffer.readUnsignedByte();
            } else if (opcode >= 10 && opcode <= 14) {
                actions[opcode - 10] = buffer.readString();
            } else if (opcode == 15) {
                int count = buffer.readUnsignedByte();
//                int[] field3300 = new int[count * 2];

                int var4;
                for (var4 = 0; var4 < count * 2; ++var4) {
                    buffer.readShort();
                }

                buffer.readInt();
                var4 = buffer.readUnsignedByte();
//                field3292 = new int[var4];

                int var5;
                for (var5 = 0; var5 < var4; ++var5) {
                    buffer.readInt();
                }

//                field3309 = new byte[count];

                for (var5 = 0; var5 < count; ++var5) {
                    buffer.readByte();
                }
            } else if (opcode == 16) {

            } else if (opcode == 17) {
                menuName = buffer.readString();
            } else if (opcode == 18) {
                buffer.readBigSmart();
            } else if (opcode == 19) {
                category = buffer.readUnsignedShort();
            } else if (opcode == 21) {
                buffer.readInt();
            } else if (opcode == 22) {
                buffer.readInt();
            } else if (opcode == 23) {
                buffer.readUnsignedByte();
                buffer.readUnsignedByte();
                buffer.readUnsignedByte();
            } else if (opcode == 24) {
                buffer.readShort();
                buffer.readShort();
            } else if (opcode == 25) {
                buffer.readBigSmart();
            } else if (opcode == 28) {
                buffer.readUnsignedByte();
            } else if (opcode == 29) {
                buffer.skip(1);
            } else if (opcode == 30) {
                buffer.skip(1);

            }
        }
    }


    @Override
    public OutputStream encode(OutputStream buffer) {
        if (sprite1 >= 0) {
            buffer.writeByte(1);
            buffer.writeBigSmart(sprite1);
        }

        if (sprite2 >= 0) {
            buffer.writeByte(2);
            buffer.writeBigSmart(sprite2);
        }

        buffer.writeByte(3);
        buffer.writeString(displayName);

        buffer.writeByte(4);
        buffer.writeMedium(colour);

        buffer.writeByte(6);
        buffer.writeByte(textSize);

            buffer.writeByte(7);

            int mask = 0;
            if (showOnWorldMap) {;
                mask = 1;
            }

            if (showOnMinimap) {
                mask =  2;
            }

            buffer.writeByte(mask);

        for (int i = 0; i < 5; i++) {
            if (!actions[i].isEmpty()) {
                buffer.writeByte(10 );
                buffer.writeString(actions[i]);
            }
        }

//        if (!menuName.isEmpty() && !menuName.isBlank()) {
//            buffer.writeByte(17);
//            buffer.writeString(menuName);
//        }

        if (category >= 0) {
            buffer.writeByte(19);
            buffer.writeShort(category);
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
