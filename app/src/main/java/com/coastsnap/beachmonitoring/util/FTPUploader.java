package com.coastsnap.beachmonitoring.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FTPUploader {
    FTPClient ftpClient = null;
    public static final String HOME_FTP_DIRECTORY = "/files";

    public FTPUploader(String host, String username, String passwd){
        ftpClient = new FTPClient();
        int replyFromServer;
        try{
            ftpClient.connect(host);
            replyFromServer = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyFromServer)){
                ftpClient.disconnect();
                System.out.println("Error al conectarse con el servidor FTP");
            }
            ftpClient.login(username, passwd);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(HOME_FTP_DIRECTORY);

        } catch (IOException e){
            System.out.println("Error al conectarse con el servidor FTP!");
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void uploadFile(String localFilePath, String filename, String serverDir) throws IOException{
        InputStream fileInputStream = new FileInputStream(localFilePath);
        boolean isUploaded = this.ftpClient.storeFile(serverDir + filename, fileInputStream);
        fileInputStream.close();
        if (isUploaded){
            System.out.println("Archivo cargado exitosamente!");
        } else {
            throw new IOException("Error al intentar subir el archivo al servidor!");
        }
    }

    public void disconnect() throws IOException{
        if (this.ftpClient.isConnected()){
            System.out.println("Desconectando del servidor...");
            ftpClient.logout();
            ftpClient.disconnect();
            System.out.println("Desconectado del servidor FTP!");
        } else {
            throw new IOException("Falla al desconectarse del servidor FTP!");
        }
    }
}
