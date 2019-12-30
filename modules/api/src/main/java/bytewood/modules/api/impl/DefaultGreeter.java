package bytewood.modules.api.impl;

import bytewood.modules.api.Greeter;

public class DefaultGreeter implements Greeter {
    @Override
    public String hello(String name) {
        return "Hello " + name;
    }
}
