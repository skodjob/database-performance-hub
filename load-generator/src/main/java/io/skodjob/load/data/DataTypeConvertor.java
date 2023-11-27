package io.skodjob.load.data;

public class DataTypeConvertor {

    public static String convertDataType(Object object) {
        if (object instanceof String) {
            //return String.format("VarChar(%s)", ((String) object).length());
            return "VarChar(255)";
        } else if (object instanceof Integer) {
            return "Integer";
        } else if (object instanceof Double) {
            return "Double";
        } else {
            return "Null";
        }
    }

}
