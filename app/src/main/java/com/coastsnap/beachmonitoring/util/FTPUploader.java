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

    /**
     * Constructor de la clase FTPUploader. Aquí se inicializan los valores para realizar la conexión
     * con el servidor FTP y se redirige hacia el directorio remoto donde se almacenarán los archivos
     * que se carguen con los métodos de la clase. Se maneja la excepción al momento de conectarse con
     * el servidor.
     *
     * @param host: dirección del servidor FTP.
     * @param username: usuario para acceder al servidor FTP.
     * @param passwd: contraseña para acceder al servidor FTP.
     */
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

    /**
     * EL método uploadFile sirve para poder cargar el archivo pasado por parámetro a través del
     * cliente FTP de jar org.apache.commons.net.ftp.FTPClient.
     *
     * @param localFilePath: ruta local del archivo a subirse.
     * @param filename: nombre del archivo con el cual se guardará en el servidor.
     * @param serverDir: ruta absoluta o relativa (dependiendo de las configuraciones del servidor)
     *                 donde se subirá el archivo.
     * @throws IOException: En caso de fallas al momento de subir el archivo ya se por el método storeFile()
     * o en la creación del InputStream, se lanzará una excepción.
     */
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

    /**
     * Método para realizar la desconexión con el servidor FTP.
     *
     * @throws IOException: En caso de fallas al momento de desconectarse del servidor por los métodos
     * logout() o disconnect(), se lanzará una excepción.
     */
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
