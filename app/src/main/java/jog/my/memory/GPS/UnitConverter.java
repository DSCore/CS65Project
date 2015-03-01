package jog.my.memory.GPS;

/**
 * Created by Devon Cormack on 2/1/15.
 */
public class UnitConverter {

    /**
     * Converts value from imperial to metric units.
     * @param value - value to convert
     * @return - Imperial value to convert
     */
    static public Double convertImperialToMetric(String value){
        double val = Double.parseDouble(value);
        val *= 1.60934;
        return val;
    }

    static public Double convertImperialToMetric(final double value){
        double val = 1.60934*value;
        return val;
    }
}
