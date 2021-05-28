package com.osiris.dyml;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DYModuleTest {

    @Test
    void nullValuesTest() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/null-values-test.yml");
        yaml.reset(); // Make sure the file is empty
        DYModule nullValueModule = yaml.add("i have no value")
                .setValue((DYValue) null)
                .setDefValue((DYValue) null);
        yaml.saveAndReload();
        assertTrue(null == nullValueModule.getValue());
        assertTrue(null == nullValueModule.getDefaultValue());
        DYModule secondModule = yaml.add("im also empty inside");
        yaml.save(true);
    }
}