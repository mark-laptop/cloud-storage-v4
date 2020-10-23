package ru.ndg.cloud.storage.v4.client.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import ru.ndg.cloud.storage.v4.client.handlers.ClientMainHandler;
import ru.ndg.cloud.storage.v4.common.services.AuthenticationClientService;
import ru.ndg.cloud.storage.v4.common.CallBack;
import ru.ndg.cloud.storage.v4.common.services.FileClientService;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

@Log4j2
@Builder
public class Network {

    private String host;
    private int port;
    private Channel currentChannel;
    private CallBack callBack;
    private FileClientService fileClientService;
    private AuthenticationClientService authenticationClientService;

    public void run(CountDownLatch latch) {
        EventLoopGroup group = new NioEventLoopGroup(1);

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            Network.this.currentChannel = ch;
                            ch.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(500 * 1024 * 1024, ClassResolvers.cacheDisabled(null)),
                                    new ClientMainHandler(Network.this.callBack)
                            );
                        }
                    });
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(this.host, this.port)).sync();
            this.fileClientService = new FileClientService();
            this.authenticationClientService = new AuthenticationClientService();
            latch.countDown();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.debug(e);
        } finally {
            group.shutdownGracefully();
        }
    }

    public void authentication(String login, String password) {
        this.authenticationClientService.sendAuthentication(this.currentChannel, login, password);
    }

    public void registration(String login, String password) {
        this.authenticationClientService.sendRegistration(this.currentChannel, login, password);
    }

    public void uploadFile(String login, String fileName) {
        this.fileClientService.sendUploadFile(this.currentChannel, login, fileName);
    }

    public void downloadFile(String login, String fileName) {
        this.fileClientService.sendDownloadFile(this.currentChannel, login, fileName);
    }

    public void renameFile(String login, String fileName, String newFileName) {
        this.fileClientService.sendRenameFile(this.currentChannel, login, fileName, newFileName);
    }

    public void deleteFile(String login, String fileName) {
        this.fileClientService.sendDeleteFile(this.currentChannel, login, fileName);
    }

    public void getListFile(String login) {
        this.fileClientService.sendGetListFiles(this.currentChannel, login);
    }
}
