package common.cli;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is used to parse the input-lines and break them down into the
 * command and the parameters.
 * 
 * @author fabianwitte
 *
 */
public class Input {
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(Input.class);

	private static final String IDENTIFIER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-.:";

	/**
	 * This subclass is used to parse the input-text character by character.
	 * 
	 * @author fabianwitte
	 *
	 */
	private class ParserContext {
		private String input;
		private int index = 0;

		/**
		 * Create a new parser context with the given input.
		 * 
		 * @param input
		 *            String to parse
		 */
		public ParserContext(String input) {
			this.input = input;
		}

		/**
		 * Returns the next readable character.
		 * 
		 * @return char
		 */
		public char next() {
			return input.charAt(index++);
		}

		/**
		 * Decrements the internal reading-index by 1.
		 */
		public void back() {
			--index;
		}

		/**
		 * Returns if no more characters can be read from this context.
		 * 
		 * @return true, if no more chars available.
		 */
		public boolean finished() {
			return index >= input.length();
		}
	}

	private String command;
	private Map<String, String> params = new HashMap<>();

	private boolean valid = true;

	/**
	 * Creates a new input instance with the given String.
	 * 
	 * @param input
	 *            Input String.
	 */
	public Input(String input) {
		parseInput(input);
	}

	/**
	 * Returns the command recognized by this input instance.
	 * 
	 * @return command string.
	 */
	public String command() {
		return this.command;
	}

	/**
	 * Returns the map of param-key=param-value pairs recognized by this input
	 * instance.
	 * 
	 * @return Map<String,String>
	 */
	public Map<String, String> params() {
		return this.params;
	}

	/**
	 * Returns if the input is valid. It is valid if no errors occurred during
	 * the parsing-process.
	 * 
	 * @return true, if input is valid.
	 */
	public boolean isValid() {
		return this.valid;
	}

	/**
	 * Parses the input String.
	 * 
	 * @param input
	 *            Input String to parse.
	 */
	private void parseInput(String input) {
		ParserContext ctx = new ParserContext(input);

		command = parseCommand(ctx);
		valid &= command != null;

		valid &= ctx.finished() || parseParamSeparator(ctx);

		while (!ctx.finished()) {
			String key = parseParamKey(ctx);
			String value = null;

			if (parseParamAssign(ctx)) {
				value = parseParamValue(ctx);
				parseParamSeparator(ctx);
			} else if (parseParamSeparator(ctx) || ctx.finished()) {
				value = "true";
			}

			valid &= key != null;
			valid &= value != null;

			if (!valid) {
				break;
			}
			params.put(key, value);
		}
	}

	/**
	 * Parses the command from the input string.
	 * 
	 * @param ctx
	 *            ParserContext to use
	 * @return Command string or null if invalid.
	 */
	private String parseCommand(ParserContext ctx) {
		return parseIdentifier(ctx);
	}

	/**
	 * Parses a parameter-name.
	 * 
	 * @param ctx
	 *            ParserContext to use.
	 * @return Parameter name or null if invalid.
	 */
	private String parseParamKey(ParserContext ctx) {
		return parseIdentifier(ctx);
	}

	/**
	 * Parses a parameter value.
	 * 
	 * @param ctx
	 *            ParserContext to use.
	 * @return Returns a parameter value or null if invalid.
	 */
	private String parseParamValue(ParserContext ctx) {
		if (ctx.finished()) {
			return "";
		}

		char current = ctx.next();
		StringBuilder buf = new StringBuilder();

		if (current == '"' && !ctx.finished()) {
			current = ctx.next();

			while (current != '"') {
				buf.append(current);

				if (ctx.finished()) {
					return null;
				}
				current = ctx.next();
			}
		} else {
			while (current != ' ' && current != '\t') {
				buf.append(current);

				if (ctx.finished()) {
					break;
				}
				current = ctx.next();
			}

			if (!ctx.finished()) {
				ctx.back();
			}
		}

		return buf.toString();
	}

	/**
	 * Parses a parameter assignment operator.
	 * 
	 * @param ctx
	 *            ParserContext to use.
	 * @return true if one assignment operator parsed.
	 */
	private boolean parseParamAssign(ParserContext ctx) {
		if (!ctx.finished() && ctx.next() == '=') {
			return true;
		}
		ctx.back();
		return false;
	}

	/**
	 * Parses parameter separation operators (whitespaces).
	 * 
	 * @param ctx
	 *            ParserContext to use.
	 * @return true if at least one parameter seperator parsed.
	 */
	private boolean parseParamSeparator(ParserContext ctx) {
		boolean valid = false;
		while (!ctx.finished() && " \t".indexOf(ctx.next()) != -1) {
			valid = true;
		}

		if (!ctx.finished()) {
			ctx.back();
		}
		return valid;
	}

	/**
	 * Parses an identifier used for parameter-keys and the command.
	 * 
	 * @param ctx
	 *            ParserContext to use.
	 * @return Identifier or null if invalid.
	 */
	private String parseIdentifier(ParserContext ctx) {
		if (ctx.finished()) {
			return null;
		}
		StringBuilder buf = new StringBuilder();

		char current = ctx.next();

		while (IDENTIFIER.indexOf(current) != -1) {
			buf.append(current);

			if (ctx.finished()) {
				break;
			}
			current = ctx.next();
		}

		if (!ctx.finished()) {
			ctx.back();
		}

		return buf.length() == 0 ? null : buf.toString();
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();

		buf.append("Command: ");
		buf.append(command);
		buf.append(" {");

		boolean first = true;

		for (Map.Entry<String, String> e : params.entrySet()) {
			if (first) {
				first = false;
			} else {
				buf.append(", ");
			}
			buf.append(e.getKey());
			buf.append(": ");
			buf.append(e.getValue());
		}
		buf.append("}");

		return buf.toString();
	}
}