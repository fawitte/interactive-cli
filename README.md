# interactive-cli
An interactive cli for Java that can be used for example for prototyping applications with multiple threads that need to be controlled during runtime (e.g. server or client). This is very useful for testing diverse application states and transitions between them without rewriting the code every time.

## Example usage
The example below demonstrates an easy way for using this "library" and creating your own commands for controlling your application.
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

If you implement the above code in a main-Method and run it, you can use the console to trigger commands. 

Try the following for example:
```
greet
greet name=Heinz
setvar who="Max Mustermann"
greet name=[who]
exit
```

The above cli-commands should print the following output:
```
Hello World!
Hello Heinz!
Hello Max Mustermann!
```

## Example executing commands from a file
Sometimes it is way more comfortable to use a list of commands already defined in a file, so does this example.

The first thing to do here is specify the commands that can be used by the file and inside the console after starting the application:
```
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

cli.start();
```

Then we can write a simple text file containing the commands we wish to be executed in linear order. Every line of the file is interpreted a exactly one command.

batch.txt:
```
echo text="Let's greet a lot!"
setvar karl.name="Karl-Heinz Müller" karl.age="42"
setvar hans.name="Hans Peter" hans.age="21"
greet name=[karl.name]
greet name=[hans.name]
printvar
exit
```

Save the file in the root directory of you appliction and type the following into your console after starting the program:
```
exec filename="batch.txt"
```

Expected console output:
```
Executing file: exec/testvars.txt
Let's greet a lot!
Hello Karl-Heinz Müller!
Hello Hans Peter!
Variables:
	karl.name: Karl-Heinz Müller
	hans.age: 21
	karl.age: 42
	hans.name: Hans Peter
.
```

You can also easily print every line before executing it (e.g. for debug purposes):
```
exec filename="batch.txt" print-lines
```
would result in the following output:
```
Executing file: exec/testvars.txt
// echo text="Let's greet a lot!"
Let's greet a lot!
// setvar karl.name="Karl-Heinz Müller" karl.age="42"
// setvar hans.name="Hans Peter" hans.age="21"
// greet name=[karl.name]
Hello Karl-Heinz Müller!
// greet name=[hans.name]
Hello Hans Peter!
// printvar
Variables:
	karl.name: Karl-Heinz Müller
	hans.age: 21
	karl.age: 42
	hans.name: Hans Peter
.
// exit
```
