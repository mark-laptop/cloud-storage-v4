package ru.ndg.cloud.storage.v4.common.services;

import com.sun.istack.internal.NotNull;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import ru.ndg.cloud.storage.v4.common.models.*;
import ru.ndg.cloud.storage.v4.common.db.DBUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
public class AuthenticationServerService {

    public void receiveAuthentication(@NotNull ChannelHandlerContext ctx, @NotNull AuthenticationRequest request) {
        User userFromDB = DBUtils.getUser(new User(request.getLogin(), request.getPassword()));
        boolean result = userFromDB.getLogin() != null;
            if (result) {
                createUserDirectory(request.getLogin());
                ctx.writeAndFlush(new AuthenticationResponse(true));
            }
            else
                ctx.writeAndFlush(new AuthenticationResponse(false));
    }

    public void receiveRegistration(@NotNull ChannelHandlerContext ctx, @NotNull RegistrationRequest request) {
        boolean result = DBUtils.saveUser(new User(request.getLogin(), request.getPassword()));
        if (result) {
            createUserDirectory(request.getLogin());
            ctx.writeAndFlush(new RegistrationResponse(true));
        }
        else
            ctx.writeAndFlush(new RegistrationResponse(false));
    }

    private boolean createUserDirectory(String login) {
        Path path = Paths.get(FileServerService.SERVER_STORAGE_DIRECTORY, login);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                log.debug(e);
                return false;
            }
        }
        return true;
    }
}