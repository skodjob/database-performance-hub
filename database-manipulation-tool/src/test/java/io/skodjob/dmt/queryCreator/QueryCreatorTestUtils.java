package io.skodjob.dmt.queryCreator;

import io.skodjob.dmt.model.DatabaseColumn;
import io.skodjob.dmt.model.DatabaseColumnEntry;
import io.skodjob.dmt.model.DatabaseEntry;
import io.skodjob.dmt.model.DatabaseTableMetadata;

import java.util.ArrayList;
import java.util.List;

public class QueryCreatorTestUtils {
    public static DatabaseTableMetadata generateTableMetadata() {
        DatabaseTableMetadata tableMetadata = new DatabaseTableMetadata();
        tableMetadata.setName("TestTable");
        DatabaseColumn primaryColumn = new DatabaseColumn("PrimaryColumn", "INT", true);
        DatabaseColumn column1 = new DatabaseColumn("Column1", "TEXT", false);
        DatabaseColumn column2 = new DatabaseColumn("Column2", "DOUBLE", false);
        tableMetadata.addColumn(primaryColumn);
        tableMetadata.addColumn(column1);
        tableMetadata.addColumn(column2);
        return tableMetadata;
    }

    public static DatabaseEntry generateDatabaseEntry() {
        List<DatabaseColumnEntry> columnEntries = new ArrayList<>();
        columnEntries.add(new DatabaseColumnEntry("100", "PrimaryColumn", "INT"));
        columnEntries.add(new DatabaseColumnEntry("test", "Column1", "TEXT"));
        columnEntries.add(new DatabaseColumnEntry("25.11", "Column2", "DOUBLE"));
        return new DatabaseEntry(columnEntries, generateTableMetadata());
    }
}
