package tk.candarlabs.pngzapp;

import tk.candarlabs.PNGZDecoder;
import tk.candarlabs.PNGZEncoder;
import tk.candarlabs.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

class EncryptorFunctions {
    static File appliedFile = null;
    static PNGZEncoder encoder = new PNGZEncoder();
    static PNGZDecoder decoder = new PNGZDecoder();
    static String operation = "";

    static boolean checkIfPNGZ(BufferedImage image){
        return image.getRGB(0,0) == new Color(13,128,37).getRGB();
    }

    static void setFile(File file){
        String extension = file.getName().substring(file.getName().lastIndexOf('.') + 1);
        boolean isPNGZ = false;

        if(extension.equals("png")){
            try{
                isPNGZ = checkIfPNGZ(Utils.fileToImage(file));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println(isPNGZ);
        if(isPNGZ){
            operation = "Decode";
            decoder.setInputFile(file);
        } else {
            operation = "Encode";
            encoder.setInputFile(file);
        }
        UIFunctions.setArrowBlue(operation);

        appliedFile = file;
    }

    static void applyOperation(){
        if (operation.equals(""))
            return;
        if (operation.equals("Encode")){
            encoder.encode();
            encoder.saveToDir(appliedFile.getParentFile());
        }
        if (operation.equals("Decode")){
            decoder.decode();
            decoder.saveToDir(appliedFile.getParentFile());
        }
    }
}
