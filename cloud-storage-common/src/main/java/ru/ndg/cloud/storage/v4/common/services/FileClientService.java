package ru.ndg.cloud.storage.v4.common.services;

import com.sun.istack.internal.NotNull;
import io.netty.channel.Channel;
import lombok.extern.log4j.Log4j2;
import ru.ndg.cloud.storage.v4.common.models.*;

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

    public void sendUploadFile(@NotNull Channel channel, @NotNull String login, @NotNull String fileName) {
        Path path = Paths.get(CLIENT_STORAGE_DIRECTORY, fileName);
        byte[] data = null;
        if (Files.exists(path)) {
            try {
                data = Files.readAllBytes(path);
            } catch (IOException e) {
                log.debug(e);
                data = new byte[0];
            }
        }
        channel.writeAndFlush(new UploadRequest(login, fileName, data));
    }

    public void sendDownloadFile(@NotNull Channel channel, @NotNull String login, @NotNull String fileName) {
        channel.writeAndFlush(new DownloadRequest(login, fileName));
    }

    public void sendDeleteFile(@NotNull Channel channel, @NotNull String login, @NotNull String fileName) {
        channel.writeAndFlush(new DeleteRequest(login, fileName));
    }

    public void sendRenameFile(@NotNull Channel channel, @NotNull String login, @NotNull String fileName, @NotNull String newFileName) {
        channel.writeAndFlush(new RenameRequest(login, fileName, newFileName));
    }

    public void sendGetListFiles(@NotNull Channel channel, @NotNull String login) {
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
