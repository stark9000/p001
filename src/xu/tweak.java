/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xu;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.jasypt.util.text.BasicTextEncryptor;

/**
 *
 * 
 */
public class tweak {

    private final BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
    java.util.Date dt = new java.util.Date();

    public tweak() {
        textEncryptor.setPassword("xyberwise");
    }

    public boolean setProperties() {
        try {

            Properties prop = new Properties();
            prop.setProperty("server_ip", textEncryptor.encrypt("localhost"));
            prop.setProperty("database_name", textEncryptor.encrypt("p001"));
            prop.setProperty("dbuser", textEncryptor.encrypt("root"));
            prop.setProperty("dbpassword", textEncryptor.encrypt("123"));
            prop.store(new FileOutputStream("config.properties"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean setProperties(String ServerIP, String DataBaseName, String UserName, String PassWord) {
        try {

            Properties prop = new Properties();
            prop.setProperty("server_ip", textEncryptor.encrypt(ServerIP));
            prop.setProperty("database_name", textEncryptor.encrypt(DataBaseName));
            prop.setProperty("dbuser", textEncryptor.encrypt(UserName));
            prop.setProperty("dbpassword", textEncryptor.encrypt(PassWord));
            prop.store(new FileOutputStream("config.properties"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public List<String> getProperties() {
        List<String> compList = null;
        try {
            compList = new ArrayList<>();
            Properties prop = new Properties();
            prop.load(new FileInputStream("config.properties"));
            compList.add(textEncryptor.decrypt(prop.getProperty("server_ip")));
            compList.add(textEncryptor.decrypt(prop.getProperty("database_name")));
            compList.add(textEncryptor.decrypt(prop.getProperty("dbuser")));
            compList.add(textEncryptor.decrypt(prop.getProperty("dbpassword")));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return compList;
    }

    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

}
