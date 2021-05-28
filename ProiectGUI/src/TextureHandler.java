
import java.io.IOException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.gl2.GLUgl2;

public class TextureHandler {

    private final int NO_TEXTURES = 1;
    private int texture[] = new int[NO_TEXTURES];
    TextureReader.Texture[] tex = new TextureReader.Texture[NO_TEXTURES];

    private GL2 gl;
    private GLUgl2 glu;


    public TextureHandler(GL2 gl, GLUgl2 glu, String path, boolean mipmapped) {
        this.gl = gl;
        this.glu = glu;

        // Generate a name (id) for the texture.
        this.gl.glGenTextures(1, texture, 0);
        // Bind (select) the texture.
        this.gl.glBindTexture(GL2.GL_TEXTURE_2D, texture[0]);

        // Read the texture from the image.
        try {
            tex[0] = TextureReader.readTexture(path);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // Define the filters used when the texture is scaled.
        this.gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
                GL2.GL_LINEAR);
        this.gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
                GL2.GL_LINEAR);

        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);

        // Construct the texture and use mipmapping in the process.
        this.makeRGBTexture(this.gl, this.glu, tex[0], GL2.GL_TEXTURE_2D, mipmapped);

    }

    public void bind() {
        this.gl.glBindTexture(GL2.GL_TEXTURE_2D, texture[0]);
    }

    public void enable() {
        this.gl.glEnable(GL2.GL_TEXTURE_2D);
    }

    public void disable() {
        this.gl.glDisable(GL2.GL_TEXTURE_2D);
    }

    private void makeRGBTexture(GL2 gl, GLUgl2 glu, TextureReader.Texture img,
                                int target, boolean mipmapped) {
        if (mipmapped) {
            glu.gluBuild2DMipmaps(target, GL2.GL_RGB8, img.getWidth(), img
                    .getHeight(), GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, img
                    .getPixels());
        } else {
            gl.glTexImage2D(target, 0, GL2.GL_RGB, img.getWidth(), img
                    .getHeight(), 0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, img
                    .getPixels());
        }
    }

    public TextureReader.Texture getTexture() {
        return tex[0];
    }
}
