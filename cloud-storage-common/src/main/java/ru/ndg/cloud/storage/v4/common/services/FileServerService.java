package ru.ndg.cloud.storage.v4.common.services;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import ru.ndg.cloud.storage.v4.common.model.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Collectors;

@Log4j2
public class FileServerService {

    static final String SERVER_STORAGE_DIRECTORY = "server-directory";

    {
        createServerDirectory();
    }

    public void receiveUploadFile(ChannelHandlerContext ctx, UploadRequest request) {
        writeFile(request.getLogin(), request.getFileName(), request.getData());
        String fileList = getListFiles(request.getLogin());
        ctx.writeAndFlush(new UploadResponse(fileList));
    }

    public void receiveDownloadFile(ChannelHandlerContext ctx, DownloadRequest request) {
        byte[] data = readFile(request.getLogin(), request.getFileName());
        ctx.writeAndFlush(new DownloadResponse(request.getFileName(), data));
    }

    public void receiveDeleteFile(ChannelHandlerContext ctx, DeleteRequest request) {
        deleteFile(request.getLogin(), request.getFileName());
        String fileList = getListFiles(request.getLogin());
        ctx.writeAndFlush(new DeleteResponse(fileList));
    }

    public void receiveRenameFile(ChannelHandlerContext ctx, RenameRequest request) {
        renameFile(request.getLogin(), request.getFileName(), request.getNewFileName());
        String fileList = getListFiles(request.getLogin());
        ctx.writeAndFlush(new RenameResponse(fileList));
    }

    public void receiveGetListFiles(ChannelHandlerContext ctx, FileListRequest request) {
        String fileList = getListFiles(request.getLogin());
        ctx.writeAndFlush(new FileListResponse(fileList));
    }

    private void deleteFile(String login, String fileName) {
        Path path = Paths.get(SERVER_STORAGE_DIRECTORY, login, fileName);
        try {
            Files.delete(path);
        } catch (IOException e) {
            log.debug(e);
        }
    }

    private void renameFile(String login, String fileName, String newFileName) {
        Path path = Paths.get(SERVER_STORAGE_DIRECTORY, login, fileName);
        Path newPath = Paths.get(SERVER_STORAGE_DIRECTORY, login, newFileName);
        try {
            Files.move(path, newPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.debug(e);
        }
    }

    private byte[] readFile(String login, String fileName) {
        Path path = Paths.get(SERVER_STORAGE_DIRECTORY, login, fileName);
        byte[] data;
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            log.debug(e);
            data = new byte[0];
        }
        return data;
    }

    private void writeFile(String login, String fileName, byte[] data) {
        Path path = Paths.get(SERVER_STORAGE_DIRECTORY, login, fileName);
        try {
            Files.write(path, data, StandardOpenOption.CREATE);
        } catch (IOException e) {
            log.debug(e);
        }
    }

    private String getListFiles(String login) {
        Path path = Paths.get(SERVER_STORAGE_DIRECTORY, login);
        String fileList = "";
        if (!Files.exists(path)) {
            return fileList;
        }

        try {
            fileList = Files.list(path).map(p -> p.getFileName().toString()).collect(Collectors.joining(","));
        } catch (IOException e) {
            log.debug(e);
        }
        return fileList;
    }

    private void createServerDirectory() {
        Path path = Paths.get(SERVER_STORAGE_DIRECTORY);
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