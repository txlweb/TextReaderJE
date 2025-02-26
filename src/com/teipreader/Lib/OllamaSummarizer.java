package com.teipreader.Lib;
import com.teipreader.LibTextParsing.TextReaderLibVa;
import com.teipreader.Main.Config_dirs;
import okhttp3.*;
import okio.BufferedSource;
import org.json.JSONObject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OllamaSummarizer extends Thread{

    private StringBuilder fullResponse = new StringBuilder();
    private int state = -1;
    private String text = "";
    private String model = "ollama::deepseek-r1:1.5b";
    public String removeThinkTags(String input) {
        // 删除 <think> 和 </think> 中间的内容及标签
        input = input.replaceAll("<think>.*?</think>", "");
        // 仅删除单独的 <think> 标签
        input = input.replaceAll("<think>", "");
        input = input.replaceAll("</think>", "");

        return input;
    }
    public String getStreamTextOutput(){
        return removeThinkTags(fullResponse.toString());
    }
    public int getStreamState(){
        return state;
    }
    // model: 设置模型
    //   本地部署类： ollama::模型名
    //   example：  ollama::deepseek-r1:1.5b （默认参数就是这个）
    //   在线API类： api地址::key (没有实现)
    //   example：  deepseek::你的apikey (没有实现，文档语焉不详😡，我没钱充他家api)
    //   example：  openai::模型名(gpt-3.5-turbo/)::你的apikey (不知道能不能用，我也没钱充他家api)

    @Override
    public void run() {

        state = -1;
        String[] parts = model.split("::");
        System.out.println(parts.length);
        if(parts.length < 2) return;
        if(Objects.equals(parts[0], "ollama")) {
            if(parts.length != 2) return;
            this.reqapi_local_ollama();
            return;
        }
        if(Objects.equals(parts[0], "openai")) {
            if(parts.length != 3) return;
            this.reqapi_online_gpt();
            return;
        }
        state = -2;
    }
    private void reqapi_local_ollama(){
        String[] parts = model.split("::");
        String API_URL = "http://127.0.0.1:11434/api/generate"; // 根据你使用的 API 地址
        // 创建 OkHttpClient 实例
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonBody = new JSONObject();
        // 创建请求体
        jsonBody.put("model", parts[1]);
        jsonBody.put("prompt", text); // 发送的章节内容
        // 将 JSON 转为 RequestBody
        RequestBody body = RequestBody.create(
                jsonBody.toString(), MediaType.parse("application/json; charset=utf-8")
        );
        //System.out.println(jsonBody.toString());
        // 构建请求
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
        // 发送请求并获取响应
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                // 如果响应码不是 2xx，抛出异常
                System.out.println((char) 27 + "[31m[E]: [HTTP REQ(AI SUMMARY)] Unexpected code " + response);
                state = response.code();
                return;
            }
            // 使用 BufferedReader 逐行读取流式响应
            //StringBuilder fullResponse = new StringBuilder();
            fullResponse.delete(0,fullResponse.length());
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 逐行解析 JSON 内容
                    JSONObject jsonLine = new JSONObject(line);
                    // 提取每行的 "response" 字段
                    if (jsonLine.has("response")) {
                        String partialResponse = jsonLine.getString("response");
                        fullResponse.append(partialResponse);
                        //System.out.print(partialResponse);
                    }
                }
            } catch (IOException e) {
                state = -2;
                throw new RuntimeException(e);
            }
            //System.out.println("");

            // 返回合并后的响应
            state = 0;

        } catch (IOException e) {
            state = -2;
            //throw new RuntimeException(e);
        }
    }
    private void reqapi_online_gpt() {
        // 解析传入的model参数，获取OpenAI、模型版本和API密钥
        String[] modelParts = model.split("::");

        String apiKey = modelParts[2];
        String modelVersion = modelParts[1];

        // 构建API请求URL
        String url = "https://api.openai.com/v1/completions";  // 流式请求时的URL

        // 构造请求体
        JSONObject requestBodyJson = new JSONObject();
        requestBodyJson.put("model", modelVersion);
        requestBodyJson.put("prompt", text);
        requestBodyJson.put("max_tokens", 150);
        requestBodyJson.put("temperature", 0.7);
        requestBodyJson.put("stream", true); // 开启流式返回

        // 创建RequestBody
        RequestBody requestBody = RequestBody.create(requestBodyJson.toString(), MediaType.parse("application/json"));
        OkHttpClient client = new OkHttpClient();
        // 创建请求对象
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + apiKey)
                .post(requestBody)
                .build();

        // 发起请求
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Request failed: " + response);
                return;
            }

            // 处理流式返回内容
            try (BufferedSource source = response.body().source()) {
                while (!source.exhausted()) {
                    String line = source.readUtf8Line(); // 读取一行数据
                    if (line != null && line.startsWith("data: ")) {
                        String responseData = line.substring(6).trim(); // 去掉 'data: ' 前缀
                        if (!responseData.equals("[DONE]")) {
                            // 将每个数据块追加到 fullResponse 中
                            JSONObject responseObject = new JSONObject(responseData);
                            String text = responseObject.getJSONArray("choices").getJSONObject(0).getString("text");
                            fullResponse.append(text);
                        }
                    }
                }
            }

            // 打印完整的响应
            System.out.println("Response: \n" + fullResponse.toString());
        } catch (IOException e) {
            state = -2;
            //throw new RuntimeException(e);
        }
    }
    public OllamaSummarizer(String text_, String model_) {
        this.text = text_;
        if(!Objects.equals(model_, "") && model_!=null) this.model = model_;
    }



}
