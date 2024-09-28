package ru.tensor.sbis.network_native.parser.model;

/**
 * Legacy-код
 * <p>
 * Created by ss.buvaylink on 11.11.2015.
 */
@SuppressWarnings("unused")
public class SingleResult {

    private String result;

    public SingleResult() {
    }

    public SingleResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public int getAsInt() {
        int res = -1;
        if (result != null) {
            res = Integer.parseInt(result);
        }
        return res;
    }

    public boolean getAsBoolean() {
        boolean res = false;
        if (result != null) {
            res = Boolean.parseBoolean(result);
        }
        return res;
    }
}