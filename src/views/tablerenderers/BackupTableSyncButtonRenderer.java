package views.tablerenderers;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * <br />
 * Created by Martin Sicho on 30.5.2014.
 */
public class BackupTableSyncButtonRenderer extends JButton implements TableCellRenderer {

    private int mRow = -1;
    private int mColumn = -1;

    public BackupTableSyncButtonRenderer() {
        setText("Synchronize");
        setOpaque(true);
    }

    public void setCoords(int row, int column) {
        mRow = row;
        mColumn = column;
    }

    /**
     * Returns the component used for drawing the cell.  This method is
     * used to configure the renderer appropriately before drawing.
     * <p/>
     * The <code>TableCellRenderer</code> is also responsible for rendering the
     * the cell representing the table's current DnD drop location if
     * it has one. If this renderer cares about rendering
     * the DnD drop location, it should query the table directly to
     * see if the given row and column represent the drop location:
     * <pre>
     *     JTable.DropLocation dropLocation = table.getDropLocation();
     *     if (dropLocation != null
     *             && !dropLocation.isInsertRow()
     *             && !dropLocation.isInsertColumn()
     *             && dropLocation.getRow() == row
     *             && dropLocation.getColumn() == column) {
     *
     *         // this cell represents the current drop location
     *         // so render it specially, perhaps with a different color
     *     }
     * </pre>
     * <p/>
     * During a printing operation, this method will be called with
     * <code>isSelected</code> and <code>hasFocus</code> values of
     * <code>false</code> to prevent selection and focus from appearing
     * in the printed output. To do other customization based on whether
     * or not the table is being printed, check the return value from
     * {@link javax.swing.JComponent#isPaintingForPrint()}.
     *
     * @param table      the <code>JTable</code> that is asking the
     *                   renderer to draw; can be <code>null</code>
     * @param value      the value of the cell to be rendered.  It is
     *                   up to the specific renderer to interpret
     *                   and draw the value.  For example, if
     *                   <code>value</code>
     *                   is the string "true", it could be rendered as a
     *                   string or it could be rendered as a check
     *                   box that is checked.  <code>null</code> is a
     *                   valid value
     * @param isSelected true if the cell is to be rendered with the
     *                   selection highlighted; otherwise false
     * @param hasFocus   if true, render cell appropriately.  For
     *                   example, put a special border on the cell, if
     *                   the cell can be edited, render in the color used
     *                   to indicate editing
     * @param row        the row index of the cell being drawn.  When
     *                   drawing the header, the value of
     *                   <code>row</code> is -1
     * @param column     the column index of the cell being drawn
     * @see javax.swing.JComponent#isPaintingForPrint()
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if(row == mRow && column == mColumn)
        {
            this.setSelected(true);
        }
        else
        {
            this.setSelected(false);
        }
        return this;
    }
}
