package common.cli;

import java.util.Map;

public interface CommandInterface {
	void execute(Map<String,String> params, InteractiveCLI source);
}