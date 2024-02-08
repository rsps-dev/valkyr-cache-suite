package suite.opengl.wrapper.impl;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import misc.CustomTab;
import org.lwjgl.opengl.GL11;
import store.codec.model.Mesh;
import suite.opengl.texture.Texture;
import suite.opengl.util.ColorUtilities;
import suite.opengl.wrapper.OpenGLWrapper;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author ReverendDread on 12/9/2019
 * https://www.rune-server.ee/members/reverenddread/
 * @project ValkyrCacheSuite
 */
@Slf4j
public class MeshWrapper extends OpenGLWrapper<Mesh> {

    /**
     * The model scale constant.
     */
    private static final float MODEL_SCALE = 1.0F;

    /**
     * Holding all used textures in this renderer.
     */
    private Map<Integer, Integer> textureMap = new HashMap<>();

    @Getter @Setter private boolean showGrid;
    @Getter @Setter private boolean showWeights;
    @Getter @Setter private boolean showPriorities;

    //Used for font rendering
    //private TrueTypeFont font;
    private Font awtFont;

    /**
     * Create a new {@code GLWrapper} {@code Object}.
     *
     * @param view The image view.
     */
    public MeshWrapper(CustomTab parent, ImageView view) {
        super(view, parent);
        awtFont = new Font("Arial", Font.PLAIN, 4);
        //font = new TrueTypeFont(awtFont, true);
    }

    @Override
    public void init() {
        glShadeModel(GL_SMOOTH);
        glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        glEnable(GL_DEPTH_TEST);
        glClearDepth(1.0F);
        glDepthFunc(GL_LEQUAL);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glEnable(GL_NORMALIZE);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        glEnable(GL_POINT_SMOOTH);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_COLOR_MATERIAL);
        glEnable(GL_ALPHA_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_COLOR_MATERIAL);
        glColorMaterial(GL_FRONT, GL_DIFFUSE);
        glCullFace(GL_BACK);
    }

    @Override
    protected void render() {

        //Prevents from rendering when not visible
        if (getParent() == null || !getParent().getTabPane().getScene().getWindow().isFocused()) {
            return;
        }

        glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        float width = (float) view.getFitWidth();
        float height = (float) view.getFitHeight();
        float scale = super.scale;

        if (showGrid) drawGrid();
        render(this.context, width / 2, height / 2, 0.0f, camera.position.x, camera.position.y, camera.position.z, scale, scale, scale);

    }

    @Override
    protected void update(Node... nodes) {
        Label label = (Label) nodes[0];
        label.setText("FPS: " + fps.get());
        if (context != null) {
            Label verts = (Label) nodes[1];
            verts.setText("V: " + context.getNumVertices());
            Label faces = (Label) nodes[2];
            faces.setText("F: " + context.getNumFaces());
        }
    }

    @Override
    protected void onTerminate() {

    }

    @Override
    protected void onResize() {
        int width = (int) view.getFitWidth();
        int height = (int) view.getFitHeight();
        glViewport(0, 0, width, height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float c = (float) Math.sqrt((double) (width * width) + (double) (height * height));
        glOrtho(0.0F, width, 0.0F, height, -c, c);//0.01F
        glMatrixMode(GL_MODELVIEW);
    }

    private void render(Mesh model, float x, float y, float z, float rx, float ry, float rz, float sx, float sy, float sz) throws IllegalStateException {

        glLoadIdentity();
        glTranslatef(x, y, z);
        glRotatef(rx, 1.0F, 0.0F, 0.0F);
        glRotatef(ry, 0.0F, 1.0F, 0.0F);
        glRotatef(rz, 0.0F, 0.0F, 1.0F);
        glScalef(sx, sy, sz);

        int numFaces = model.getNumFaces();
        byte[] faceTypes = model.getFaceTypes();
        short[] faceIndicesA = model.getFaceIndicesA();
        short[] faceIndicesB = model.getFaceIndicesB();
        short[] faceIndicesC = model.getFaceIndicesC();
        short[] textureMappingP = model.getTextureMappingP();
        short[] textureMappingM = model.getTextureMappingM();
        short[] textureMappingN = model.getTextureMappingN();
        short[] faceTextures = model.getFaceMaterials();
        int[] verticesX = model.getVerticesX();
        int[] verticesY = model.getVerticesY();
        int[] verticesZ = model.getVerticesZ();
        int[] vertexSkins = model.getVertexSkins();

        boolean hasAlpha = model.getFaceAlphas() != null;
        boolean hasFaceTypes = faceTypes != null;

        for (int i = 0; i < numFaces; i++) {
            int alpha = hasAlpha ? model.getFaceAlphas()[i] : 0;
            if (alpha == -1) {
                continue;
            }
            alpha = ~alpha & 0xFF;
            final int faceType = hasFaceTypes ? faceTypes[i] & 0x3 : 0;
            int faceA;
            int faceB;
            int faceC;
            switch (faceType) {
                case 0:
                case 1:
                    faceA = faceIndicesA[i];
                    faceB = faceIndicesB[i];
                    faceC = faceIndicesC[i];
                    break;
                case 2:
                case 3:
                    faceA = textureMappingP[i];
                    faceB = textureMappingM[i];
                    faceC = textureMappingN[i];
                    break;
                default:
                    throw new IllegalStateException("Unknown face type=" + faceType);
            }

            short textureId = faceTextures == null ? -1 : faceTextures[i];
            float[] u = null;
            float[] v = null;
            int color = ColorUtilities.forHSBColor(model.getFaceColors()[i]);
            if (RENDER_TEXTURES && textureId != -1) {

                glEnable(GL_TEXTURE_2D);

                Texture texture = model.getTextures()[i];

                int openGlId = getTexture(texture);

                if (model.getTexturedUCoordinates() == null || model.getTexturedVCoordinates() == null) {
                    model.computeTextureCoordinates();
                }
                u = model.getTexturedUCoordinates()[i];
                v = model.getTexturedVCoordinates()[i];

                glBindTexture(GL_TEXTURE_2D, openGlId);
            }
            glBegin(GL_TRIANGLES);

            glColor4ub((byte)(color >> 16), (byte) (color >> 8), (byte) color, (byte) alpha);
            if (RENDER_TEXTURES && textureId != -1) {
                glTexCoord2f(u[0], v[0]);
            }
            glVertex3f(verticesX[faceA] / MODEL_SCALE, verticesY[faceA] / MODEL_SCALE, verticesZ[faceA] / MODEL_SCALE);
            if (RENDER_TEXTURES && textureId != -1) {
                glTexCoord2f(u[1], v[1]);
            }
            glVertex3f(verticesX[faceB] / MODEL_SCALE, verticesY[faceB] / MODEL_SCALE, verticesZ[faceB] / MODEL_SCALE);
            if (RENDER_TEXTURES && textureId != -1) {
                glTexCoord2f(u[2], v[2]);
            }
            glVertex3f(verticesX[faceC] / MODEL_SCALE, verticesY[faceC] / MODEL_SCALE, verticesZ[faceC] / MODEL_SCALE);

            glEnd();
            if (RENDER_TEXTURES && textureId != -1) {
                glDisable(GL_TEXTURE_2D);
            }

            if (showWeights) {
                glDisable(GL_DEPTH_TEST); //have to disable before drawing txt
                //font.drawString(verticesX[faceA] / MODEL_SCALE, verticesY[faceA] / MODEL_SCALE, "nigger 1");
                //font.drawString(verticesX[faceB] / MODEL_SCALE, verticesY[faceB] / MODEL_SCALE, "nigger 2");
                //font.drawString(verticesX[faceC] / MODEL_SCALE, verticesY[faceC] / MODEL_SCALE, "nigger 3");
                glEnable(GL_DEPTH_TEST);
            }

        }
    }

    private int getTexture(Texture texture) {
        Integer openGlId = textureMap.get(texture.getId());
        if (openGlId != null) {
            return openGlId;
        }
        int width = (int) texture.getImage().getWidth();
        int height = (int) texture.getImage().getHeight();

        int glTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, glTexture);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, texture.toByteBuffer());

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        textureMap.put(texture.getId(), glTexture);
        return glTexture;
    }

    private void drawGrid() {
        float[] colours = { 137, 137, 137 };
        GL11.glColor4f(colours[0], colours[1], colours[2], 1f);
        glBegin(GL_LINES);
        for (float f2 = -800.0f; f2 <= 800.0f; f2 += 100.0f) {
            GL11.glVertex3f(f2, 0.0f, 800.0f);
            GL11.glVertex3f(f2, 0.0f, -800.0f);
            GL11.glVertex3f(800.0f, 0.0f, f2);
            GL11.glVertex3f(-800.0f, 0.0f, f2);
        }
        glEnd();
        drawAxisMarkers(6, 10f);
    }

    private void drawAxisMarkers(int num, float size) {

        glLineWidth(1);
        glColor3f(0.4f, 0.4f, 0.4f);
        glBegin(GL_LINES);
        glVertex3f(0, 0, 0);
        glVertex3f(0, -1f, size * num);//+Y
        glEnd();

        glLineWidth(1);
        glColor4f(0.2f, 0.2f, 0.2f, 0.5f);
        glBegin(GL_LINES);
        glVertex3f(0, 0.05f, 0);
        glVertex3f(0, 0.05f, size * num);//+Y
        glEnd();

        glLineWidth(2);
        glColor4f(0f, 0f, 1.0f, 0.5f);
        glBegin(GL_LINES);
        glVertex3f(0, 0, 0);
        glVertex3f(0, -1f, -size * num);//-Y
        glEnd();

        glLineWidth(2);
        glColor4f(1.0f, 0f, 0f, 0.5f);//RED
        glBegin(GL_LINES);
        glVertex3f(0, 0, 0);
        glVertex3f(size * num, -1f, 0);//+X
        glEnd();

        glLineWidth(1);
        glColor3f(0.4f, 0.4f, 0.4f);
        glBegin(GL_LINES);
        glVertex3f(0, 0, 0);
        glVertex3f(-size * num, -1f, 0);//-X
        glEnd();

        glLineWidth(1);
        glColor3f(0.2f, 0.2f, 0.2f);
        glBegin(GL_LINES);
        glVertex3f(0, 0.5f, 0);
        glVertex3f(-size * num, -1f, 0);//-X
        glEnd();

        glLineWidth(2);
        glColor4f(0f, 1.0f, 0f, 0.5f);
        glBegin(GL_LINES);
        glVertex3f(0, 0, 0);
        glVertex3f(0, -size * num, 0);//-Z
        glEnd();
    }

}
