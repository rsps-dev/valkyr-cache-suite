import com.google.common.collect.Maps;
import javafx.util.Pair;
import org.apache.commons.lang3.ArrayUtils;
import store.io.impl.InputStream;
import store.io.impl.OutputStream;
import suite.annotation.OrderType;
import store.plugin.extension.ConfigExtensionBase;
import store.utilities.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */

/**
 * @author ReverendDread
 * Sep 23, 2019
 */
public class SequenceConfig extends ConfigExtensionBase {

	@Override
	public void decode(int opcode, InputStream buffer) {
		int var3;
		int var4;
		if (opcode == 1) {
			var3 = buffer.readUnsignedShort();
			frameLenghts = new int[var3];
			for (var4 = 0; var4 < var3; ++var4) {
				frameLenghts[var4] = buffer.readUnsignedShort();
			}
			frameFileIds = new int[var3];
			frameArchiveIds = new int[var3];
			for (var4 = 0; var4 < var3; ++var4) {
				frameFileIds[var4] = buffer.readUnsignedShort();
			}
			for (var4 = 0; var4 < var3; ++var4) {
				frameArchiveIds[var4] = buffer.readUnsignedShort();
			}
		} else if (opcode == 2) {
			frameStep = buffer.readUnsignedShort();
		} else if (opcode == 3) {
			var3 = buffer.readUnsignedByte();
			interleaveLeave = new int[var3];
			for (var4 = 0; var4 < var3; ++var4) {
				interleaveLeave[var4] = buffer.readUnsignedByte();
			}
			//interleaveLeave[var3] = 9999999;
		} else if (opcode == 4) {
			stretches = true;
		} else if (opcode == 5) {
			forcedPriority = buffer.readUnsignedByte();
		} else if (opcode == 6) {
			leftHandItem = buffer.readUnsignedShort();
		} else if (opcode == 7) {
			rightHandItem = buffer.readUnsignedShort();
		} else if (opcode == 8) {
			maxLoops = buffer.readUnsignedByte();
		} else if (opcode == 9) {
			precedenceAnimating = buffer.readUnsignedByte();
		} else if (opcode == 10) {
			priority = buffer.readUnsignedByte();
		} else if (opcode == 11) {
			replyMode = buffer.readUnsignedByte();
		} else if (opcode == 12) {
			var3 = buffer.readUnsignedByte();
			skinFileIds = new int[var3];
			skinArchiveIds = new int[var3];
			for (var4 = 0; var4 < var3; ++var4) {
				skinFileIds[var4] = buffer.readUnsignedShort();
			}
			for (var4 = 0; var4 < var3; ++var4) {
				skinArchiveIds[var4] = buffer.readUnsignedShort();
			}
		} else if (opcode == 13) {
			var3 = buffer.readUnsignedByte();
			sounds = new int[var3];
			for (var4 = 0; var4 < var3; ++var4) {
				sounds[var4] = buffer.read24BitInt();
			}
		}
		else if (opcode == 14) {
			animMayaID = buffer.readInt();
		}
		else if (opcode == 15)
		{
			var3 = buffer.readUnsignedShort();
			animMayaFrameSounds = new HashMap<>();

			for (var4 = 0; var4 < var3; ++var4)
			{
				int var5 = buffer.readUnsignedShort();
				int var6 = buffer.read24BitInt();
				animMayaFrameSounds.put(var5, var6);
			}
		}
		else if (opcode == 16)
		{
			animMayaStart = buffer.readUnsignedShort();
			animMayaEnd = buffer.readUnsignedShort();
		}
		else if (opcode == 17)
		{
			animMayaMasks = new boolean[256];

			var3 = buffer.readUnsignedByte();

			for (var4 = 0; var4 < var3; ++var4)
			{
				animMayaMasks[buffer.readUnsignedByte()] = true;
			}
		} else {
			System.err.println("sequence : " + id + ", error decoding opcode : " + opcode + ", previous opcodes: " + Arrays.toString(previousOpcodes));
		}
		ArrayUtils.add(previousOpcodes, opcode);
	}

	@Override
	public OutputStream encode(OutputStream buffer) {
		
		if (frameLenghts != null && frameFileIds != null) {
			buffer.writeByte(1);
			int size = Math.min(frameLenghts.length, frameFileIds.length);
			buffer.writeShort(size);
			for (int index = 0; index < size; index++) {
				buffer.writeShort(frameLenghts[index]);
			}
			for (int index = 0; index < size; index++) {
				buffer.writeShort(frameFileIds[index]);
			}
			for (int index = 0; index < size; index++) {
				buffer.writeShort(frameArchiveIds[index]);
			}
		}
		
		if (frameStep > -1) {
			buffer.writeByte(2);
			buffer.writeShort(frameStep);
		}
		
		if (interleaveLeave != null) {
			buffer.writeByte(3);
			buffer.writeByte(interleaveLeave.length);
			for (int index = 0; index < interleaveLeave.length; index++) {
				buffer.writeByte(interleaveLeave[index]);
			}
		}
		
		if (stretches) {
			buffer.writeByte(4);
		}
		
		if (forcedPriority > -1) {
			buffer.writeByte(5);
			buffer.writeByte(forcedPriority);
		}
		
		if (leftHandItem > -1) {
			buffer.writeByte(6);
			buffer.writeShort(leftHandItem);
		}
		
		if (rightHandItem > -1) {
			buffer.writeByte(7);
			buffer.writeShort(rightHandItem);	
		}
		
		if (maxLoops > -1) {
			buffer.writeByte(8);
			buffer.writeByte(maxLoops);
		}
		
		if (precedenceAnimating > -1) {
			buffer.writeByte(9);
			buffer.writeByte(precedenceAnimating);
		}
		
		if (priority > -1) {
			buffer.writeByte(10);
			buffer.writeByte(priority);
		}
		
		if (replyMode > -1) {
			buffer.writeByte(11);
			buffer.writeByte(replyMode);
		}
		
		if (skinFileIds != null) {
			buffer.writeByte(12);
			int length = Math.min(skinFileIds.length, skinArchiveIds.length);
			buffer.writeByte(length);
			for (int index = 0; index < length; index++) {
				buffer.writeShort(skinFileIds[index]);
			}
			for (int index = 0; index < length; index++) {
				buffer.writeShort(skinArchiveIds[index]);
			}
		}
		
		if (sounds != null) {
			buffer.writeByte(13);
			buffer.writeByte(sounds.length);
			for (int index = 0; index < sounds.length; index++) {
				buffer.write24BitInt(sounds[index]);
			}
		}
		
		buffer.writeByte(0);
			
		return buffer;
	}

	@Override
	public String toString() {
		return "[" + id + "]";
	}
	
	public int[] frameFileIds;
	public int[] frameArchiveIds;
	public int[] skinFileIds;
	public int[] skinArchiveIds;
	public int[] frameLenghts;
	public int rightHandItem = -1;
	public int[] interleaveLeave;
	public boolean stretches = false;
	public int forcedPriority = 5;
	public int maxLoops = 99;
	public int[] sounds;
	public int precedenceAnimating = -1;
	public int leftHandItem = -1;
	public int replyMode = 2;
	public int frameStep = -1;
	public int priority = -1;

	public int animMayaID = -1;
	public Map<Integer, Integer> animMayaFrameSounds;
	public int animMayaStart;
	public int animMayaEnd;
	public boolean[] animMayaMasks;

	private static Map<Field, Integer> fieldPriorities;

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
