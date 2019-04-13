package com.nanodegree.bianca.capstone;

import android.content.Context;
import android.util.Log;

public class Util {
    public static String formatSummary(String label, float value) {
        return String.format("%s US$ %.2f", label, value);
    }
}
