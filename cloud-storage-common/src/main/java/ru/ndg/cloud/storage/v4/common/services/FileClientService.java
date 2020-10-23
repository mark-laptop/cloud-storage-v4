package ru.ndg.cloud.storage.v4.common.services;

import io.netty.channel.Channel;
import lombok.extern.log4j.Log4j2;
import ru.ndg.cloud.storage.v4.common.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
public class FileClientService {

    public static final String CLIENT_STORAGE_DIRECTORY = "client-directory";

    {
        createUserDirectory();
    }

    public void sendUploadFile(Channel channel, String login, String fileName) {
        Path path = Paths.get(CLIENT_STORAGE_DIRECTORY, fileName);
        byte[] bytes = null;
        if (Files.exists(path)) {
            try {
                bytes = Files.readAllBytes(path);
            } catch (IOException e) {
                log.debug(e);
                bytes = new byte[0];
            }
        }
        channel.writeAndFlush(new UploadRequest(login, fileName, bytes));
    }

    public void sendDownloadFile(Channel channel, String login, String fileName) {
        channel.writeAndFlush(new DownloadRequest(login, fileName));
    }

    public void sendDeleteFile(Channel channel, String login, String fileName) {
        channel.writeAndFlush(new DeleteRequest(login, fileName));
    }

    public void sendRenameFile(Channel channel, String login, String fileName, String newFileName) {
        channel.writeAndFlush(new RenameRequest(login, fileName, newFileName));
    }

    public void sendGetListFiles(Channel channel, String login) {
        channel.writeAndFlush(new FileListRequest(login));
    }

    private void createUserDirectory() {
        Path path = Paths.get(CLIENT_STORAGE_DIRECTORY);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                log.debug(e);
                throw new RuntimeException(e);
            }
        }
    }
}
