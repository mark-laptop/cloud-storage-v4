package ru.ndg.cloud.storage.v4.client.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j2;
import ru.ndg.cloud.storage.v4.common.CallBack;
import ru.ndg.cloud.storage.v4.common.model.*;

@Log4j2
public class ClientMainHandler extends ChannelInboundHandlerAdapter {

    private CallBack callBack;

    public ClientMainHandler(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof Message)) return;

        if (msg instanceof AuthenticationResponse) {
            callBack.authenticationCallback(((AuthenticationResponse) msg).isResult());
            return;
        }
        if (msg instanceof RegistrationResponse) {
            callBack.registrationCallback(((RegistrationResponse) msg).isResult());
            return;
        }
        if (msg instanceof FileListResponse) {
            callBack.fileListCallback(((FileListResponse) msg).getFileList());
            return;
        }
        if (msg instanceof UploadResponse) {
            callBack.fileUploadCallback(((UploadResponse) msg).getFileList());
            return;
        }
        if (msg instanceof DownloadResponse) {
            callBack.fileDownloadCallback(((DownloadResponse) msg).getFileName(), ((DownloadResponse) msg).getData());
            return;
        }
        if (msg instanceof DeleteResponse) {
            callBack.fileDeleteCallback(((DeleteResponse) msg).getFileList());
            return;
        }
        if (msg instanceof RenameResponse) {
            callBack.fileRenameCallback(((RenameResponse) msg).getFileList());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug(cause);
        callBack = null;
        ctx.close();
    }
}