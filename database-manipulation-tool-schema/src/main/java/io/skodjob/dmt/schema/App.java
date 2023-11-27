package io.skodjob.dmt.schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "TEST RUN FOR DMT SCHEMA" );
        DatabaseEntry entry = new DatabaseEntry("TestTableName", "name");
        entry.addColumnEntry(new DatabaseColumnEntry("testValue1", "name", "testDataType1"));
        entry.addColumnEntry(new DatabaseColumnEntry("testValue2", "name2", "testDataType2"));
        try {
            var str = new ObjectMapper().writeValueAsString(entry);
            System.out.println("STRING: " + str);
            var parsedString = new ObjectMapper().readValue(str, DatabaseEntry.class);
            System.out.println("DATABASE_ENTRY: " + parsedString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println( "TEST RUN FOR DMT LOAD RESULT SCHEMA" );
        LoadResult loadResult = new LoadResult(1000, 100, 950);
        try {
            var str = new ObjectMapper().writeValueAsString(loadResult);
            System.out.println("STRING: " + str);
            var parsedString = new ObjectMapper().readValue(str, LoadResult.class);
            System.out.println("LOAD_RESULT: " + parsedString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
