package bytewood.modules.app;

import bytewood.modules.api.Greeter;

import java.util.ServiceLoader;

public class Main {

    public static void main(String[] args) {
        ServiceLoader.load(Greeter.class)
                .findFirst()
                .ifPresent(greeter ->
                    System.out.println(greeter.hello("You"))
                );
    }
}
