# interactive-cli
An interactive cli for Java that can be used for example for prototyping applications with multiple threads that need to be controlled during runtime (e.g. server or client). This is very useful for testing diverse application states and transitions between them without rewriting the code every time.

## Example usage
InteractiveCLI cli = new InteractiveCLI();

cli.registerCommand("exit", Commands.EXIT);
cli.registerCommand("set", Commands.SETVAR);
cli.registerCommand("print", Commands.PRINTVAR);

cli.registerCommand("greet", (params, cli)->{
  String name = params.getOrDefault("name", "World");
  System.out.println("Hello " + name + "!");
});

cli.start();
