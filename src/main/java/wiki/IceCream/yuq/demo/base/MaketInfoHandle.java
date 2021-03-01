package wiki.IceCream.yuq.demo.base;

import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;

public class MaketInfoHandle {

    public static BigDecimal[] getPrice(JSONObject infoObject){
        BigDecimal[] price = new BigDecimal[2];
        price[0] = new BigDecimal(infoObject.getJSONObject("buy").get("max").toString());
        price[1] = new BigDecimal(infoObject.getJSONObject("sell").get("min").toString());
        return price;
    }

    public static String changePrice(BigDecimal price){
        String result = "ISK";
        BigDecimal q= new BigDecimal(10000);
        BigDecimal compareT = new BigDecimal(0);
        BigDecimal compareT2 = new BigDecimal(1);
        BigDecimal M1 = new BigDecimal(price.divide(q,2,0).stripTrailingZeros().toPlainString());
        BigDecimal M2 = new BigDecimal(M1.divide(q,4,0).stripTrailingZeros().toPlainString());;
        BigDecimal M3 = price.divideAndRemainder(q)[1];
        BigDecimal M4 = price.divideAndRemainder(q)[1];
        if(M3.compareTo(compareT) == 1 && M1.compareTo(compareT2) == -1){
            result = String.valueOf(M3)+result;
        }
        if(M1.compareTo(compareT2) == 1 && M2.compareTo(compareT2) == -1){
            result = String.valueOf(M1)+"万"+result;
        }
        if(M2.compareTo(compareT2) == 1){
            result = String.valueOf(M2)+"亿"+result;
        }
        return result;
    }

    public static String priceResult(BigDecimal[] price){
        String result = "求购出价：";
        if(!("ISK").equals(changePrice(price[0]))){
            result += changePrice(price[0]);
        }else {
            result += "暂无";
        }
        result += "\n卖方出价：";
        if(!("ISK").equals(changePrice(price[1]))){
            result += changePrice(price[1])+"\n";
        }else {
            result += "暂无\n";
        }
        String nullKey = "求购出价：暂无\n卖方出价：暂无\n";
        if(result.contains(nullKey)){
            return null;
        }else {
            return result;
        }

    }
}
