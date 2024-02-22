    import com.google.common.collect.Maps;
    import javafx.util.Pair;
    import store.io.impl.InputStream;
    import store.io.impl.OutputStream;
    import store.plugin.extension.ConfigExtensionBase;
    import store.utilities.ReflectionUtils;
    import suite.annotation.OrderType;

    import java.lang.reflect.Field;
    import java.util.Map;

    public class IDKConfig extends ConfigExtensionBase {

        private static Map<Field, Integer> fieldPriorities;
        @OrderType(priority = 1)
        public int[] recolorToReplace;
        @OrderType(priority = 2)
        public int[] recolorToFind;
        @OrderType(priority = 3)
        public int[] retextureToFind;
        @OrderType(priority = 4)
        public int[] retextureToReplace;
        @OrderType(priority = 5)
        public int bodyPartId = -1;
        @OrderType(priority = 6)
        public int[] models;
        @OrderType(priority = 7)
        public int[] chatheadModels = new int[]
        {
            -1, -1, -1, -1, -1
        };
        public boolean nonSelectable = false;

        @Override
        public void decode(int opcode, InputStream buffer) {
                if (opcode == 1)
                {
                    bodyPartId = buffer.readUnsignedByte();
                }
                else if (opcode == 2)
                {
                    int length = buffer.readUnsignedByte();
                    models = new int[length];

                    for (int index = 0; index < length; ++index)
                    {
                        models[index] = buffer.readUnsignedShort();
                    }
                }
                else if (opcode == 3)
                {
                    nonSelectable = true;
                }
                else if (opcode == 40)
                {
                    int length = buffer.readUnsignedByte();
                    recolorToFind = new int[length];
                    recolorToReplace = new int[length];

                    for (int index = 0; index < length; ++index)
                    {
                        recolorToFind[index] = buffer.readShort();
                        recolorToReplace[index] = buffer.readShort();
                    }
                }
                else if (opcode == 41)
                {
                    int length = buffer.readUnsignedByte();
                    retextureToFind = new int[length];
                    retextureToReplace = new int[length];

                    for (int index = 0; index < length; ++index)
                    {
                        retextureToFind[index] = buffer.readShort();
                        retextureToReplace[index] = buffer.readShort();
                    }
                }
                else if (opcode >= 60 && opcode < 70)
                {
                    chatheadModels[opcode - 60] = buffer.readUnsignedShort();
                }
            }


        @Override
        public OutputStream encode(OutputStream buffer) {
            buffer.writeByte(1);
            buffer.writeByte(bodyPartId);
            buffer.writeByte(2);
            buffer.writeByte(models.length);
            for (int index = 0; index < models.length; index++)
            {
                buffer.writeShort(models[index]);
            }
            buffer.writeByte(3);
            buffer.writeByte(nonSelectable ? 1 : 0);
            buffer.writeByte(40);
            buffer.writeByte(recolorToFind.length);
            for (int index = 0; index < recolorToFind.length; index++)
            {
                buffer.writeShort(recolorToFind[index]);
                buffer.writeShort(recolorToReplace[index]);
            }
            buffer.writeByte(41);
            buffer.writeByte(retextureToFind.length);
            for (int index = 0; index < retextureToFind.length; index++)
            {
                buffer.writeShort(retextureToFind[index]);
                buffer.writeShort(retextureToReplace[index]);
            }
            for (int index = 0; index < chatheadModels.length; index++)
            {
                if (chatheadModels[index] != -1)
                {
                    buffer.writeByte(60 + index);
                    buffer.writeShort(chatheadModels[index]);
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
