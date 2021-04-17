package com.example.malko;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class Session {
    public static final String KEY_LOGIN = generateKey();

    public static String generateKey() {
        try {
            SecureRandom secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG", "IBMJCE");

            // Get 128 random bytes
            byte[] randomBytes = new byte[128];
            secureRandomGenerator.nextBytes(randomBytes);

            //Get random integer in range
            int randInRange = secureRandomGenerator.nextInt(9999999);

            return String.valueOf(randInRange);
        } catch (NoSuchProviderException | NoSuchAlgorithmException e) {
            Log.e("Session", e.getMessage());
            return "000000000";
        }
    }

    public static void save(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userdata", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //Save data with key
        editor.putString(key, value);
        editor.apply();
    }

    public static String get(Context context, String key) {
        // Key param for usage
        SharedPreferences sharedPreferences = context.getSharedPreferences("userdata", Context.MODE_PRIVATE);
        // Get by key and default value if null.
        return sharedPreferences.getString(key, "false");
    }
}
