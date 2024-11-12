package by.bsu.dependency.context;

import org.junit.jupiter.api.BeforeEach;

class AutoScanApplicationContextTest {

    private ApplicationContext applicationContext;

    @BeforeEach
    void init() {
        applicationContext = new AutoScanApplicationContext("by.bsu.dependency.example");
    }

    //TODO

}