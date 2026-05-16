package com.dismai.initialize.execute;

import com.dismai.initialize.execute.base.AbstractApplicationExecute;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;

import static com.dismai.initialize.constant.InitializeHandlerType.APPLICATION_COMMAND_LINE_RUNNER;

public class ApplicationCommandLineRunnerExecute extends AbstractApplicationExecute implements CommandLineRunner {
    
    public ApplicationCommandLineRunnerExecute(ConfigurableApplicationContext applicationContext){
        super(applicationContext);
    }
    
    @Override
    public void run(final String... args) {
        execute();
    }
    
    @Override
    public String type() {
        return APPLICATION_COMMAND_LINE_RUNNER;
    }
}
