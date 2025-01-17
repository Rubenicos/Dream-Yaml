package com.osiris.dyml.db;

import com.osiris.dyml.DYModule;
import com.osiris.dyml.DreamYaml;
import com.osiris.dyml.exceptions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * A single DreamYamlDB (DreamYamlDatabase) object, represents a single database. <br>
 * To be more exact, its just a single yaml file with a structure like this: <br>
 * <pre>
 * tables:
 *   table_potatoes:
 *     column1_potato_names:
 *       - Russet                     # First row
 *       - Jewel Yam                  # Second row
 *       -                            # Third row
 *     column2_potato_description:
 *       - Most people know this one as the "classic potato".
 *       -    # This means there is no value, thus it returns null
 *       - "" # Note that this also returns null, there are no empty values
 *   another_table:
 *     column1:
 *       - ....
 * </pre>
 * This class provides several database related methods, that don't exist
 * in the regular {@link DreamYaml} class. <br>
 * Note that this database is persistent, but you will have to call {@link #save()} manually to achieve this. <br>
 * Also note that the database (yaml file) gets loaded into memory, thus changing/retrieving/deleting and generally working
 * with values is a lot faster compared to regular databases, but this also means that its more memory intensive. <br>
 * That's why DreamYamlDB is perfect for working with small to medium amounts of data.
 */
public class DreamYamlDB {
    private DreamYaml yaml;

    /**
     * Creates a new yaml file in the current working directory, with a random, unused name.
     */
    public DreamYamlDB() {
        String name = "DreamYaml-DB-" + new Random().nextInt(10000000);
        File yamlFile = null;
        for (int i = 1; i < 11; i++) {
            try {
                yamlFile = new File(System.getProperty("user.dir") + "/" + name + ".yml");
                if (!yamlFile.exists())
                    break;
                else
                    name = "DreamYaml-DB-" + new Random().nextInt(10000000);
            } catch (Exception ignored) {
            }
        }
        init(yamlFile);
    }

    /**
     * Creates a yml file in the current working directory with the provided name
     * and uses that as the database. <br>
     *
     * @param name of the database.
     */
    public DreamYamlDB(String name) {
        init(new File(System.getProperty("user.dir") + "/" + name + ".yml"));
    }

    public DreamYamlDB(Path yamlFilePath) {
        init(yamlFilePath.toFile());
    }

    public DreamYamlDB(File yamlFile) {
        init(yamlFile);
    }

    public DreamYamlDB(DreamYaml yaml) {
        init(yaml);
    }

    private void init(File yamlFile) {
        init(new DreamYaml(yamlFile));
    }

    private void init(DreamYaml yaml) {
        Objects.requireNonNull(yaml);
        this.yaml = yaml;
        yaml.setRemoveLoadedNullValuesEnabled(false);
    }

    public DreamYaml getYaml() {
        return yaml;
    }

    /**
     * This is the first thing you should do after initialising. <br>
     * See {@link DreamYaml#load()} for details.
     */
    public DreamYamlDB load() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        yaml.load();
        return this;
    }

    /**
     * See {@link DreamYaml#save()} for details.
     */
    public DreamYamlDB save() throws DYWriterException, IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        yaml.save();
        return this;
    }

    /**
     * See {@link DreamYaml#saveAndLoad()} for details.
     */
    public DreamYamlDB saveAndLoad() throws DYWriterException, IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        yaml.saveAndLoad();
        return this;
    }

    /**
     * Note that this triggers {@link #saveAndLoad()}, to ensure the database/yaml-file, <br>
     * as well as the parent/child modules of this {@link DreamYamlDB} object are up-to-date. <br>
     * See {@link DreamYaml#add(String...)} for details.
     */
    public DYTable addTable(String name) throws NotLoadedException, IllegalKeyException, DuplicateKeyException, DYWriterException, IOException, DYReaderException, IllegalListException {
        DYTable table = new DYTable(yaml.add("tables", name));
        yaml.saveAndLoad();
        return table;
    }

    /**
     * Note that this triggers {@link #saveAndLoad()}, to ensure the database/yaml-file, <br>
     * as well as the parent/child modules of this {@link DreamYamlDB} object are up-to-date. <br>
     * See {@link DreamYaml#put(String...)} for details.
     */
    public DYTable putTable(String name) throws NotLoadedException, IllegalKeyException, DYWriterException, IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        DYTable table = new DYTable(yaml.put("tables", name));
        yaml.saveAndLoad();
        return table;
    }

    /**
     * Note that this triggers {@link #saveAndLoad()}, to ensure the database/yaml-file, <br>
     * as well as the parent/child modules of this {@link DreamYamlDB} object are up-to-date. <br>
     * See {@link DreamYaml#remove(DYModule)} for details. <br>
     */
    public DreamYamlDB removeTable(DYTable table) {
        Objects.requireNonNull(table);
        yaml.remove("tables", table.getName());
        return this;
    }

    /**
     * Note that the returned {@link DYTable} object is not persistent. <br>
     * That means that when you call this method for the same table again,
     * another {@link DYTable} object is returned. <br>
     */
    public DYTable getTable(String name) {
        List<DYTable> tables = getTables();
        for (DYTable t :
                tables) {
            if (t.getName().equals(name))
                return t;
        }
        return null;
    }

    public DYTable getTableAtIndex(int index) {
        return getTables().get(index);
    }

    public List<DYTable> getTables() {
        List<DYTable> tables = new ArrayList<>();
        for (DYModule tableModule :
                yaml.get("tables").getChildModules()) {
            tables.add(new DYTable(tableModule));
        }
        return tables;
    }

}
