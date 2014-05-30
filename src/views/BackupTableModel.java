package views;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.Date;
import java.util.Vector;

/**
 * <br />
 * Created by Martin Sicho on 30.5.2014.
 */
class BackupTableModel extends AbstractTableModel {

    private String[] mTableHeader = {
            "Selected"
            , "Synchronize"
            , "Name"
            , "Original Directory"
            , "Backup Directory"
            , "Shallow"
            , "Last Synchronized"
    };
    private Vector<Vector> mTableData;

    BackupTableModel() {
        mTableData = new Vector<>();
    }

    public void fillRow(String name, String original, String backup, boolean shallow, Date date) {
        Vector<Object> row = new Vector<>();
        row.add(new JCheckBox());
        row.add(new JButton("Synchronize"));
        row.add(name);
        row.add(original);
        row.add(backup);
        row.add(shallow);
        row.add(date);
        mTableData.add(row);
    }

    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    @Override
    public int getRowCount() {
        if (mTableData.isEmpty()) {
            return 0;
        }
        else {
            return mTableData.size();
        }
    }

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    @Override
    public int getColumnCount() {
        return mTableHeader.length;
    }

    /**
     * Returns a default name for the column using spreadsheet conventions:
     * A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
     * returns an empty string.
     *
     * @param column the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    @Override
    public String getColumnName(int column) {
        return mTableHeader[column];
    }

    /**
     * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     *
     * @param columnIndex the column being queried
     * @return the Object.class
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return mTableData.get(rowIndex).get(columnIndex);
    }

    /**
     * Returns false.  This is the default implementation for all cells.
     *
     * @param rowIndex    the row being queried
     * @param columnIndex the column being queried
     * @return false
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex < 2;
    }
}
