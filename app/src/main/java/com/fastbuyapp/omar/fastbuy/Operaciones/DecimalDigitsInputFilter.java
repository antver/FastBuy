package com.fastbuyapp.omar.fastbuy.Operaciones;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalDigitsInputFilter implements InputFilter {
    /*Pattern mPattern;
    public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
        mPattern= Pattern.compile("[0-9]{0," + (digitsBeforeZero) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher=mPattern.matcher(dest);

        if(!matcher.matches())
            return "";

        return null;
    }*/
    private int mDigitsBeforeZero;
    private int mDigitsAfterZero;
    private Pattern mPattern;
    private static final int DIGITS_BEFORE_ZERO_DEFAULT = 100;
    private static final int DIGITS_AFTER_ZERO_DEFAULT = 100;
    public DecimalDigitsInputFilter(Integer digitsBeforeZero, Integer digitsAfterZero) {
        this.mDigitsBeforeZero = (digitsBeforeZero != null ? digitsBeforeZero : DIGITS_BEFORE_ZERO_DEFAULT);
        this.mDigitsAfterZero = (digitsAfterZero != null ? digitsAfterZero : DIGITS_AFTER_ZERO_DEFAULT);
        mPattern = Pattern.compile("-?[0-9]{0," + (mDigitsBeforeZero) + "}+((\\.[0-9]{0," + (mDigitsAfterZero) + "})?)||(\\.)?");
    }
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String replacement = source.subSequence(start, end).toString();
        String newVal = dest.subSequence(0, dstart).toString() + replacement + dest.subSequence(dend, dest.length()).toString();
        Matcher matcher = mPattern.matcher(newVal);
        if (matcher.matches())
            return null;
        if (TextUtils.isEmpty(source))
            return dest.subSequence(dstart, dend);
        else
            return "";
    }
}