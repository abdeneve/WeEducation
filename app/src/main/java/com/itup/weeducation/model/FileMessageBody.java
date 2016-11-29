package com.itup.weeducation.model;

/**
 * Created by Alex-Dell on 10/19/2016.
 */

public abstract class FileMessageBody extends MessageBody {

    private String FileName;
    private String LocalUrl;
    private String RemoteUrl;

    public FileMessageBody() {
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getLocalUrl() {
        return LocalUrl;
    }

    public void setLocalUrl(String localUrl) {
        LocalUrl = localUrl;
    }

    public String getRemoteUrl() {
        return RemoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        RemoteUrl = remoteUrl;
    }
}
