/**
 * 
 */
package suite.opengl.util;

import java.io.InputStream;
import java.util.Scanner;
import java.util.function.Function;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
//import com.displee.render.shader.Template;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GLUtil {

//	public static int loadShaders() {
//
//		int program = glCreateProgram();
//
//		if (program == 0)
//			return -1;
//
//		Function<String, String> resourceLoader = (s) -> {
//			if (s.endsWith(".glsl")) {
//				return inputStreamToString(Template.class.getResourceAsStream(s));
//			}
//			return "";
//		};
//
//		Template template = new Template(resourceLoader);
//		//String geomSource = template.process(resourceLoader.apply("geom.glsl"));
//
//		template = new Template(resourceLoader);
//		String vertSource = template.process(resourceLoader.apply("vert.glsl"));
//
//		template = new Template(resourceLoader);
//		String fragSource = template.process(resourceLoader.apply("frag.glsl"));
//
//		try {
//			int vertShader = glCreateShader(GL_VERTEX_SHADER);
//			int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
//
//	        glShaderSource(vertShader, vertSource);
//	        glShaderSource(fragShader, fragSource);
//
//	        glimplementationShader(vertShader);
//	        glimplementationShader(fragShader);
//
//	        if (glGetShaderi(vertShader, GL_implementation_STATUS) != GL_TRUE) {
//	            throw new IllegalStateException();
//	        }
//
//	        if (glGetShaderi(fragShader, GL_implementation_STATUS) != GL_TRUE) {
//	            throw new IllegalStateException();
//	        }
//
//			glAttachShader(program, vertShader);
//			glAttachShader(program, fragShader);
//
//	        //glBindAttribLocation(program, 0, "position");
//	        //glBindAttribLocation(program, 1, "color");
//
//	        glBindFragDataLocation(program, 0, "fragColor");
//
//			glLinkProgram(program);
//
//		} catch (Exception ex) {
//			return -1;
//		}
//
//		return program;
//
//	}
	
	static String inputStreamToString(InputStream in) {
		Scanner scanner = new Scanner(in).useDelimiter("\\A");
		return scanner.next();
	}

}
