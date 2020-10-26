package ru.ndg.cloud.storage.v4.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j2;
import ru.ndg.cloud.storage.v4.common.db.DBUtils;
import ru.ndg.cloud.storage.v4.common.models.*;
import ru.ndg.cloud.storage.v4.common.services.AuthenticationServerService;
import ru.ndg.cloud.storage.v4.common.services.FileServerService;

@Log4j2
public class ServerMainHandler extends ChannelInboundHandlerAdapter {

    private AuthenticationServerService authenticationServerService;
    private FileServerService fileServerService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.authenticationServerService = new AuthenticationServerService();
        this.fileServerService = new FileServerService();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.fileServerService = null;
        this.authenticationServerService = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof Message)) return;

        if (msg instanceof AuthenticationRequest) {
            this.authenticationServerService.receiveAuthentication(ctx, (AuthenticationRequest) msg);
            return;
        }
        if (msg instanceof RegistrationRequest) {
            this.authenticationServerService.receiveRegistration(ctx, (RegistrationRequest) msg);
            return;
        }
        if (msg instanceof UploadRequest) {
            this.fileServerService.receiveUploadFile(ctx, (UploadRequest) msg);
            return;
        }
        if (msg instanceof DownloadRequest) {
            this.fileServerService.receiveDownloadFile(ctx, (DownloadRequest) msg);
            return;
        }
        if (msg instanceof FileListRequest) {
            this.fileServerService.receiveGetListFiles(ctx, (FileListRequest) msg);
            return;
        }
        if (msg instanceof DeleteRequest) {
            this.fileServerService.receiveDeleteFile(ctx, (DeleteRequest) msg);
            return;
        }
        if (msg instanceof RenameRequest) {
            this.fileServerService.receiveRenameFile(ctx, (RenameRequest) msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug(cause);
        this.fileServerService = null;
        this.authenticationServerService = null;
        DBUtils.close();
        ctx.close();
    }
}
