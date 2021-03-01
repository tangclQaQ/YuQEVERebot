package wiki.IceCream.yuq.demo.base;

public class MapUtil {


    /**
     * 通过indexof匹配想要查询的字符
     */
    public static boolean checkKey(String key,String filters) {
        if (key.contains(filters)){
            return true;
        }else {
            return false;
        }
    }

}
