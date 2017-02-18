package playground;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.cli.Commands;
import common.cli.InteractiveCLI;

public class TestClass {
	private static final Logger logger = LogManager.getLogger(TestClass.class);
	
	public static void main(String[] args) {
		InteractiveCLI cli = new InteractiveCLI();
		
		cli.registerCommand("exit", Commands.EXIT);
		cli.registerCommand("setvar", Commands.SETVAR);
		cli.registerCommand("printvar", Commands.PRINTVAR);
		cli.registerCommand("help", Commands.HELP);
		cli.registerCommand("echo", Commands.ECHO);
		cli.registerCommand("exec", Commands.EXECUTE_FILE);

		cli.registerCommand("greet", (params, source)->{
		  String name = params.getOrDefault("name", "World");
		  System.out.println("Hello " + name + "!");
		});
		
		cli.setAsync(false);
		cli.start();
	}
}