package io.github.axtuki1.jinro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DigestUtils extends org.apache.commons.codec.digest.DigestUtils {

    // https://weblabo.oscasierra.net/java-file-digest-1/

    public static String sha1Hex(File file) throws FileNotFoundException, IOException {
        return sha1Hex(new FileInputStream(file));
    }

    public static String sha256Hex(File file) throws FileNotFoundException, IOException {
        return sha256Hex(new FileInputStream(file));
    }

    public static String sha384Hex(File file) throws FileNotFoundException, IOException {
        return sha384Hex(new FileInputStream(file));
    }

    public static String sha512Hex(File file) throws FileNotFoundException, IOException {
        return sha512Hex(new FileInputStream(file));
    }

    public static String md5Hex(File file) throws FileNotFoundException, IOException {
        return md5Hex(new FileInputStream(file));
    }
}