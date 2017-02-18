# interactive-cli
An interactive cli for Java that can be used for example for prototyping applications with multiple threads that need to be controlled during runtime (e.g. server or client). This is very useful for testing diverse application states and transitions between them without rewriting the code every time.

## Example usage
```
InteractiveCLI cli = new InteractiveCLI();

cli.registerCommand("exit", Commands.EXIT);
cli.registerCommand("setvar", Commands.SETVAR);
cli.registerCommand("printvar", Commands.PRINTVAR);

cli.registerCommand("greet", (params, source)->{
  String name = params.getOrDefault("name", "World");
  System.out.println("Hello " + name + "!");
});

cli.start();
```

If you implement the above code in a main-Method and run it, you can use the console to trigger commands. Try the following for example:
```
greet
setvar who="Max Mustermann"
greet name=[who]
exit
```
The above cli-commands should print the following output:
> Hello World!
>
> Hello Max Mustermann!
