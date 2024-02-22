package utility;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.google.common.io.Files;
import store.plugin.PluginType;

/**
 * @author ReverendDread Jun 22, 2018
 */
public class StringUtilities {

	/**
	 * Formats an int array for viewing
	 * 
	 * @param array the int array
	 * @return the formatted array as a string.
	 */
	public static final String formatIntArrayToString(int[] array) {
		String formatted = "";
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				formatted = formatted + array[i] + ";";
			}
		}
		return formatted;
	}

	/**
	 * Formats string array for viewing.
	 * 
	 * @param options the options to format.
	 * @return the formated array as a string.
	 */
	public static final String formatStringArrayToString(String[] options) {
		String formatted = "";
		for (int index = 0; index < options.length; index++) {
			String option = options[index];
			formatted = formatted + (option == null ? "null" : option) + ";";
		}
		return formatted;
	}

	/**
	 * Formats a set of arrays for display.
	 * 
	 * @param original the original array
	 * @param modified the modified array.
	 * @return the formatted array as a string.
	 */
	public static final String formatMultiArrayInt(int[] original, int[] modified) {
		String formatted = "";
		if (original != null && modified != null) {
			for (int i = 0; i < original.length; i++) {
				formatted = formatted + original[i] + "=" + modified[i] + ";";
			}
		}
		return formatted.isEmpty() ? "null" : formatted;
	}

	/**
	 * Formats a set of arrays for display.
	 * 
	 * @param original the original array
	 * @param modified the modified array.
	 * @return the formatted array as a string.
	 */
	public static final String formatMultiArrayShort(short[] original, short[] modified) {
		String formatted = "";
		if (original != null && modified != null) {
			for (int i = 0; i < original.length; i++) {
				formatted = formatted + original[i] + "=" + modified[i] + ";";
			}
		}
		return formatted.isEmpty() ? "null" : formatted;
	}

	/**
	 * Creates a readable stacktrace error message.
	 * 
	 * @param throwable
	 * @return
	 */
	public static String getStackTrace(final Throwable throwable) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

	/**
	 * @param type
	 * @return
	 */
	public static String enumNameToLower(PluginType type) {
		return type.name().toLowerCase();
	}
	
	public static String stripDat(String name) {
		return name.substring(0, name.length() - ".dat".length());
	}

	public static String stripExtension(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}

	public static int stripId(String name) {
		// Find the index of the first underscore
		int underscoreIndex = name.indexOf('_');
		if (underscoreIndex != -1) {
			// Extract the numeric part from the beginning of the name until the underscore
			String numericPart = name.substring(0, underscoreIndex);
			// Parse the numeric part to an integer
			return Integer.parseInt(numericPart);
		} else {
			// Find the index of the ".dat" extension
			int extensionIndex = name.lastIndexOf(".dat");
			if (extensionIndex == -1) {
				throw new IllegalArgumentException("Invalid file name format: " + name);
			}
			// Extract the numeric part from the beginning of the name until the ".dat" extension
			String numericPart = name.substring(0, extensionIndex);
			// Parse the numeric part to an integer
			return Integer.parseInt(numericPart);
		}
	}
	
	public static String getFormattedEnumName(String name) {
		String lower = name.toLowerCase().replace("_", " ");
		return lower.substring(0, 1).toUpperCase() + lower.substring(1);
	}

	public static String getFileName(String path) {
		return Files.getNameWithoutExtension(path);
	}

	public static String getFileExtension(File file) {
		String name = file.getName();
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return ""; // empty extension
		}
		return name.substring(lastIndexOf);
	}

}
