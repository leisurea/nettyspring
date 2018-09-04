package cn.leisure.server.controller;

import com.google.gson.Gson;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cn.leisure.server.netty.TcpClientMap;
import cn.leisure.utils.helper.CommonMethod;
import io.netty.channel.socket.SocketChannel;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/date")
    public String getdate() {
        Date date = CommonMethod.getCurData();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return "当前时间：" + dateFormat.format(date) + "  有 " + TcpClientMap.getMap().size() + " 个客户端在线";
    }

    @RequestMapping(value = "/foo", produces = "text/html; charset=utf-8")
    public @ResponseBody
    String getShopInJSON(HttpServletRequest request) {
        return "你好";
    }

    @RequestMapping(value = "/clients", produces = "text/html; charset=utf-8")
    public @ResponseBody
    String getClientList() {
        List<String> list = new ArrayList<>();
        for (Map.Entry entry : TcpClientMap.getMap().entrySet()) {
            SocketChannel channel = (SocketChannel) entry.getValue();
            list.add("key:" + entry.getKey() + "--value:" + channel.localAddress().getHostString());
        }
        return new Gson().toJson(list);
    }

}
