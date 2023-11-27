package io.skodjob.load.data.builder;

import io.skodjob.dmt.schema.DatabaseColumnEntry;
import io.skodjob.dmt.schema.DatabaseEntry;
import io.skodjob.load.data.DataTypeConvertor;
import io.skodjob.load.data.enums.Tables;

public class FoodDataBuilder extends DataBuilder {

    public FoodDataBuilder() {
        super();
        super.table = Tables.FOOD;
    }

    @Override
    public DatabaseEntry generateDataRow(Integer idPool) {
        DatabaseEntry schema = createDefaultScheme(RANDOM.nextInt(idPool), table.toString().toLowerCase());
        //schema.setOperation(randomEnum(Operations.class, RANDOM).toString().toLowerCase());

        String dish = dataFaker.food().dish();

        schema.getColumnEntries().add(new DatabaseColumnEntry(dish, "dish",
                DataTypeConvertor.convertDataType(dish)));


        int counterI = 0;
        for (; counterI < RANDOM.nextInt(7); counterI++) {
            String ing = dataFaker.food().ingredient();
            schema.getColumnEntries().add(new DatabaseColumnEntry(ing, String.format("ingredient_%d", counterI),
                    DataTypeConvertor.convertDataType(ing)));
        }
        schema.getColumnEntries().add(new DatabaseColumnEntry(String.valueOf(counterI), "ingredients",
                DataTypeConvertor.convertDataType(counterI)));


        int counterV = 0;
        for (; counterV < RANDOM.nextInt(5); counterV++) {
            String ing = dataFaker.food().vegetable();
            schema.getColumnEntries().add(new DatabaseColumnEntry(ing, String.format("vegetable_%d", counterI),
                    DataTypeConvertor.convertDataType(ing)));
        }
        schema.getColumnEntries().add(new DatabaseColumnEntry(String.valueOf(counterV), "vegetables",
                DataTypeConvertor.convertDataType(counterV)));

        int counterS = 0;
        for (; counterS < RANDOM.nextInt(6); counterS++) {
            String ing = dataFaker.food().spice();
            schema.getColumnEntries().add(new DatabaseColumnEntry(ing, String.format("spice_%d", counterS),
                    DataTypeConvertor.convertDataType(ing)));
        }
        schema.getColumnEntries().add(new DatabaseColumnEntry(String.valueOf(counterS), "spices",
                DataTypeConvertor.convertDataType(counterS)));

        normalise(schema);
        return schema;
    }
}
