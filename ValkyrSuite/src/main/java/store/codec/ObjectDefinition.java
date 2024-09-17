package store.codec;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import store.CacheLibrary;
import store.io.impl.InputStream;
import store.io.impl.OutputStream;

@Getter @Setter @Slf4j
public class ObjectDefinition implements AbstractDefinition, Cloneable {

	/**
	 * @param i
	 */
	public ObjectDefinition(int i) {
		this.id = i;
	}

	@Override
	public void decode(InputStream buffer) {
		while (true) {
			int opcode = buffer.readUnsignedByte(); 
			if (opcode == 0) {
				break;
			}
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
				setWallOrDoor(buffer.readUnsignedByte());
			} else if (opcode == 21) {
				setContouredGround(0);
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
				setDecorDisplacement(buffer.readUnsignedByte());
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
				short[] recolorToFind = new short[length];
				short[] recolorToReplace = new short[length];
	
				for (int index = 0; index < length; ++index) {
					recolorToFind[index] = (short) buffer.readShort();
					recolorToReplace[index] = (short) buffer.readShort();
				}
	
				setRecolorToFind(recolorToFind);
				setRecolorToReplace(recolorToReplace);
			} else if (opcode == 41) {
				int length = buffer.readUnsignedByte();
				short[] retextureToFind = new short[length];
				short[] textureToReplace = new short[length];
	
				for (int index = 0; index < length; ++index) {
					retextureToFind[index] = (short) buffer.readShort();
					textureToReplace[index] = (short) buffer.readShort();
				}
	
				setRetextureToFind(retextureToFind);
				setTextureToReplace(textureToReplace);
			} else if (opcode == 62) {
				setRotated(true);
			} else if (opcode == 64) {
				setShadow(false);
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
				setObstructsGround(true);
			} else if (opcode == 74) {
				setSolid(true);
			} else if (opcode == 75) {
				setSupportsItems(buffer.readUnsignedByte());
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
				setAmbientSoundId(buffer.readUnsignedShort());
				setAmbientSoundDistance(buffer.readUnsignedByte());
			} else if (opcode == 79) {
				setAmbientSoundChangeTicksMin(buffer.readUnsignedShort());
				setAmbientSoundChangeTicksMax(buffer.readUnsignedShort());
				setAmbientSoundDistance(buffer.readUnsignedByte());
				int length = buffer.readUnsignedByte();
				int[] ambientSoundIds = new int[length];
	
				for (int index = 0; index < length; ++index) {
					ambientSoundIds[index] = buffer.readUnsignedShort();
				}
	
				setAmbientSoundIds(ambientSoundIds);
			} else if (opcode == 81) {
				setContouredGround(buffer.readUnsignedByte());
			} else if (opcode == 82) {
				setMapAreaId(buffer.readUnsignedShort());
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
					}
	
					else {
						value = buffer.readInt();
					}
	
					params.put(key, value);
				}
				setParams(params);
			} else {
				log.warn("Unrecognized opcode {}", opcode);
			}
		}
	}

	@Override
	public byte[] encode() {
		OutputStream buffer = new OutputStream();
		
		if (objectModels != null && objectTypes != null) {
			if (objectTypes != null) {
				buffer.writeByte(1);
				int size = Math.min(objectModels.length, objectTypes.length);
				buffer.writeByte(size);
				for (int index = 0; index < size; index++) {
					buffer.writeShort(objectModels[index]);
					buffer.writeByte(objectTypes[index]);
				}
			} else {
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
		
		buffer.writeByte(19);
		buffer.writeByte(wallOrDoor);
		
		if (decorDisplacement == 0) {
			buffer.writeByte(21);
		}
		
		if (!nonFlatShading) {
			buffer.writeByte(22);
		}
		
		if (aBool2111) {
			buffer.writeByte(23);
		}
		
		buffer.writeByte(24);
		buffer.writeShort(animationID);
			
		if (interactType > 0) {
			buffer.writeByte(27);
		}
		
		buffer.writeByte(28);
		buffer.writeByte(decorDisplacement);
		
		buffer.writeByte(29);
		buffer.writeByte(ambient);
		
		buffer.writeByte(39);
		buffer.writeByte(contrast);
		
		if (actions != null) {
			for (int action = 0; action < actions.length; action++) {
				if (actions[action] == null)
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
		
		if (!shadow) {
			buffer.writeByte(64);
		}
		
		buffer.writeByte(65);
		buffer.writeShort(modelSizeX);

		buffer.writeByte(66);
		buffer.writeShort(modelSizeHeight);
		
		buffer.writeByte(67);
		buffer.writeShort(modelSizeY);
		
		buffer.writeByte(68);
		buffer.writeShort(mapSceneID);

		buffer.writeByte(70);
		buffer.writeShort(offsetX);
		
		buffer.writeByte(71);
		buffer.writeShort(offsetHeight);
		
		buffer.writeByte(72);
		buffer.writeShort(offsetY);
		
		if (obstructsGround) {
			buffer.writeByte(73);
		}
		
		if (isSolid) {
			buffer.writeByte(74);
		}
		
		buffer.writeByte(75);
		buffer.writeByte(supportsItems);
		
		if (configChangeDest != null) {	
			int length = configChangeDest.length;
			boolean hasTransforms = length > 0;
			buffer.writeByte(hasTransforms ? 92 : 77);
			buffer.writeShort(varbitID);
			buffer.writeShort(varpID);
			if (hasTransforms) {
				buffer.writeShort(configChangeDest[length - 1]);
			}
			buffer.writeByte(length - 2);
			for (int transform = 0; transform <= (length - 1); transform++) {
				buffer.writeShort(configChangeDest[transform]);
			}
		}
		
		buffer.writeByte(78);
		buffer.writeShort(ambientSoundId);
		buffer.writeByte(ambientSoundDistance);
		
		if (ambientSoundIds != null) {
			buffer.writeByte(79);
			buffer.writeShort(ambientSoundChangeTicksMin);
			buffer.writeShort(ambientSoundChangeTicksMax);
			buffer.writeByte(ambientSoundDistance);
			buffer.writeByte(ambientSoundIds.length);
			for (int index = 0; index < ambientSoundIds.length; index++) {
				buffer.writeShort(ambientSoundIds[index]);
			}
		}

		buffer.writeByte(81);
		buffer.writeByte(contouredGround);
		
		buffer.writeByte(82);
		buffer.writeShort(mapAreaId);
	
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
		
		return buffer.flip();
	}

	@Override
	public boolean save(CacheLibrary cache) {
		System.out.println("Above");
		cache.getIndex(2).addArchive(6).addFile(id, encode());
		System.out.println("Below");
		return cache.getIndex(2).update();
	}

	@Override
	public String toString() {
		return this.id + " - " + this.name;
	}

	@Override
	public Cloneable clone() {
		return this;
	}
	
	public int id;
	public short[] retextureToFind;
	public int decorDisplacement = 16;
	public boolean isSolid = false;
	public String name = "null";
	public int[] objectModels;
	public int[] objectTypes;
	public short[] recolorToFind;
	public int mapAreaId = -1;
	public short[] textureToReplace;
	public int sizeX = 1;
	public int sizeY = 1;
	public int ambientSoundDistance = 0;
	public int[] ambientSoundIds;
	public int offsetX = 0;
	public boolean nonFlatShading = false;
	public int wallOrDoor = -1;
	public int animationID = -1;
	public int varbitID = -1;
	public int ambient = 0;
	public int contrast = 0;
	public String[] actions = new String[5];
	public int interactType = 2;
	public int mapSceneID = -1;
	public short[] recolorToReplace;
	public boolean shadow = true;
	public int modelSizeX = 128;
	public int modelSizeHeight = 128;
	public int modelSizeY = 128;
	public int objectID;
	public int offsetHeight = 0;
	public int offsetY = 0;
	public boolean obstructsGround = false;
	public int contouredGround = -1;
	public int supportsItems = -1;
	public int[] configChangeDest;
	public boolean isRotated = false;
	public int varpID = -1;
	public int ambientSoundId = -1;
	public boolean aBool2111 = false;
	public int ambientSoundChangeTicksMin = 0;
	public int ambientSoundChangeTicksMax = 0;
	public boolean blocksProjectile = true;
	public Map<Integer, Object> params = null;
	
	public void post()
	{
		if (getWallOrDoor() == -1)
		{
			setWallOrDoor(0);
			if (getObjectModels() != null && (getObjectTypes() == null || getObjectTypes()[0] == 10))
			{
				setWallOrDoor(1);
			}

			for (int var1 = 0; var1 < 5; ++var1)
			{
				if (getActions()[var1] != null)
				{
					setWallOrDoor(1);
				}
			}
		}

		if (getSupportsItems() == -1)
		{
			setSupportsItems(getInteractType() != 0 ? 1 : 0);
		}
	}

}
