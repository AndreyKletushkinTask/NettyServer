import httpserver.HTTPHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;




public class Main {

	public static void main(String[] args) {
		new Main().start();

	}
	
	public void start() {
		EventLoopGroup boss = new NioEventLoopGroup(1);
		EventLoopGroup worker = new NioEventLoopGroup();
		ServerBootstrap b = new ServerBootstrap();
		b.group(boss, worker).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline p = ch.pipeline();
						p.addLast(new HttpRequestDecoder());
						p.addLast(new HttpResponseEncoder());
						p.addLast(new HTTPHandler());

					}

				});

		try {
			b.bind(80).sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	
		
	}

}
