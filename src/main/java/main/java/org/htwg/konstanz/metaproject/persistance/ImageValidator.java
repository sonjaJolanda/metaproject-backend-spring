/*
* This class was created for the issue "validate profile picture"
* This class checks if a file is a picture, based on the base64code
* This class is only a prototype
* */

package main.java.org.htwg.konstanz.metaproject.persistance;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

@Service
public class ImageValidator {

    public static byte[] convertToImg(String base64) throws IOException {
        return Base64.decodeBase64(base64);
    }

    public static boolean writeByteToImageFile(byte[] imgBytes, String imgFileName, String imgType) throws IOException {
        File imgFile = new File(imgFileName);
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgBytes));
        ImageIO.write(img, imgType, imgFile);
        return isAnImageBASE64(imgFile);
    }
    public boolean goo2(String base64code) throws IOException {
        String base64 = base64code;
        String[] type = base64.split("/");
        type = type[1].split(";");
        String imgType = type[0];

        String[] splitted = base64.split(",");

        byte[] base64Val = convertToImg(splitted[1]);
        return writeByteToImageFile(base64Val, "Generatedimage." + imgType, imgType);
    }
    public boolean goo() throws IOException {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter base64 string to be converted to image");
        String base64 = s.nextLine();
        String[] type = base64.split("/");
        type = type[1].split(";");
        String imgType = type[0];

        String[] splitted = base64.split(",");

// byte[] base64Val=convertToImg(base64);
        byte[] base64Val = convertToImg(splitted[1]);
        return writeByteToImageFile(base64Val, "C:\\Users\\stvahabi\\Desktop\\Generatedimage." + imgType, imgType);
//System.out.println("Saved the base64 as image");
    }

    public static boolean isAnImageBASE64(File file) {

        try {

            if (file.exists()) {
                Image image = ImageIO.read(file);
                if (image == null) {
                    file.delete();
                    return false;
                } else {
                    file.delete();
                    return true;
                }
            } else {
                return false;
            }

        } catch (IOException ex) {
            return false;
        }

    }

    public boolean isAnImage(String filepath) {
        if (filepath == null || filepath.length() < 5) {
            return false;
        } else {

            try {

                File file = new File(filepath);
                if (file.exists()) {
                    Image image = ImageIO.read(file);
                    if (image == null) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }

            } catch (IOException ex) {
                return false;
            }
        }
    }

    public boolean isImage(String filepath) {
        if (filepath == null || filepath.length() < 5) {
            return false;
        } else {
            File file = new File(filepath);
            if (file.exists()) {

                MimetypesFileTypeMap mime = new MimetypesFileTypeMap();
                mime.addMimeTypes("image png jpeg jpg bmp");
                String mimeType = mime.getContentType(file).split("/")[0];
// System.out.println("Mimetype = " +
// mime.getContentType(file.getAbsolutePath()));

                if (mimeType.equals("image")) {
                    return true;
                } else {
                    return false;
                }

            } else {
                return false;
            }
        }
    }

}


