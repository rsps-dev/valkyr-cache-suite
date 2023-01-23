import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Maps;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import store.io.impl.InputStream;
import store.io.impl.OutputStream;
import suite.annotation.OrderType;
import store.plugin.extension.ConfigExtensionBase;
import store.utilities.ReflectionUtils;

/**
 * 
 */

/**
 * @author ReverendDread
 * Sep 22, 2019
 */
@Setter @Getter @Slf4j
public class ObjectConfig extends ConfigExtensionBase {

	@Override
	public void decode(int opcode, InputStream buffer) {
		try {
			if (opcode == 1) {
				int length = buffer.readUnsignedByte();
				if (length > 0) {
					int[] objectTypes = new int[length];
					int[] objectModels = new int[length];
					for (int index = 0; index < length; ++index) {
						objectModels[index] = buffer.readUnsignedShort();
						objectTypes[index] = buffer.readUnsignedByte();
					}
					setObjectTypes(objectTypes);
					setObjectModels(objectModels);
				}
			} else if (opcode == 2) {
				setName(buffer.readString());
			} else if (opcode == 5) {
				int length = buffer.readUnsignedByte();
				if (length > 0) {
					setObjectTypes(null);
					int[] objectModels = new int[length];
					for (int index = 0; index < length; ++index) {
						objectModels[index] = buffer.readUnsignedShort();
					}
					setObjectModels(objectModels);
				}
			} else if (opcode == 14) {
				setSizeX(buffer.readUnsignedByte());
			} else if (opcode == 15) {
				setSizeY(buffer.readUnsignedByte());
			} else if (opcode == 17) {
				setInteractType(0);
				setBlocksProjectile(false);
			} else if (opcode == 18) {
				setBlocksProjectile(false);
			} else if (opcode == 19) {
				setAnInt2088(buffer.readUnsignedByte());
			} else if (opcode == 21) {
				setAnInt2105(0);
			} else if (opcode == 22) {
				setNonFlatShading(false);
			} else if (opcode == 23) {
				setABool2111(true);
			} else if (opcode == 24) {
				setAnimationID(buffer.readUnsignedShort());
				if (getAnimationID() == 0xFFFF) {
					setAnimationID(-1);
				}
			} else if (opcode == 27) {
				setInteractType(1);
			} else if (opcode == 28) {
				setAnInt2069(buffer.readUnsignedByte());
			} else if (opcode == 29) {
				setAmbient(buffer.readByte());
			} else if (opcode == 39) {
				setContrast(buffer.readByte());
			} else if (opcode >= 30 && opcode < 35) {
				String[] actions = getActions();
				actions[opcode - 30] = buffer.readString();
				if (actions[opcode - 30].equalsIgnoreCase("Hidden")) {
					actions[opcode - 30] = null;
				}
			} else if (opcode == 40) {
				int length = buffer.readUnsignedByte();
				int[] recolorToFind = new int[length];
				int[] recolorToReplace = new int[length];
				for (int index = 0; index < length; ++index) {
					recolorToFind[index] = buffer.readUnsignedShort();
					recolorToReplace[index] = buffer.readUnsignedShort();
				}
				setRecolorToFind(recolorToFind);
				setRecolorToReplace(recolorToReplace);
			} else if (opcode == 41) {
				int length = buffer.readUnsignedByte();
				int[] retextureToFind = new int[length];
				int[] textureToReplace = new int[length];
				for (int index = 0; index < length; ++index) {
					retextureToFind[index] = buffer.readUnsignedShort();
					textureToReplace[index] = buffer.readUnsignedShort();
				}
				setRetextureToFind(retextureToFind);
				setTextureToReplace(textureToReplace);
			} else if (opcode == 61) {
					buffer.readUnsignedShort();
			} else if (opcode == 62) {
				setRotated(true);
			} else if (opcode == 64) {
				setABool2097(false);
			} else if (opcode == 65) {
				setModelSizeX(buffer.readUnsignedShort());
			} else if (opcode == 66) {
				setModelSizeHeight(buffer.readUnsignedShort());
			} else if (opcode == 67) {
				setModelSizeY(buffer.readUnsignedShort());
			} else if (opcode == 68) {
				setMapSceneID(buffer.readUnsignedShort());
			} else if (opcode == 69) {
				buffer.readByte();
			} else if (opcode == 70) {
				setOffsetX(buffer.readShort());
			} else if (opcode == 71) {
				setOffsetHeight(buffer.readShort());
			} else if (opcode == 72) {
				setOffsetY(buffer.readShort());
			} else if (opcode == 73) {
				setABool2104(true);
			} else if (opcode == 74) {
				setSolid(true);
			} else if (opcode == 75) {
				setAnInt2106(buffer.readUnsignedByte());
			} else if (opcode == 77) {
				int varpID = buffer.readUnsignedShort();
				if (varpID == 0xFFFF) {
					varpID = -1;
				}
				setVarbitID(varpID);

				int configId = buffer.readUnsignedShort();
				if (configId == 0xFFFF) {
					configId = -1;
				}
				setVarpID(configId);

				int length = buffer.readUnsignedByte();
				int[] configChangeDest = new int[length + 2];

				for (int index = 0; index <= length; ++index) {
					configChangeDest[index] = buffer.readUnsignedShort();
					if (0xFFFF == configChangeDest[index]) {
						configChangeDest[index] = -1;
					}
				}

				configChangeDest[length + 1] = -1;

				setConfigChangeDest(configChangeDest);
			} else if (opcode == 78) {
				setAnInt2110(buffer.readUnsignedShort());
				setAnInt2083(buffer.readUnsignedByte());
				if (anInt2083 == 255) {
					anInt2083 = -1;
				}
			} else if (opcode == 79) {
				setAnInt2112(buffer.readUnsignedShort());
				setAnInt2113(buffer.readUnsignedShort());
				setAnInt2083(buffer.readUnsignedByte());
				if (anInt2083 == 255) {
					anInt2083 = -1;
				}
				int length = buffer.readUnsignedByte();
				int[] anIntArray2084 = new int[length];

				for (int index = 0; index < length; ++index) {
					anIntArray2084[index] = buffer.readUnsignedShort();
				}

				setAnIntArray2084(anIntArray2084);
			} else if (opcode == 81) {
				setAnInt2105(buffer.readUnsignedByte());
			} else if (opcode == 82) {
				setMapAreaId(buffer.readUnsignedShort());
			} else if (opcode == 89) {
			} else if (opcode == 92) {
				int varpID = buffer.readUnsignedShort();
				if (varpID == 0xFFFF) {
					varpID = -1;
				}
				setVarbitID(varpID);

				int configId = buffer.readUnsignedShort();
				if (configId == 0xFFFF) {
					configId = -1;
				}
				setVarpID(configId);

				int var = buffer.readUnsignedShort();
				if (var == 0xFFFF) {
					var = -1;
				}

				int length = buffer.readUnsignedByte();
				int[] configChangeDest = new int[length + 2];

				for (int index = 0; index <= length; ++index) {
					configChangeDest[index] = buffer.readUnsignedShort();
					if (0xFFFF == configChangeDest[index]) {
						configChangeDest[index] = -1;
					}
				}
				configChangeDest[length + 1] = var;
				setConfigChangeDest(configChangeDest);
			} else if (opcode == 249) {
				int length = buffer.readUnsignedByte();

				Map<Integer, Object> params = new HashMap<>(length);
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
				setParams(params);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public OutputStream encode(OutputStream buffer) {
		
		if (objectTypes != null && objectTypes.length > 0) {
			buffer.writeByte(1);
			int size = Math.min(objectModels.length, objectTypes.length);
			buffer.writeByte(size);
			for (int index = 0; index < size; index++) {
				buffer.writeShort(objectModels[index]);
				buffer.writeByte(objectTypes[index]);
			}
		} else {
			if (objectModels != null) {
				buffer.writeByte(5);
				buffer.writeByte(objectModels.length);
				for (int index = 0; index < objectModels.length; index++) {
					buffer.writeShort(objectModels[index]);
				}
			}
		}
		
		if (!name.equalsIgnoreCase("null")) {
			buffer.writeByte(2);
			buffer.writeString(name);
		}
		
		buffer.writeByte(14);
		buffer.writeByte(sizeX);
		
		buffer.writeByte(15);
		buffer.writeByte(sizeY);
		
		if (interactType == 0 && !blocksProjectile) {
			buffer.writeByte(17);
		}
		
		if (!blocksProjectile) {
			buffer.writeByte(18);
		}
		
		if (anInt2088 > -1) {
			buffer.writeByte(19);
			buffer.writeByte(anInt2088);
		}
		
		if (anInt2069 == 0) {
			buffer.writeByte(21);
		}
		
		if (!nonFlatShading) {
			buffer.writeByte(22);
		}
		
		if (aBool2111) {
			buffer.writeByte(23);
		}
		
		if (animationID > -1) {
			buffer.writeByte(24);
			buffer.writeShort(animationID);
		}	
		
		if (interactType > 0) {
			buffer.writeByte(27);
		}
		
		buffer.writeByte(28);
		buffer.writeByte(anInt2069);
		
		buffer.writeByte(29);
		buffer.writeByte(ambient);
		
		buffer.writeByte(39);
		buffer.writeByte(contrast);
		
		if (actions != null) {
			for (int action = 0; action < actions.length; action++) {
				if (actions[action] == null || actions[action].isEmpty())
					continue;
				buffer.writeByte(action + 30);
				buffer.writeString(actions[action]);
			}
		}
		
		if (recolorToFind != null && recolorToReplace != null) {
			buffer.writeByte(40);
			int size = Math.min(recolorToFind.length, recolorToReplace.length);
			buffer.writeByte(size);
			for (int index = 0; index < size; index++) {
				buffer.writeShort(recolorToFind[index]);
				buffer.writeShort(recolorToReplace[index]);
			}
		}
		
		if (retextureToFind != null && textureToReplace != null) {
			buffer.writeByte(41);
			int size = Math.min(retextureToFind.length, textureToReplace.length);
			buffer.writeByte(size);
			for (int index = 0; index < size; index++) {
				buffer.writeShort(retextureToFind[index]);
				buffer.writeShort(textureToReplace[index]);
			}
		}

		if (isRotated) {
			buffer.writeByte(62);
		}
		
		if (!aBool2097) {
			buffer.writeByte(64);
		}
		
		buffer.writeByte(65);
		buffer.writeShort(modelSizeX);

		buffer.writeByte(66);
		buffer.writeShort(modelSizeHeight);
		
		buffer.writeByte(67);
		buffer.writeShort(modelSizeY);
		
		if (mapSceneID > -1) {
			buffer.writeByte(68);
			buffer.writeShort(mapSceneID);
		}

		buffer.writeByte(70);
		buffer.writeShort(offsetX);
		
		buffer.writeByte(71);
		buffer.writeShort(offsetHeight);
		
		buffer.writeByte(72);
		buffer.writeShort(offsetY);
		
		if (aBool2104) {
			buffer.writeByte(73);
		}
		
		if (isSolid) {
			buffer.writeByte(74);
		}
		
		if (anInt2106 > -1) {
			buffer.writeByte(75);
			buffer.writeByte(anInt2106);
		}
		
		if ((varbitID != -1 && varpID != -1) && (configChangeDest != null && configChangeDest.length > 0)) {	
			int length = configChangeDest.length;
			boolean hasTransforms = configChangeDest[configChangeDest.length - 1] != -1;
			buffer.writeByte(hasTransforms ? 92 : 77);
			buffer.writeShort(varbitID);
			buffer.writeShort(varpID);
			if (hasTransforms) {
				buffer.writeShort(configChangeDest[length - 1]);
			}
			buffer.writeByte(length - 2);
			for (int transform = 0; transform <= (length - 2); transform++) {
				buffer.writeShort(configChangeDest[transform]);
			}
		}
		
		if (anInt2110 > -1 && anInt2083 > 0) {
			buffer.writeByte(78);
			buffer.writeShort(anInt2110);
			buffer.writeByte(anInt2083);
		}
		
		if (anIntArray2084 != null && anInt2083 > 0) {
			buffer.writeByte(79);
			buffer.writeShort(anInt2112);
			buffer.writeShort(anInt2113);
			buffer.writeByte(anInt2083);
			buffer.writeByte(anIntArray2084.length);
			for (int index = 0; index < anIntArray2084.length; index++) {
				buffer.writeShort(anIntArray2084[index]);
			}
		}

		if (anInt2105 > -1) {
			buffer.writeByte(81);
			buffer.writeByte(anInt2105);
		}
		
		if (mapAreaId > -1) {
			buffer.writeByte(82);
			buffer.writeShort(mapAreaId);
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
	public void onDecodeFinish() {
		if (getAnInt2088() == -1) {
			setAnInt2088(0);
			if (getObjectModels() != null && (getObjectTypes() == null || getObjectTypes()[0] == 10)) {
				setAnInt2088(1);
			}
			for (int var1 = 0; var1 < 5; ++var1) {
				if (getActions()[var1] != null) {
					setAnInt2088(1);
				}
			}
		}
		if (getAnInt2106() == -1) {
			setAnInt2106(getInteractType() != 0 ? 1 : 0);
		}
	}

	@Override
	public String toString() {
		return "[" + this.id + "] " + this.name;
	}
	
	@OrderType(priority = 7)
	public int[] retextureToFind;
	@OrderType(priority = 100)
	public int anInt2069 = 16;
	@OrderType(priority = 21)
	public boolean isSolid = false;
	@OrderType(priority = 1)
	public String name = "null";
	@OrderType(priority = 2)
	public int[] objectModels;
	@OrderType(priority = 3)
	public int[] objectTypes;
	@OrderType(priority = 5)
	public int[] recolorToFind;
	@OrderType(priority = 28)
	public int mapAreaId = -1;
	@OrderType(priority = 8)
	public int[] textureToReplace;
	@OrderType(priority = 9)
	public int sizeX = 1;
	@OrderType(priority = 10)
	public int sizeY = 1;
	@OrderType(priority = 100)
	public int anInt2083 = 0;
	@OrderType(priority = 100)
	public int[] anIntArray2084;
	@OrderType(priority = 14)
	public int offsetX = 0;
	@OrderType(priority = 18)
	public boolean nonFlatShading = false;
	@OrderType(priority = 100)
	public int anInt2088 = -1;
	@OrderType(priority = 100)
	public int animationID = -1;
	@OrderType(priority = 24)
	public int varbitID = -1;
	@OrderType(priority = 19)
	public int ambient = 0;
	@OrderType(priority = 20)
	public int contrast = 0;
	@OrderType(priority = 4)
	public String[] actions = new String[5];
	@OrderType(priority = 26)
	public int interactType = 2;
	@OrderType(priority = 27)
	public int mapSceneID = -1;
	@OrderType(priority = 6)
	public int[] recolorToReplace;
	@OrderType(priority = 100)
	public boolean aBool2097 = true;
	@OrderType(priority = 11)
	public int modelSizeX = 128;
	@OrderType(priority = 13)
	public int modelSizeHeight = 128;
	@OrderType(priority = 12)
	public int modelSizeY = 128;
	@OrderType(priority = 16)
	public int offsetHeight = 0;
	@OrderType(priority = 15)
	public int offsetY = 0;
	@OrderType(priority = 100)
	public boolean aBool2104 = false;
	@OrderType(priority = 100)
	public int anInt2105 = -1;
	@OrderType(priority = 100)
	public int anInt2106 = -1;
	@OrderType(priority = 25)
	public int[] configChangeDest;
	@OrderType(priority = 17)
	public boolean isRotated = false;
	@OrderType(priority = 23)
	public int varpID = -1;
	@OrderType(priority = 100)
	public int anInt2110 = -1;
	@OrderType(priority = 100)
	public boolean aBool2111 = false;
	@OrderType(priority = 100)
	public int anInt2112 = 0;
	@OrderType(priority = 100)
	public int anInt2113 = 0;
	@OrderType(priority = 22)
	public boolean blocksProjectile = true;
	@OrderType(priority = 101)
	public Map<Integer, Object> params = null;

	public boolean setRandomizeAnimStart;

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
