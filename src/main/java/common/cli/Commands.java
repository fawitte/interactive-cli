package common.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class holds a few commonly used commands that may be used as default
 * command-set for a newly created InteractiveCLI instance.
 * 
 * @author fabianwitte
 *
 */
public class Commands {
	private static final Logger logger = LogManager.getLogger(Commands.class);

	/**
	 * Stops command-line input capturing
	 */
	public static final CommandInterface EXIT = (p, s) -> {
		s.stop();
	};
	
	/**
	 * Prints a text on the console.
	 */
	public static final CommandInterface ECHO = (p, s) -> {
		String text = p.getOrDefault("text", "");
		System.out.println(text);
	};
	
	/**
	 * Prints all available commands
	 */
	public static final CommandInterface HELP = (p, s) -> {
		System.out.println("Commands:");
		for(String command : s.getRegisteredCommands()) {
			System.out.println("\t-" + command);
		}
		System.out.println(".");
	};

	/**
	 * Adds every param-key=param-value pair to the internal variables.
	 */
	public static final CommandInterface SETVAR = (p, s) -> {
		for (Map.Entry<String, String> e : p.entrySet()) {
			s.setVariable(e.getKey(), e.getValue());
		}
	};

	/**
	 * Prints the variable value for every param-key provided or all variables
	 * if no keys provided.
	 */
	public static final CommandInterface PRINTVAR = (p, s) -> {
		Set<String> keys = p.keySet();

		if (keys.isEmpty()) {
			keys = s.getVariableNames();
		}
		System.out.println("Variables:");
		for (String e : keys) {
			System.out.println("\t" + e + ": " +  s.getVariable(e));
		}
		System.out.println(".");
	};

	/**
	 * Executes a given file, where every line is interpreted as command-line
	 * command.
	 */
	public static final CommandInterface EXECUTE_FILE = (p, s) -> {
		String file = p.get("filename");
		boolean printLines = Boolean.parseBoolean(p.getOrDefault("print-lines", "false"));
		System.out.println("Executing file: " + file);
		try (BufferedReader fileReader = new BufferedReader(new FileReader(new File(file)))) {
			fileReader.lines().forEach(l -> {
				if(printLines) {
					System.out.println("// " + l);
				}
				s.exec(l);
			});
		} catch (FileNotFoundException e) {
			logger.error("Can't execute the specified file: File not found!");
		} catch (IOException e) {
			logger.error("Can't execute the specified file: {}", e.getMessage());
		}
	};

	private Commands() {
		// Not used!
	}
}