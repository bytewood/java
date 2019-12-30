import bytewood.modules.api.Greeter;
import bytewood.modules.api.impl.DefaultGreeter;

module bytewood.modules.api {
    exports bytewood.modules.api;
    provides Greeter with DefaultGreeter;
}
