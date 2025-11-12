package fr.flaily.xynon.utils.render.shader;

import fr.flaily.xynon.utils.FileUtils;
import fr.flaily.xynon.utils.ResourceUtils;
import fr.flaily.xynon.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class ShaderUtil implements Utils {
    private final int programID;

    public ShaderUtil(String fragmentShaderLoc, String vertexShaderLoc) {
        int program = glCreateProgram();
        try {
            int fragmentShaderID;
            switch (fragmentShaderLoc) {
                case "roundedRect":
                    fragmentShaderID = createShader(new ByteArrayInputStream(roundedRect.getBytes()), GL_FRAGMENT_SHADER);
                    break;
                case "roundedRectGradient":
                    fragmentShaderID = createShader(new ByteArrayInputStream(roundedRectGradient.getBytes()), GL_FRAGMENT_SHADER);
                    break;
                default:
                    try {
//                        InputStream shaderStream = mc.getResourceManager().getResource(new ResourceLocation("xynon", fragmentShaderLoc)).getInputStream();
                        InputStream shaderStream = mc.getResourceManager().getResource(new ResourceLocation(fragmentShaderLoc)).getInputStream();
                        fragmentShaderID = createShader(shaderStream, GL_FRAGMENT_SHADER);
                        System.err.println("Loaded shader from " + fragmentShaderLoc);
                        Minecraft.logger.warn("Loaded shader from " + fragmentShaderLoc);
                    } catch (IOException e) {
                        fragmentShaderID = 0;
                        Minecraft.logger.warn("Failed to load shader from " + fragmentShaderLoc + ": " + e.getMessage());
                        System.err.println("Failed to load shader from " + fragmentShaderLoc + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
            }
            glAttachShader(program, fragmentShaderID);

//            int vertexShaderID = createShader(mc.getResourceManager().getResource(new ResourceLocation("xynon", fragmentShaderLoc)).getInputStream(), GL_VERTEX_SHADER);
//            int vertexShaderID = createShader(
//                    mc.getResourceManager().getResource(new ResourceLocation("xynon", vertexShaderLoc)).getInputStream(),
//                    GL_VERTEX_SHADER
//            );
            int vertexShaderID = createShader(mc.getResourceManager().getResource(new ResourceLocation(vertexShaderLoc)).getInputStream(), GL_VERTEX_SHADER);
            glAttachShader(program, vertexShaderID);


        } catch (IOException e) {
            e.printStackTrace();
        }

        glLinkProgram(program);
        int status = glGetProgrami(program, GL_LINK_STATUS);

        if (status == 0) {
            throw new IllegalStateException("Shader failed to link!");
        }
        this.programID = program;
    }

    public final long CREATION_TIME = System.currentTimeMillis();

    public ShaderUtil(String fragmentShaderLoc) {
        this(fragmentShaderLoc, "shaders/vertex.vsh");
    }

    public static void drawFramebuffer(int framebufferTextureId, int width, int height) {
        // Bind the framebuffer's texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebufferTextureId);

        // Set up 2D orthographic projection
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, width, height, 0, -1, 1);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        // Draw a fullscreen quad with the framebuffer texture
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glTexCoord2f(0, 0);
            GL11.glVertex2f(0, 0);

            GL11.glTexCoord2f(1, 0);
            GL11.glVertex2f(width, 0);

            GL11.glTexCoord2f(1, 1);
            GL11.glVertex2f(width, height);

            GL11.glTexCoord2f(0, 1);
            GL11.glVertex2f(0, height);
        }
        GL11.glEnd();

        // Restore matrices
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        // Unbind texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void init() {
        glUseProgram(programID);
    }

    public void unload() {
        glUseProgram(0);
    }

    public int getUniform(String name) {
        return glGetUniformLocation(programID, name);
    }


    public void setUniformf(String name, float... args) {
        int loc = glGetUniformLocation(programID, name);
        switch (args.length) {
            case 1:
                glUniform1f(loc, args[0]);
                break;
            case 2:
                glUniform2f(loc, args[0], args[1]);
                break;
            case 3:
                glUniform3f(loc, args[0], args[1], args[2]);
                break;
            case 4:
                glUniform4f(loc, args[0], args[1], args[2], args[3]);
                break;
        }
    }

    public void setUniformb(String name, boolean... args) {
        int loc = glGetUniformLocation(programID, name);
        switch (args.length) {
            case 1:
                glUniform1i(loc, args[0] ? 1 : 0);
                break;
            case 2:
                glUniform2i(loc, args[0] ? 1 : 0, args[1] ? 1 : 0);
                break;
            case 3:
                glUniform3i(loc, args[0] ? 1 : 0, args[1] ? 1 : 0, args[2] ? 1 : 0);
                break;
            case 4:
                glUniform4i(loc, args[0] ? 1 : 0, args[1] ? 1 : 0, args[2] ? 1 : 0, args[3] ? 1 : 0);
                break;
            default:
                throw new IllegalArgumentException("Only 1 to 4 boolean arguments are supported.");
        }
    }


    public void setUniformi(String name, int... args) {
        int loc = glGetUniformLocation(programID, name);
        if (args.length > 1) glUniform2i(loc, args[0], args[1]);
        else glUniform1i(loc, args[0]);
    }

    public static void drawQuads(float x, float y, float width, float height) {
        //if (mc.gameSettings.ofFastRender) return;
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(x, y);
        glTexCoord2f(0, 1);
        glVertex2f(x, y + height);
        glTexCoord2f(1, 1);
        glVertex2f(x + width, y + height);
        glTexCoord2f(1, 0);
        glVertex2f(x + width, y);
        glEnd();
    }

    private static int quadVao = -1;

    public static void drawFullscreenQuad() {
        if (quadVao == -1) {
            // Create VAO once
            quadVao = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(quadVao);

            int vbo = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

            float[] vertices = {
                    -1f, -1f,
                    1f, -1f,
                    -1f,  1f,
                    -1f,  1f,
                    1f, -1f,
                    1f,  1f
            };

            FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
            buffer.put(vertices).flip();
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

            GL20.glEnableVertexAttribArray(0);
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
        }

        // Bind & draw
        GL30.glBindVertexArray(quadVao);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
        GL30.glBindVertexArray(0);
    }

    public static void drawQuads() {
        ScaledResolution sr = new ScaledResolution(mc);
        float width = (float) sr.getScaledWidth_double();
        float height = (float) sr.getScaledHeight_double();
        float texFix = 0.00001f;

        glBegin(GL_QUADS);
        glTexCoord2f(texFix, 1 - texFix);
        glVertex2f(0, 0);

        glTexCoord2f(0, 0);
        glVertex2f(0, height);

        glTexCoord2f(1, 0);
        glVertex2f(width, height);

        glTexCoord2f(1, 1);
        glVertex2f(width, 0);
        glEnd();
    }

    private int createShader(InputStream inputStream, int shaderType) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, FileUtils.readInputStream(inputStream));
        glCompileShader(shader);


        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            System.out.println(glGetShaderInfoLog(shader, 4096));
            throw new IllegalStateException(String.format("Shader (%s) failed to compile!", shaderType));
        }

        return shader;
    }


    private final String roundedRectGradient = "#version 120\n" +
            "\n" +
            "uniform vec2 location, rectSize;\n" +
            "uniform vec4 color1, color2, color3, color4;\n" +
            "uniform float radius;\n" +
            "\n" +
            "#define NOISE .5/255.0\n" +
            "\n" +
            "float roundSDF(vec2 p, vec2 b, float r) {\n" +
            "    return length(max(abs(p) - b , 0.0)) - r;\n" +
            "}\n" +
            "\n" +
            "vec3 createGradient(vec2 coords, vec3 color1, vec3 color2, vec3 color3, vec3 color4){\n" +
            "    vec3 color = mix(mix(color1.rgb, color2.rgb, coords.y), mix(color3.rgb, color4.rgb, coords.y), coords.x);\n" +
            "    //Dithering the color\n" +
            "    // from https://shader-tutorial.dev/advanced/color-banding-dithering/\n" +
            "    color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898, 78.233))) * 43758.5453));\n" +
            "    return color;\n" +
            "}\n" +
            "\n" +
            "void main() {\n" +
            "    vec2 st = gl_TexCoord[0].st;\n" +
            "    vec2 halfSize = rectSize * .5;\n" +
            "    \n" +
            "    float smoothedAlpha =  (1.0-smoothstep(0.0, 2., roundSDF(halfSize - (gl_TexCoord[0].st * rectSize), halfSize - radius - 1., radius))) * color1.a;\n" +
            "    gl_FragColor = vec4(createGradient(st, color1.rgb, color2.rgb, color3.rgb, color4.rgb), smoothedAlpha);\n" +
            "}";


    private String roundedRect = "#version 120\n" +
            "\n" +
            "uniform vec2 location, rectSize;\n" +
            "uniform vec4 color;\n" +
            "uniform float radius;\n" +
            "uniform bool blur;\n" +
            "\n" +
            "float roundSDF(vec2 p, vec2 b, float r) {\n" +
            "    return length(max(abs(p) - b, 0.0)) - r;\n" +
            "}\n" +
            "\n" +
            "\n" +
            "void main() {\n" +
            "    vec2 rectHalf = rectSize * .5;\n" +
            "    // Smooth the result (free antialiasing).\n" +
            "    float smoothedAlpha =  (1.0-smoothstep(0.0, 1.0, roundSDF(rectHalf - (gl_TexCoord[0].st * rectSize), rectHalf - radius - 1., radius))) * color.a;\n" +
            "    gl_FragColor = vec4(color.rgb, smoothedAlpha);// mix(quadColor, shadowColor, 0.0);\n" +
            "\n" +
            "}";

}
