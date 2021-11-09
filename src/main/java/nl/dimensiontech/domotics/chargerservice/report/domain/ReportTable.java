package nl.dimensiontech.domotics.chargerservice.report.domain;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReportTable {

    private final Table<Integer, TableColumn, TableCell> table = HashBasedTable.create();
    private final List<TableColumn> tableColumns = new ArrayList<>();

    public void createRow(List<TableCell> tableCells) {
        int rowNum = table.rowKeySet().size() + 1;

        for (int i = 0; i < tableCells.size(); i++) {
            table.put(rowNum, tableColumns.get(i), tableCells.get(i));
        }
    }
}
