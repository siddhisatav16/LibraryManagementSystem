package com.finlogic.dao;

import java.util.prefs.Preferences;

public class SettingsDAO {
    private static final Preferences prefs = Preferences.userRoot().node("com.finlogic.library");

    public static double getPenaltyRate() {
        return prefs.getDouble("penaltyRate", 10.0);
    }

    public static void setPenaltyRate(double rate) {
        prefs.putDouble("penaltyRate", rate);
    }
}
