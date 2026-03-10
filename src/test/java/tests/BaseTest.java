package tests;

import config.TestConfig;
import org.aeonbits.owner.ConfigFactory;

public abstract class BaseTest {

    protected TestConfig config =
            ConfigFactory.create(TestConfig.class);

}