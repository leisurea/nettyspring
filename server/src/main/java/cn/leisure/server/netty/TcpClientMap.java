package cn.leisure.server.netty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by yaozb on 15-4-11.
 */
public class TcpClientMap {

    private static Map<String, SocketChannel> map = new ConcurrentHashMap<String, SocketChannel>();

    public static Map<String, SocketChannel> getMap() {
        return map;
    }

    public static void add(String clientId, SocketChannel socketChannel) {
        map.put(clientId, socketChannel);
    }

    public static Channel get(String clientId) {
        return map.get(clientId);
    }

    public static void remove(SocketChannel socketChannel) {
        for (Map.Entry entry : map.entrySet()) {
            if (entry.getValue() == socketChannel) {
                map.remove(entry.getKey());
            }
        }
    }

}
