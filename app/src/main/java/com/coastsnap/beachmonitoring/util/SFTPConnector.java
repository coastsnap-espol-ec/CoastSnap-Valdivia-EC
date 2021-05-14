package com.coastsnap.beachmonitoring.util;


import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;

import java.io.IOException;

public class SFTPConnector {
    private static final String remoteHost = "200.10.147.17";
    private static final String username = "coastsnap";
    private static final String password = "P4ss#V4ld.";
    private static final String remoteDir = "ftp/files/";


    public void uploadFileUsingVfs(String localFile, String filename) throws IOException {

        FileSystemManager manager = VFS.getManager();

        FileObject local = manager.resolveFile(localFile);
        FileObject remote = manager.resolveFile("sftp://" + username + ":" + password + "@" + remoteHost + "/" + remoteDir + filename);
        remote.copyFrom(local, Selectors.SELECT_SELF);

        local.close();
        remote.close();
    }
}
