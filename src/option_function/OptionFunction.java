package option_function;

public class OptionFunction {

    private final String option;
    private final Service_Function function;

    public OptionFunction(String option, Service_Function function){
        this.option = option;
        this.function = function;
    }

    public String getOption() {
        return option;
    }

    public Service_Function getFunction() {
        return function;
    }

}
