package config;

import org.aeonbits.owner.Config;

@Config.Sources({
        "classpath:application.properties"
})
public interface TestConfig extends Config {

    @Key("base.url")
    String baseUrl();
}
