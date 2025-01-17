package com.osiris.dyml.db;

import com.osiris.dyml.DYValueContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the row of a {@link DYTable}. Example:
 * <pre>
 * column1 | column2
 * =================
 * rock    | paper   <--- This is the row at index 0
 * tree    | grass   <--- This is the row at index 1
 * </pre>
 *
 * @see DYColumn
 */
public class DYRow {
    private final int rowIndex;
    private final Map<DYValueContainer, DYColumn> valuesAndColumns;

    public DYRow(int rowIndex, Map<DYValueContainer, DYColumn> valuesAndColumns) {
        this.rowIndex = rowIndex;
        this.valuesAndColumns = valuesAndColumns;
    }

    /**
     * Values are read from left to right. Example table:
     * <pre>
     *     column1 | column2
     *     =================
     *     rock    | paper
     * </pre>
     * The examples, returned list has two {@link DYValueContainer}s. <br>
     * The first one contains 'rock' and the second one 'paper'. <br>
     */
    public List<DYValueContainer> getValues() {
        return new ArrayList<>(valuesAndColumns.keySet());
    }

    /**
     * This rows index position in the table. Example table:
     * <pre>
     *     column1 | column2
     *     =================
     *     rock    | paper   <--- Index: 0
     *     tree    | grass   <--- Index: 1
     * </pre>
     */
    public int getRowIndex() {
        return rowIndex;
    }


    /**
     * Returns a map with values mapped to their columns.
     */
    public Map<DYValueContainer, DYColumn> getValuesAndColumns() {
        return valuesAndColumns;
    }

    public DYColumn getColumnFromValue(DYValueContainer value) {
        return valuesAndColumns.get(value);
    }

    /**
     * Returns the linked {@link DYValueContainer} for the provided {@link DYColumn}.
     *
     * @throws NullPointerException if the provided column is null, or couldn't be found in the map.
     */
    public DYValueContainer getValueFromColumn(DYColumn column) {
        Objects.requireNonNull(column);
        DYValueContainer[] values = valuesAndColumns.keySet().toArray(new DYValueContainer[0]);
        int index = 0;
        DYValueContainer val = null;
        for (DYColumn col :
                valuesAndColumns.values()) {
            if (col.getName().equals(column.getName()))
                val = values[index];
            index++;
        }
        if (val == null)
            throw new NullPointerException("Column '" + column.getName() + "' couldn't be found in: " + values);
        else
            return val;
    }

}
