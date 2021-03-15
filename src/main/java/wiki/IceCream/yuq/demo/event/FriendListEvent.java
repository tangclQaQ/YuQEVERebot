package wiki.IceCream.yuq.demo.event;

import ch.qos.logback.core.util.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.icecreamqaq.yuq.event.GroupInviteEvent;
import com.icecreamqaq.yuq.event.GroupMessageEvent;
import com.icecreamqaq.yuq.event.NewFriendRequestEvent;
import com.icecreamqaq.yuq.message.At;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import com.icecreamqaq.yuq.message.Text;
import wiki.IceCream.yuq.demo.base.SearchMarket;
import wiki.IceCream.yuq.demo.controller.TestGroupController;

import javax.inject.Inject;
import java.io.*;
import java.util.*;

import static wiki.IceCream.yuq.demo.untils.StaticFunction.qDebug;

@EventListener
public class FriendListEvent {

    @Inject
    private SearchMarket searchMarket;

    public TreeMap<String, Integer> itemList = new TreeMap<String, Integer>(new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    });;

    /***
     * 事件的注册，并不会限制你在某个类去注册，只要你的类标记了 EventListener_ 注解。
     *
     * NewFriendRequestEvent 事件
     * 当有新的好友申请的时候，会触发本事件。
     * 如果您将事件的 accept 属性设置为 true，并同时取消了事件，那么将同意好友请求。
     * 否则将忽略（不进行任何处理）这个好友请求。
     */
    @Event
    public void newFriendRequestEvent(NewFriendRequestEvent event) {
        event.setAccept(true);
        event.setCancel(true);
    }

    @Event
    public void newGroupRequestEvent(GroupInviteEvent event) {
        event.setAccept(true);
        event.setCancel(true);
    }

    //软件启动时的事件
    @Event
    public void AppEnableEvent(AppStartEvent event) {
        System.out.print("--------------------------------------------------------------");
        System.out.print("LoadItemListEvent");
        ExcelReader reader = null;

        reader = ExcelUtil.getReader("C://123.xls", 0);
        if(reader != null) {
            for (int i = 1; i < reader.getSheet().getLastRowNum(); i++) {
                int aCell =(int) reader.getCell(0, i).getNumericCellValue();
                String bCell = reader.getCell(1, i).toString();
                itemList.put(bCell, aCell);
            }
        }
        System.out.print("数据总数量：" + String.valueOf(itemList.size()) + "\n");

        Properties properties = new Properties();
        // 使用InPutStream流读取properties文件
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("C:/config.properties"));
            properties.load(bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 获取key对应的value值
        String secretId = properties.getProperty("secretId");
        String secretKey = properties.getProperty("secretKey");
        SearchMarket.secretId = secretId;
        SearchMarket.secretKey = secretKey;
    }

    @Event
    public void newGroupMessageEventt(GroupMessageEvent message) {
        MessageItem iat = message.getMessage().getBody().get(0);
        for (MessageItem messageItem1 : message.getMessage().getBody()) {
            if (messageItem1 instanceof Text){
                Text text = (Text) messageItem1;
                String textStr = text.getText();
                textStr = textStr.trim();
                if(textStr.contains("爱") ||textStr.contains("喜欢")) {
                    message.getGroup().sendMessage(TestGroupController.getMif().text(searchMarket.caihongpi()).toMessage());
                }
            }
        }
        if (iat instanceof At){
            At at = (At) iat;
            if (at.getUser() == 2938604711L){
                StringBuilder sb = new StringBuilder();
                for (MessageItem messageItem : message.getMessage().getBody()) {
                    if (messageItem instanceof Text){
                        Text text = (Text) messageItem;
                        String textStr = text.getText();
                        textStr = textStr.trim();
                        if ("读消息".equals(textStr)) return;
                        sb.append(textStr);
                    }
                }
                String textChat = searchMarket.chat(sb.toString());
                message.getGroup().sendMessage(TestGroupController.getMif().at(message.getSender().getId()).plus(textChat));
            }
        }
    }

}
