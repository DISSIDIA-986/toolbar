package com.dissidia986.util;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CompoundInterestUtil {

     public static class InputHelper {

          public static String getInput(String prompt) {
               BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

               System.out.print(prompt);
               System.out.flush();
              
               try {
                    return stdin.readLine();
               } catch (Exception e) {
                    return "Error: " + e.getMessage();
               }
          }
     }
     public static void main(String[] args) {

          /**
          * Standard Compound Interest formula A = P(1+R)^n A = total amount P =
          * amount you put in R = interest rate n = period in years
          */

          System.out.println("复利——时间的力量");
          StringBuffer sb = new StringBuffer();
          for(int i=1;i<=30;i++){
               BigDecimal addRate = new BigDecimal("1.08").setScale(8, BigDecimal.ROUND_HALF_DOWN);
              
               sb.append(i).append("年:").append(addRate.pow(i).setScale(8, BigDecimal.ROUND_HALF_DOWN).doubleValue()).append("\r");
          }
          System.out.println(sb.toString());
          String inputAmount = InputHelper.getInput("输入每年存款 (i.e 10000): ");
          String inputRate = InputHelper.getInput("输入年化收益率 (i.e 3): ");
          String inputYears = InputHelper.getInput("输入年限 (i.e 10): ");

          // 每年存款
          BigDecimal amountAnnual = new BigDecimal(inputAmount).setScale(8,
                    BigDecimal.ROUND_HALF_DOWN);
          // Bank's interest rate in double
          BigDecimal rate = new BigDecimal(inputRate).divide(new BigDecimal("100")).setScale(4,
                    BigDecimal.ROUND_HALF_DOWN);
          // Years in integer you want your money to be saved
          int years = Integer.parseInt(inputYears);

          SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM");
          Calendar startCal = Calendar.getInstance();
          for (int x = 1; x <= years; x++) {
               startCal.add(Calendar.YEAR, 1);
               BigDecimal sumThatYear = BigDecimal.ZERO;
               for (int y = 1; y <= x; y++) {
                    BigDecimal totalOne = amountAnnual.multiply(BigDecimal.ONE.add(rate).pow(y))
                              .setScale(8, BigDecimal.ROUND_HALF_DOWN);
                    sumThatYear = sumThatYear.add(totalOne);
               }
               sumThatYear = sumThatYear.setScale(8, BigDecimal.ROUND_HALF_DOWN);
               System.out.printf("时间:%s\t初始存款:%s\t年存款:%s\t年收益率:%s\t投入总额:%s\t本息合计:%s", sdf
                         .format(startCal.getTime()), sumThatYear.subtract(amountAnnual).doubleValue(),
                         amountAnnual.intValue(), rate.doubleValue(),
                         amountAnnual.multiply(new BigDecimal(x))
                                   .setScale(4, BigDecimal.ROUND_HALF_DOWN).doubleValue(), sumThatYear
                                   .doubleValue());
               System.out.println();
          }

     }

}