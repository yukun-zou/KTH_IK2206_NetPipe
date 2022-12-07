import java.util.Properties;

/**
 * Process command line arguments of the form "--argument=value".
 * Store arguments as in Arguments object (derived from Properties).
 *
 */

public class Arguments extends Properties {
    Properties argumentSpecs = new Properties();

    public void setDefault(String arg, String value) {
        this.setProperty(arg, value);
    }

    public void setArgumentSpec(String arg, String valuedescription) {
        this.argumentSpecs.setProperty(arg, valuedescription);
    }

    public void loadArguments(String args[]) throws IllegalArgumentException {
        for(String argument : args) {
            if(!argument.startsWith("--")) {
                    throw new IllegalArgumentException("Argument does not start with \"--\"");
            }
            String[] keyValue = argument.substring(2).split("=", 2);

            if (this.argumentSpecs.getProperty(keyValue[0]) == null) {
                throw new IllegalArgumentException("Illegal Argument: \"" +  keyValue[0] + "\"");
            }
            if(keyValue.length != 2 || keyValue[1].length() < 1) {
                throw new IllegalArgumentException("Argument \"" +  keyValue[0] + "\" needs a value");
            }
            this.setProperty(keyValue[0], keyValue[1]);
        }
    }

    public String get(String arg) {
        return this.getProperty(arg);
    }
}

