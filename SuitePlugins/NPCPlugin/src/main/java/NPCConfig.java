import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Maps;
import javafx.util.Pair;
import store.io.impl.InputStream;
import store.io.impl.OutputStream;
import suite.annotation.OrderType;
import store.plugin.extension.ConfigExtensionBase;
import store.utilities.ReflectionUtils;

/**
 * @author ReverendDread
 * Sep 19, 2019
 */
public class NPCConfig extends ConfigExtensionBase {

	@Override
	public void decode(int opcode, InputStream buffer) {
		int length;
		int index;
		if (opcode == 1) {
			length = buffer.readUnsignedByte();
			models = new int[length];
			for (index = 0; index < length; ++index) {
				models[index] = buffer.readUnsignedShort();
			}
		} else if (opcode == 2) {
			name = buffer.readString();
		} else if (opcode == 12) {
			tileSpacesOccupied = buffer.readUnsignedByte();
		} else if (opcode == 13) {
			stanceAnimation = buffer.readUnsignedShort();
		} else if (opcode == 14) {
			walkAnimation = buffer.readUnsignedShort();
		} else if (opcode == 15) {
			turnLeftSequence = buffer.readUnsignedShort();
		} else if (opcode == 16) {
			turnRightSequence = buffer.readUnsignedShort();
		} else if (opcode == 17) {
			walkAnimation = buffer.readUnsignedShort();
			rotate180Animation = buffer.readUnsignedShort();
			rotate90RightAnimation = buffer.readUnsignedShort();
			rotate90LeftAnimation = buffer.readUnsignedShort();
		} else if (opcode == 18) {
			buffer.readUnsignedShort(); // Not used yet
		} else if (opcode >= 30 && opcode < 35) {
			options[opcode - 30] = buffer.readString();
			if (options[opcode - 30].equalsIgnoreCase("Hidden")) {
				options[opcode - 30] = null;
			}
		} else if (opcode == 40) {
			length = buffer.readUnsignedByte();
			recolorToFind = new int[length];
			recolorToReplace = new int[length];
			for (index = 0; index < length; ++index) {
				recolorToFind[index] = buffer.readUnsignedShort();
				recolorToReplace[index] = buffer.readUnsignedShort();
			}
		} else if (opcode == 41) {
			length = buffer.readUnsignedByte();
			retextureToFind = new int[length];
			retextureToReplace = new int[length];
			for (index = 0; index < length; ++index) {
				retextureToFind[index] = buffer.readUnsignedShort();
				retextureToReplace[index] = buffer.readUnsignedShort();
			}
		} else if (opcode == 60) {
			length = buffer.readUnsignedByte();
			models_2 = new int[length];
			for (index = 0; index < length; ++index) {
				models_2[index] = buffer.readUnsignedShort();
			}
		} else if (opcode == 93) {
			renderOnMinimap = false;
		} else if (opcode == 95) {
			combatLevel = buffer.readUnsignedShort();
		} else if (opcode == 97) {
			resizeX = buffer.readUnsignedShort();
		} else if (opcode == 98) {
			resizeY = buffer.readUnsignedShort();
		} else if (opcode == 99) {
			hasRenderPriority = true;
		} else if (opcode == 100) {
			ambient = buffer.readByte();
		} else if (opcode == 101) {
			contrast = buffer.readByte();
		} else if (opcode == 102) {
			headIcon = buffer.readUnsignedShort();
		} else if (opcode == 103) {
			rotation = buffer.readUnsignedShort();
		} else if (opcode == 106) {
			varbitIndex = buffer.readUnsignedShort();
			if (65535 == varbitIndex) {
				varbitIndex = -1;
			}
			varpIndex = buffer.readUnsignedShort();
			if (65535 == varpIndex) {
				varpIndex = -1;
			}
			length = buffer.readUnsignedByte();
			configs = new int[length + 2];
			for (index = 0; index <= length; ++index) {
				configs[index] = buffer.readUnsignedShort();
				if (configs[index] == 65535) {
					configs[index] = -1;
				}
			}
			configs[length + 1] = -1;
		} else if (opcode == 107) {
			isClickable = false;
		} else if (opcode == 109) {
			aBool2170 = false;
		} else if (opcode == 111) {
			aBool2190 = true;
		} else if (opcode == 114) {
			runAnimation = buffer.readUnsignedShort();
		} else if (opcode == 115) {
			runAnimation = buffer.readUnsignedShort();
			runRotate180Animation = buffer.readUnsignedShort();
			runRotateLeftAnimation = buffer.readUnsignedShort();
			runRotateRightAnimation = buffer.readUnsignedShort();
		} else if (opcode == 116) {
			crawlAnimation = buffer.readUnsignedShort();
		} else if (opcode == 117) {
			crawlAnimation = buffer.readUnsignedShort();
			crawlRotate180Animation = buffer.readUnsignedShort();
			crawlRotateLeftAnimation = buffer.readUnsignedShort();
			crawlRotateRightAnimation = buffer.readUnsignedShort();
		} else if (opcode == 118) {
			varbitIndex = buffer.readUnsignedShort();
			if (varbitIndex == 65535) {
				varbitIndex = -1;
			}
			varpIndex = buffer.readUnsignedShort();
			if (varpIndex == 65535) {
				varpIndex = -1;
			}
			int var = buffer.readUnsignedShort();
			if (var == 65535) {
				var = -1;
			}
			length = buffer.readUnsignedByte();
			configs = new int[length + 2];
			for (index = 0; index <= length; ++index) {
				configs[index] = buffer.readUnsignedShort();
				if (configs[index] == 65535) {
					configs[index] = -1;
				}
			}
			configs[length + 1] = var;
		} else if (opcode == 249) {
			length = buffer.readUnsignedByte();
			params = new HashMap<>(length);
			for (int i = 0; i < length; i++) {
				boolean isString = buffer.readUnsignedByte() == 1;
				int key = buffer.read24BitInt();
				Object value;
				if (isString) {
					value = buffer.readString();
				}
				else {
					value = buffer.readInt();
				}
				params.put(key, value);
			}
		}
	}

	@Override
	public OutputStream encode(OutputStream buffer) {
		
		if (models != null) {
			buffer.writeByte(1);
			buffer.writeByte(models.length);
			for (int index = 0; index < models.length; index++) {
				buffer.writeShort(models[index]);
			}
		}
		
		if (!name.equals("null")) {
			buffer.writeByte(2);
			buffer.writeString(name);
		}
		
		if (tileSpacesOccupied > 1) {
			buffer.writeByte(12);
			buffer.writeByte(tileSpacesOccupied);
		}
		
		if (stanceAnimation > -1) {
			buffer.writeByte(13);
			buffer.writeShort(stanceAnimation);
		}
		
		if (walkAnimation > -1) {
			buffer.writeByte(14);
			buffer.writeShort(walkAnimation);
		}
		
		if (turnLeftSequence > -1) {
			buffer.writeByte(15);
			buffer.writeShort(turnLeftSequence);
		}
		
		if (turnRightSequence > -1) {
			buffer.writeByte(16);
			buffer.writeShort(turnRightSequence);
		}
		
		if (walkAnimation > -1 && rotate180Animation > -1 && rotate90LeftAnimation > -1 && rotate90RightAnimation > -1) {
			buffer.writeByte(17);
			buffer.writeShort(walkAnimation);
			buffer.writeShort(rotate180Animation);
			buffer.writeShort(rotate90RightAnimation);
			buffer.writeShort(rotate90LeftAnimation);
		}
		
		if (options != null) {
			for (int index = 0; index < options.length; index++) {
				if(options[index] == null || options[index].isEmpty())
					continue;
				buffer.writeByte(index + 30);
				buffer.writeString(options[index]);
			}
		}
		
		if (recolorToFind != null && recolorToReplace != null) {
			buffer.writeByte(40);
			int length = Math.min(recolorToFind.length, recolorToReplace.length);
			buffer.writeByte(length);
			for (int index = 0; index < length; index++) {
				buffer.writeShort(recolorToFind[index]);
				buffer.writeShort(recolorToReplace[index]);
			}
		}
		
		if (retextureToFind != null && retextureToReplace != null) {
			buffer.writeByte(41);
			int length = Math.min(retextureToFind.length, retextureToReplace.length);
			buffer.writeByte(length);
			for (int index = 0; index < length; index++) {
				buffer.writeShort(retextureToFind[index]);
				buffer.writeShort(retextureToReplace[index]);
			}
		}
		
		if (models_2 != null) {
			buffer.writeByte(60);
			buffer.writeByte(models_2.length);
			for (int index = 0; index < models_2.length; index++) {
				buffer.writeShort(models_2[index]);
			}
		}
		
		if (!renderOnMinimap) {
			buffer.writeByte(93);
		}
		
		if (combatLevel > -1) {
			buffer.writeByte(95);
			buffer.writeShort(combatLevel);
		}
		
		if (resizeX != 128) {
			buffer.writeByte(97);
			buffer.writeShort(resizeX);
		}
		
		if (resizeY != 128) {
			buffer.writeByte(98);
			buffer.writeShort(resizeY);
		}
		
		if (hasRenderPriority) {
			buffer.writeByte(99);
		}
		
		if (ambient > 0) {
			buffer.writeByte(100);
			buffer.writeByte(ambient);
		}
		
		if (contrast > 0) {
			buffer.writeByte(101);
			buffer.writeByte(contrast);
		}
		
		if (headIcon > -1) {
			buffer.writeByte(102);
			buffer.writeShort(headIcon);
		}
		
		if (rotation != 32) {
			buffer.writeByte(103);
			buffer.writeShort(rotation);
		}
		
		if ((varbitIndex != -1 && varpIndex != -1) && (configs != null && configs.length > 0)) {	
			int length = configs.length;
			boolean hasTransforms = configs[configs.length - 1] != -1;
			buffer.writeByte(hasTransforms ? 118 : 106);
			buffer.writeShort(varbitIndex);
			buffer.writeShort(varpIndex);
			if (hasTransforms) {
				buffer.writeShort(configs[length - 1]);
			}
			buffer.writeByte(length - 2);
			for (int transform = 0; transform <= (length - 2); transform++) {
				buffer.writeShort(configs[transform]);
			}
		}
		
		if (!isClickable) {
			buffer.writeByte(107);
		}
		
		if (!aBool2170) {
			buffer.writeByte(109);
		}
		
		if (aBool2190) {
			buffer.writeByte(111);
		}
		
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
		
		buffer.writeByte(0);
		
		return buffer;
	}

	@Override
	public String toString() {
		return "[" + this.id + "] " + this.name;
	}

	@OrderType(priority = 1)
	public String name = "null";
	@OrderType(priority = 6)
	public int[] recolorToFind;
	public int rotation = 32;
	@OrderType(priority = 7)
	public int[] recolorToReplace;
	public int[] models;
	public int[] models_2;
	public int stanceAnimation = -1;
	public int turnLeftSequence = -1;
	public int tileSpacesOccupied = 1;
	public int walkAnimation = -1;
	@OrderType(priority = 8)
	public int[] retextureToReplace;
	public int rotate90RightAnimation = -1;
	public boolean aBool2170 = true;
	public int resizeX = 128;
	public int contrast = 0;
	public int rotate180Animation = -1;
	public int varbitIndex = -1;
	@OrderType(priority = 3)
	public String[] options = new String[5];
	@OrderType(priority = 5)
	public boolean renderOnMinimap = true;
	@OrderType(priority = 2)
	public int combatLevel = -1;
	public int rotate90LeftAnimation = -1;
	public int resizeY = 128;
	public boolean hasRenderPriority = false;
	public int ambient = 0;
	public int headIcon = -1;
	public int[] configs;
	@OrderType(priority = 7)
	public int[] retextureToFind;
	public int varpIndex = -1;
	@OrderType(priority = 4)
	public boolean isClickable = true;
	public int turnRightSequence = -1;
	public boolean aBool2190 = false;
	public int runAnimation = -1;
	public int runRotate180Animation = -1;
	public int runRotateLeftAnimation = -1;
	public int runRotateRightAnimation = -1;
	public int crawlAnimation = -1;
	public int crawlRotate180Animation = -1;
	public int crawlRotateLeftAnimation = -1;
	public int crawlRotateRightAnimation = -1;
	public Map<Integer, Object> params = null;

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
