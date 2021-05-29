package wiki.IceCream.yuq.demo.controller;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.job.JobManager;
import com.icecreamqaq.yuq.YuQ;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.NextContext;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import com.icecreamqaq.yuq.message.Text;
import wiki.IceCream.yuq.demo.base.SearchMarket;

import javax.inject.Inject;

/***
 * GroupController 代表了，这个 Controller 将响应群消息。
 */
@GroupController
public class TestGroupController {

    /***
     * YuQ 接口是 YuQ Framework 向用户提供的一个便于使用的 API。
     * 通过注入 YuQ 来获得 YuQ 的实例，来调用 YuQ Framework 的绝大部分功能。
     * 如发送消息，撤回消息等等。
     */
    @Inject
    private YuQ yuq;

    /***
     * MessageItemFactory 用来创建 Message 的具体内容。
     */
    @Inject
    private static MessageItemFactory mif;

    @Inject
    private SearchMarket searchMarket;

    String help =
            "1:国服市场查询,格式：.jita 马克瑞级\n" +
            "2:欧服物价 .ojita+后缀\n" +
                    "3:彩虹屁功能 \n" +
                    "4:闲聊功能 \n" +
                    "5:藏头诗功能，格式：藏头诗 我爱中国 \n" +
            "目前应该可以自动通过好友申请和群邀请了。\n" +
            "其他详情可咨询作者QQ：664798563";

    /***
     * Before 则为具体的控制器的动作前置验证，也可以称作拦截器，负责在 Action 处理消息之前进行验证。
     *
     * Before 方法接收 0 - 多个参数，通常，您所写下的参数，将会以名称匹配来进行依赖注入。
     *   支持注入的名称 - 注入的内容
     *     qq - 发送消息的 QQ 账号
     *     group - 发送消息的 QQ 群号
     *     message - 具体的 Message 对象
     *     messageId - 消息 ID
     *     sourceMessage - 未经处理的源消息内容，则为具体的 Runtime 的消息内容
     *     actionContext - 当前消息的 ActionContext 对象
     *     以及，您 Before 传递回来的需要保存的对象。
     *
     * Before 方法可以接受任何类型的返回值，当您返回了一个值的时候，框架会帮您保存起来，名称则为将类名的第一个字母转化小写后的名字。
     *
     * Before 方法可以抛出异常，来作为验证失败的中断处理方法。
     * 当您抛出了一个 Message 类型的异常后，如果您没有设置任何接收的 QQ，或 QQ 群，那么我们将会将消息发送至当前消息来源者，如果您设置了接收对象，那么发送至您的接收对象。
     * 当您想中断处理链路，并且不进行任何返回的时候，您可以抛出 DoNone 类型的异常。
     *
     * 一个 Controller 类内，可以接受多个 Before，他们按照一定的顺序依次执行，当所有 Before 执行完成之后，将继续执行 Action。
     */
//    @Before
//    public void before(long qq) {
//        if (qq % 2 == 0) throw mif.text("你没有使用该命令的权限！").toMessage().toThrowable();
//    }

    private boolean menuFlag = true;

    /***
     * Action 内，不仅可以写单级指令，还可以写多级指令。
     * 最后的 {flag} 则代表了一个可变内容，他可以根据方法参数类型，自动映射为指定类型。
     */
    @Action(".jita {name}")
    public Object searchCnMarket(long qq, String name) {
        return mif.at(qq).plus(searchMarket.searchMarket(name, true, 3));
    }

    @Action(".ojita {name}")
    public Object searchEuMarket(long qq, String name) {
        return mif.at(qq).plus(searchMarket.searchMarket(name, false, 3));
    }

    @Action("{param1}机器人{param2}")
    public String chat(String param1, String param2) {
        return searchMarket.chat(param1 + "小微" + param2);
    }

    @Action("{param1}市场机{param2}")
    public String chat1(String param1, String param2) {
        return searchMarket.chat(param1 + "小微" + param2);
    }

    @Action("{param1}市场鸡{param2}")
    public String chat2(String param1, String param2) {
        return searchMarket.chat(param1 + "小微" + param2);
    }

    @Action("{param1}彩虹屁{param2}")
    public String caihongpi(String param1, String param2) {
        return searchMarket.caihongpi();
    }

    @Action("藏头诗 {text}")
    public String cangtoushi(String text) {
        return searchMarket.cangtoushi(text);
    }

    @Action("土味情话")
    public String tuweiqinghua() {
        return searchMarket.tuweiqinghua();
    }

    @Action("帮助")
    public Object menu(long qq) {;
        return mif.at(qq).plus(help);
    }

    @Action("菜单")
    public Object menu1(long qq) {;
        return mif.at(qq).plus(help);
    }

    @Action("功能")
    public Object menu2(long qq) {;
        return mif.at(qq).plus(help);
    }

//    @Action("测试")
//    public Object ceshi(long qq) {
//        return Message.Companion.toMessageByRainCode("<Rain:Xml:5,&&&lt&&&?xml version=\"1.0\" encoding=\"utf-8\"?&&&gt&&&&&&lt&&&msg serviceID=\"5\" templateID=\"12345\" brief=\"[分享]\" token=\"33b2153a5721e938ed7662bc2699d147\" timestamp=\"1614757667\" nonce=\"1016341428\"&&&gt&&&&&&lt&&&item layout=\"0\"&&&gt&&&&&&lt&&&image uuid=\"{B0730F97-CE29-0123-0AA6-1C2E2AE09FB2}.png\" md5=\"B0730F97CE2901230AA61C2E2AE09FB2\" GroupFiledid=\"2579894839\" minWidth=\"100\" minHeight=\"100\" maxWidth=\"180\" maxHeight=\"180\"/&&&gt&&&&&&lt&&&/item&&&gt&&&&&&lt&&&source name=\"兽耳桌面\" icon=\"http://i.gtimg.cn/open/app_icon/05/78/51/85//1105785185_100_m.png?t=1613970849\" appid=\"1105785185\" action=\"\" i_actionData=\"\" a_actionData=\"\" url=\"\"/&&&gt&&&&&&lt&&&/msg&&&gt&&&>");
//    }

//    @Action("帮助")
//    public Object menu(long qq) {
//        String help = "1:KM记录查询,格式：击坠/损失+角色ID (未完成）\n" +
//                "2:装配方案查询,格式：装配+船只名 (分类复制名称即可回复)不全面，没时间做(未完成）\n" +
//                "3:国服市场查询,格式：.jita 马克瑞级\n" +
//                "4:欧服物价 .ojita+后缀\n" +
//                "5:虫洞查询,格式：虫洞+后缀，要空格。(待开发)\n" +
//                "6:简略角色简介,格式：人物+后缀(待开发)\n" +
//                "7:增强时间查询(未完成）\n" +
//                "8:国服攻略(待开发)\n" +
//                "10：eve状态 格式：实时状态(待开发)\n" +
//                "11：EVE新闻或EVE公告(待开发)\n" +
//                "15::欧服完全攻略(待开发)\n" +
//                "目前应该可以自动通过好友申请和群邀请了。";
//        return mif.at(qq).plus(help);
//    }












    public static MessageItemFactory getMif() {
        return mif;
    }

    /***
     * Action 则为具体的控制器的动作，负责处理收到的消息。
     *
     * Action 方法接收 0 - 多个参数，通常，您所写下的参数，将会以名称匹配来进行依赖注入。
     *   支持注入的名称 - 注入的内容
     *     qq - 发送消息的 QQ 账号
     *     group - 发送消息的 QQ 群号
     *     message - 具体的 Message 对象
     *     messageId - 消息 ID
     *     sourceMessage - 未经处理的源消息内容，则为具体的 Runtime 的消息内容
     *     actionContext - 当前消息的 ActionContext 对象
     *     以及，您 Before 传递回来的需要保存的对象。
     *
     * Action 可接收方法可以接受任何类型的返回值，当您返回了一个值的时候，
     *   如果您返回的是 Message 类型的时候，我们会帮您发送这个消息，如果您没有设置任何接收的 QQ，或 QQ 群，那么我们将会将消息发送至当前消息来源者，如果您设置了接收对象，那么发送至您的接收对象。
     *   如果您返回了一个 String 类型的时候，我们会帮您构建一个 Message，并发送到当前消息的来源。
     *   如果您返回了一个 MessageItem 类型的时候，我们会帮您构建一个 Message，并发送到当前消息的来源。
     *   如果您返回的是其他类型，我们会帮您调用 toString 方法，并构建一个 Message，然后发送到当前消息的来源。
     *
     * Action 方法可以抛出异常，来返回一些信息。
     *   当您抛出了一个 Message 类型的异常后，如果您没有设置任何接收的 QQ，或 QQ 群，那么我们将会将消息发送至当前消息来源者，如果您设置了接收对象，那么发送至您的接收对象。
     *   当您想中断处理链路，并且不进行任何返回的时候，您可以抛出 DoNone 类型的异常。
     * @return
     */
//    @Action("菜单")
//    public Object menu(long qq) {
//        if (menuFlag)
//            return mif.at(qq).plus("，您好。\n" +
//                    "这里是基础菜单。" +
//                    "但是由于这是一个演示 Demo，他没有什么功能。" +
//                    "所以也并没有菜单。" +
//                    "那就这样吧。");
//        return "菜单被禁用！";
//    }

    /***
     * Action 内，不仅可以写单级指令，还可以写多级指令。
     * 最后的 {flag} 则代表了一个可变内容，他可以根据方法参数类型，自动映射为指定类型。
     */
    @Action("设置 菜单开关 {flag}")
    public String menu2(boolean flag) {
        menuFlag = flag;
        return "菜单开关：" + flag;
    }

    /***
     * 可以在路由内书写 { 名称 : 正则表达式 } 来动态匹配指令上的内容。
     * 如果你想匹配任意内容，则 : 及后续可以省略。示例：{color}
     * 本例子则代表只匹配单个文本。
     */
    @Action("发个{color:.}包")
    public String sendPackage(String color) {
        return String.format("QQ%s包！", color);
    }

    /***
     * YuQ 可以将您发送的数字QQ号，或者 At 某人，智能转化为您所需要的内容。
     * 本处就将对象转化为 Member 的实例。
     * 通过调用 Member 的 ban 方法，可以将目标禁言一段时间。单位：秒。
     * 通过书写 Member 类型的 qq 参数，即可获取当前消息发送者的 Member 实例。
     * 通过调用 Member 的 isAdmin 方法，可以获取当前目标是否具有管理员权限。（管理员与群主都具有管理员权限）
     *
     * 本 Action 的作用，如果发送者是管理员，就将目标禁言一段时间，如果发送人不是管理员，就将自己禁言一段时间。
     */
    @Action("禁言 {sb} {time}")
    public String ban(Member sb, Member qq, int time) {
        if (time < 60) time = 60;
        if (qq.isAdmin()) {
            sb.ban(time);
            return "好的";
        }

        qq.ban(time);
        return "您没有使用该命令的权限！为了防止恶意操作，你已被禁言相同时间。";
    }

    @Inject
    private JobManager jobManager;

    /***
     * 我们可以通过注入 JobManager 快速，动态的创建定时与时钟任务。
     * 写在路由中的 {time} 与整级路由 {nr} 最大的不同，就是写在路由中的，只能用 String 类型接受，并不能智能匹配成其他类型。
     */
    @Action("{time}秒后说 {nr}")
    public Object timeSend(String time, MessageItem nr, Group group) {
        Message nm = new Message().plus(nr);
        jobManager.registerTimer(() -> group.sendMessage(nm), Integer.parseInt(time) * 1000);
        return "好的";
    }


    /***
     * NextContext 注解用来声明完成之后进入某个上下文。
     * 这是一个用来进入上下文的 Action，当然，他与普通的 Action 没有什么区别。
     * 他可以完成普通 Action 的所有功能。
     * 只是当他在正常完成之后，会帮你把上下文自动切换到 NextContext 中声明的内容。
     *
     * 正常完成则为，当 Action 方法没有产生任何异常时，Action 方为正常完成。
     */
    @Action("绑定手机号")
    @NextContext("bindPhone")
    public void bindPhone(long qq) {
        if (qq % 2 == 0) throw mif.text("您无需绑定手机号码。").toMessage().toThrowable();
    }

}
