package common.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class provides a simple, command-pattern based way of implementing an
 * interactive commandline. The cli can either run in the current thread and
 * therefore blocks it, or it creates a new thread for cli input and command
 * execution.
 * 
 * @author fabianwitte
 *
 */
public class InteractiveCLI implements Runnable {
	private static final Logger logger = LogManager.getLogger(InteractiveCLI.class);

	private static final String THREAD_NAME = "interactive-cli-thread";
	private boolean keepRunning = false;
	private boolean async = false;
	private Thread hostingThread = null;
	private InputStream inputStream = System.in;

	private Map<String, CommandInterface> commandMap = new HashMap<>();
	private Map<String, String> variables = new HashMap<>();

	/**
	 * Sets if the cli should be run in a separate thread.
	 * 
	 * @param async
	 *            tru if the cli runs in a separate thread.
	 */
	public void setAsync(boolean async) {
		this.async = async;
	}

	/**
	 * Returns if the cli runs in a separate thread.
	 * 
	 * @return true, if the cli uses a separate thread.
	 */
	public boolean isAsync() {
		return this.async;
	}

	/**
	 * Sets the input stream used for the cli.
	 * 
	 * Defaults to System.in
	 * 
	 * @param inputStream
	 *            InputStream instance used.
	 */
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * Returns the InputStream instance used for cli input capturing.
	 * 
	 * @return InputStream instance used.
	 */
	public InputStream getInputStream() {
		return this.inputStream;
	}

	/**
	 * Starts the cli and captures user input.
	 */
	public void start() {
		this.keepRunning = true;

		if (async) {
			hostingThread = new Thread(this, THREAD_NAME);
			hostingThread.start();
		} else {
			this.run();
		}
	}

	/**
	 * Stops the cli and therefore user-input capturing.
	 */
	public void stop() {
		this.keepRunning = false;
		hostingThread = null;
	}

	/**
	 * Returns if the cli is currently running.
	 * 
	 * @return true, if running.
	 */
	public boolean isRunning() {
		return keepRunning;
	}

	/**
	 * Registers a new command for commandline execution.
	 * 
	 * @param key
	 *            text-command used to trigger the registered command.
	 * @param command
	 *            action to be executed when the command is captured.
	 */
	public void registerCommand(String key, CommandInterface command) {
		if (command != null) {
			commandMap.put(key, command);
		}
	}

	/**
	 * Returns a set with all available commands.
	 * 
	 * @return Set with commands as strings.
	 */
	public Set<String> getRegisteredCommands() {
		return commandMap.keySet();
	}

	/**
	 * Sets an internal variable to the value provided.
	 * 
	 * @param key
	 *            variable name.
	 * @param value
	 *            variable value.
	 */
	public void setVariable(String key, String value) {
		this.variables.put(key, value);
	}

	/**
	 * Returns the value of the internal variable with the given key.
	 * 
	 * @param key
	 *            name of the internal variable.
	 * @return value of the variable or null if not found.
	 */
	public String getVariable(String key) {
		return this.variables.get(key);
	}

	/**
	 * Returns a set with all internal variable names.
	 * 
	 * @return Set with variable names.
	 */
	public Set<String> getVariableNames() {
		return this.variables.keySet();
	}

	/**
	 * Executes the given command if possible. This can be used to easily test
	 * the class and run commands from a text-file.
	 * 
	 * @param command
	 *            command to execute.
	 */
	public void exec(String command) {
		Input input = new Input(command);
		if (input.isValid()) {
			execute(input);
		} else {
			logger.warn("Invalid command: {}", input);
		}
	}

	/**
	 * Executes an action for the given input if possible.
	 * 
	 * @param input
	 *            captured and parsed input.
	 */
	private void execute(Input input) {
		if (commandMap.containsKey(input.command())) {
			Map<String, String> params = input.params();
			filterParams(params);
			commandMap.get(input.command()).execute(params, this);
		}
	}

	/**
	 * Filters the input params and replaces values by variable values if
	 * possible.
	 * 
	 * @param params
	 *            Map of param-key and param-value pairs.
	 */
	private void filterParams(Map<String, String> params) {
		Map<String, String> replace = new HashMap<>();
		for (Map.Entry<String, String> e : params.entrySet()) {
			if (e.getValue().startsWith("[") && e.getValue().endsWith("]")) {
				if (variables.containsKey(e.getValue().substring(1, e.getValue().length() - 1))) {
					replace.put(e.getKey(), variables.get(e.getValue().substring(1, e.getValue().length() - 1)));
				}
			}
		}
		params.putAll(replace);
	}

	@Override
	public void run() {
		try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(inputStream))) {
			while (keepRunning) {
				Input input = new Input(consoleReader.readLine());
				if (input.isValid()) {
					execute(input);
				} else {
					logger.warn("Invalid command: {}", input);
				}
			}
		} catch (IOException e) {
			logger.error("An error ocurred while using CLI input: " + e.getMessage());
		}
	}
}