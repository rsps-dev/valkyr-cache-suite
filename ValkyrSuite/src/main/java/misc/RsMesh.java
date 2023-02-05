/**
 * 
 */
package misc;

import java.util.Arrays;

import store.codec.model.EmissiveTriangle;
import store.io.Stream;
import store.io.impl.InputStream;
import store.io.impl.OutputStream;

/**
 * Created at: Apr 15, 2017 5:43:53 PM
 * 
 * @author Walied-Yassen (gayboi)
 * @author ReverendDread - Updated decoder to RS3
 */
public class RsMesh extends Mesh {

	/**
	 * Constructs a new {@link RsMesh} object instance.
	 *
	 * @param data
	 *            the raw mesh file data.
	 */
	public RsMesh(int id) {
		this.id = id;
	}

	/**
     * Constructs a new {@link RsMesh} object instance.
     * 
     * @param data
     *            the raw mesh file data.
     */
    public RsMesh(byte[] data) {
        super(data);
    }

    /**
     * Constructs a new {@link RsMesh} object instance.
     * 
     * @param numVertices
     *            the amount of vertices within this mesh.
     * @param numFaces
     *            the amount of faces within this mesh.
     * @param numTextures
     *            the amount of textures within this mesh.
     */
    public RsMesh(int numVertices, int numFaces, int numTextures) {
        super(numVertices, numFaces, numTextures);
    }

	/**
	 * 
	 */
	public RsMesh() {
		// TODO Auto-generated constructor stub
	}

	/*
     * (non-Javadoc)
     * 
     * @see com.wycody.engine.media.model.Mesh#load(byte[])
     */
    @Override
    public void decode(byte[] data) {
    	
    	InputStream first = new InputStream(data);
        InputStream second = new InputStream(data);
        InputStream third = new InputStream(data);
        InputStream fourth = new InputStream(data);
        InputStream fifth = new InputStream(data);
        InputStream sixth = new InputStream(data);
        InputStream seventh = new InputStream(data);
        
        int modelType = first.readUnsignedByte();
        if (modelType != 1) {
        	throw new IllegalArgumentException("Invalid model identifer: " + modelType);
        } else {
	        first.readByte();
	        version = first.readUnsignedByte();
	        first.setPosition(data.length - 26);
	        numVertices = first.readUnsignedShort();
	        numFaces = first.readUnsignedShort();
	        numTextures = first.readUnsignedShort();
	        int footerFlags = first.readUnsignedByte();
	        boolean hasFaceTypes = (footerFlags & 0x1) == 1;
	        boolean hasParticleEffects = (footerFlags & 0x2) == 2;
	        boolean hasBillboards = (footerFlags & 0x4) == 4;
	        boolean hasExtendedVertexSkins = (footerFlags & 0x10) == 16;
	        boolean hasExtendedTriangleSkins = (footerFlags & 0x20) == 32;
	        boolean hasExtendedBillboards = (footerFlags & 0x40) == 64;
	        boolean hasTextureUV = (footerFlags & 0x80) == 128;

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

	        if (!hasExtendedVertexSkins) {
	        	if (hasVertexSkins == 1)
	        		numVertexSkins = numVertices;
	        	else
	        		numVertexSkins = 0;
	        }
	        if (hasExtendedTriangleSkins) {
	        	if (hasFaceSkins == 1)
	        		numFaceSkins = numFaces;
	        	else
	        		numFaceSkins = 0;
	        }

	        int simpleTextureFaceCount = 0;
	        int complexTextureFaceCount = 0;
	        int cubeTextureFaceCount = 0;
	        if (numTextures > 0) {
	            faceMappings = new byte[numTextures];
	            first.setPosition(3);
	            for (int tri = 0; tri < numTextures; tri++) {
	                byte type = faceMappings[tri] = (byte) first.readByte();
	                if (type == 0) {
	                    simpleTextureFaceCount++;
	                }
	                if (type >= 1 && type <= 3) {
	                    complexTextureFaceCount++;
	                }
	                if (type == 2) {
	                    cubeTextureFaceCount++;
	                }
	            }
	        }
	        int offset = 3 + numTextures;
	        int vertexFlagsOffset = offset;
	        offset += numVertices;
	        int faceTypesOffset = offset;
	        if (hasFaceTypes) {
	            offset += numFaces;
	        }
	        int facesCompressTypeOffset = offset;
	        offset += numFaces;
	        int facePrioritiesOffset = offset;
	        if (modelPriority == 255) {
	            offset += numFaces;
	        }
	        int faceSkinsOffset = offset;
	        offset += numFaceSkins;
	        int vertexSkinsOffset = offset;
	        offset += numVertexSkins;
	        int faceAlphasOffset = offset;
	        if (hasFaceAlpha == 1) {
	            offset += numFaces;
	        }
	        int faceIndicesOffset = offset;
	        offset += faceIndices;
	        int faceMaterialsOffset = offset;
	        if (hasFaceTextures == 1) {
	            offset += numFaces * 2;
	        }
	        int faceTextureIndicesOffset = offset;
	        offset += textureIndices;
	        int faceColorsOffset = offset;
	        offset += numFaces * 2;
	        int vertexXOffsetOffset = offset;
	        offset += modelVerticesX;
	        int vertexYOffsetOffset = offset;
	        offset += modelVerticesY;
	        int vertexZOffsetOffset = offset;
	        offset += modelVerticesZ;
	        int simpleTexturesOffset = offset;
	        offset += simpleTextureFaceCount * 6;
	        int complexTexturesOffset = offset;
	        offset += complexTextureFaceCount * 6;
	        int textureBytes = 6;
	        if (version == 14) {
	            textureBytes = 7;
	        } else if (version >= 15) {
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
	        int particleEffectsOffset = offset;
			int face_uv_index_offset = data.length;
			int vertex_uv_offset = data.length;
			int tex_coord_u_offset = data.length;
			int tex_coord_v_offset = data.length;
	        if (hasTextureUV) {
	        	InputStream uvBuffer = new InputStream(data);
	        	uvBuffer.setPosition(data.length - 26);
	        	uvBuffer.setPosition(uvBuffer.getPosition() - data[uvBuffer.getPosition() - 1]);
	        	textureUVCoordCount = uvBuffer.readUnsignedShort();
	        	int extras_data_size = uvBuffer.readUnsignedShort();
	        	int uv_index_data_size = uvBuffer.readUnsignedShort();
				face_uv_index_offset = particleEffectsOffset + extras_data_size;
				vertex_uv_offset = face_uv_index_offset + uv_index_data_size;
				tex_coord_u_offset = vertex_uv_offset + numVertices;
				tex_coord_v_offset = tex_coord_u_offset + textureUVCoordCount * 2;
	        }
	        verticesX = new int[numVertices];
	        verticesY = new int[numVertices];
	        verticesZ = new int[numVertices];
	        faceIndicesA = new short[numFaces];
	        faceIndicesB = new short[numFaces];
	        faceIndicesC = new short[numFaces];
	        if (hasVertexSkins == 1) {
	            vertexSkins = new int[numVertices];
	        }
	        if (hasFaceTypes) {
	            faceTypes = new byte[numFaces];
	        }
	        if (modelPriority == 255) {
	            facePriorities = new byte[numFaces];
	        } else {
	            priority = (byte) modelPriority;
	        }
	        if (hasFaceAlpha == 1) {
	            faceAlphas = new byte[numFaces];
	        }
	        if (hasFaceSkins == 1) {
	            faceSkins = new int[numFaces];
	        }
	        if (hasFaceTextures == 1) {
	            faceMaterials = new short[numFaces];
	        }
	        if (hasFaceTextures == 1 && (numTextures > 0 || textureUVCoordCount > 0)) {
	            faceTextureIndices = new short[numFaces];
	        }
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
	        second.setPosition(vertexXOffsetOffset);
	        third.setPosition(vertexYOffsetOffset);
	        fourth.setPosition(vertexZOffsetOffset);
	        fifth.setPosition(vertexSkinsOffset);
	        int baseX = 0;
	        int baseY = 0;
	        int baseZ = 0;
	        for (int vertex = 0; vertex < numVertices; vertex++) {
	            int pflag = first.readUnsignedByte();
	            int xOffset = 0;
	            if ((pflag & 0x1) != 0) {
	                xOffset = second.readUnsignedSmart();
	            }
	            int yOffset = 0;
	            if ((pflag & 0x2) != 0) {
	                yOffset = third.readUnsignedSmart();
	            }
	            int zOffset = 0;
	            if ((pflag & 0x4) != 0) {
	                zOffset = fourth.readUnsignedSmart();
	            }
	            verticesX[vertex] = baseX + xOffset;
	            verticesY[vertex] = baseY + yOffset;
	            verticesZ[vertex] = baseZ + zOffset;
	            baseX = verticesX[vertex];
	            baseY = verticesY[vertex];
	            baseZ = verticesZ[vertex];
	            if (hasVertexSkins == 1) {
	                if (hasExtendedVertexSkins) {
	                    vertexSkins[vertex] = fifth.readSmartNS();
	                } else {
	                    vertexSkins[vertex] = fifth.readUnsignedByte();
	                    if (vertexSkins[vertex] == 255) {
	                        vertexSkins[vertex] = -1;
	                    }
	                }
	            }
	        }
	        if (textureUVCoordCount > 0) {
	        	first.setPosition(vertex_uv_offset);
	        	second.setPosition(tex_coord_u_offset);
	        	third.setPosition(tex_coord_v_offset);
	        	vertexUVOffset = new int[numVertices];
	        	int coord = 0;
	        	int size = 0;
	        	for (; coord < numVertices; coord++) {
					vertexUVOffset[coord] = size;
					size += first.readUnsignedByte();
	        	}
				uvCoordVertexA = new byte[numFaces];
				uvCoordVertexB = new byte[numFaces];
				uvCoordVertexC = new byte[numFaces];
				textureCoordU = new float[textureUVCoordCount];
				textureCoordV = new float[textureUVCoordCount];
				for (coord = 0; coord < textureUVCoordCount; coord++) {
					textureCoordU[coord] = (second.readShort() / 4096.0F);
					textureCoordV[coord] = (third.readShort() / 4096.0F);
				}
	        }
	        first.setPosition(faceColorsOffset);
	        second.setPosition(faceTypesOffset);
	        third.setPosition(facePrioritiesOffset);
	        fourth.setPosition(faceAlphasOffset);
	        fifth.setPosition(faceSkinsOffset);
	        sixth.setPosition(faceMaterialsOffset);
	        seventh.setPosition(faceTextureIndicesOffset);
	        for (int face = 0; face < numFaces; face++) {
	            faceColors[face] = (short) first.readUnsignedShort();
	            if (hasFaceTypes) {
	                faceTypes[face] = (byte) second.readByte();
	            }
	            if (modelPriority == 255) {
	                facePriorities[face] = (byte) third.readByte();
	            }
	            if (hasFaceAlpha == 1) {
	                faceAlphas[face] = (byte) fourth.readByte();
	            }
	            if (hasFaceSkins == 1) {
	                if (hasExtendedTriangleSkins) {
	                    faceSkins[face] = fifth.readSmartNS();
	                } else {
	                    faceSkins[face] = fifth.readUnsignedByte();
	                    if (faceSkins[face] == 255) {
	                        faceSkins[face] = -1;
	                    }
	                }
	            }
	            if (hasFaceTextures == 1) {
	                faceMaterials[face] = (short) (sixth.readUnsignedShort() - 1);
	            }
	            if (faceTextureIndices != null) {
	                if (faceMaterials[face] != -1) {
	                	if (version >= 16)
	                		faceTextureIndices[face] = (byte) (seventh.readSmart() - 1);
	                	else
	                		faceTextureIndices[face] = (byte) (seventh.readUnsignedByte() - 1);
	                } else {
	                    faceTextureIndices[face] = (byte) -1;
	                }
	            }
	        }
	        maxIndex = -1;
	        first.setPosition(faceIndicesOffset);
	        second.setPosition(facesCompressTypeOffset);
	        third.setPosition(face_uv_index_offset);
	        decodeIndices(first, second, third);
	        first.setPosition(simpleTexturesOffset);
	        second.setPosition(complexTexturesOffset);
	        third.setPosition(texturesScaleOffset);
	        fourth.setPosition(texturesRotationOffset);
	        fifth.setPosition(texturesDirectionOffset);
	        sixth.setPosition(texturesTranslationOffset);
	        decodeMapping(first, second, third, fourth, fifth, sixth);
	        first.setPosition(particleEffectsOffset);
	        if (hasParticleEffects) {
	            int numEmitters = first.readUnsignedByte();
	            if (numEmitters > 0) {
	                emitters = new EmissiveTriangle[numEmitters];
	                for (int index = 0; index < numEmitters; index++) {
	                    int emitter = first.readUnsignedShort();
	                    int face = first.readUnsignedShort();
	                    byte pri;
	                    if (modelPriority == 255)
	                        pri = facePriorities[face];
	                    else
	                        pri = (byte) modelPriority;                        
	                    emitters[index] = new EmissiveTriangle(emitter, face, faceIndicesA[face], faceIndicesB[face], faceIndicesC[face], pri);
	                }
	            }
	            int numEffectors = first.readUnsignedByte();
	            if (numEffectors > 0) {
	                effectors = new EffectiveVertex[numEffectors];
	                for (int index = 0; index < numEffectors; index++) {
	                    int effector = first.readUnsignedShort();
	                    int vertex = first.readUnsignedShort();
	                    effectors[index] = new EffectiveVertex(effector, vertex);
	                }
	            }
	        }
	        if (hasBillboards) {
	            int numBillboards = first.readUnsignedByte();
	            if (numBillboards > 0) {
	                billboards = new FaceBillboard[numBillboards];
	                for (int index = 0; index < numBillboards; index++) {
	                    int id = first.readUnsignedShort();
	                    int face = first.readUnsignedShort();
	                    int skin;
	                    if (hasExtendedBillboards) {
	                        skin = first.readSmartNS();
	                    } else {
	                        skin = first.readUnsignedByte();
	                        if (skin == 255) {
	                            skin = -1;
	                        }
	                    }
	                    byte distance = (byte) first.readByte();
	                    billboards[index] = new FaceBillboard(id, face, skin, distance);
	                }
	            }
	        }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.wycody.engine.media.mesh.Mesh#encode()
     */
    @Override
    public byte[] encodeOld() {
    	
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
        boolean hasFaceTextures = faceTextureIndices != null;
        
        /* serialize the faces */
        for (int face = 0; face < numFaces; face++) {
        	boolean isTexture = faceTextureIndices != null && faceTextureIndices[face] != -1;
        	face_colors_buffer.writeShort(isTexture ? faceTextureIndices[face] : faceColors[face]);
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
    
    @Override
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
            if (faceTextureIndices != null) {
                if (faceMaterials[face] != -1) {
                    face_textures_buffer.writeByte(faceTextureIndices[face] + 1);
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
     * Decodes the face indices from the given buffers.
     * 
     * @param indicesBuffer
     *            the face index buffer.
     * @param typesBuffer
     *            the face type buffer.
     */
    private void decodeIndices(InputStream indicesBuffer, InputStream typesBuffer, InputStream uvBuffer) {
        short a = 0;
        short b = 0;
        short c = 0;
        int acc = 0;
        for (int face = 0; face < numFaces; face++) {
            int type = typesBuffer.readUnsignedByte();
            if (type == 1) {
                a = (short) (indicesBuffer.readUnsignedSmart() + acc);
                acc = a;
                b = (short) (indicesBuffer.readUnsignedSmart() + acc);
                acc = b;
                c = (short) (indicesBuffer.readUnsignedSmart() + acc);
                acc = c;
                faceIndicesA[face] = a;
                faceIndicesB[face] = b;
                faceIndicesC[face] = c;
                if (a > maxIndex) {
                    maxIndex = a;
                }
                if (b > maxIndex) {
                    maxIndex = b;
                }
                if (c > maxIndex) {
                    maxIndex = c;
                }
            }
            if (type == 2) {
                b = c;
                c = (short) (indicesBuffer.readUnsignedSmart() + acc);
                acc = c;
                faceIndicesA[face] = a;
                faceIndicesB[face] = b;
                faceIndicesC[face] = c;
                if (c > maxIndex) {
                    maxIndex = c;
                }
            }
            if (type == 3) {
                a = c;
                c = (short) (indicesBuffer.readUnsignedSmart() + acc);
                acc = c;
                faceIndicesA[face] = a;
                faceIndicesB[face] = b;
                faceIndicesC[face] = c;
                if (c > maxIndex) {
                    maxIndex = c;
                }
            }
            if (type == 4) {
                short bk = a;
                a = b;
                b = bk;
                c = (short) (indicesBuffer.readUnsignedSmart() + acc);
                acc = c;
                faceIndicesA[face] = a;
                faceIndicesB[face] = b;
                faceIndicesC[face] = c;
                if (c > maxIndex) {
                    maxIndex = c;
                }
            }
            if (textureUVCoordCount > 0 && (type & 0x8) != 0) {
            	uvCoordVertexA[face] = (byte) uvBuffer.readUnsignedByte();
            	uvCoordVertexB[face] = (byte) uvBuffer.readUnsignedByte();
            	uvCoordVertexC[face] = (byte) uvBuffer.readUnsignedByte();
            }
        }
        maxIndex++;
    }

    /**
     * Decodes the texture mapping from the given buffers.
     * 
     * @param simpleBuffer
     *            the simple texture mappings buffer.
     * @param complexBuffer
     *            the complex texture mappings buffer.
     * @param scaleBuffer
     *            the textures scale buffer.
     * @param rotationBuffer
     *            the textures rotation buffer.
     * @param directionBuffer
     *            the textures direction buffer.
     * @param translationBuffer
     *            the textures translation buffer.
     */
    private void decodeMapping(InputStream simpleBuffer, InputStream complexBuffer, InputStream scaleBuffer, InputStream rotationBuffer, InputStream directionBuffer, InputStream translationBuffer) {
        for (int index = 0; index < numTextures; index++) {
            int type = faceMappings[index] & 0xff;
            if (type == 0) {
                textureMappingP[index] = (short) simpleBuffer.readUnsignedShort();
                textureMappingM[index] = (short) simpleBuffer.readUnsignedShort();
                textureMappingN[index] = (short) simpleBuffer.readUnsignedShort();
            }
            if (type == 1) {
                textureMappingP[index] = (short) complexBuffer.readUnsignedShort();
                textureMappingM[index] = (short) complexBuffer.readUnsignedShort();
                textureMappingN[index] = (short) complexBuffer.readUnsignedShort();
                if (version < 15) {
                    textureScaleX[index] = scaleBuffer.readUnsignedShort();
                    if (version < 14) {
                        textureScaleY[index] = scaleBuffer.readUnsignedShort();
                    } else {
                        textureScaleY[index] = scaleBuffer.read24BitInt();
                    }
                    textureScaleZ[index] = scaleBuffer.readUnsignedShort();
                } else {
                    textureScaleX[index] = scaleBuffer.read24BitInt();
                    textureScaleY[index] = scaleBuffer.read24BitInt();
                    textureScaleZ[index] = scaleBuffer.read24BitInt();
                }
                textureRotation[index] = (byte) rotationBuffer.readByte();
                textureDirection[index] = (byte) directionBuffer.readByte();
                textureSpeed[index] = translationBuffer.readByte();
            }
            if (type == 2) {
                textureMappingP[index] = (short) complexBuffer.readUnsignedShort();
                textureMappingM[index] = (short) complexBuffer.readUnsignedShort();
                textureMappingN[index] = (short) complexBuffer.readUnsignedShort();
                if (version < 15) {
                    textureScaleX[index] = scaleBuffer.readUnsignedShort();
                    if (version < 14) {
                        textureScaleY[index] = scaleBuffer.readUnsignedShort();
                    } else {
                        textureScaleY[index] = scaleBuffer.read24BitInt();
                    }
                    textureScaleZ[index] = scaleBuffer.readUnsignedShort();
                } else {
                    textureScaleX[index] = scaleBuffer.read24BitInt();
                    textureScaleY[index] = scaleBuffer.read24BitInt();
                    textureScaleZ[index] = scaleBuffer.read24BitInt();
                }
                textureRotation[index] = (byte) rotationBuffer.readByte();
                textureDirection[index] = (byte) directionBuffer.readByte();
                textureSpeed[index] = translationBuffer.readByte();
                textureTransU[index] = translationBuffer.readByte();
                textureTransV[index] = translationBuffer.readByte();
            }
            if (type == 3) {
                textureMappingP[index] = (short) complexBuffer.readUnsignedShort();
                textureMappingM[index] = (short) complexBuffer.readUnsignedShort();
                textureMappingN[index] = (short) complexBuffer.readUnsignedShort();
                if (version < 15) {
                    textureScaleX[index] = scaleBuffer.readUnsignedShort();
                    if (version < 14) {
                        textureScaleY[index] = scaleBuffer.readUnsignedShort();
                    } else {
                        textureScaleY[index] = scaleBuffer.read24BitInt();
                    }
                    textureScaleZ[index] = scaleBuffer.readUnsignedShort();
                } else {
                    textureScaleX[index] = scaleBuffer.read24BitInt();
                    textureScaleY[index] = scaleBuffer.read24BitInt();
                    textureScaleZ[index] = scaleBuffer.read24BitInt();
                }
                textureRotation[index] = (byte) rotationBuffer.readByte();
                textureDirection[index] = (byte) directionBuffer.readByte();
                textureSpeed[index] = translationBuffer.readByte();
            }
        }
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
    private void encodeMapping(OutputStream simple, OutputStream complex, OutputStream scale, OutputStream rotation, OutputStream direction, OutputStream translation) {
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
    private void encodeIndices(OutputStream ibuffer, OutputStream tbuffer) {
        short lasta = 0;
        short lastb = 0;
        short lastc = 0;
        int pacc = 0;
        for (int fndex = 0; fndex < numFaces; fndex++) {
            short cura = faceIndicesA[fndex];
            short curb = faceIndicesB[fndex];
            short curc = faceIndicesC[fndex];
            if (cura == lastb && curb == lasta && curc != lastc) {
                tbuffer.writeByte(4);
                ibuffer.writeUnsignedSmart(curc - pacc);
                short back = lasta;
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
    private void encodeMappingRS2(OutputStream simple, OutputStream complex, OutputStream scale, OutputStream rotation, OutputStream direction, OutputStream translation) {
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
    private void encodeIndicesRS2(OutputStream ibuffer, OutputStream tbuffer) {
        short lasta = 0;
        short lastb = 0;
        short lastc = 0;
        int pacc = 0;
        for (int fndex = 0; fndex < numFaces; fndex++) {
            short cura = faceIndicesA[fndex];
            short curb = faceIndicesB[fndex];
            short curc = faceIndicesC[fndex];
            if (cura == lastb && curb == lasta && curc != lastc) {
                tbuffer.writeByte(4);
                ibuffer.writeUnsignedSmart(curc - pacc);
                short back = lasta;
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
    
    public String toString() {
    	return "";
    }

}
