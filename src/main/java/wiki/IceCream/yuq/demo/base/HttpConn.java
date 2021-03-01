package wiki.IceCream.yuq.demo.base;

import com.alibaba.fastjson.JSONObject;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpConn {

    static OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                            .callTimeout(60, TimeUnit.SECONDS)
                                            .readTimeout(60,TimeUnit.SECONDS)
                                            .build();

    public static JSONObject getMarKetInfo(String getUrl) throws IOException {
        Request request = new Request.Builder().url(getUrl).build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        JSONObject marketInfo = JSONObject.parseObject(response.body().string());
        System.out.println(marketInfo);
        return marketInfo;
    }

    public static String httpGet(String getUrl) throws IOException {
        Request request = new Request.Builder().url(getUrl).build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        String result = response.body().string();
        System.out.println(result);
        return result;
    }
}
