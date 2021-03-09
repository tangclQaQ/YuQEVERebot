package wiki.IceCream.yuq.demo.base;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import wiki.IceCream.yuq.demo.event.FriendListEvent;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

public class SearchMarket {
    @Inject
    private FriendListEvent loadEvent;

    String cnMarketUrl = "https://www.ceve-market.org/api/market/region/10000002/type/";
    String euMarketUrl = "https://www.ceve-market.org/tqapi/market/region/10000002/type/";
    private final static Charset UTF8 = StandardCharsets.UTF_8;

    public String searchMarket(String name, Boolean isCn, int num) {
        String result = "查询结果：\n";
        int i = 0;
        for (HashMap.Entry<String, Integer> entry : loadEvent.itemList.entrySet()) {
            if (MapUtil.checkKey(entry.getKey(), name)) {
                String url = "";
                if (isCn) {
                    url = cnMarketUrl + String.valueOf(entry.getValue()) + ".json";
                } else {
                    url = euMarketUrl + String.valueOf(entry.getValue()) + ".json";
                }
                try {
                    JSONObject itemInfo = HttpConn.getMarKetInfo(url);
                    String midResult = MaketInfoHandle.priceResult(MaketInfoHandle.getPrice(itemInfo));
                    if (midResult != null && i < num) {
                        i++;
                        result = result + entry.getKey() + ":\n";
                        result = result + midResult + "\n";
                    }
                    if (i == num) {
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return "";
                }
            }
        }
        if ("查询结果：\n" == result) {
            result = "未有合理反馈结果。可能名称错误或目前尚无对应价格。";
        } else {
            result = result + "数据来源：ceve-market";
        }
        return result;
    }

    // 接口鉴权V3
    private String AuthenticationV3(String secretId, String secretKey, String params, String timestamp, String service, String host, String algorithm) throws Exception {

        // 鉴权所需参数
        timestamp = timestamp;
        service = service;
        host = host;
        algorithm = algorithm;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = sdf.format(new Date(Long.valueOf(timestamp + "000")));

        // ************* 步骤 1：拼接规范请求串 *************
        String httpRequestMethod = "POST";
        String canonicalUri = "/";
        String canonicalQueryString = "";
        String canonicalHeaders = "content-type:application/json; charset=utf-8\n" + "host:" + host + "\n";
        String signedHeaders = "content-type;host";

        String payload = params;
        String hashedRequestPayload = sha256Hex(payload);
        String canonicalRequest = httpRequestMethod + "\n" + canonicalUri + "\n" + canonicalQueryString + "\n"
                + canonicalHeaders + "\n" + signedHeaders + "\n" + hashedRequestPayload;

        // ************* 步骤 2：拼接待签名字符串 *************
        String credentialScope = date + "/" + service + "/" + "tc3_request";
        String hashedCanonicalRequest = sha256Hex(canonicalRequest);
        String stringToSign = algorithm + "\n" + timestamp + "\n" + credentialScope + "\n" + hashedCanonicalRequest;

        // ************* 步骤 3：计算签名 *************
        byte[] secretDate = hmac256(("TC3" + secretKey).getBytes(UTF8), date);
        byte[] secretService = hmac256(secretDate, service);
        byte[] secretSigning = hmac256(secretService, "tc3_request");
        String signature = DatatypeConverter.printHexBinary(hmac256(secretSigning, stringToSign)).toLowerCase();

        // ************* 步骤 4：拼接 Authorization *************
        String authorization = algorithm + " " + "Credential=" + secretId + "/" + credentialScope + ", "
                + "SignedHeaders=" + signedHeaders + ", " + "Signature=" + signature;

        return authorization;
    }

    private byte[] hmac256(byte[] key, String msg) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
        mac.init(secretKeySpec);
        return mac.doFinal(msg.getBytes(UTF8));
    }

    private String sha256Hex(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] d = md.digest(s.getBytes(UTF8));
        return DatatypeConverter.printHexBinary(d).toLowerCase();
    }

    // 发送https请求
    private static String httpsExecute(String httpsRequestMethod, String url, Map<String, String> headers, String body) throws IOException {
        // 构建请求参数
        String result = "";
        BufferedReader in = null;
        OutputStream os = null;
        URLConnection urlConnection;
        // 创建Url链接
        urlConnection = new URL(url).openConnection();

        // 将链接转换为HTTPS链接
        HttpsURLConnection urlCon = (HttpsURLConnection) urlConnection;

        // 设置HTTPMethod，当前仅只是GET和POST
        urlCon.setRequestMethod(httpsRequestMethod);
        if (httpsRequestMethod.equals("POST")) {    // POST请求必须
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
        }

        // 添加请求头部
        if (headers != null) {
            for (Map.Entry<String, String> kv : headers.entrySet()) {
                urlCon.setRequestProperty(kv.getKey(), kv.getValue());
            }
        }

        // 发送数据
        os = urlCon.getOutputStream();
        os.write(body.getBytes());
        os.flush();

        // 获取响应
        String line;
        in = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
        while ((line = in.readLine()) != null) {
            result += line;
        }

        if (in != null) {
            in.close();
        }

        if (os != null) {
            os.close();
        }

        return result;
    }

    // 1. 文本会话请求
    private String TextProcess(String secretId, String secretKey, String region, String botId, String botEnv, String inputText, String terminalId, String sessionAttributes, String platformType, String platformId) throws Exception {

        // 接口所需参数
        String params = "{\"BotId\":" + "\"" + botId + "\"" + "," +
                "\"BotEnv\":" + "\"" + botEnv + "\"" + "," +
                "\"TerminalId\":" + "\"" + terminalId + "\"" + "," +
                "\"InputText\":" + "\"" + inputText + "\"" + "," +
                "\"SessionAttributes\": " + "\"" + sessionAttributes + "\"" + "," +
                "\"PlatformType\":" + "\"" + platformType + "\"" + "," +
                "\"PlatformId\":" + "\"" + platformId + "\"" + "}";


        // 腾讯云api公共参数
        final String acrion = "TextProcess";
        final String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        final String version = "2019-06-27";
        final String host = "tbp.tencentcloudapi.com";
        final String algorithm = "TC3-HMAC-SHA256";

        // 接口鉴权v3
        String authorization = AuthenticationV3(secretId, secretKey, params, timestamp, "tbp", host, algorithm);

        // 设置请求头
        TreeMap<String, String> headers = new TreeMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Host", host);
        headers.put("X-TC-Action", acrion);
        headers.put("X-TC-Region", region);
        headers.put("X-TC-Timestamp", timestamp);
        headers.put("X-TC-Version", version);
        headers.put("Authorization", authorization);

        // 发送https请求
        String resp = httpsExecute("POST", "https://" + host, headers, params);

        return resp;
    }

    public String chat(String str) {
        //  基本常量参数的定义
        String secretId = "AKID1u3B7pBm1A2ex1wPi05uXCSSR9tENJwI";
        String secretKey = "g6F9zZw47ybxby70Wrzt55VffQvCZG4e";
        String region = "ap-guangzhou";
        String terminal = "sdfghjkl1112";

        // 请求参数，用户根据实际自己填充
        String botId = "d4094fc2-f2e3-43e2-8673-4f323598e201";
        String botEnv = "release";
        String inputText = str;
        String sessionAttributes = "xxx";
        String platformType = "";
        String platformId = "";

        // 文本会话请求
        String response = null;
        try {
            response = TextProcess(secretId, secretKey, region, botId, botEnv, inputText, terminal, sessionAttributes, platformType, platformId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(response);
        if (response != null) {
            JSONObject chatInfo = JSONObject.parseObject(response);
            String ret = chatInfo.getJSONObject("Response").getString("ResponseText");
            return ret;
        } else {
            return "我出错啦！";
        }
    }

    public String caihongpi() {
        try {
            return HttpConn.httpGet("https://chp.shadiao.app/api.php");
        } catch (IOException e) {
            e.printStackTrace();
            return "网络出错啦！";
        }
    }

    //藏头诗
    public String cangtoushi(String text) {
        try {
            String result = HttpConn.httpGet("http://api.tianapi.com/txapi/cangtoushi/index?key=958ffed0b06c952d299d80677c37fd58&word=" + text);
            JSONObject obj = JSONObject.parseObject(result);
            int code = obj.getInteger("code");
            if (code == 200) {
                JSONArray array = obj.getJSONArray("newslist");
                if (array.size() >= 1) {
                    String returnText = array.getJSONObject(0).getString("list");
                    returnText = returnText.replace('，', '\n');
                    returnText = returnText.replace('。', '\n');
                    returnText = returnText.substring(0, returnText.length() - 1);
                    return returnText;
                } else {
                    return "数据异常";
                }
            } else if (code == 150) {
                return "该免费接口可用调用次数用完了，各位哥哥姐姐快给作者投个食，让他给这个api充个值吧";
            } else {
                return obj.getString("msg");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "网络出错啦！";
        }
    }

    //土味情话
    public String tuweiqinghua() {
        try {
            String result = HttpConn.httpGet("http://api.tianapi.com/txapi/saylove/index?key=958ffed0b06c952d299d80677c37fd58");
            JSONObject obj = JSONObject.parseObject(result);
            int code = obj.getInteger("code");
            if (code == 200) {
                JSONArray array = obj.getJSONArray("newslist");
                if (array.size() >= 1) {
                    String returnText = array.getJSONObject(0).getString("content");
                    return returnText;
                } else {
                    return "数据异常";
                }
            } else if (code == 150) {
                return "该免费接口可用调用次数用完了，各位哥哥姐姐快给作者投个食，让他给这个api充个值吧，或者第二天再玩";
            } else {
                return obj.getString("msg");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "网络出错啦！";
        }
    }
}
