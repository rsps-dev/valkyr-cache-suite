package store.codec.model;

import com.google.common.collect.Maps;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import misc.EffectiveVertex;
import misc.FaceBillboard;
import store.CacheLibrary;
import store.io.Stream;
import store.io.impl.InputStream;
import store.io.impl.OutputStream;
import store.plugin.extension.ConfigExtensionBase;
import store.utilities.ReflectionUtils;
import suite.annotation.OrderType;
import suite.opengl.texture.Texture;
import suite.opengl.util.ArrayUtilities;
import suite.opengl.util.ColorUtilities;
import suite.opengl.util.MathUtilities;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter @Setter
public class Mesh extends ConfigExtensionBase {

	public static final int CENTROID_TRANSFORMATION = 0;
	public static final int POSITION_TRANSFORMATION = 1;
	public static final int ROTATION_TRANSFORMATION = 2;
	public static final int SCALE_TRANSFORMATION = 3;
	public static final int ALPHA_TRANSFORMATION = 5;

	public static final int TRANSFORM_X = 0b1;
	public static final int TRANSFORM_Y = 0b10;
	public static final int TRANSFORM_Z = 0b100;

	/**
	 * The default mesh version.
	 */
	protected static final int DEFAULT_VERSION = 12;

	public int id;

	public int[][] vertexGroups;
	public int[][] faceGroups;

	public int version = 12;
	public int numVertices = 0;
	public int maxIndex = 0;
	public int numFaces = 0;
	public byte priority = 0;
	public int numTextures = 0;
	public byte[] faceMappings;
	public int textureUVCoordCount;
	public int[] verticesX;
	public int[] verticesY;
	public int[] verticesZ;
	public short[] faceIndicesA;
	public short[] faceIndicesB;
	public short[] faceIndicesC;
	public int[] vertexSkins;
	public byte[] faceTypes;
	public byte[] facePriorities;
	public byte[] faceAlphas;
	public int[] faceSkins;
	public short[] faceMaterials;
	public short[] faceColors;
	public short[] faceTextures;
	public short[] textureMappingP;
	public short[] textureMappingM;
	public short[] textureMappingN;
	public int[] textureScaleX;
	public int[] textureScaleY;
	public int[] textureScaleZ;
	public byte[] textureRotation;
	public byte[] textureDirection;
	public int[] textureSpeed;
	public int[] textureTransU;
	public int[] textureTransV;
	public int[] vertexUVOffset;
	public byte[] uvCoordVertexA;
	public byte[] uvCoordVertexB;
	public byte[] uvCoordVertexC;
	public float[] textureCoordU;
	public float[] textureCoordV;
	public EmissiveTriangle[] emitters;
	public EffectiveVertex[] effectors;
	public FaceBillboard[] billboards;
	private Texture[] textures;
	private float[][] texturedUCoordinates;
	private float[][] texturedVCoordinates;


	public Mesh() {

	}

	/**
	 * Constructs a new {@code ModelDefinition} {@code Object}.
	 * @param id The model id.
	 */
	public Mesh(int id) {
		this.id = id;
		decode(CacheLibrary.get());
	}

	public Mesh(Mesh mesh) {
		this.id = mesh.id;
		this.vertexGroups = ArrayUtilities.copyOf(mesh.vertexGroups);
		this.faceGroups = ArrayUtilities.copyOf(mesh.faceGroups);
		this.version = mesh.version;
		this.numVertices = mesh.numVertices;
		this.maxIndex = mesh.maxIndex;
		this.numFaces = mesh.numFaces;
		this.priority = mesh.priority;
		this.numTextures = mesh.numTextures;
		this.faceMappings = ArrayUtilities.copyOf(mesh.faceMappings);
		this.textureUVCoordCount = mesh.textureUVCoordCount;
		this.verticesX = ArrayUtilities.copyOf(mesh.verticesX);
		this.verticesY = ArrayUtilities.copyOf(mesh.verticesY);
		this.verticesZ = ArrayUtilities.copyOf(mesh.verticesZ);
		this.faceIndicesA = ArrayUtilities.copyOf(mesh.faceIndicesA);
		this.faceIndicesB = ArrayUtilities.copyOf(mesh.faceIndicesB);
		this.faceIndicesC = ArrayUtilities.copyOf(mesh.faceIndicesC);
		this.vertexSkins = ArrayUtilities.copyOf(mesh.vertexSkins);
		this.faceTypes = ArrayUtilities.copyOf(mesh.faceTypes);
		this.facePriorities = ArrayUtilities.copyOf(mesh.facePriorities);
		this.faceAlphas = ArrayUtilities.copyOf(mesh.faceAlphas);
		this.faceSkins = ArrayUtilities.copyOf(mesh.faceSkins);
		this.faceMaterials = ArrayUtilities.copyOf(mesh.faceMaterials);
		this.faceColors = ArrayUtilities.copyOf(mesh.faceColors);
		this.faceTextures = ArrayUtilities.copyOf(mesh.faceTextures);
		this.textureMappingP = ArrayUtilities.copyOf(mesh.textureMappingP);
		this.textureMappingM = ArrayUtilities.copyOf(mesh.textureMappingM);
		this.textureMappingN = ArrayUtilities.copyOf(mesh.textureMappingN);
		this.textureScaleX = ArrayUtilities.copyOf(mesh.textureScaleX);
		this.textureScaleY = ArrayUtilities.copyOf(mesh.textureScaleY);
		this.textureScaleZ = ArrayUtilities.copyOf(mesh.textureScaleZ);
		this.textureRotation = ArrayUtilities.copyOf(mesh.textureRotation);
		this.textureDirection = ArrayUtilities.copyOf(mesh.textureDirection);
		this.textureSpeed = ArrayUtilities.copyOf(mesh.textureSpeed);
		this.textureTransU = ArrayUtilities.copyOf(mesh.textureTransU);
		this.textureTransV = ArrayUtilities.copyOf(mesh.textureTransV);
		this.vertexUVOffset = ArrayUtilities.copyOf(mesh.vertexUVOffset);
		this.uvCoordVertexA = ArrayUtilities.copyOf(mesh.uvCoordVertexA);
		this.uvCoordVertexB = ArrayUtilities.copyOf(mesh.uvCoordVertexB);
		this.uvCoordVertexC = ArrayUtilities.copyOf(mesh.uvCoordVertexC);
		this.textureCoordU = ArrayUtilities.copyOf(mesh.textureCoordU);
		this.textureCoordV = ArrayUtilities.copyOf(mesh.textureCoordV);
		//this.emitters = mesh.emitters;
		//this.effectors = mesh.effectors;
		//this.billboards = mesh.billboards;
	}

	public Mesh(Mesh... meshs) {
		if (meshs.length <= 0) {
			return;
		}
		this.id = meshs[0].getId();
		boolean shareTypes = false;
		boolean sharePriorities = false;
		boolean shareAlpha = false;
		boolean shareColours = false;
		boolean shareTextures = false;
		boolean shareTextureCoords = false;
		boolean shareVertexSkins = false;
		for (Mesh mesh : meshs) {
			if (mesh == null || mesh.getId() == -1)
				continue;
			this.numVertices += mesh.numVertices;
			this.numFaces += mesh.numFaces;
			this.numTextures += mesh.numTextures;
			shareTypes |= mesh.faceTypes != null;
			if (mesh.facePriorities != null) {
				sharePriorities = true;
			} else {
				if (priority == -1)
					priority = mesh.priority;
				if (priority != mesh.priority)
					sharePriorities = true;
			}
			shareAlpha |= mesh.faceAlphas != null;
			shareColours |= mesh.faceColors != null;
			shareTextureCoords |= mesh.textureMappingP != null;
			shareVertexSkins |= mesh.vertexSkins != null;
		}
		verticesX = new int[numVertices];
		verticesY = new int[numVertices];
		verticesZ = new int[numVertices];
		faceIndicesA = new short[numFaces];
		faceIndicesB = new short[numFaces];
		faceIndicesC = new short[numFaces];
		textureMappingP = new short[numTextures];
		textureMappingM = new short[numTextures];
		textureMappingN = new short[numTextures];
		if (shareTypes)
			faceTypes = new byte[numFaces];
		if (sharePriorities)
			facePriorities = new byte[numFaces];
		if (shareAlpha)
			faceAlphas = new byte[numFaces];
		if (shareColours)
			faceColors = new short[numFaces];
		if (shareTextures)
			faceMaterials = new short[numFaces];
		if (shareTextureCoords)
			faceTextures = new short[numFaces];
		if (shareVertexSkins)
			vertexSkins = new int[numVertices];
		numVertices = 0;
		numFaces = 0;
		numTextures = 0;
		int textureIndex = 0;
		for (Mesh mesh : meshs) {
			if (mesh == null || mesh.getId() == -1)
				continue;
			int vertices = numVertices;
			for (int vertex = 0; vertex < mesh.numVertices; vertex++) {
				int x = mesh.verticesX[vertex];
				int y = mesh.verticesY[vertex];
				int z = mesh.verticesZ[vertex];
				verticesX[numVertices] = x;
				verticesY[numVertices] = y;
				verticesZ[numVertices] = z;
				if(mesh.vertexSkins != null) {
					vertexSkins[numVertices] = mesh.vertexSkins[vertex];
				}
				numVertices++;
			}
			for (int face = 0; face < mesh.numFaces; face++) {
				faceIndicesA[numFaces] = (short) (mesh.faceIndicesA[face] + vertices);
				faceIndicesB[numFaces] = (short) (mesh.faceIndicesB[face] + vertices);
				faceIndicesC[numFaces] = (short) (mesh.faceIndicesC[face] + vertices);
				if (shareTypes) {
					if (mesh.faceTypes == null) {
						faceTypes[numFaces] = 0;
					} else {
						byte type = mesh.faceTypes[face];
						if ((type & 2) == 2)
							type += textureIndex << 2;
						faceTypes[numFaces] = type;
					}
				}
				if (sharePriorities) {
					if (mesh.facePriorities == null)
						facePriorities[numFaces] = mesh.priority;
					else
						facePriorities[numFaces] = mesh.facePriorities[face];
				}
				if (shareAlpha) {
					if (mesh.faceAlphas == null)
						faceAlphas[numFaces] = 0;
					else
						faceAlphas[numFaces] = mesh.faceAlphas[face];
				}
				if (shareColours) {
					faceColors[numFaces] = mesh.faceColors[face];
				}
				if (shareTextures) {
					if (mesh.faceMaterials != null) {
						faceMaterials[numFaces] = mesh.faceMaterials[numFaces];
					} else {
						faceMaterials[numFaces] = -1;
					}
				}
				if (shareTextureCoords) {
					if (mesh.faceTextures != null && mesh.faceTextures[numFaces] != -1)
						mesh.faceTextures[numFaces] = (short) (mesh.faceTextures[numFaces] + numTextures);
					else
						faceTextures[numFaces] = -1;
				}
				numFaces++;
			}

			if (mesh.textureMappingP != null) {
				for (int face = 0; face < mesh.numTextures; face++) {
					textureMappingP[numTextures] = (short) (mesh.textureMappingP[face] + vertices);
					textureMappingM[numTextures] = (short) (mesh.textureMappingM[face] + vertices);
					textureMappingN[numTextures] = (short) (mesh.textureMappingN[face] + vertices);
					numTextures++;
				}
			}

			textureIndex += mesh.numTextures;
		}
	}


	/**
	 * Decode this model.
	 * @param library The cache library.
	 */
	public void decode(CacheLibrary library) {
		byte[] data = library.getIndex(library.is317() ? 1 : 7).getArchive(id).getFile(0).getData();
		if (data == null) {
			throw new RuntimeException("No model data found for model " + id);
		}
		if (!library.is317() && (library.isRS3())) {
			decodeRS3(data);
		} else if ((data[data.length - 1] == -1 && data[data.length - 2] == -1)) {
			decodeRS2(data);
		} else {
			decode317(data);
		}
	}

	/**
	 * Decode this model.
	 * @param data The raw model data.
	 * @param rs3 if the model format is rs3
	 */
	public void decode(byte[] data, boolean rs3) {
		if (data == null) {
			throw new RuntimeException("No model data found for model " + id);
		}
		if (rs3) {
			decodeRS3(data);
		} else if ((data[data.length - 1] == -1 && data[data.length - 2] == -1)) {
			decodeRS2(data);
		} else {
			decode317(data);
		}
	}

	/**
	 * Decodes the RS3 model format.
	 * @param data
	 */
	public void decode317(byte[] data) {

		InputStream buffer = new InputStream(data);
		buffer.setPosition(data.length - 18);
		MeshHeader header = new MeshHeader();
		header.setData(data);
		header.setVertices(buffer.readUnsignedShort());
		header.setFaceCount(buffer.readUnsignedShort());
		header.setTexturedFaceCount(buffer.readUnsignedByte());

		int useTextures = buffer.readUnsignedByte();
		int useFacePriority = buffer.readUnsignedByte();
		int useTransparency = buffer.readUnsignedByte();
		int useFaceSkinning = buffer.readUnsignedByte();
		int useVertexSkinning = buffer.readUnsignedByte();
		int xDataOffset = buffer.readUnsignedShort();
		int yDataOffset = buffer.readUnsignedShort();
		int zDataOffset = buffer.readUnsignedShort();
		int faceDataLength = buffer.readUnsignedShort();

		int offset = 0;
		header.setVertexDirectionOffset(offset);
		offset += header.getVertices();

		header.setFaceTypeOffset(offset);
		offset += header.getFaceCount();

		header.setFacePriorityOffset(offset);

		if (useFacePriority == 255) {
			offset += header.getFaceCount();
		} else {
			header.setFacePriorityOffset(-useFacePriority - 1);
		}

		header.setFaceSkinOffset(offset);
		if (useFaceSkinning == 1) {
			offset += header.getFaceCount();
		} else {
			header.setFaceSkinOffset(-1);
		}

		header.setTexturePointerOffset(offset);
		if (useTextures == 1) {
			offset += header.getFaceCount();
		} else {
			header.setTexturePointerOffset(-1);
		}

		header.setVertexSkinOffset(offset);
		if (useVertexSkinning == 1) {
			offset += header.getVertices();
		} else {
			header.setVertexSkinOffset(-1);
		}

		header.setFaceAlphaOffset(offset);
		if (useTransparency == 1) {
			offset += header.getFaceCount();
		} else {
			header.setFaceAlphaOffset(-1);
		}

		header.setFaceDataOffset(offset);
		offset += faceDataLength;

		header.setColourDataOffset(offset);
		offset += header.getFaceCount() * 2;

		header.setUvMapFaceOffset(offset);
		offset += header.getTexturedFaceCount() * 6;

		header.setXDataOffset(offset);
		offset += xDataOffset;

		header.setYDataOffset(offset);
		offset += yDataOffset;

		header.setZDataOffset(offset);
		offset += zDataOffset;

		numVertices = header.getVertices();
		numFaces = header.getFaceCount();
		numTextures = header.getTexturedFaceCount();
		verticesX = new int[numVertices];
		verticesY = new int[numVertices];
		verticesZ = new int[numVertices];
		faceIndicesA = new short[numFaces];
		faceIndicesB = new short[numFaces];
		faceIndicesC = new short[numFaces];
		textureMappingP = new short[numTextures];
		textureMappingM = new short[numTextures];
		textureMappingN = new short[numTextures];

		if (header.getVertexSkinOffset() >= 0) {
			vertexSkins = new int[numVertices];
		}

		if (header.getTexturePointerOffset() >= 0) {
			faceTypes = new byte[numFaces];
		}

		if (header.getFacePriorityOffset() >= 0) {
			facePriorities = new byte[numFaces];
		} else {
			priority = (byte) (-header.getFacePriorityOffset() - 1);
		}

		if (header.getFaceAlphaOffset() >= 0) {
			faceAlphas = new byte[numFaces];
		}

		if (header.getFaceSkinOffset() >= 0) {
			faceSkins = new int[numFaces];
		}

		faceColors = new short[numFaces];
		InputStream directions = new InputStream(header.getData());
		directions.setPosition(header.getVertexDirectionOffset());

		InputStream verticesXBuffer = new InputStream(header.getData());
		verticesXBuffer.setPosition(header.getXDataOffset());

		InputStream verticesYBuffer = new InputStream(header.getData());
		verticesYBuffer.setPosition(header.getYDataOffset());

		InputStream verticesZBuffer = new InputStream(header.getData());
		verticesZBuffer.setPosition(header.getZDataOffset());

		InputStream bones = new InputStream(header.getData());
		bones.setPosition(header.getVertexSkinOffset());

		int baseX = 0;
		int baseY = 0;
		int baseZ = 0;

		for (int vertex = 0; vertex < numVertices; vertex++) {
			int mask = directions.readUnsignedByte();
			int x = 0;
			if ((mask & 1) != 0) {
				x = verticesXBuffer.readSmart();
			}

			int y = 0;
			if ((mask & 2) != 0) {
				y = verticesYBuffer.readSmart();
			}

			int z = 0;
			if ((mask & 4) != 0) {
				z = verticesZBuffer.readSmart();
			}

			verticesX[vertex] = baseX + x;
			verticesY[vertex] = baseY + y;
			verticesZ[vertex] = baseZ + z;
			baseX = verticesX[vertex];
			baseY = verticesY[vertex];
			baseZ = verticesZ[vertex];

			if (vertexSkins != null) {
				vertexSkins[vertex] = bones.readUnsignedByte();
			}
		}

		InputStream colours = directions;
		colours.setPosition(header.getColourDataOffset());

		InputStream points = verticesXBuffer;
		points.setPosition(header.getTexturePointerOffset());

		InputStream priorities = verticesYBuffer;
		priorities.setPosition(header.getFacePriorityOffset());

		InputStream alphas = verticesZBuffer;
		alphas.setPosition(header.getFaceAlphaOffset());

		bones.setPosition(header.getFaceBoneOffset());

		for (int face = 0; face < numFaces; face++) {
			faceColors[face] = (short) colours.readUnsignedShort();
			if (faceTypes != null) {
				faceTypes[face] = (byte) points.readUnsignedByte();
			}
			if (facePriorities != null) {
				facePriorities[face] = (byte) priorities.readUnsignedByte();
			}
			if (faceAlphas != null) {
				faceAlphas[face] = (byte) alphas.readUnsignedByte();
			}
			if (faceSkins != null) {
				faceSkins[face] = bones.readUnsignedByte();
			}
		}

		InputStream faceData = directions;
		faceData.setPosition(header.getFaceDataOffset());

		InputStream types = verticesXBuffer;
		types.setPosition(header.getFaceTypeOffset());

		short faceX = 0;
		short faceY = 0;
		short faceZ = 0;
		offset = 0;

		for (int vertex = 0; vertex < numFaces; vertex++) {
			int type = types.readUnsignedByte();

			if (type == 1) {
				faceX = (short) (faceData.readSmart() + offset);
				offset = faceX;
				faceY = (short) (faceData.readSmart() + offset);
				offset = faceY;
				faceZ = (short) (faceData.readSmart() + offset);
				offset = faceZ;

				faceIndicesA[vertex] = faceX;
				faceIndicesB[vertex] = faceY;
				faceIndicesC[vertex] = faceZ;
			} else if (type == 2) {
				faceY = faceZ;
				faceZ = (short) (faceData.readSmart() + offset);
				offset = faceZ;

				faceIndicesA[vertex] = faceX;
				faceIndicesB[vertex] = faceY;
				faceIndicesC[vertex] = faceZ;
			} else if (type == 3) {
				faceX = faceZ;
				faceZ = (short) (faceData.readSmart() + offset);
				offset = faceZ;

				faceIndicesA[vertex] = faceX;
				faceIndicesB[vertex] = faceY;
				faceIndicesC[vertex] = faceZ;
			} else if (type == 4) {
				short temp = faceX;
				faceX = faceY;
				faceY = temp;

				faceZ = (short) (faceData.readSmart() + offset);
				offset = faceZ;

				faceIndicesA[vertex] = faceX;
				faceIndicesB[vertex] = faceY;
				faceIndicesC[vertex] = faceZ;
			}
		}

		InputStream maps = directions;
		maps.setPosition(header.getUvMapFaceOffset());

		for (int index = 0; index < numTextures; index++) {
			textureMappingP[index] = (short) maps.readUnsignedShort();
			textureMappingM[index] = (short) maps.readUnsignedShort();
			textureMappingN[index] = (short) maps.readUnsignedShort();
		}

//		if (faceMaterials == null) {
//			return;
//		}
//
//		//custom
//		Map<Short, Texture> map = new HashMap<>();
//		textures = new Texture[faceMaterials.length];
//		for (int i = 0; i < faceMaterials.length; i++) {
//			short textureId = faceMaterials[i];
//			if (textureId == -1) {
//				continue;
//			}
//			Texture texture = map.get(textureId);
//			if (texture == null) {
//				texture = new Texture(textureId);
//				texture.decode(CacheLibrary.get());
//				map.put(textureId, texture);
//			}
//			textures[i] = texture;
//		}
//		map.clear();

	}

	/**
	 * Decodes the RS3 model format.
	 * @param data
	 */
	public void decodeRS2(byte[] data) {
		InputStream first = new InputStream(data);
		InputStream second = new InputStream(data);
		InputStream third = new InputStream(data);
		InputStream fourth = new InputStream(data);
		InputStream fifth = new InputStream(data);
		InputStream sixth = new InputStream(data);
		InputStream seventh = new InputStream(data);

		first.skip(data.length - 23);
		numVertices = first.readUnsignedShort();
		numFaces = first.readUnsignedShort();
		numTextures = first.readUnsignedByte();
		int flags = first.readUnsignedByte();

		boolean hasFaceRenderTypes = (flags & 0x1) == 1;
		boolean hasParticleEffects = (flags & 0x2) == 2;
		boolean hasBillboards = (flags & 0x4) == 4;
		boolean hasVersion = (flags & 0x8) == 8;

		if (hasVersion) {
			first.position -= 7;
			version = first.readUnsignedByte();
			first.position += 6;
		}

		int modelPriority = first.readUnsignedByte();
		int hasFaceAlpha = first.readUnsignedByte();
		int hasFaceBones = first.readUnsignedByte();
		int hasFaceTextures = first.readUnsignedByte();
		int hasVertexSkins = first.readUnsignedByte();
		int modelVerticesX = first.readUnsignedShort();
		int modelVerticesY = first.readUnsignedShort();
		int modelVerticesZ = first.readUnsignedShort();
		int faceIndices = first.readUnsignedShort();
		int textureIndices = first.readUnsignedShort();
		int simpleTextureFaceCount = 0;
		int complexTextureFaceCount = 0;
		int cubeTextureFaceCount = 0;

		if (numTextures > 0) {
			faceMappings = new byte[numTextures];
			first.setPosition(0);
			for (int face = 0; face < numTextures; face++) {
				byte type = (faceMappings[face] = (byte) first.readByte());
				if (type == 0)
					simpleTextureFaceCount++;
				if (type >= 1 && type <= 3)
					complexTextureFaceCount++;
				if (type == 2)
					cubeTextureFaceCount++;
			}
		}

		int accumulator = numTextures;
		int vertexFlagsOffset = accumulator;
		accumulator += numVertices;

		int faceTypesOffset = accumulator;
		if (hasFaceRenderTypes)
			accumulator += numFaces;

		int facesCompressTypeOffset = accumulator;
		accumulator += numFaces;

		int facesPrioritiesOffset = accumulator;
		if (modelPriority == 255)
			accumulator += numFaces;


		int faceAlphasOffset = accumulator;
		if (hasFaceBones == 1)
			accumulator += numFaces;

		int faceIndicesOffset = accumulator;
		if (hasVertexSkins == 1)
			accumulator += numVertices;

		int i_183_ = accumulator;
		if (hasFaceAlpha == 1)
			accumulator += numFaces;

		int i_184_ = accumulator;
		accumulator += faceIndices;

		int i_185_ = accumulator;
		if (hasFaceTextures == 1)
			accumulator += numFaces * 2;

		int i_186_ = accumulator;
		accumulator += textureIndices;

		int i_187_ = accumulator;
		accumulator += numFaces * 2;

		int verticesXOffsetOffset = accumulator;
		accumulator += modelVerticesX;

		int verticesYOffsetOffset = accumulator;
		accumulator += modelVerticesY;

		int verticesZOffsetOffset = accumulator;
		accumulator += modelVerticesZ;

		int i_191_ = accumulator;
		accumulator += simpleTextureFaceCount * 6;

		int i_192_ = accumulator;
		accumulator += complexTextureFaceCount * 6;

		int textureBytes = 6;
		if (version == 14)
			textureBytes = 7;
		else if (version >= 15)
			textureBytes = 9;

		int i_194_ = accumulator;
		accumulator += complexTextureFaceCount * textureBytes;

		int i_195_ = accumulator;
		accumulator += complexTextureFaceCount;

		int i_196_ = accumulator;
		accumulator += complexTextureFaceCount;

		int i_197_ = accumulator;
		accumulator += complexTextureFaceCount + cubeTextureFaceCount * 2;

		int i_198_ = accumulator;
		verticesX = new int[numVertices];
		verticesY = new int[numVertices];
		verticesZ = new int[numVertices];
		faceIndicesA = new short[numFaces];
		faceIndicesB = new short[numFaces];
		faceIndicesC = new short[numFaces];
		if (hasVertexSkins == 1)
			vertexSkins = new int[numVertices];
		if (hasFaceRenderTypes)
			faceTypes = new byte[numFaces];
		if (modelPriority == 255)
			facePriorities = new byte[numFaces];
		else
			priority = (byte) modelPriority;
		if (hasFaceAlpha == 1)
			faceAlphas = new byte[numFaces];
		if (hasFaceBones == 1)
			faceSkins = new int[numFaces];
		if (hasFaceTextures == 1)
			faceMaterials = new short[numFaces];
		if (hasFaceTextures == 1 && numTextures > 0)
			faceTextures = new short[numFaces];
		faceColors = new short[numFaces];
		if (numTextures > 0) {
			textureMappingP = new short[numTextures];
			textureMappingM = new short[numTextures];
			textureMappingN = new short[numTextures];
			if (complexTextureFaceCount > 0) {
				textureScaleX = new int[complexTextureFaceCount];
				textureScaleY = new int[complexTextureFaceCount];
				textureScaleZ = new int[complexTextureFaceCount];
				textureRotation = new byte[complexTextureFaceCount];
				textureDirection = new byte[complexTextureFaceCount];
				textureSpeed = new int[complexTextureFaceCount];
			}
			if (cubeTextureFaceCount > 0) {
				textureTransU = new int[cubeTextureFaceCount];
				textureTransV = new int[cubeTextureFaceCount];
			}
		}

		first.setPosition(vertexFlagsOffset);
		second.setPosition(verticesXOffsetOffset);
		third.setPosition(verticesYOffsetOffset);
		fourth.setPosition(verticesZOffsetOffset);
		fifth.setPosition(faceIndicesOffset);

		int baseX = 0;
		int baseY = 0;
		int baseZ = 0;
		for (int vertex = 0; vertex < numVertices; vertex++) {
			int flag = first.readUnsignedByte();
			int xOffset = 0;
			if ((flag & 0x1) != 0)
				xOffset = second.readSmart();
			int yOffset = 0;
			if ((flag & 0x2) != 0)
				yOffset = third.readSmart();
			int zOffset = 0;
			if ((flag & 0x4) != 0)
				zOffset = fourth.readSmart();
			verticesX[vertex] = baseX + xOffset;
			verticesY[vertex] = baseY + yOffset;
			verticesZ[vertex] = baseZ + zOffset;
			baseX = verticesX[vertex];
			baseY = verticesY[vertex];
			baseZ = verticesZ[vertex];
			if (hasVertexSkins == 1)
				vertexSkins[vertex] = fifth.readUnsignedByte();
		}

		first.setPosition(i_187_);
		second.setPosition(faceTypesOffset);
		third.setPosition(facesPrioritiesOffset);
		fourth.setPosition(i_183_);
		fifth.setPosition(faceAlphasOffset);
		sixth.setPosition(i_185_);
		seventh.setPosition(i_186_);

		for (int face = 0; face < numFaces; face++) {
			faceColors[face] = (short) first.readUnsignedShort();
			if (hasFaceRenderTypes)
				faceTypes[face] = (byte) second.readByte();
			if (modelPriority == 255)
				facePriorities[face] = (byte) third.readByte();
			if (hasFaceAlpha == 1)
				faceAlphas[face] = (byte) fourth.readByte();
			if (hasFaceBones == 1)
				faceSkins[face] = fifth.readUnsignedByte();
			if (hasFaceTextures == 1)
				faceMaterials[face] = (short) (sixth.readUnsignedShort() - 1);
			if (faceTextures != null) {
				if (faceMaterials[face] != -1) {
					faceTextures[face] = (byte) (seventh.readUnsignedByte() - 1);
				} else
					faceTextures[face] = (byte) -1;
			}
		}
		maxIndex = -1;
		first.setPosition(i_184_);
		second.setPosition(facesCompressTypeOffset);
		decodeIndicesRS2(first, second);
		first.setPosition(i_191_);
		second.setPosition(i_192_);
		third.setPosition(i_194_);
		fourth.setPosition(i_195_);
		fifth.setPosition(i_196_);
		sixth.setPosition(i_197_);
		decodeMapping(first, second, third, fourth, fifth, sixth);
		first.setPosition(i_198_);
		if (hasParticleEffects) {
			int vertex = first.readUnsignedByte();
			if (vertex > 0) {
				emitters = new EmissiveTriangle[vertex];
				for (int index = 0; index < vertex; index++) {
					int id = first.readUnsignedShort();
					int face = first.readUnsignedShort();
					byte priority;
					if (modelPriority == 255)
						priority = facePriorities[face];
					else
						priority = (byte) modelPriority;
					emitters[index] = new EmissiveTriangle(id, face, faceIndicesA[face], faceIndicesB[face], faceIndicesC[face], priority);
				}
			}
			int count = first.readUnsignedByte();
			if (count > 0) {
				effectors = new EffectiveVertex[count];
				for (int index = 0; index < count; index++) {
					int skin = first.readUnsignedShort();
					int point = first.readUnsignedShort();
					effectors[index] = new EffectiveVertex(skin, point);
				}
			}
		}
		if (hasBillboards) {
			int billboardCount = first.readUnsignedByte();
			if (billboardCount > 0) {
				billboards = new FaceBillboard[billboardCount];
				for (int index = 0; index < billboardCount; index++) {
					int type = first.readUnsignedShort();
					int face = first.readUnsignedShort();
					int priority = first.readUnsignedByte();
					byte magnitude = (byte) first.readByte();
					billboards[index] = new FaceBillboard(type, face, priority, magnitude);
				}
			}
		}

		if (faceMaterials == null) {
			return;
		}

		//custom
		Map<Short, Texture> map = new HashMap<>();
		textures = new Texture[faceMaterials.length];
		for (int i = 0; i < faceMaterials.length; i++) {
			short textureId = faceMaterials[i];
			if (textureId == -1) {
				continue;
			}
			Texture texture = map.get(textureId);
			if (texture == null) {
				texture = new Texture(textureId);
				texture.decode(CacheLibrary.get());
				map.put(textureId, texture);
			}
			textures[i] = texture;
		}
		map.clear();
	}

	/**
	 * Decodes the RS3 model format.
	 * @param data
	 */
	public void decodeRS3(byte[] data) {
		InputStream first = new InputStream(data);
		InputStream second = new InputStream(data);
		InputStream third = new InputStream(data);
		InputStream fourth = new InputStream(data);
		InputStream fifth = new InputStream(data);
		InputStream sixth = new InputStream(data);
		InputStream seventh = new InputStream(data);
		int var9 = first.readUnsignedByte();
		if(var9 != 1) {
		} else {
			first.readUnsignedByte();
			this.version = first.readUnsignedByte();
			first.setPosition(data.length - 26);
			this.numVertices = first.readUnsignedShort();
			this.numFaces = first.readUnsignedShort();
			this.numTextures = first.readUnsignedShort();
			int footerFlags = first.readUnsignedByte();
			boolean hasFaceTypes = (footerFlags & 1) == 1;
			boolean hasParticleEffects = (footerFlags & 2) == 2;
			boolean hasBillboards = (footerFlags & 4) == 4;
			boolean hasExtendedVertexSkins = (footerFlags & 16) == 16;
			boolean hasExtendedTriangleSkins = (footerFlags & 32) == 32;
			boolean hasExtendedBillboards = (footerFlags & 64) == 64;
			boolean hasTextureUV = (footerFlags & 128) == 128;
			int modelPriority = first.readUnsignedByte();
			int hasFaceAlpha = first.readUnsignedByte();
			int hasFaceSkins = first.readUnsignedByte();
			int hasFaceTextures = first.readUnsignedByte();
			int hasVertexSkins = first.readUnsignedByte();
			int modelVerticesX = first.readUnsignedShort();
			int modelVerticesY = first.readUnsignedShort();
			int modelVerticesZ = first.readUnsignedShort();
			int faceIndices = first.readUnsignedShort();
			int textureIndices = first.readUnsignedShort();
			int numVertexSkins = first.readUnsignedShort();
			int numFaceSkins = first.readUnsignedShort();
			if(!hasExtendedVertexSkins) {
				if(hasVertexSkins == 1) {
					numVertexSkins = this.numVertices;
				} else {
					numVertexSkins = 0;
				}
			}

			if(!hasExtendedTriangleSkins) {
				if(hasFaceSkins == 1) {
					numFaceSkins = this.numFaces;
				} else {
					numFaceSkins = 0;
				}
			}

			int simpleTextureFaceCount = 0;
			int complexTextureFaceCount = 0;
			int cubeTextureFaceCount = 0;
			int offset;
			if(this.numTextures > 0) {
				this.faceMappings = new byte[this.numTextures];
				first.setPosition(3);

				for(offset = 0; offset < this.numTextures; ++offset) {
					byte type = this.faceMappings[offset] = (byte) first.readByte();
					if(type == 0) {
						++simpleTextureFaceCount;
					}

					if(type >= 1 && type <= 3) {
						++complexTextureFaceCount;
					}

					if(type == 2) {
						++cubeTextureFaceCount;
					}
				}
			}

			offset = 3 + this.numTextures;
			int vertexFlagsOffset = offset;
			offset += this.numVertices;
			int faceTypesOffset = offset;
			if(hasFaceTypes) {
				offset += this.numFaces;
			}

			int facesCompressTypeOffset = offset;
			offset += this.numFaces;
			int facePrioritiesOffset = offset;
			if(modelPriority == 255) {
				offset += this.numFaces;
			}

			int faceSkinsOffset = offset;
			offset += numFaceSkins;
			int vertexSkinsOffset = offset;
			offset += numVertexSkins;
			int faceAlphasOffset = offset;
			if(hasFaceAlpha == 1) {
				offset += this.numFaces;
			}

			int faceIndicesOffset = offset;
			offset += faceIndices;
			int faceMaterialsOffset = offset;
			if(hasFaceTextures == 1) {
				offset += this.numFaces * 2;
			}

			int faceTextureIndicesOffset = offset;
			offset += textureIndices;
			int faceColorsOffset = offset;
			offset += this.numFaces * 2;
			int verticesXOffsetOffset = offset;
			offset += modelVerticesX;
			int verticesYOffsetOffset = offset;
			offset += modelVerticesY;
			int verticesZOffsetOffset = offset;
			offset += modelVerticesZ;
			int simpleTexturesOffset = offset;
			offset += simpleTextureFaceCount * 6;
			int complexTexturesOffset = offset;
			offset += complexTextureFaceCount * 6;
			byte textureBytes = 6;
			if(this.version == 14) {
				textureBytes = 7;
			} else if(this.version >= 15) {
				textureBytes = 9;
			}

			int texturesScaleOffset = offset;
			offset += complexTextureFaceCount * textureBytes;
			int texturesRotationOffset = offset;
			offset += complexTextureFaceCount;
			int texturesDirectionOffset = offset;
			offset += complexTextureFaceCount;
			int texturesTranslationOffset = offset;
			offset += complexTextureFaceCount + cubeTextureFaceCount * 2;
			int modelDataLength1 = data.length;
			int modelDataLength2 = data.length;
			int modelDataLength3 = data.length;
			int modelDataLength4 = data.length;
			int baseY;
			int baseZ;

			if(hasTextureUV) {
				InputStream uvBuffer = new InputStream(data);
				uvBuffer.setPosition(data.length - 26);
				uvBuffer.setPosition(data[uvBuffer.getPosition() - 1]);
				this.textureUVCoordCount = uvBuffer.readUnsignedShort();
				baseY = uvBuffer.readUnsignedShort();
				baseZ = uvBuffer.readUnsignedShort();
				modelDataLength1 = offset + baseY;
				modelDataLength2 = modelDataLength1 + baseZ;
				modelDataLength3 = modelDataLength2 + this.numVertices;
				modelDataLength4 = modelDataLength3 + this.textureUVCoordCount * 2;
			}

			this.verticesX = new int[this.numVertices];
			this.verticesY = new int[this.numVertices];
			this.verticesZ = new int[this.numVertices];
			this.faceIndicesA = new short[this.numFaces];
			this.faceIndicesB = new short[this.numFaces];
			this.faceIndicesC = new short[this.numFaces];
			if(hasVertexSkins == 1) {
				this.vertexSkins = new int[this.numVertices];
			}

			if(hasFaceTypes) {
				this.faceTypes = new byte[this.numFaces];
			}

			if(modelPriority == 255) {
				this.facePriorities = new byte[this.numFaces];
			} else {
				this.priority = (byte)modelPriority;
			}

			if(hasFaceAlpha == 1) {
				this.faceAlphas = new byte[this.numFaces];
			}

			if(hasFaceSkins == 1) {
				this.faceSkins = new int[this.numFaces];
			}

			if(hasFaceTextures == 1) {
				this.faceMaterials = new short[this.numFaces];
			}

			if(hasFaceTextures == 1 && (this.numTextures > 0 || this.textureUVCoordCount > 0)) {
				this.faceTextures = new short[this.numFaces];
			}

			this.faceColors = new short[this.numFaces];
			if(this.numTextures > 0) {
				this.textureMappingP = new short[this.numTextures];
				this.textureMappingM = new short[this.numTextures];
				this.textureMappingN = new short[this.numTextures];
				if(complexTextureFaceCount > 0) {
					this.textureScaleX = new int[complexTextureFaceCount];
					this.textureScaleY = new int[complexTextureFaceCount];
					this.textureScaleZ = new int[complexTextureFaceCount];
					this.textureRotation = new byte[complexTextureFaceCount];
					this.textureDirection = new byte[complexTextureFaceCount];
					this.textureSpeed = new int[complexTextureFaceCount];
				}

				if(cubeTextureFaceCount > 0) {
					this.textureTransU = new int[cubeTextureFaceCount];
					this.textureTransV = new int[cubeTextureFaceCount];
				}
			}

			first.setPosition(vertexFlagsOffset);
			second.setPosition(verticesXOffsetOffset);
			third.setPosition(verticesYOffsetOffset);
			fourth.setPosition(verticesZOffsetOffset);
			fifth.setPosition(vertexSkinsOffset);
			int baseX = 0;
			baseY = 0;
			baseZ = 0;

			int vertexCount;
			int size;
			int xOffset;
			int yOffset;
			int zOffset;
			for(vertexCount = 0; vertexCount < this.numVertices; ++vertexCount) {
				size = first.readUnsignedByte();
				xOffset = 0;
				if((size & 1) != 0) {
					xOffset = second.readSmart();
				}

				yOffset = 0;
				if((size & 2) != 0) {
					yOffset = third.readSmart();
				}

				zOffset = 0;
				if((size & 4) != 0) {
					zOffset = fourth.readSmart();
				}

				this.verticesX[vertexCount] = baseX + xOffset;
				this.verticesY[vertexCount] = baseY + yOffset;
				this.verticesZ[vertexCount] = baseZ + zOffset;
				baseX = this.verticesX[vertexCount];
				baseY = this.verticesY[vertexCount];
				baseZ = this.verticesZ[vertexCount];
				if(hasVertexSkins == 1) {
					if(hasExtendedVertexSkins) {
						this.vertexSkins[vertexCount] = fifth.readSmartNS();
					} else {
						this.vertexSkins[vertexCount] = fifth.readUnsignedByte();
						if(this.vertexSkins[vertexCount] == 255) {
							this.vertexSkins[vertexCount] = -1;
						}
					}
				}
			}

			if(this.textureUVCoordCount > 0) {
				first.setPosition(modelDataLength2);
				second.setPosition(modelDataLength3);
				third.setPosition(modelDataLength4);
				this.vertexUVOffset = new int[this.numVertices];
				vertexCount = 0;

				for(size = 0; vertexCount < this.numVertices; ++vertexCount) {
					this.vertexUVOffset[vertexCount] = size;
					size += first.readUnsignedByte();
				}

				this.uvCoordVertexA = new byte[this.numFaces];
				this.uvCoordVertexB = new byte[this.numFaces];
				this.uvCoordVertexC = new byte[this.numFaces];
				this.textureCoordU = new float[this.textureUVCoordCount];
				this.textureCoordV = new float[this.textureUVCoordCount];

				for(vertexCount = 0; vertexCount < this.textureUVCoordCount; ++vertexCount) {
					this.textureCoordU[vertexCount] = (float)second.readShort() / 4096.0F;
					this.textureCoordV[vertexCount] = (float)third.readShort() / 4096.0F;
				}
			}

			first.setPosition(faceColorsOffset);
			second.setPosition(faceTypesOffset);
			third.setPosition(facePrioritiesOffset);
			fourth.setPosition(faceAlphasOffset);
			fifth.setPosition(faceSkinsOffset);
			sixth.setPosition(faceMaterialsOffset);
			seventh.setPosition(faceTextureIndicesOffset);

			for(vertexCount = 0; vertexCount < this.numFaces; ++vertexCount) {
				this.faceColors[vertexCount] = (short)first.readUnsignedShort();
				if(hasFaceTypes) {
					this.faceTypes[vertexCount] = (byte) second.readByte();
				}

				if(modelPriority == 255) {
					this.facePriorities[vertexCount] = (byte) third.readByte();
				}

				if(hasFaceAlpha == 1) {
					this.faceAlphas[vertexCount] = (byte) fourth.readByte();
				}

				if(hasFaceSkins == 1) {
					if(hasExtendedTriangleSkins) {
						this.faceSkins[vertexCount] = fifth.readSmartNS();
					} else {
						this.faceSkins[vertexCount] = fifth.readUnsignedByte();
						if(this.faceSkins[vertexCount] == 255) {
							this.faceSkins[vertexCount] = -1;
						}
					}
				}

				if(hasFaceTextures == 1) {
					this.faceMaterials[vertexCount] = (short)(sixth.readUnsignedShort() - 1);
				}

				if(this.faceTextures != null) {
					if(this.faceMaterials[vertexCount] != -1) {
						if(this.version >= 16) {
							this.faceTextures[vertexCount] = (short)(seventh.readUnsignedSmart() - 1);
						} else {
							this.faceTextures[vertexCount] = (short)(seventh.readUnsignedByte() - 1);
						}
					} else {
						this.faceTextures[vertexCount] = -1;
					}
				}
			}

			this.maxIndex = -1;
			first.setPosition(faceIndicesOffset);
			second.setPosition(facesCompressTypeOffset);
			third.setPosition(modelDataLength1);
			this.decodeIndices(first, second, third);
			first.setPosition(simpleTexturesOffset);
			second.setPosition(complexTexturesOffset);
			third.setPosition(texturesScaleOffset);
			fourth.setPosition(texturesRotationOffset);
			fifth.setPosition(texturesDirectionOffset);
			sixth.setPosition(texturesTranslationOffset);
			this.decodeMapping(first, second, third, fourth, fifth, sixth);
			first.setPosition(offset);
			if(hasParticleEffects) {
				vertexCount = first.readUnsignedByte();
				if(vertexCount > 0) {
					this.emitters = new EmissiveTriangle[vertexCount];

					for(size = 0; size < vertexCount; ++size) {
						xOffset = first.readUnsignedShort();
						yOffset = first.readUnsignedShort();
						byte pri;
						if(modelPriority == 255) {
							pri = this.facePriorities[yOffset];
						} else {
							pri = (byte)modelPriority;
						}

						this.emitters[size] = new EmissiveTriangle(xOffset, yOffset, this.faceIndicesA[yOffset], this.faceIndicesB[yOffset], this.faceIndicesC[yOffset], pri);
					}
				}
				size = first.readUnsignedByte();
				if(size > 0) {
					this.effectors = new EffectiveVertex[size];

					for(xOffset = 0; xOffset < size; ++xOffset) {
						yOffset = first.readUnsignedShort();
						zOffset = first.readUnsignedShort();
						this.effectors[xOffset] = new EffectiveVertex(yOffset, zOffset);
					}
				}
			}

			if(hasBillboards) {
				vertexCount = first.readUnsignedByte();
				if(vertexCount > 0) {
					this.billboards = new FaceBillboard[vertexCount];
					for(size = 0; size < vertexCount; ++size) {
						xOffset = first.readUnsignedShort();
						yOffset = first.readUnsignedShort();
						if(hasExtendedBillboards) {
							zOffset = first.readSmartNS();
						} else {
							zOffset = first.readUnsignedByte();
							if(zOffset == 255) {
								zOffset = -1;
							}
						}
						byte distance = (byte) first.readByte();
						this.billboards[size] = new FaceBillboard(xOffset, yOffset, zOffset, distance);
					}
				}
			}
		}

		if (faceMaterials == null) {
			return;
		}

		//custom
		Map<Short, Texture> map = new HashMap<>();
		textures = new Texture[faceMaterials.length];
		for (int i = 0; i < faceMaterials.length; i++) {
			short textureId = faceMaterials[i];
			if (textureId == -1) {
				continue;
			}
			Texture texture = map.get(textureId);
			if (texture == null) {
				texture = new Texture(textureId);
				texture.decode(CacheLibrary.get());
				map.put(textureId, texture);
			}
			textures[i] = texture;
		}
		map.clear();

	}

	/**
	 * Decode the face indices.
	 * @param var1 The first stream.
	 * @param var2 The second stream.
	 * @param var3 The third stream.
	 */
	public void decodeIndices(InputStream var1, InputStream var2, InputStream var3) {
		short var4 = 0;
		short var5 = 0;
		short var6 = 0;
		short var7 = 0;
		for(int var8 = 0; var8 < this.numFaces; ++var8) {
			int var9 = var2.readUnsignedByte();
			int var10 = var9 & 7;
			if(var10 == 1) {
				this.faceIndicesA[var8] = var4 = (short)(var1.readSmart() + var7);
				this.faceIndicesB[var8] = var5 = (short)(var1.readSmart() + var4);
				this.faceIndicesC[var8] = var6 = (short)(var1.readSmart() + var5);
				var7 = var6;
				if(var4 > this.maxIndex) {
					this.maxIndex = var4;
				}

				if(var5 > this.maxIndex) {
					this.maxIndex = var5;
				}

				if(var6 > this.maxIndex) {
					this.maxIndex = var6;
				}
			}
			if(var10 == 2) {
				var5 = var6;
				var6 = (short)(var1.readSmart() + var7);
				var7 = var6;
				this.faceIndicesA[var8] = var4;
				this.faceIndicesB[var8] = var5;
				this.faceIndicesC[var8] = var6;
				if(var6 > this.maxIndex) {
					this.maxIndex = var6;
				}
			}

			if(var10 == 3) {
				var4 = var6;
				var6 = (short)(var1.readSmart() + var7);
				var7 = var6;
				this.faceIndicesA[var8] = var4;
				this.faceIndicesB[var8] = var5;
				this.faceIndicesC[var8] = var6;
				if(var6 > this.maxIndex) {
					this.maxIndex = var6;
				}
			}

			if(var10 == 4) {
				short var11 = var4;
				var4 = var5;
				var5 = var11;
				var6 = (short)(var1.readSmart() + var7);
				var7 = var6;
				this.faceIndicesA[var8] = var4;
				this.faceIndicesB[var8] = var11;
				this.faceIndicesC[var8] = var6;
				if(var6 > this.maxIndex) {
					this.maxIndex = var6;
				}
			}

			if(this.textureUVCoordCount > 0 && (var9 & 8) != 0) {
				this.uvCoordVertexA[var8] = (byte)var3.readUnsignedByte();
				this.uvCoordVertexB[var8] = (byte)var3.readUnsignedByte();
				this.uvCoordVertexC[var8] = (byte)var3.readUnsignedByte();
			}
		}

		++this.maxIndex;
	}

	/**
	 * Decode the texture mappings.
	 * @param var1 The first stream.
	 * @param var2 The second stream.
	 * @param var3 The third stream.
	 * @param var4 The fourth stream.
	 * @param var5 The fifth stream.
	 * @param var6 The sixth stream.
	 */
	public void decodeMapping(InputStream var1, InputStream var2, InputStream var3, InputStream var4, InputStream var5, InputStream var6) {
		for(int var7 = 0; var7 < this.numTextures; ++var7) {
			int var8 = this.faceMappings[var7] & 255;
			if(var8 == 0) {
				this.textureMappingP[var7] = (short)var1.readUnsignedShort();
				this.textureMappingM[var7] = (short)var1.readUnsignedShort();
				this.textureMappingN[var7] = (short)var1.readUnsignedShort();
			}

			if(var8 == 1) {
				this.textureMappingP[var7] = (short)var2.readUnsignedShort();
				this.textureMappingM[var7] = (short)var2.readUnsignedShort();
				this.textureMappingN[var7] = (short)var2.readUnsignedShort();
				if(this.version < 15) {
					this.textureScaleX[var7] = var3.readUnsignedShort();
					if(this.version < 14) {
						this.textureScaleY[var7] = var3.readUnsignedShort();
					} else {
						this.textureScaleY[var7] = var3.read24BitInt();
					}
					this.textureScaleZ[var7] = var3.readUnsignedShort();
				} else {
					this.textureScaleX[var7] = var3.read24BitInt();
					this.textureScaleY[var7] = var3.read24BitInt();
					this.textureScaleZ[var7] = var3.read24BitInt();
				}

				this.textureRotation[var7] = (byte) var4.readByte();
				this.textureDirection[var7] = (byte) var5.readByte();
				this.textureSpeed[var7] = var6.readByte();
			}

			if(var8 == 2) {
				this.textureMappingP[var7] = (short)var2.readUnsignedShort();
				this.textureMappingM[var7] = (short)var2.readUnsignedShort();
				this.textureMappingN[var7] = (short)var2.readUnsignedShort();
				if(this.version < 15) {
					this.textureScaleX[var7] = var3.readUnsignedShort();
					if(this.version < 14) {
						this.textureScaleY[var7] = var3.readUnsignedShort();
					} else {
						this.textureScaleY[var7] = var3.read24BitInt();
					}

					this.textureScaleZ[var7] = var3.readUnsignedShort();
				} else {
					this.textureScaleX[var7] = var3.read24BitInt();
					this.textureScaleY[var7] = var3.read24BitInt();
					this.textureScaleZ[var7] = var3.read24BitInt();
				}

				this.textureRotation[var7] = (byte) var4.readByte();
				this.textureDirection[var7] = (byte) var5.readByte();
				this.textureSpeed[var7] = var6.readByte();
				this.textureTransU[var7] = var6.readByte();
				this.textureTransV[var7] = var6.readByte();
			}

			if(var8 == 3) {
				this.textureMappingP[var7] = (short)var2.readUnsignedShort();
				this.textureMappingM[var7] = (short)var2.readUnsignedShort();
				this.textureMappingN[var7] = (short)var2.readUnsignedShort();
				if(this.version < 15) {
					this.textureScaleX[var7] = var3.readUnsignedShort();
					if(this.version < 14) {
						this.textureScaleY[var7] = var3.readUnsignedShort();
					} else {
						this.textureScaleY[var7] = var3.read24BitInt();
					}

					this.textureScaleZ[var7] = var3.readUnsignedShort();
				} else {
					this.textureScaleX[var7] = var3.read24BitInt();
					this.textureScaleY[var7] = var3.read24BitInt();
					this.textureScaleZ[var7] = var3.read24BitInt();
				}

				this.textureRotation[var7] = (byte) var4.readByte();
				this.textureDirection[var7] = (byte) var5.readByte();
				this.textureSpeed[var7] = var6.readByte();
			}
		}
	}

	/**
	 * Decode the face indices.
	 * @param var1 The first stream.
	 * @param var2 The second stream.
	 */
	public void decodeIndicesRS2(InputStream var1, InputStream var2) {
		short var4 = 0;
		short var5 = 0;
		short var6 = 0;
		short var7 = 0;
		for(int var8 = 0; var8 < this.numFaces; ++var8) {
			int var9 = var2.readUnsignedByte();
			if(var9 == 1) {
				this.faceIndicesA[var8] = var4 = (short)(var1.readSmart() + var7);
				this.faceIndicesB[var8] = var5 = (short)(var1.readSmart() + var4);
				this.faceIndicesC[var8] = var6 = (short)(var1.readSmart() + var5);
				var7 = var6;
				if(var4 > this.maxIndex) {
					this.maxIndex = var4;
				}

				if(var5 > this.maxIndex) {
					this.maxIndex = var5;
				}

				if(var6 > this.maxIndex) {
					this.maxIndex = var6;
				}
			}
			if(var9 == 2) {
				var5 = var6;
				var6 = (short)(var1.readSmart() + var7);
				var7 = var6;
				this.faceIndicesA[var8] = var4;
				this.faceIndicesB[var8] = var5;
				this.faceIndicesC[var8] = var6;
				if(var6 > this.maxIndex) {
					this.maxIndex = var6;
				}
			}
			if(var9 == 3) {
				var4 = var6;
				var6 = (short)(var1.readSmart() + var7);
				var7 = var6;
				this.faceIndicesA[var8] = var4;
				this.faceIndicesB[var8] = var5;
				this.faceIndicesC[var8] = var6;
				if(var6 > this.maxIndex) {
					this.maxIndex = var6;
				}
			}
			if(var9 == 4) {
				short var11 = var4;
				var4 = var5;
				var5 = var11;
				var6 = (short)(var1.readSmart() + var7);
				var7 = var6;
				this.faceIndicesA[var8] = var4;
				this.faceIndicesB[var8] = var11;
				this.faceIndicesC[var8] = var6;
				if(var6 > this.maxIndex) {
					this.maxIndex = var6;
				}
			}
		}
		++this.maxIndex;
	}

	public void computeAnimationTables() {
		if (vertexSkins != null) {
			int vertexWeightCount[] = new int[256];
			int maxVertexWeight = 0;
			for (int vertexIndex = 0; vertexIndex < numVertices; vertexIndex++) {
				int vertexWeight = vertexSkins[vertexIndex];
				vertexWeightCount[vertexWeight]++;
				if (vertexWeight > maxVertexWeight) {
					maxVertexWeight = vertexWeight;
				}
			}

			vertexGroups = new int[maxVertexWeight + 1][];
			for (int k1 = 0; k1 <= maxVertexWeight; k1++) {
				vertexGroups[k1] = new int[vertexWeightCount[k1]];
				vertexWeightCount[k1] = 0;
			}

			for (int vertexIndex = 0; vertexIndex < numVertices; vertexIndex++) {

				int weight = vertexSkins[vertexIndex];
				vertexGroups[weight][vertexWeightCount[weight]++] = vertexIndex;
			}

			vertexSkins = null;
		}
		if (faceSkins != null) {
			int ai1[] = new int[256];
			int k = 0;
			for (int i1 = 0; i1 < numFaces; i1++) {
				int l1 = faceSkins[i1];
				ai1[l1]++;
				if (l1 > k) {
					k = l1;
				}
			}

			faceGroups = new int[k + 1][];
			for (int i2 = 0; i2 <= k; i2++) {
				faceGroups[i2] = new int[ai1[i2]];
				ai1[i2] = 0;
			}

			for (int k2 = 0; k2 < numFaces; k2++) {
				int i3 = faceSkins[k2];
				faceGroups[i3][ai1[i3]++] = k2;
			}

			faceSkins = null;
		}
	}

	public static int centerX, centerY, centerZ;

	public static void resetReferencePoint() {
		centerX = 0;
		centerY = 0;
		centerZ = 0;
	}
	/**
	 *
	 * @param type
	 * @param applyToGroups
	 * @param x
	 * @param y
	 * @param z
	 */
	public void animate(int type, int[] applyToGroups, int x, int y, int z, double fraction) {
		switch(type) {

			case CENTROID_TRANSFORMATION:
				int changedVertices = 0;
				centerX = 0;
				centerY = 0;
				centerZ = 0;
				for (int k2 = 0; k2 < applyToGroups.length; k2++) {
					int l3 = applyToGroups[k2];
					if (l3 < vertexGroups.length) {
						int ai5[] = vertexGroups[l3];
						for (int j6 : ai5) {
							centerX += verticesX[j6];
							centerY += verticesY[j6];
							centerZ += verticesZ[j6];
							changedVertices++;
						}

					}
				}

				if (changedVertices > 0) {
					centerX = (int) (x + centerX / changedVertices);
					centerY = (int) (y + centerY / changedVertices);
					centerZ = (int) (z + centerZ / changedVertices );
				} else {
					centerX = x;
					centerY = y;
					centerZ = z;
				}
				return;

			case POSITION_TRANSFORMATION:
				for (int index = 0; index < applyToGroups.length; index++) {
					int groupToApply = applyToGroups[index];
					if (groupToApply < vertexGroups.length) {
						int[] vertices = vertexGroups[groupToApply];
						for (int vertex : vertices) {
							verticesX[vertex] += x;
							verticesY[vertex] += y;
							verticesZ[vertex] += z;
						}
					}
				}
				return;

			case ROTATION_TRANSFORMATION:
				for (int index = 0; index < applyToGroups.length; index++) {
					int groupToApply = applyToGroups[index];
					if (groupToApply < vertexGroups.length) {
						int[] vertices = vertexGroups[groupToApply];
						for (int vertex : vertices) {

							verticesX[vertex] -= centerX;
							verticesY[vertex] -= centerY;
							verticesZ[vertex] -= centerZ;
							int normalizedX = (x & 0xff) * 8;
							int normalizedY = (y & 0xff) * 8;
							int normalizedZ = (z & 0xff) * 8;

							if (normalizedZ != 0) {
								int sine = MathUtilities.SINE[normalizedZ];
								int cosine = MathUtilities.COSINE[normalizedZ];
								int rotatedX = verticesY[vertex] * sine + verticesX[vertex] * cosine >> 16;
								verticesY[vertex] = verticesY[vertex] * cosine - verticesX[vertex] * sine >> 16;
								verticesX[vertex] = rotatedX;
							}

							if (normalizedX != 0) {
								int sine = MathUtilities.SINE[normalizedX];
								int cosine = MathUtilities.COSINE[normalizedX];
								int rotatedY = verticesY[vertex] * cosine - verticesZ[vertex] * sine >> 16;
								verticesZ[vertex] = verticesY[vertex] * sine + verticesZ[vertex] * cosine  >> 16;
								verticesY[vertex] = rotatedY;
							}

							if (normalizedY != 0) {
								int sine = MathUtilities.SINE[normalizedY];
								int cosine = MathUtilities.COSINE[normalizedY];
								int rotatedX = verticesZ[vertex] * sine + verticesX[vertex] * cosine >> 16;
								verticesZ[vertex] = verticesZ[vertex] * cosine - verticesX[vertex] * sine >> 16;
								verticesX[vertex] = rotatedX;
							}

							verticesX[vertex] += centerX;
							verticesY[vertex] += centerY;
							verticesZ[vertex] += centerZ;
						}
					}
				}
				return;

			case SCALE_TRANSFORMATION:
				for (int index = 0; index < applyToGroups.length; index++) {
					int groupToApply = applyToGroups[index];
					if (groupToApply < vertexGroups.length) {
						int[] vertices = vertexGroups[groupToApply];
						for (int vertex : vertices) {
							verticesX[vertex] -= centerX;
							verticesY[vertex] -= centerY;
							verticesZ[vertex] -= centerZ;
							verticesX[vertex] = (int) ((verticesX[vertex] * x) / 128);
							verticesY[vertex] = (int) ((verticesY[vertex] * y) / 128);
							verticesZ[vertex] = (int) ((verticesZ[vertex] * z) / 128);
							verticesX[vertex] += centerX;
							verticesY[vertex] += centerY;
							verticesZ[vertex] += centerZ;
						}
					}
				}
				return;

			case ALPHA_TRANSFORMATION:
				if(faceAlphas == null || faceGroups == null)
					return;
				for (int index = 0; index < applyToGroups.length; index++) {
					int groupToApply = applyToGroups[index];
					if (groupToApply < faceGroups.length) {
						int faces[] = faceGroups[groupToApply];
						for (int faceIndex : faces) {
							faceAlphas[faceIndex] += x * 8;
							if (faceAlphas[faceIndex] < 0) {
								faceAlphas[faceIndex] = 0;
							}
							if (faceAlphas[faceIndex] > 255) {
								faceAlphas[faceIndex] = (byte) 255;
							}
						}
					}
				}
				return;
			default:
				System.out.println("TRANSFORM TYPE IS " + type);
		}
	}

//	/**
//	 * Modifies the mesh by the spot animation information.
//	 * @param original
//	 * @param spot
//	 * @return
//	 */
//	public Mesh getModifiedSpotAnim(Mesh original, SpotAnimation spot) {
//		if (spot == null)
//			return null;
//		Mesh mesh = original.copy();
//		if (spot.getRecolorToFind() != null) {
//			for (int find = 0; find < spot.getRecolorToFind().length; find++) {
//				mesh.replaceColor(spot.getRecolorToFind()[find], spot.getRecolorToReplace()[find]);
//			}
//		}
//		if (spot.getBreadthScale() != 128 || spot.getDepthScale() != 128) {
//			mesh.resize(spot.getBreadthScale(), spot.getBreadthScale(), spot.getDepthScale());
//		}
//		if (spot.getOrientation() != 0) {
//			for (int pass = 0; pass < spot.getOrientation() / 90; pass++) {
//				mesh.rotate90degress();
//			}
//		}
//		return mesh;
//	}

	/**
	 * Copys the mesh and all its components.
	 * @return
	 */
	public Mesh copy() {
		return new Mesh(this);
	}

	public void applyRecolors(int[] find, int[] replace) {
		if (Objects.nonNull(faceColors) && Objects.nonNull(find) && Objects.nonNull(replace)) {
			for (int index = 0; index < find.length; index++) {
				replaceColor(find[index], replace[index]);
			}
		}
	}

	/**
	 * Packs vertices into a float array for rendering
	 * @return
	 */
	public float[] getVerticesAsFloat() {
		float[] vals = new float[numFaces * 9];
		for(int index = 0;index<numFaces;index++) {
			int baseIndex = 9 * index;
			int faceX = faceIndicesA[index];
			int faceY = faceIndicesA[index];
			int faceZ = faceIndicesA[index];
			vals[baseIndex] = verticesX[faceX] * 1f;
			vals[baseIndex + 1] = verticesY[faceY] * 1f;
			vals[baseIndex + 2] = verticesZ[faceZ] * 1f;

			faceX = faceIndicesB[index];
			faceY = faceIndicesB[index];
			faceZ = faceIndicesB[index];

			vals[baseIndex + 3] = verticesX[faceX] * 1f;
			vals[baseIndex + 4] = verticesY[faceY] * 1f;
			vals[baseIndex + 5] = verticesZ[faceZ] * 1f;


			faceX = faceIndicesC[index];
			faceY = faceIndicesC[index];
			faceZ = faceIndicesC[index];


			vals[baseIndex + 6] = verticesX[faceX] * 1f;
			vals[baseIndex + 7] = verticesY[faceY] * 1f;
			vals[baseIndex + 8] = verticesZ[faceZ] * 1f;
		}
		return vals;
	}

	/**
	 * Packs face indices into an int array for rendering
	 * @return
	 */
	public int[] getMappedFaces() {
		int[] vals = new int[numFaces * 3];
		for(int index = 0;index<numFaces;index++) {
			int baseIndex = index * 3;
			vals[baseIndex] = index * 9;
			vals[baseIndex + 1] = index + 1 * 9;
			vals[baseIndex + 2] = index + 2 * 9;
		}
		return vals;
	}

	/**
	 * Maps face colors into a float array for rendering
	 * @return
	 */
	public float[] getMappedColours() {
		float[] val = new float[numFaces * 12];
		for(int index = 0;index<numFaces;index++) {
			int baseIndex = 12 * index;
			int rgb = ColorUtilities.forHSBColor(faceColors[index]);
			float alpha = getFaceAlphas() == null ? 1f : getFaceAlphas()[index] / 256f;
			for (int pass = 0; pass < 3; pass++) {
				val[baseIndex + (pass * 4)] = (float) ((rgb >> 16) & 0xff) / 256f;
				val[baseIndex + (pass * 4) + 1] = (float) ((rgb >> 8) & 0xff) / 256f;
				val[baseIndex + (pass * 4) + 2] = (float) (rgb & 0xff) / 256f;
				val[baseIndex + (pass * 4) + 3] = (float) alpha;
			}
		}
		return val;
	}

	public float[] getMappedTextures() {
		float[] val = new float[numTextures * 3];
		for (int index = 0 ; index < numTextures; index++) {
			val[index] = (float) (getTextureMappingP()[index]);
			val[index + 1] = (float) (getTextureMappingM()[index]);
			val[index + 2] = (float) (getTextureMappingN()[index]);
		}
		return val;
	}

	/**
	 * Moves a mesh based on given args
	 * @param vertexX
	 * @param vertexY
	 * @param vertexZ
	 */
	public void translate(int vertexX, int vertexY, int vertexZ) {
		for(int index = 0;index<numVertices;index++) {
			verticesX[index] += vertexX;
			verticesY[index] += vertexY;
			verticesZ[index] += vertexZ;
		}

	}

	/**
	 * Scales a mesh based on the given multiplier
	 * @param mutliplier
	 */
	public void scale(float mutliplier) {
		for(int index = 0;index<numVertices;index++) {
			verticesX[index] *= mutliplier;
			verticesY[index] *= mutliplier;
			verticesZ[index] *= mutliplier;
		}
	}

	/**
	 * Resizes a mesh based on given args
	 * @param x
	 * @param z
	 * @param y
	 */
	public void resize(int x, int z, int y) {
		for (int vertex = 0; vertex < numVertices; vertex++) {
			verticesX[vertex] = verticesX[vertex] * x / 128;
			verticesY[vertex] = verticesY[vertex] * y / 128;
			verticesZ[vertex] = verticesZ[vertex] * z / 128;
		}
	}

	/**
	 * Rotates the mesh by 90 degress
	 */
	public void rotate90degress() {
		for (int vertex = 0; vertex < numVertices; vertex++) {
			int i = verticesX[vertex];
			verticesX[vertex] = verticesZ[vertex];
			verticesZ[vertex] = -i;
		}
	}

	/**
	 * Replaces a color with another
	 * @param find
	 * @param replace
	 */
	public void replaceColor(int find, int replace) {
		for (int k = 0; k < numFaces; k++) {
			if (faceColors[k] == find) {
				faceColors[k] = (short) replace;
			}
		}
	}

	/**
	 * Replaces a texture with another
	 * @param find
	 * @param replace
	 */
	public void replaceTexture(int find, int replace) {
		for (int k = 0; k < numFaces; k++) {
			if (faceMaterials[k] == find) {
				faceMaterials[k] = (short) replace;
			}
		}
	}

	@Override
	public void decode(int opcode, InputStream buffer) {
		decode(CacheLibrary.get());
	}

	@Override
	public OutputStream encode(OutputStream master) {
		return null;
	}

	public byte[] encode317() {
		/* create the master buffer */
		OutputStream master = new OutputStream(0);

		/* create the temporary buffers */
		OutputStream vertex_flags_buffer = new OutputStream(0);
		OutputStream face_types_buffer = new OutputStream(0);
		OutputStream face_index_types_buffer = new OutputStream(0);
		OutputStream face_priorities_buffer = new OutputStream(0);
		OutputStream face_skins_buffer = new OutputStream(0);
		OutputStream vertex_skins_buffer = new OutputStream(0);
		OutputStream face_alphas_buffer = new OutputStream(0);
		OutputStream face_indices_buffer = new OutputStream(0);
		OutputStream face_colors_buffer = new OutputStream(0);
		OutputStream vertex_x_buffer = new OutputStream(0);
		OutputStream vertex_y_buffer = new OutputStream(0);
		OutputStream vertex_z_buffer = new OutputStream(0);
		OutputStream simple_textures_buffer = new OutputStream(0);
		OutputStream complex_textures_buffer = new OutputStream(0);
		OutputStream texture_scale_buffer = new OutputStream(0);
		OutputStream texture_rotation_buffer = new OutputStream(0);
		OutputStream texture_direction_buffer = new OutputStream(0);
		OutputStream texture_translation_buffer = new OutputStream(0);
		OutputStream particle_effects_buffer = new OutputStream(0);
		OutputStream footer_buffer = new OutputStream(0);

		/* create the vertices variables */
		boolean hasVertexSkins = vertexSkins != null;
		boolean hasExtendedVertexSkins = false;

		/* serialize the vertices */
		int baseX = 0, baseY = 0, baseZ = 0;
		for (int vertex = 0; vertex < numVertices; vertex++) {
			int actualX = verticesX[vertex] - baseX;
			int actualY = verticesY[vertex] - baseY;
			int actualZ = verticesZ[vertex] - baseZ;
			int flag = 0;
			if (actualX != 0) {
				vertex_x_buffer.writeUnsignedSmart(actualX);
				flag |= 0x1;
			}
			if (actualY != 0) {
				vertex_y_buffer.writeUnsignedSmart(actualY);
				flag |= 0x2;
			}
			if (actualZ != 0) {
				vertex_z_buffer.writeUnsignedSmart(actualZ);
				flag |= 0x4;
			}
			vertex_flags_buffer.writeByte(flag);
			baseX = verticesX[vertex];
			baseY = verticesY[vertex];
			baseZ = verticesZ[vertex];
			if (hasVertexSkins) {
				int weight = vertexSkins[vertex];
				if (weight >= -1 && weight <= 254) {
					vertex_skins_buffer.writeByte(weight);
				}
			}
		}

		/* create the faces variables */
		boolean hasFaceTypes = faceTypes != null;
		boolean hasFacePriorities = facePriorities != null;
		boolean hasFaceAlpha = faceAlphas != null;
		boolean hasFaceSkins = faceSkins != null;
		boolean hasExtendedFaceSkins = false;
		boolean hasFaceTextures = faceTextures != null;

		/* serialize the faces */
		for (int face = 0; face < numFaces; face++) {
			boolean isTexture = faceTextures != null && faceTextures[face] != -1;
			face_colors_buffer.writeShort(isTexture ? faceTextures[face] : faceColors[face]);
			if (hasFaceTypes) {
				face_types_buffer.writeByte(faceTypes[face]);
			}
			if (hasFacePriorities) {
				face_priorities_buffer.writeByte(facePriorities[face]);
			}
			if (hasFaceAlpha) {
				face_alphas_buffer.writeByte(faceAlphas[face]);
			}
			if (hasFaceSkins) {
				int weight = faceSkins[face];
				if (weight >= -1 && weight <= 254) {
					face_skins_buffer.writeByte(weight);
				} else {
					face_skins_buffer.writeSmart(weight + 1);
					hasExtendedFaceSkins = true;
				}
			}
		}

		/* serialize the face indices */
		encodeIndices(face_indices_buffer, face_index_types_buffer);

		/* serialize the texture mapping */
		encodeMapping(simple_textures_buffer, complex_textures_buffer, texture_scale_buffer, texture_rotation_buffer, texture_direction_buffer, texture_translation_buffer);

		/* create the footer data */
		footer_buffer.writeShort(numVertices);
		footer_buffer.writeShort(numFaces);
		footer_buffer.writeByte(numTextures);
		footer_buffer.writeBoolean(hasFaceTextures);
		footer_buffer.writeByte(hasFacePriorities ? -1 : priority);
		footer_buffer.writeBoolean(hasFaceAlpha);
		footer_buffer.writeBoolean(hasFaceSkins);
		footer_buffer.writeBoolean(hasVertexSkins);
		footer_buffer.writeShort(vertex_x_buffer.getDataTrimmed().length);
		footer_buffer.writeShort(vertex_y_buffer.getDataTrimmed().length);
		footer_buffer.writeShort(vertex_z_buffer.getDataTrimmed().length);
		footer_buffer.writeShort(face_indices_buffer.getDataTrimmed().length);

		if (hasExtendedVertexSkins) {
			footer_buffer.writeShort(vertex_skins_buffer.length());
		}
		if (hasExtendedFaceSkins) {
			footer_buffer.writeShort(face_skins_buffer.length());
		}

		OutputStream[] buffers = new OutputStream[] { vertex_flags_buffer, face_index_types_buffer, face_priorities_buffer, face_skins_buffer, face_types_buffer, vertex_skins_buffer, face_alphas_buffer, face_indices_buffer, face_colors_buffer, simple_textures_buffer, vertex_x_buffer, vertex_y_buffer, vertex_z_buffer, complex_textures_buffer, texture_scale_buffer, texture_rotation_buffer,
				texture_direction_buffer, texture_translation_buffer, particle_effects_buffer, footer_buffer };
		for (int i = 0; i < buffers.length; i++) {
			Stream buffer = buffers[i];
			master.writeBytes(buffer.getDataTrimmed());
		}
		return master.getDataTrimmed();
	}

	/**
	 * Encodes the texture mappings.
	 *
	 * @param simple
	 *            the simple mapping buffer.
	 * @param complex
	 *            the complex mapping buffer.
	 * @param scale
	 *            the scale buffer.
	 * @param rotation
	 *            the rotation buffer.
	 * @param direction
	 *            the direction buffer.
	 * @param translation
	 *            the translation buffer.
	 */
	public void encodeMapping(OutputStream simple, OutputStream complex, OutputStream scale, OutputStream rotation, OutputStream direction, OutputStream translation) {
		for (int face = 0; face < numTextures; face++) {
			int type = faceMappings[face] & 0xff;
			if (type == 0) {
				simple.writeShort(textureMappingP[face]);
				simple.writeShort(textureMappingM[face]);
				simple.writeShort(textureMappingN[face]);
			} else {
				int scaleX = textureScaleX[face];
				int scaleY = textureScaleY[face];
				int scaleZ = textureScaleZ[face];
				if (type == 1) {
					complex.writeShort(textureMappingP[face]);
					complex.writeShort(textureMappingM[face]);
					complex.writeShort(textureMappingN[face]);
					if (version >= 15 || scaleX > 0xffff || scaleZ > 0xffff) {
						if (version < 15) {
							version = 15;
						}
						scale.write24BitInt(scaleX);
						scale.write24BitInt(scaleY);
						scale.write24BitInt(scaleZ);
					} else {
						scale.writeShort(scaleX);
						if (version < 14 && scaleY > 0xffff) {
							version = 14;
						}
						if (version < 14) {
							scale.writeShort(scaleY);
						} else {
							scale.write24BitInt(scaleY);
						}
						scale.writeShort(scaleZ);
					}
					rotation.writeByte(textureRotation[face]);
					direction.writeByte(textureDirection[face]);
					translation.writeByte(textureSpeed[face]);
				} else if (type == 2) {
					complex.writeShort(textureMappingP[face]);
					complex.writeShort(textureMappingM[face]);
					complex.writeShort(textureMappingN[face]);
					if (version >= 15 || scaleX > 0xffff || scaleZ > 0xffff) {
						if (version < 15) {
							version = 15;
						}
						scale.write24BitInt(scaleX);
						scale.write24BitInt(scaleY);
						scale.write24BitInt(scaleZ);
					} else {
						scale.writeShort(scaleX);
						if (version < 14 && scaleY > 0xffff) {
							version = 14;
						}
						if (version < 14) {
							scale.writeShort(scaleY);
						} else {
							scale.write24BitInt(scaleY);
						}
						scale.writeShort(scaleZ);
					}
					rotation.writeByte(textureRotation[face]);
					direction.writeByte(textureDirection[face]);
					translation.writeByte(textureSpeed[face]);
					translation.writeByte(textureTransU[face]);
					translation.writeByte(textureTransV[face]);
				} else if (type == 3) {
					complex.writeShort(textureMappingP[face]);
					complex.writeShort(textureMappingM[face]);
					complex.writeShort(textureMappingN[face]);
					if (version >= 15 || scaleX > 0xffff || scaleZ > 0xffff) {
						if (version < 15) {
							version = 15;
						}
						scale.write24BitInt(scaleX);
						scale.write24BitInt(scaleY);
						scale.write24BitInt(scaleZ);
					} else {
						scale.writeShort(scaleX);
						if (version < 14 && scaleY > 0xffff) {
							version = 14;
						}
						if (version < 14) {
							scale.writeShort(scaleY);
						} else {
							scale.write24BitInt(scaleY);
						}
						scale.writeShort(scaleZ);
					}
					rotation.writeByte(textureRotation[face]);
					direction.writeByte(textureDirection[face]);
					translation.writeByte(textureSpeed[face]);
				}
			}
		}
	}


	/**
	 * Encodes the indices list.
	 *
	 * @param ibuffer
	 *            the indices buffer.
	 * @param tbuffer
	 *            the indices compression type buffer.
	 */
	public void encodeIndices(OutputStream ibuffer, OutputStream tbuffer) {
		int lasta = 0;
		int lastb = 0;
		int lastc = 0;
		int pacc = 0;
		for (int fndex = 0; fndex < numFaces; fndex++) {
			int cura = faceIndicesA[fndex];
			int curb = faceIndicesB[fndex];
			int curc = faceIndicesC[fndex];
			if (cura == lastb && curb == lasta && curc != lastc) {
				tbuffer.writeByte(4);
				ibuffer.writeUnsignedSmart(curc - pacc);
				int back = lasta;
				lasta = lastb;
				lastb = back;
				pacc = lastc = curc;
			} else if (cura == lastc && curb == lastb && curc != lastc) {
				tbuffer.writeByte(3);
				ibuffer.writeUnsignedSmart(curc - pacc);
				lasta = lastc;
				pacc = lastc = curc;
			} else if (cura == lasta && curb == lastc && curc != lastc) {
				tbuffer.writeByte(2);
				ibuffer.writeUnsignedSmart(curc - pacc);
				lastb = lastc;
				pacc = lastc = curc;
			} else {
				tbuffer.writeByte(1);
				ibuffer.writeUnsignedSmart(cura - pacc);
				ibuffer.writeUnsignedSmart(curb - cura);
				ibuffer.writeUnsignedSmart(curc - curb);
				lasta = cura;
				lastb = curb;
				pacc = lastc = curc;
			}
		}
	}

	public byte[] encodeRS2() {

		/* create the master buffer */
		OutputStream master = new OutputStream(0);

		/* create the temporary buffers */
		OutputStream face_mappings_buffer = new OutputStream(0);
		OutputStream vertex_flags_buffer = new OutputStream(0);
		OutputStream face_types_buffer = new OutputStream(0);
		OutputStream face_index_types_buffer = new OutputStream(0);
		OutputStream face_priorities_buffer = new OutputStream(0);
		OutputStream face_skins_buffer = new OutputStream(0);
		OutputStream vertex_skins_buffer = new OutputStream(0);
		OutputStream face_alphas_buffer = new OutputStream(0);
		OutputStream face_indices_buffer = new OutputStream(0);
		OutputStream face_materials_buffer = new OutputStream(0);
		OutputStream face_textures_buffer = new OutputStream(0);
		OutputStream face_colors_buffer = new OutputStream(0);
		OutputStream vertex_x_buffer = new OutputStream(0);
		OutputStream vertex_y_buffer = new OutputStream(0);
		OutputStream vertex_z_buffer = new OutputStream(0);
		OutputStream simple_textures_buffer = new OutputStream(0);
		OutputStream complex_textures_buffer = new OutputStream(0);
		OutputStream texture_scale_buffer = new OutputStream(0);
		OutputStream texture_rotation_buffer = new OutputStream(0);
		OutputStream texture_direction_buffer = new OutputStream(0);
		OutputStream texture_translation_buffer = new OutputStream(0);
		OutputStream particle_effects_buffer = new OutputStream(0);
		OutputStream footer_buffer = new OutputStream(0);
		OutputStream[] buffers = new OutputStream[] { face_mappings_buffer, vertex_flags_buffer, face_types_buffer, face_index_types_buffer, face_priorities_buffer, face_skins_buffer, vertex_skins_buffer, face_alphas_buffer, face_indices_buffer, face_materials_buffer, face_textures_buffer, face_colors_buffer, vertex_x_buffer, vertex_y_buffer, vertex_z_buffer, simple_textures_buffer, complex_textures_buffer, texture_scale_buffer, texture_rotation_buffer,
				texture_direction_buffer, texture_translation_buffer, particle_effects_buffer, footer_buffer };

		/* serialize the face mapping types */
		if (numTextures > 0) {
			for (int face = 0; face < numTextures; face++) {
				face_mappings_buffer.writeByte(faceMappings[face]);
			}
		}

		/* create the vertices variables */
		boolean hasVertexSkins = vertexSkins != null;
		boolean hasExtendedVertexSkins = false;

		/* serialize the vertices */
		int baseX = 0, baseY = 0, baseZ = 0;
		for (int vertex = 0; vertex < numVertices; vertex++) {
			int x = verticesX[vertex];
			int y = verticesY[vertex];
			int z = verticesZ[vertex];
			int xoff = x - baseX;
			int yoff = y - baseY;
			int zoff = z - baseZ;
			int flag = 0;
			if (xoff != 0) {
				vertex_x_buffer.writeUnsignedSmart(xoff);
				flag |= 0x1;
			}
			if (yoff != 0) {
				vertex_y_buffer.writeUnsignedSmart(yoff);
				flag |= 0x2;
			}
			if (zoff != 0) {
				vertex_z_buffer.writeUnsignedSmart(zoff);
				flag |= 0x4;
			}
			vertex_flags_buffer.writeByte(flag);
			verticesX[vertex] = baseX + xoff;
			verticesY[vertex] = baseY + yoff;
			verticesZ[vertex] = baseZ + zoff;
			baseX = verticesX[vertex];
			baseY = verticesY[vertex];
			baseZ = verticesZ[vertex];
			if (hasVertexSkins) {
				int weight = vertexSkins[vertex];
				if (weight >= -1 && weight <= 254) {
					vertex_skins_buffer.writeByte(weight);
				} else {
					vertex_skins_buffer.writeSmart(weight + 1);
					hasExtendedVertexSkins = true;
				}
			}
		}

		/* create the faces variables */
		boolean hasFaceTypes = faceTypes != null;
		boolean hasFacePriorities = facePriorities != null;
		boolean hasFaceAlpha = faceAlphas != null;
		boolean hasFaceSkins = faceSkins != null;
		boolean hasExtendedFaceSkins = false;
		boolean hasFaceTextures = faceMaterials != null;

		/* serialize the faces */
		for (int face = 0; face < numFaces; face++) {
			face_colors_buffer.writeShort(faceColors[face]);
			if (hasFaceTypes) {
				face_types_buffer.writeByte(faceTypes[face]);
			}
			if (hasFacePriorities) {
				face_priorities_buffer.writeByte(facePriorities[face]);
			}
			if (hasFaceAlpha) {
				face_alphas_buffer.writeByte(faceAlphas[face]);
			}
			if (hasFaceSkins) {
				int weight = faceSkins[face];
				if (weight >= -1 && weight <= 254) {
					face_skins_buffer.writeByte(weight);
				} else {
					face_skins_buffer.writeSmart(weight + 1);
					hasExtendedFaceSkins = true;
				}
			}
			if (hasFaceTextures) {
				face_materials_buffer.writeShort(faceMaterials[face] + 1);
			}
			if (faceTextures != null) {
				if (faceMaterials[face] != -1) {
					face_textures_buffer.writeByte(faceTextures[face] + 1);
				}
			}
		}

		/* serialize the face indices */
		encodeIndicesRS2(face_indices_buffer, face_index_types_buffer);

		/* serialize the texture mapping */
		encodeMappingRS2(simple_textures_buffer, complex_textures_buffer, texture_scale_buffer, texture_rotation_buffer, texture_direction_buffer, texture_translation_buffer);

		/* create the particle effects variables */
		boolean hasParticleEffects = emitters != null || effectors != null;

		/* serialize the particle effects */
		if (hasParticleEffects) {
			int numEmitters = emitters != null ? emitters.length : 0;
			if (numEmitters > 0) {
				for (int index = 0; index < numEmitters; index++) {
					EmissiveTriangle triangle = emitters[index];
					particle_effects_buffer.writeShort(triangle.getId());
					particle_effects_buffer.writeShort(triangle.getType());
				}
			}
			int numEffectors = effectors != null ? effectors.length : 0;
			if (numEffectors > 0) {
				for (int index = 0; index < numEffectors; index++) {
					EffectiveVertex vertex = effectors[index];
					particle_effects_buffer.writeShort(vertex.getEffector());
					particle_effects_buffer.writeShort(vertex.getVertex());
				}
			}
		}

		/* create the billboards variables */
		boolean hasBillboards = billboards != null;
		boolean hasExtendedBillboards = false;

		/* serialize the billboards */
		if (hasBillboards) {
			particle_effects_buffer.writeByte(billboards.length);
			for (int index = 0; index < billboards.length; index++) {
				FaceBillboard billboard = billboards[index];
				particle_effects_buffer.writeShort(billboard.getId());
				particle_effects_buffer.writeShort(billboard.getFace());
				int depth = billboard.getDepth();
				if (depth >= -1 && depth <= 254) { // 0 - 255
					particle_effects_buffer.writeByte(depth);
				} else {
					particle_effects_buffer.writeSmart(depth + 1);
					hasExtendedBillboards = true;
				}
				particle_effects_buffer.writeByte(billboard.getDistance());
			}
		}

		/* create the footer data */
		boolean hasVersion = version != DEFAULT_VERSION;
		if (hasVersion) {
			footer_buffer.writeByte(version);
		}
		footer_buffer.writeShort(numVertices);
		footer_buffer.writeShort(numFaces);
		footer_buffer.writeByte(numTextures);
		int flags = 0;
		if (hasFaceTypes) {
			flags |= 0x1;
		}
		if (hasParticleEffects) {
			flags |= 0x2;
		}
		if (hasBillboards) {
			flags |= 0x4;
		}
		if (hasVersion) {
			flags |= 0x8;
		}
		if (hasExtendedVertexSkins) {
			flags |= 0x10;
		}
		if (hasExtendedFaceSkins) {
			flags |= 0x20;
		}
		if (hasExtendedBillboards) {
			flags |= 0x40;
		}
		footer_buffer.writeByte(flags);
		footer_buffer.writeByte(hasFacePriorities ? -1 : priority);
		footer_buffer.writeBoolean(hasFaceAlpha);
		footer_buffer.writeBoolean(hasFaceSkins);
		footer_buffer.writeBoolean(hasFaceTextures);
		footer_buffer.writeBoolean(hasVertexSkins);
		footer_buffer.writeShort(vertex_x_buffer.length());
		footer_buffer.writeShort(vertex_y_buffer.length());
		footer_buffer.writeShort(vertex_z_buffer.length());
		footer_buffer.writeShort(face_indices_buffer.length());
		footer_buffer.writeShort(face_textures_buffer.length());
		if (hasExtendedVertexSkins) {
			footer_buffer.writeShort(vertex_skins_buffer.length());
		}
		if (hasExtendedFaceSkins) {
			footer_buffer.writeShort(face_skins_buffer.length());
		}
		for (int i = 0; i < buffers.length; i++) {
			OutputStream buffer = buffers[i];
			master.writeBytes(buffer.getDataTrimmed());
		}
		master.writeByte(255);
		master.writeByte(255);
		return master.getDataTrimmed();
	}

	/**
	 * Encodes the texture mappings.
	 *
	 * @param simple
	 *            the simple mapping buffer.
	 * @param complex
	 *            the complex mapping buffer.
	 * @param scale
	 *            the scale buffer.
	 * @param rotation
	 *            the rotation buffer.
	 * @param direction
	 *            the direction buffer.
	 * @param translation
	 *            the translation buffer.
	 */
	public void encodeMappingRS2(OutputStream simple, OutputStream complex, OutputStream scale, OutputStream rotation, OutputStream direction, OutputStream translation) {
		for (int face = 0; face < numTextures; face++) {
			int type = faceMappings[face] & 0xff;
			if (type == 0) {
				simple.writeShort(textureMappingP[face]);
				simple.writeShort(textureMappingM[face]);
				simple.writeShort(textureMappingN[face]);
			} else {
				int scaleX = textureScaleX[face];
				int scaleY = textureScaleY[face];
				int scaleZ = textureScaleZ[face];
				if (type == 1) {
					complex.writeShort(textureMappingP[face]);
					complex.writeShort(textureMappingM[face]);
					complex.writeShort(textureMappingN[face]);
					if (version >= 15 || scaleX > 0xffff || scaleZ > 0xffff) {
						if (version < 15) {
							version = 15;
						}
						scale.write24BitInt(scaleX);
						scale.write24BitInt(scaleY);
						scale.write24BitInt(scaleZ);
					} else {
						scale.writeShort(scaleX);
						if (version < 14 && scaleY > 0xffff) {
							version = 14;
						}
						if (version < 14) {
							scale.writeShort(scaleY);
						} else {
							scale.write24BitInt(scaleY);
						}
						scale.writeShort(scaleZ);
					}
					rotation.writeByte(textureRotation[face]);
					direction.writeByte(textureDirection[face]);
					translation.writeByte(textureSpeed[face]);
				} else if (type == 2) {
					complex.writeShort(textureMappingP[face]);
					complex.writeShort(textureMappingM[face]);
					complex.writeShort(textureMappingN[face]);
					if (version >= 15 || scaleX > 0xffff || scaleZ > 0xffff) {
						if (version < 15) {
							version = 15;
						}
						scale.write24BitInt(scaleX);
						scale.write24BitInt(scaleY);
						scale.write24BitInt(scaleZ);
					} else {
						scale.writeShort(scaleX);
						if (version < 14 && scaleY > 0xffff) {
							version = 14;
						}
						if (version < 14) {
							scale.writeShort(scaleY);
						} else {
							scale.write24BitInt(scaleY);
						}
						scale.writeShort(scaleZ);
					}
					rotation.writeByte(textureRotation[face]);
					direction.writeByte(textureDirection[face]);
					translation.writeByte(textureSpeed[face]);
					translation.writeByte(textureTransU[face]);
					translation.writeByte(textureTransV[face]);
				} else if (type == 3) {
					complex.writeShort(textureMappingP[face]);
					complex.writeShort(textureMappingM[face]);
					complex.writeShort(textureMappingN[face]);


					if (version >= 15 || scaleX > 0xffff || scaleZ > 0xffff) {
						if (version < 15) {
							version = 15;
						}
						scale.write24BitInt(scaleX);
						scale.write24BitInt(scaleY);
						scale.write24BitInt(scaleZ);
					} else {
						scale.writeShort(scaleX);
						if (version < 14 && scaleY > 0xffff) {
							version = 14;
						}
						if (version < 14) {
							scale.writeShort(scaleY);
						} else {
							scale.write24BitInt(scaleY);
						}
						scale.writeShort(scaleZ);
					}
					rotation.writeByte(textureRotation[face]);
					direction.writeByte(textureDirection[face]);
					translation.writeByte(textureSpeed[face]);
				}
			}
		}
	}


	/**
	 * Encodes the indices list.
	 *
	 * @param ibuffer
	 *            the indices buffer.
	 * @param tbuffer
	 *            the indices compression type buffer.
	 */
	public void encodeIndicesRS2(OutputStream ibuffer, OutputStream tbuffer) {
		int lasta = 0;
		int lastb = 0;
		int lastc = 0;
		int pacc = 0;
		for (int fndex = 0; fndex < numFaces; fndex++) {
			int cura = faceIndicesA[fndex];
			int curb = faceIndicesB[fndex];
			int curc = faceIndicesC[fndex];
			if (cura == lastb && curb == lasta && curc != lastc) {
				tbuffer.writeByte(4);
				ibuffer.writeUnsignedSmart(curc - pacc);
				int back = lasta;
				lasta = lastb;
				lastb = back;
				pacc = lastc = curc;
			} else if (cura == lastc && curb == lastb && curc != lastc) {
				tbuffer.writeByte(3);
				ibuffer.writeUnsignedSmart(curc - pacc);
				lasta = lastc;
				pacc = lastc = curc;
			} else if (cura == lasta && curb == lastc && curc != lastc) {
				tbuffer.writeByte(2);
				ibuffer.writeUnsignedSmart(curc - pacc);
				lastb = lastc;
				pacc = lastc = curc;
			} else {
				tbuffer.writeByte(1);
				ibuffer.writeUnsignedSmart(cura - pacc);
				ibuffer.writeUnsignedSmart(curb - cura);
				ibuffer.writeUnsignedSmart(curc - curb);
				lasta = cura;
				lastb = curb;
				pacc = lastc = curc;
			}
		}
	}

	public void computeTextureCoordinates() {
		int[] indices = new int[numFaces];
		for (int i = 0; i < numFaces; i++) {
			indices[i] = i;
		}
		Particle particle = getParticle(indices, numFaces);

		float[] fs = new float[2];//the fuck is this

		texturedUCoordinates = new float[numFaces][];
		texturedVCoordinates = new float[numFaces][];

		for (int i = 0; i < numFaces; i++) {
			int faceMaterialId = faceMaterials[i];
			int faceTextureId = faceTextures[i];
			if (faceMaterialId != -1) {
				float[] uCoordinates = new float[3];
				float[] vCoordinates = new float[3];
				if (faceTextureId == -1) {
					uCoordinates[0] = 0.0F;
					vCoordinates[0] = 1.0F;
					uCoordinates[1] = 1.0F;
					vCoordinates[1] = 1.0F;
					uCoordinates[2] = 0.0F;
					vCoordinates[2] = 0.0F;
				} else {
					faceTextureId &= 0xff;
					byte i_28_ = faceMappings[faceTextureId];
					if (i_28_ == 0) {
						short i_29_ = faceIndicesA[i];
						short i_30_ = faceIndicesB[i];
						short i_31_ = faceIndicesC[i];
						short i_32_ = textureMappingP[faceTextureId];
						short i_33_ = textureMappingM[faceTextureId];
						short i_34_ = textureMappingN[faceTextureId];
						float f = (float) (this.verticesX[i_32_]);
						float f_35_ = (float) (this.verticesY[i_32_]);
						float f_36_ = (float) (this.verticesZ[i_32_]);
						float f_37_ = ((float) (this.verticesX[i_33_]) - f);
						float f_38_ = ((float) (this.verticesY[i_33_]) - f_35_);
						float f_39_ = ((float) (this.verticesZ[i_33_]) - f_36_);
						float f_40_ = ((float) (this.verticesX[i_34_]) - f);
						float f_41_ = ((float) (this.verticesY[i_34_]) - f_35_);
						float f_42_ = ((float) (this.verticesZ[i_34_]) - f_36_);
						float f_43_ = ((float) (this.verticesX[i_29_]) - f);
						float f_44_ = ((float) (this.verticesY[i_29_]) - f_35_);
						float f_45_ = ((float) (this.verticesZ[i_29_]) - f_36_);
						float f_46_ = ((float) (this.verticesX[i_30_]) - f);
						float f_47_ = ((float) (this.verticesY[i_30_]) - f_35_);
						float f_48_ = ((float) (this.verticesZ[i_30_]) - f_36_);
						float f_49_ = ((float) (this.verticesX[i_31_]) - f);
						float f_50_ = ((float) (this.verticesY[i_31_]) - f_35_);
						float f_51_ = ((float) (this.verticesZ[i_31_]) - f_36_);
						float f_52_ = f_38_ * f_42_ - f_39_ * f_41_;
						float f_53_ = f_39_ * f_40_ - f_37_ * f_42_;
						float f_54_ = f_37_ * f_41_ - f_38_ * f_40_;
						float f_55_ = f_41_ * f_54_ - f_42_ * f_53_;
						float f_56_ = f_42_ * f_52_ - f_40_ * f_54_;
						float f_57_ = f_40_ * f_53_ - f_41_ * f_52_;
						float f_58_ = 1.0F / (f_55_ * f_37_ + f_56_ * f_38_ + f_57_ * f_39_);
						uCoordinates[0] = (f_55_ * f_43_ + f_56_ * f_44_ + f_57_ * f_45_) * f_58_;
						uCoordinates[1] = (f_55_ * f_46_ + f_56_ * f_47_ + f_57_ * f_48_) * f_58_;
						uCoordinates[2] = (f_55_ * f_49_ + f_56_ * f_50_ + f_57_ * f_51_) * f_58_;
						f_55_ = f_38_ * f_54_ - f_39_ * f_53_;
						f_56_ = f_39_ * f_52_ - f_37_ * f_54_;
						f_57_ = f_37_ * f_53_ - f_38_ * f_52_;
						f_58_ = 1.0F / (f_55_ * f_40_ + f_56_ * f_41_ + f_57_ * f_42_);
						vCoordinates[0] = (f_55_ * f_43_ + f_56_ * f_44_ + f_57_ * f_45_) * f_58_;
						vCoordinates[1] = (f_55_ * f_46_ + f_56_ * f_47_ + f_57_ * f_48_) * f_58_;
						vCoordinates[2] = (f_55_ * f_49_ + f_56_ * f_50_ + f_57_ * f_51_) * f_58_;
					} else {
						short i_59_ = faceIndicesA[i];
						short i_60_ = faceIndicesB[i];
						short i_61_ = faceIndicesC[i];
						int i_62_ = particle.verticesX[faceTextureId];
						int i_63_ = particle.verticesY[faceTextureId];
						int i_64_ = particle.verticesZ[faceTextureId];
						float[] fs_65_ = particle.coordinates[faceTextureId];
						byte i_66_ = textureDirection[faceTextureId];
						float f = (float) textureSpeed[faceTextureId] / 256.0F;
						if (i_28_ == 1) {
							float f_67_ = ((float) textureScaleZ[faceTextureId] / 1024.0F);
							method6904(this.verticesX[i_59_], this.verticesY[i_59_], this.verticesZ[i_59_], i_62_, i_63_, i_64_, fs_65_, f_67_, i_66_, f, fs);
							uCoordinates[0] = fs[0];
							vCoordinates[0] = fs[1];
							method6904(this.verticesX[i_60_], this.verticesY[i_60_], this.verticesZ[i_60_], i_62_, i_63_, i_64_, fs_65_, f_67_, i_66_, f, fs);
							uCoordinates[1] = fs[0];
							vCoordinates[1] = fs[1];
							method6904(this.verticesX[i_61_], this.verticesY[i_61_], this.verticesZ[i_61_], i_62_, i_63_, i_64_, fs_65_, f_67_, i_66_, f, fs);
							uCoordinates[2] = fs[0];
							vCoordinates[2] = fs[1];
							float f_68_ = f_67_ / 2.0F;
							if ((i_66_ & 0x1) == 0) {
								if (uCoordinates[1] - uCoordinates[0] > f_68_) {
									uCoordinates[1] -= f_67_;
								} else if (uCoordinates[0] - uCoordinates[1] > f_68_) {
									uCoordinates[1] += f_67_;
								}
								if (uCoordinates[2] - uCoordinates[0] > f_68_) {
									uCoordinates[2] -= f_67_;
								} else if (uCoordinates[0] - uCoordinates[2] > f_68_) {
									uCoordinates[2] += f_67_;
								}
							} else {
								if (vCoordinates[1] - vCoordinates[0] > f_68_) {
									vCoordinates[1] -= f_67_;
								} else if (vCoordinates[0] - vCoordinates[1] > f_68_) {
									vCoordinates[1] += f_67_;
								}
								if (vCoordinates[2] - vCoordinates[0] > f_68_) {
									vCoordinates[2] -= f_67_;
								} else if (vCoordinates[0] - vCoordinates[2] > f_68_) {
									vCoordinates[2] += f_67_;
								}
							}
						} else if (i_28_ == 2) {
							float f_69_ = ((float) textureTransU[faceTextureId] / 256.0F);
							float f_70_ = ((float) textureTransV[faceTextureId] / 256.0F);
							int i_71_ = (this.verticesX[i_60_] - (this.verticesX[i_59_]));
							int i_72_ = (this.verticesY[i_60_] - (this.verticesY[i_59_]));
							int i_73_ = (this.verticesZ[i_60_] - (this.verticesZ[i_59_]));
							int i_74_ = (this.verticesX[i_61_] - (this.verticesX[i_59_]));
							int i_75_ = (this.verticesY[i_61_] - (this.verticesY[i_59_]));
							int i_76_ = (this.verticesZ[i_61_] - (this.verticesZ[i_59_]));
							int i_77_ = i_72_ * i_76_ - i_75_ * i_73_;
							int i_78_ = i_73_ * i_74_ - i_76_ * i_71_;
							int i_79_ = i_71_ * i_75_ - i_74_ * i_72_;
							float f_80_ = (64.0F / (float) textureScaleX[faceTextureId]);
							float f_81_ = (64.0F / (float) textureScaleY[faceTextureId]);
							float f_82_ = (64.0F / (float) textureScaleZ[faceTextureId]);
							float f_83_ = (((float) i_77_ * fs_65_[0] + (float) i_78_ * fs_65_[1] + (float) i_79_ * fs_65_[2]) / f_80_);
							float f_84_ = (((float) i_77_ * fs_65_[3] + (float) i_78_ * fs_65_[4] + (float) i_79_ * fs_65_[5]) / f_81_);
							float f_85_ = (((float) i_77_ * fs_65_[6] + (float) i_78_ * fs_65_[7] + (float) i_79_ * fs_65_[8]) / f_82_);
							int i_86_ = method6936(f_83_, f_84_, f_85_);
							method6939(this.verticesX[i_59_], this.verticesY[i_59_], this.verticesZ[i_59_], i_62_, i_63_, i_64_, i_86_, fs_65_, i_66_, f, f_69_, f_70_, fs);
							uCoordinates[0] = fs[0];
							vCoordinates[0] = fs[1];
							method6939(this.verticesX[i_60_], this.verticesY[i_60_], this.verticesZ[i_60_], i_62_, i_63_, i_64_, i_86_, fs_65_, i_66_, f, f_69_, f_70_, fs);
							uCoordinates[1] = fs[0];
							vCoordinates[1] = fs[1];
							method6939(this.verticesX[i_61_], this.verticesY[i_61_], this.verticesZ[i_61_], i_62_, i_63_, i_64_, i_86_, fs_65_, i_66_, f, f_69_, f_70_, fs);
							uCoordinates[2] = fs[0];
							vCoordinates[2] = fs[1];
						} else if (i_28_ == 3) {
							method6903(this.verticesX[i_59_], this.verticesY[i_59_], this.verticesZ[i_59_], i_62_, i_63_, i_64_, fs_65_, i_66_, f, fs);
							uCoordinates[0] = fs[0];
							vCoordinates[0] = fs[1];
							method6903(this.verticesX[i_60_], this.verticesY[i_60_], this.verticesZ[i_60_], i_62_, i_63_, i_64_, fs_65_, i_66_, f, fs);
							uCoordinates[1] = fs[0];
							vCoordinates[1] = fs[1];
							method6903(this.verticesX[i_61_], this.verticesY[i_61_], this.verticesZ[i_61_], i_62_, i_63_, i_64_, fs_65_, i_66_, f, fs);
							uCoordinates[2] = fs[0];
							vCoordinates[2] = fs[1];
							if ((i_66_ & 0x1) == 0) {
								if (uCoordinates[1] - uCoordinates[0] > 0.5F) {
									uCoordinates[1]--;
								} else if (uCoordinates[0] - uCoordinates[1] > 0.5F) {
									uCoordinates[1]++;
								}
								if (uCoordinates[2] - uCoordinates[0] > 0.5F) {
									uCoordinates[2]--;
								} else if (uCoordinates[0] - uCoordinates[2] > 0.5F) {
									uCoordinates[2]++;
								}
							} else {
								if (vCoordinates[1] - vCoordinates[0] > 0.5F) {
									vCoordinates[1]--;
								} else if (vCoordinates[0] - vCoordinates[1] > 0.5F) {
									vCoordinates[1]++;
								}
								if (vCoordinates[2] - vCoordinates[0] > 0.5F) {
									vCoordinates[2]--;
								} else if (vCoordinates[0] - vCoordinates[2] > 0.5F) {
									vCoordinates[2]++;
								}
							}
						}
					}
				}
				texturedUCoordinates[i] = uCoordinates;
				texturedVCoordinates[i] = vCoordinates;
			}
		}
	}

	private Particle getParticle(int[] is, int i) {
		int[] verticesX = null;
		int[] verticesY = null;
		int[] verticesZ = null;
		float[][] coordinates = null;
		if (faceTextures != null) {
			int i_33_ = numTextures;
			int[] is_34_ = new int[i_33_];
			int[] is_35_ = new int[i_33_];
			int[] is_36_ = new int[i_33_];
			int[] is_37_ = new int[i_33_];
			int[] is_38_ = new int[i_33_];
			int[] is_39_ = new int[i_33_];
			for (int i_40_ = 0; i_40_ < i_33_; i_40_++) {
				is_34_[i_40_] = 2147483647;
				is_35_[i_40_] = -2147483647;
				is_36_[i_40_] = 2147483647;
				is_37_[i_40_] = -2147483647;
				is_38_[i_40_] = 2147483647;
				is_39_[i_40_] = -2147483647;
			}
			for (int i_41_ = 0; i_41_ < i; i_41_++) {
				int i_42_ = is[i_41_];
				if (faceTextures[i_42_] != -1) {
					int i_43_ = faceTextures[i_42_] & 0xff;
					for (int i_44_ = 0; i_44_ < 3; i_44_++) {
						short i_45_;
						if (i_44_ == 0) {
							i_45_ = faceIndicesA[i_42_];
						} else if (i_44_ == 1) {
							i_45_ = faceIndicesB[i_42_];
						} else {
							i_45_ = faceIndicesC[i_42_];
						}
						int i_46_ = this.verticesX[i_45_];
						int i_47_ = this.verticesY[i_45_];
						int i_48_ = this.verticesZ[i_45_];
						if (i_46_ < is_34_[i_43_]) {
							is_34_[i_43_] = i_46_;
						}
						if (i_46_ > is_35_[i_43_]) {
							is_35_[i_43_] = i_46_;
						}
						if (i_47_ < is_36_[i_43_]) {
							is_36_[i_43_] = i_47_;
						}
						if (i_47_ > is_37_[i_43_]) {
							is_37_[i_43_] = i_47_;
						}
						if (i_48_ < is_38_[i_43_]) {
							is_38_[i_43_] = i_48_;
						}
						if (i_48_ > is_39_[i_43_]) {
							is_39_[i_43_] = i_48_;
						}
					}
				}
			}
			verticesX = new int[i_33_];
			verticesY = new int[i_33_];
			verticesZ = new int[i_33_];
			coordinates = new float[i_33_][];
			for (int i_49_ = 0; i_49_ < i_33_; i_49_++) {
				byte i_50_ = faceMappings[i_49_];
				if (i_50_ > 0) {
					verticesX[i_49_] = (is_34_[i_49_] + is_35_[i_49_]) / 2;
					verticesY[i_49_] = (is_36_[i_49_] + is_37_[i_49_]) / 2;
					verticesZ[i_49_] = (is_38_[i_49_] + is_39_[i_49_]) / 2;
					float f;
					float f_51_;
					float f_52_;
					if (i_50_ == 1) {
						int i_53_ = textureScaleX[i_49_];
						if (i_53_ == 0) {
							f = 1.0F;
							f_52_ = 1.0F;
						} else if (i_53_ > 0) {
							f = 1.0F;
							f_52_ = (float) i_53_ / 1024.0F;
						} else {
							f_52_ = 1.0F;
							f = (float) -i_53_ / 1024.0F;
						}
						f_51_ = 64.0F / (float) textureScaleY[i_49_];
					} else if (i_50_ == 2) {
						f = 64.0F / (float) textureScaleX[i_49_];
						f_51_ = 64.0F / (float) textureScaleY[i_49_];
						f_52_ = 64.0F / (float) textureScaleZ[i_49_];
					} else {
						f = (float) textureScaleX[i_49_] / 1024.0F;
						f_51_ = (float) textureScaleY[i_49_] / 1024.0F;
						f_52_ = (float) textureScaleZ[i_49_] / 1024.0F;
					}
					coordinates[i_49_] = method6907(textureMappingP[i_49_], textureMappingM[i_49_], textureMappingN[i_49_], textureRotation[i_49_] & 0xff, f, f_51_, f_52_);
				}
			}
		}
		return new Particle(verticesX, verticesY, verticesZ, coordinates);
	}

	private float[] method6907(int i, int i_54_, int i_55_, int i_56_, float f, float f_57_, float f_58_) {
		float[] fs = new float[9];
		float[] fs_59_ = new float[9];
		float f_60_ = (float) Math.cos((float) i_56_ * 0.024543693F);
		float f_61_ = (float) Math.sin((float) i_56_ * 0.024543693F);
		float f_62_;
		fs[0] = f_60_;
		fs[1] = 0.0F;
		fs[2] = f_61_;
		fs[3] = 0.0F;
		fs[4] = 1.0F;
		fs[5] = 0.0F;
		fs[6] = -f_61_;
		fs[7] = 0.0F;
		fs[8] = f_60_;
		float[] fs_63_ = new float[9];
		float f_64_ = 1.0F;
		float f_65_ = 0.0F;
		f_60_ = (float) i_54_ / 32767.0F;
		f_61_ = -(float) Math.sqrt(1.0F - f_60_ * f_60_);
		f_62_ = 1.0F - f_60_;
		float f_66_ = (float) Math.sqrt(i * i + i_55_ * i_55_);
		if (f_66_ == 0.0F && f_60_ == 0.0F) {
			fs_59_ = fs;
		} else {
			if (f_66_ != 0.0F) {
				f_64_ = (float) -i_55_ / f_66_;
				f_65_ = (float) i / f_66_;
			}
			fs_63_[0] = f_60_ + f_64_ * f_64_ * f_62_;
			fs_63_[1] = f_65_ * f_61_;
			fs_63_[2] = f_65_ * f_64_ * f_62_;
			fs_63_[3] = -f_65_ * f_61_;
			fs_63_[4] = f_60_;
			fs_63_[5] = f_64_ * f_61_;
			fs_63_[6] = f_64_ * f_65_ * f_62_;
			fs_63_[7] = -f_64_ * f_61_;
			fs_63_[8] = f_60_ + f_65_ * f_65_ * f_62_;
			fs_59_[0] = fs[0] * fs_63_[0] + fs[1] * fs_63_[3] + fs[2] * fs_63_[6];
			fs_59_[1] = fs[0] * fs_63_[1] + fs[1] * fs_63_[4] + fs[2] * fs_63_[7];
			fs_59_[2] = fs[0] * fs_63_[2] + fs[1] * fs_63_[5] + fs[2] * fs_63_[8];
			fs_59_[3] = fs[3] * fs_63_[0] + fs[4] * fs_63_[3] + fs[5] * fs_63_[6];
			fs_59_[4] = fs[3] * fs_63_[1] + fs[4] * fs_63_[4] + fs[5] * fs_63_[7];
			fs_59_[5] = fs[3] * fs_63_[2] + fs[4] * fs_63_[5] + fs[5] * fs_63_[8];
			fs_59_[6] = fs[6] * fs_63_[0] + fs[7] * fs_63_[3] + fs[8] * fs_63_[6];
			fs_59_[7] = fs[6] * fs_63_[1] + fs[7] * fs_63_[4] + fs[8] * fs_63_[7];
			fs_59_[8] = fs[6] * fs_63_[2] + fs[7] * fs_63_[5] + fs[8] * fs_63_[8];
		}
		fs_59_[0] *= f;
		fs_59_[1] *= f;
		fs_59_[2] *= f;
		fs_59_[3] *= f_57_;
		fs_59_[4] *= f_57_;
		fs_59_[5] *= f_57_;
		fs_59_[6] *= f_58_;
		fs_59_[7] *= f_58_;
		fs_59_[8] *= f_58_;
		return fs_59_;
	}

	private int method6936(float f, float f_239_, float f_240_) {
		float f_241_ = f < 0.0F ? -f : f;
		float f_242_ = f_239_ < 0.0F ? -f_239_ : f_239_;
		float f_243_ = f_240_ < 0.0F ? -f_240_ : f_240_;
		if (f_242_ > f_241_ && f_242_ > f_243_) {
			if (f_239_ > 0.0F) {
				return 0;
			}
			return 1;
		}
		if (f_243_ > f_241_ && f_243_ > f_242_) {
			if (f_240_ > 0.0F) {
				return 2;
			}
			return 3;
		}
		if (f > 0.0F) {
			return 4;
		}
		return 5;
	}

	private void method6939(int i, int i_271_, int i_272_, int i_273_, int i_274_, int i_275_, int i_276_, float[] fs, int i_277_, float f, float f_278_, float f_279_, float[] fs_280_) {
		i -= i_273_;
		i_271_ -= i_274_;
		i_272_ -= i_275_;
		float f_281_ = ((float) i * fs[0] + (float) i_271_ * fs[1] + (float) i_272_ * fs[2]);
		float f_282_ = ((float) i * fs[3] + (float) i_271_ * fs[4] + (float) i_272_ * fs[5]);
		float f_283_ = ((float) i * fs[6] + (float) i_271_ * fs[7] + (float) i_272_ * fs[8]);
		float f_284_;
		float f_285_;
		if (i_276_ == 0) {
			f_284_ = f_281_ + f + 0.5F;
			f_285_ = -f_283_ + f_279_ + 0.5F;
		} else if (i_276_ == 1) {
			f_284_ = f_281_ + f + 0.5F;
			f_285_ = f_283_ + f_279_ + 0.5F;
		} else if (i_276_ == 2) {
			f_284_ = -f_281_ + f + 0.5F;
			f_285_ = -f_282_ + f_278_ + 0.5F;
		} else if (i_276_ == 3) {
			f_284_ = f_281_ + f + 0.5F;
			f_285_ = -f_282_ + f_278_ + 0.5F;
		} else if (i_276_ == 4) {
			f_284_ = f_283_ + f_279_ + 0.5F;
			f_285_ = -f_282_ + f_278_ + 0.5F;
		} else {
			f_284_ = -f_283_ + f_279_ + 0.5F;
			f_285_ = -f_282_ + f_278_ + 0.5F;
		}
		if (i_277_ == 1) {
			float f_286_ = f_284_;
			f_284_ = -f_285_;
			f_285_ = f_286_;
		} else if (i_277_ == 2) {
			f_284_ = -f_284_;
			f_285_ = -f_285_;
		} else if (i_277_ == 3) {
			float f_287_ = f_284_;
			f_284_ = f_285_;
			f_285_ = -f_287_;
		}
		fs_280_[0] = f_284_;
		fs_280_[1] = f_285_;
	}

	private void method6903(int i, int i_0_, int i_1_, int i_2_, int i_3_, int i_4_, float[] fs, int i_5_, float f, float[] fs_6_) {
		i -= i_2_;
		i_0_ -= i_3_;
		i_1_ -= i_4_;
		float f_7_ = (float) i * fs[0] + (float) i_0_ * fs[1] + (float) i_1_ * fs[2];
		float f_8_ = (float) i * fs[3] + (float) i_0_ * fs[4] + (float) i_1_ * fs[5];
		float f_9_ = (float) i * fs[6] + (float) i_0_ * fs[7] + (float) i_1_ * fs[8];
		float f_10_ = (float) Math.sqrt((double) (f_7_ * f_7_ + f_8_ * f_8_ + f_9_ * f_9_));
		float f_11_ = ((float) Math.atan2((double) f_7_, (double) f_9_) / 6.2831855F + 0.5F);
		float f_12_ = ((float) Math.asin((double) (f_8_ / f_10_)) / 3.1415927F + 0.5F + f);
		if (i_5_ == 1) {
			float f_13_ = f_11_;
			f_11_ = -f_12_;
			f_12_ = f_13_;
		} else if (i_5_ == 2) {
			f_11_ = -f_11_;
			f_12_ = -f_12_;
		} else if (i_5_ == 3) {
			float f_14_ = f_11_;
			f_11_ = f_12_;
			f_12_ = -f_14_;
		}
		fs_6_[0] = f_11_;
		fs_6_[1] = f_12_;
	}

	private void method6904(int i, int i_15_, int i_16_, int i_17_, int i_18_, int i_19_, float[] fs, float f, int i_20_, float f_21_, float[] fs_22_) {
		i -= i_17_;
		i_15_ -= i_18_;
		i_16_ -= i_19_;
		float f_23_ = ((float) i * fs[0] + (float) i_15_ * fs[1] + (float) i_16_ * fs[2]);
		float f_24_ = ((float) i * fs[3] + (float) i_15_ * fs[4] + (float) i_16_ * fs[5]);
		float f_25_ = ((float) i * fs[6] + (float) i_15_ * fs[7] + (float) i_16_ * fs[8]);
		float f_26_ = ((float) Math.atan2((double) f_23_, (double) f_25_) / 6.2831855F + 0.5F);
		if (f != 1.0F) {
			f_26_ *= f;
		}
		float f_27_ = f_24_ + 0.5F + f_21_;
		if (i_20_ == 1) {
			float f_28_ = f_26_;
			f_26_ = -f_27_;
			f_27_ = f_28_;
		} else if (i_20_ == 2) {
			f_26_ = -f_26_;
			f_27_ = -f_27_;
		} else if (i_20_ == 3) {
			float f_29_ = f_26_;
			f_26_ = f_27_;
			f_27_ = -f_29_;
		}
		fs_22_[0] = f_26_;
		fs_22_[1] = f_27_;
	}

	@Override
	public String toString() {
		return id + "";
	}

	public static Map<Field, Integer> fieldPriorities;

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
