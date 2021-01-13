package wiki.IceCream.yuq.demo.event;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.icecreamqaq.yuq.event.NewFriendRequestEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static wiki.IceCream.yuq.demo.untils.StaticFunction.qDebug;

@EventListener
public class FriendListEvent {

//    var itemList: MutableMap<Int, Any> = TreeMap()
    Map<Integer, String> itemList;

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

    //软件启动时的事件
    @Event
    public void AppEnableEvent(AppStartEvent event) {
        System.out.print("--------------------------------------------------------------");
        System.out.print("LoadItemListEvent");
        ExcelReader reader = null;
        try {
            reader = ExcelUtil.getReader(this.getClass().getResource("conf/123.xls").openStream(), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if(reader != null) {
//            for (int i = 0; i < reader.getPhysicalRowCount(); i++) {
//                String aCell = reader.getCell(0, i).toString();
//                String bCell = reader.getCell(1, i).toString();
////                itemList[Integer.valueOf(aCell)] = bCell;
//            }
//        }
    }


}
