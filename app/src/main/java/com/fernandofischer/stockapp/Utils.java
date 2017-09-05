package com.fernandofischer.stockapp;

import java.math.BigDecimal;

/**
 * Created by fernandofischer on 03/09/17.
 */

public class Utils {

    public static int priceToInt(String value) {

        BigDecimal dollars = new BigDecimal(value.replace(",","."));
        if(dollars.scale()>2)
        {
            throw new IllegalArgumentException();
        }
        int cents = dollars.multiply(new BigDecimal(100)).intValue();

        return cents;
    }

    public static String priceToString(int value) {
        BigDecimal price = new BigDecimal(value).movePointLeft(2);
        return price.toString().replace(".",",");
    }

}
