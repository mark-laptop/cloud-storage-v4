package ru.ndg.cloud.storage.v4.common;

public interface CallBack {

    void authenticationCallback(boolean result);
    void registrationCallback(boolean result);

    void fileListCallback(String fileList);
    void fileUploadCallback(String fileList);
    void fileDownloadCallback(String fileName, byte[] data);
    void fileDeleteCallback(String fileList);
    void fileRenameCallback(String fileList);
}
