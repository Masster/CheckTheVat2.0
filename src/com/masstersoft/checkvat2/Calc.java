package com.masstersoft.checkvat2;

import java.math.BigDecimal;

/**
 * Created by Masster on 24.02.14.
 */
public class Calc {

    double Sum;
    double Vat;
    double SumN;
    double SumVN;
    double NDS1;
    double NDS2;

    public Calc(double S, double N) {
        Sum = S;
        Vat = N;
    }

    public double GetSum() {
        return Sum;
    }

    public double GetVat() {
        return Vat;
    }

    public double GetSumVN() {
        return SumVN;
    }

    public double GetSumN() {
        return SumN;
    }

    public double GetNDS1() {
        return NDS1;
    }

    public double GetNDS2() {
        return NDS2;
    }

    public void SetSum(double Val) {
        Sum = Val;
    }

    public void SetVat(double Val) {
        Vat = Val;
    }

    /*
    * Функция округляющая число до X знаков после запятой
    * */
    double RoundToX(double value) {
        BigDecimal decimal;
        decimal = new BigDecimal(value);
        decimal = decimal.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return decimal.doubleValue();
    }

    double RoundToX(double value, int X) {
        BigDecimal decimal;
        decimal = new BigDecimal(value);
        decimal = decimal.setScale(X, BigDecimal.ROUND_HALF_EVEN);
        return decimal.doubleValue();
    }

    /*
    * Sum - сумма поступающая от пользователя
    * N - величина НДС в процентах, например, N=18%
    * SumVN - сумма без НДС
    * SumN - сумма с НДС
    * NDS1 - НДС из суммы без НДС
    * NDS2 - НДС из суммы с НДС
    * */
    public void GetAll() {
        SumVN = Sum * 100 / (100 + Vat);
        SumVN = RoundToX(SumVN);

        NDS1 = Sum * Vat / 100;
        NDS1 = RoundToX(NDS1);

        SumN = Sum + NDS1;
        SumN = RoundToX(SumN);

        NDS2 = Sum - SumVN;
        NDS2 = RoundToX(NDS2);
    }
}
