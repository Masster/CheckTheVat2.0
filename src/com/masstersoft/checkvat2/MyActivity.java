package com.masstersoft.checkvat2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

public class MyActivity extends Activity {
    final String PrefMain = "MainPreferenses";
    /**
     * Called when the activity is first created.
     */

    private final String PREF_NAME = "CheckTheVAT_prefs";
    private final String DayOfInstall = "DayOfInstall";
    private final String YearOfInstall = "YearOfInstall";
    public EditText edSum;
    public Button edVat;
    public TextView tvSumN;
    public TextView tvSumVN;
    public TextView tvNDS1;
    public TextView tvNDS2;
    public TextView tvSum;
    public Calc cl;
    double Sum;
    double Vat;
    int DecN;
    int ReN;
    SharedPreferences sPref;
    String stringPref;

    InputMethodManager IMM;
    DecimalFormat DF;

    private Locale locale;
    private String lang;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sPref = getSharedPreferences(PrefMain, Context.MODE_PRIVATE);
        lang = sPref.getString("lang", "default");
        if (lang.equals("default")) {
            lang = getResources().getConfiguration().locale.getCountry();
        }
        locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, null);

        setContentView(R.layout.main);

        edSum = (EditText) findViewById(R.id.edAmount);
        edVat = (Button) findViewById(R.id.edVat);

        edVat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog(DecN, ReN);
            }
        });
        DecN = 0;
        ReN = 0;

        tvSumN = (TextView) findViewById(R.id.tvSumN);
        tvSumVN = (TextView) findViewById(R.id.tvSumVN);
        tvNDS1 = (TextView) findViewById(R.id.tvNDS1);
        tvNDS2 = (TextView) findViewById(R.id.tvNDS2);
        tvSum = (TextView) findViewById(R.id.tvSum);

        Sum = 0;
        Vat = 0;

        cl = new Calc(0, 0);
        IMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        DF = new DecimalFormat();
        DF.setGroupingSize(3);

        int trialPeriod = -1;
        trialPeriod = CheckTrialPeriod(30);
        boolean keyProgram = CheckKeyProgram();

        System.out.println("trialPeriod = " + String.valueOf(trialPeriod) + " keyProgram = " + String.valueOf(keyProgram));

        if (trialPeriod == 3 && !keyProgram)
            setContentView(R.layout.main2);


    }

    private int CheckTrialPeriod(int deltaDays) {
        Calendar cal = Calendar.getInstance();
        SharedPreferences ShPref;
        SharedPreferences.Editor edit;
        ShPref = this.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        edit = ShPref.edit();
        int res = -1;

        if (!ShPref.contains(DayOfInstall)) {
            // Это первый запуск приложения, поэтому сохраняю день года и сам год, когда программу установили.
            edit.putInt(DayOfInstall, cal.get(Calendar.DAY_OF_YEAR));
            edit.putInt(YearOfInstall, cal.get(Calendar.YEAR));
            edit.commit();
            res = 1;
        } else {
            // Не первый запуск уже, стоит проверить что да как с датой установки программы.
            int day = ShPref.getInt(DayOfInstall, cal.get(Calendar.DAY_OF_YEAR));
            int year = ShPref.getInt(YearOfInstall, cal.get(Calendar.YEAR));
            if (cal.get(Calendar.DAY_OF_YEAR) - day > deltaDays) {
                // Приложение стоит более deltaDays дней, значит надо программу блокировать.
                res = 3;
            } else
                res = 2;
        }
        return res;
    }

    private boolean CheckKeyProgram() {
        boolean res;
        String packageName = "com.masster.MultyLay";
        PackageManager pm = this.getPackageManager();
        PackageInfo pi = null;
        try {
            if (pm != null) {
                pi = pm.getPackageInfo(packageName, 0);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        res = (pi != null);

        return res;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void ShowDialog(int dn, int rn) {
        final Dialog d = new Dialog(MyActivity.this);
        d.setTitle("Ставка НДС");
        d.setContentView(R.layout.dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.numberPicker2);
        np.setMaxValue(40);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
        np.setValue(dn);
        np2.setMaxValue(9);
        np2.setMinValue(0);
        np2.setWrapSelectorWheel(false);
        np2.setValue(rn);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edVat.setText(String.valueOf(np.getValue()) + '.' + String.valueOf(np2.getValue()));
                DecN = np.getValue();
                ReN = np2.getValue();
                testPress(v);
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    public void testPress(View v) {
        GetValues();

        cl.SetSum(Sum);
        cl.SetVat(Vat);
        cl.GetAll();

        SetValue(cl.GetSumVN(), tvSumVN);
        SetValue(cl.GetSumN(), tvSumN);
        SetValue(cl.GetNDS1(), tvNDS1);
        SetValue(cl.GetNDS2(), tvNDS2);
        SetValue(cl.GetSum(), tvSum);

        IMM.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void GetValues() {
        Sum = 0;
        Vat = 0;
        if (edSum.getText().toString().length() > 0) Sum = Double.parseDouble(edSum.getText().toString());
        if (edVat.getText().toString().length() > 0) Vat = Double.parseDouble(edVat.getText().toString());
    }

    public void SetValue(double Value, TextView tv) {
        tv.setText(String.format("%,.2f", Value));
    }

    public void ClearInput(View v) {
        edSum.setText("");
        edSum.setActivated(true);
        testPress(v);
        IMM.showSoftInput(edSum, InputMethodManager.SHOW_FORCED);
    }

    @Override
    protected void onPause() {
        super.onPause();

        stringPref = String.valueOf(DecN) + ";" + String.valueOf(ReN) + ";" + String.valueOf(Sum);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(PrefMain, stringPref);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String str[];
        if (sPref.contains(PrefMain)) {
            stringPref = sPref.getString(PrefMain, "0;0;0");
            str = stringPref.split(";");
            DecN = Integer.parseInt(str[0]);
            ReN = Integer.parseInt(str[1]);
            Vat = DecN + ReN / 10.0;
            Sum = Double.parseDouble(str[2]);
            cl.SetSum(Sum);
            cl.SetVat(Vat);
            cl.GetAll();

            SetValue(cl.GetSumVN(), tvSumVN);
            SetValue(cl.GetSumN(), tvSumN);
            SetValue(cl.GetNDS1(), tvNDS1);
            SetValue(cl.GetNDS2(), tvNDS2);
            SetValue(cl.GetVat(), edVat);
            SetValue(cl.GetSum(), tvSum);
            edVat.setText(String.valueOf(cl.GetVat()));
            edSum.setText(String.format("%.2f", cl.GetSum()).replaceAll(",", "."));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cat2:
                //Купить

                return true;
            case R.id.action_cat3:
                //About
                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
                builder.setTitle(R.string.action_kitten)
                        .setMessage("Программа разработана компанией MassterSoft.com")
                        .setIcon(R.drawable.ic_launcher)
                        .setCancelable(false)
                        .setNegativeButton("ОК",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                }
                        );
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            case R.id.action_settings:
                //Язык
                final String[] langs = {"Русский", "English"};

                AlertDialog.Builder builder1 = new AlertDialog.Builder(MyActivity.this);
                builder1.setTitle(R.string.action_settings)
                        .setItems(langs, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //==================================== эти вещи надо переделывать неподетски! ======================
                                System.out.println(langs[which]);
                                if (which == 0) lang = "ru";
                                else
                                    lang = "en";
                                locale = new Locale(lang);
                                Locale.setDefault(locale);
                                Configuration config = new Configuration();
                                config.locale = locale;
                                getBaseContext().getResources().updateConfiguration(config, null);
                                SharedPreferences.Editor editor = sPref.edit();
                                editor.putString("lang", lang);
                                editor.commit();
                                //======================================================
                            }
                        })
                        .setIcon(R.drawable.ic_launcher)
                        .setCancelable(false);
                AlertDialog alert1 = builder1.create();
                alert1.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, null);
    }
}
