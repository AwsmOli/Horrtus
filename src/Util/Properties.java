package Util;

import java.io.*;

/**
 * Util Class for Properties
 * Created by olfad on 17.06.2014.
 */
public class Properties {
    public static String get(String propertyName){
        InputStream inputStream = null;
        java.util.Properties properties = new java.util.Properties();

        try {
            inputStream = new FileInputStream("config.properties");
            properties.load(inputStream);

            return properties.getProperty(propertyName);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();

                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static void set(String propertyName, String propertyValue){
        java.util.Properties properties = new java.util.Properties();
        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream("config.properties");
            properties.setProperty(propertyName, propertyValue);

            properties.store(outputStream,null);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
