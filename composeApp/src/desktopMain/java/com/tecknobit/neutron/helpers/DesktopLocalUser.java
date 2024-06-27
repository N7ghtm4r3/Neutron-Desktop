package com.tecknobit.neutron.helpers;

import com.tecknobit.neutroncore.helpers.LocalUser;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * The {@code DesktopLocalUser} class is useful to represent a user in a desktop application
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see LocalUser
 */
public class DesktopLocalUser extends LocalUser {

    /**
     * {@code preferences} the manager of the local preferences
     */
    private final Preferences preferences;

    /**
     * Constructor to init {@link DesktopLocalUser} class <br>
     * <p>
     * No-any params required
     */
    public DesktopLocalUser() {
        preferences = Preferences.userRoot().node("tecknobit/neutron/desktop");
        initLocalUser();
    }

    /**
     * Method to store and set a preference
     *
     * @param key: the key of the preference
     * @param value: the value of the preference
     */
    @Override
    protected void setPreference(String key, String value) {
        if (value == null)
            preferences.remove(key);
        else
            preferences.put(key, value);
    }

    /**
     * Method to get a stored preference
     *
     * @param key: the key of the preference to get
     * @return the preference stored as {@link String}
     */
    @Override
    protected String getPreference(String key) {
        return preferences.get(key, null);
    }

    /**
     * Method to clear the current local user session <br>
     * No-any params required
     */
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
