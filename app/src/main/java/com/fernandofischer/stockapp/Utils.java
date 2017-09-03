package com.fernandofischer.stockapp;

import java.math.BigDecimal;

/**
 * Created by fernandofischer on 03/09/17.
 */

public class Utils {

    public static int priceToInt(String value) {

   /*     NumberFormat nf = NumberFormat.getInstance();
        Number number = null;
        try {
            number = nf.parse(value.replace(",","."));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //long l = number.longValue();

        return (int) (number.longValue()*100); */

        BigDecimal dollars = new BigDecimal(value.replace(",","."));
        if(dollars.scale()>2)
        {
            throw new IllegalArgumentException();
        }
        int cents = dollars.multiply(new BigDecimal(100)).intValue();

        return cents;
    }

    public static String priceToString(int value) {
       // NumberFormat n = NumberFormat();//.   getCurrencyInstance(Locale.US);
       // String s = n.format(value / 100.0);
      //  return s;
        BigDecimal price = new BigDecimal(value).movePointLeft(2);

        return price.toString().replace(".",",");
    }

}
