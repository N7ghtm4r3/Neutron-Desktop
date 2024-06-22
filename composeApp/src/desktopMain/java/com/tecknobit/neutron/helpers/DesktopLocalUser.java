package com.tecknobit.neutron.helpers;

import com.tecknobit.neutroncore.helpers.LocalUser;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class DesktopLocalUser extends LocalUser {

    private final Preferences preferences;

    public DesktopLocalUser() {
        preferences = Preferences.userRoot().node("tecknobit/neutron/desktop");
        initLocalUser();
    }

    @Override
    protected void setPreference(String key, String value) {
        if (value == null)
            preferences.remove(key);
        else
            preferences.put(key, value);
    }

    @Override
    protected String getPreference(String key) {
        return preferences.get(key, null);
    }

    @Override
    public void clear() {
        try {
            preferences.clear();
            initLocalUser();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

}
