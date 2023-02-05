package script;

import com.google.common.collect.Maps;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import store.io.impl.InputStream;
import store.io.impl.OutputStream;
import store.plugin.extension.ConfigExtensionBase;
import store.utilities.ReflectionUtils;
import suite.annotation.OrderType;

import java.lang.reflect.Field;
import java.util.Map;

import static script.CS2Opcode.*;

@Slf4j
@Setter
@Getter
public class CS2Script extends ConfigExtensionBase {

    private static Map<Field, Integer> fieldPriorities;

    @OrderType(priority = 1)
    public int localIntCount;
    @OrderType(priority = 2)
    public String[] stringOrphands;
    @OrderType(priority = 3)
    public int[] instructions;
    @OrderType(priority = 4)
    public int localStringCount;
    @OrderType(priority = 5)
    public int intStackCount;
    @OrderType(priority = 6)
    public int stringStackCount;
    @OrderType(priority = 7)
    public int[] intOrphands;

    @OrderType(priority = 8)
    public Map<Integer, Integer>[] switches;

    @Override
    public void decode(InputStream buffer) {

        buffer.setPosition(buffer.buffer.length - 2);
        int switchLength = buffer.readUnsignedShort();

        int endIdx = buffer.buffer.length - 2 - switchLength - 12;
        buffer.setPosition(endIdx);
        int numOpcodes = buffer.readInt();
        localIntCount = buffer.readUnsignedShort();
        localStringCount = buffer.readUnsignedShort();
        intStackCount = buffer.readUnsignedShort();
        stringStackCount = buffer.readUnsignedShort();

        int numSwitches = buffer.readUnsignedByte();
        if (numSwitches > 0)
        {

            @SuppressWarnings("unchecked") Map<Integer, Integer>[] switches = new Map[numSwitches];
            setSwitches(switches);

            for (int i = 0; i < numSwitches; i++)
            {
                switches[i] = Maps.newHashMap();

                int count = buffer.readUnsignedShort();
                while (count-- > 0)
                {
                    int key = buffer.readInt(); // int from stack is compared to this
                    int pcOffset = buffer.readInt(); // pc jumps by this

                    switches[i].put(key, pcOffset);
                }
            }
        }
        buffer.setPosition(0);
        buffer.readStringOrNull();
        instructions = new int[numOpcodes];
        intOrphands = new int[numOpcodes];
        stringOrphands = new String[numOpcodes];
        int opcode;
        int index = 0;
        while (buffer.getPosition() < endIdx)
        {
            opcode = buffer.readUnsignedShort();
            if (opcode == SCONST)
            {
                stringOrphands[index] = buffer.readString();
            }
            else if(opcode < 100 && opcode != CS2Opcode.RETURN && opcode != CS2Opcode.POP_INT && opcode != CS2Opcode.POP_STRING)
            {
                intOrphands[index] = buffer.readInt();
            }
            else
            {
                intOrphands[index] = buffer.readUnsignedByte();
            }
            instructions[index++] = opcode;
        }
    }

    @Override
    public OutputStream encode(OutputStream buffer) {

        int[] instructions = getInstructions();
        int[] intOperands = getIntOrphands();
        String[] stringOperands = getStringOrphands();
        Map<Integer, Integer>[] switches = getSwitches();

        buffer.writeByte(0); // null string
        for (int i = 0; i < instructions.length; ++i)
        {
            int opcode = instructions[i];
            buffer.writeShort(opcode);
            if (opcode == SCONST)
            {
                buffer.writeString(stringOperands[i]);
            }
            else if (opcode < 100 && opcode != CS2Opcode.RETURN && opcode != CS2Opcode.POP_INT && opcode != CS2Opcode.POP_STRING)
            {
                buffer.writeInt(intOperands[i]);
            }
            else
            {
                buffer.writeByte(intOperands[i]);
            }
        }
        buffer.writeInt(instructions.length);
        buffer.writeShort(getLocalIntCount());
        buffer.writeShort(getLocalStringCount());
        buffer.writeShort(getIntStackCount());
        buffer.writeShort(getStringStackCount());
        int switchStart = buffer.position;
        if (switches == null)
        {
            buffer.writeByte(0);
        }
        else
        {
            buffer.writeByte(switches.length);
            for (Map<Integer, Integer> s : switches)
            {
                buffer.writeShort(s.size());
                for (Map.Entry<Integer, Integer> e : s.entrySet())
                {
                    buffer.writeInt(e.getKey());
                    buffer.writeInt(e.getValue());
                }
            }
        }
        int switchLength = buffer.position - switchStart;
        buffer.writeShort(switchLength);

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
