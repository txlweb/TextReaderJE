package com.teipreader.Lib;
import com.teipreader.LibTextParsing.TextReaderLibVa;
import com.teipreader.Main.Config_dirs;
import okhttp3.*;
import okio.BufferedSource;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
    //   example：  deepseek::你的apikey (已实现，使用v3，有点小贵)
    //   example：  openai::你的apikey::模型名(gpt-3.5-turbo/) (不知道能不能用，我也没钱充他家api)

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
        if(Objects.equals(parts[0], "deepseek")) {
            if(parts.length != 2) return;
            this.reqapi_deepseek();
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
    public void reqapi_online_gpt() {
        String[] parts = model.split("::");
        String API_URL = "https://api.openai.com/v1/chat/completions"; // OpenAI API 地址

        // 创建 OkHttpClient 实例
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(120, TimeUnit.SECONDS) // 设置读取超时为 120 秒
                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时为 10 秒
                .writeTimeout(10, TimeUnit.SECONDS) // 设置写入超时为 10 秒
                .build();

        // 创建请求体
        JSONObject jsonBody = new JSONObject();
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "developer")
                .put("content", new JSONObject().put("type", "text").put("text", "你是一个总结故事梗概的助手，尽可能简洁地给出用户发给你的下文的故事梗概，并分析文中出现的人物形象（仅主要人物），不要续写！中文回答！")));
        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", new JSONObject().put("type", "text").put("text", text)));
        jsonBody.put("messages", messages);

        // 设置 response_format 为 JSON 模式
        JSONObject responseFormat = new JSONObject();
        responseFormat.put("type", "json_object");
        jsonBody.put("response_format", responseFormat);

        // 定义期望的 JSON 模式
        JSONObject jsonSchema = new JSONObject();
        jsonSchema.put("type", "object");
        JSONObject properties = new JSONObject();
        properties.put("answer", new JSONObject().put("type", "string"));
        properties.put("explanation", new JSONObject().put("type", "string"));
        jsonSchema.put("properties", properties);
        jsonSchema.put("required", new JSONArray().put("answer"));
        jsonBody.put("json_schema", jsonSchema);

        // 将 JSON 转为 RequestBody
        RequestBody body = RequestBody.create(
                jsonBody.toString(), MediaType.parse("application/json; charset=utf-8")
        );

        // 构建请求
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", parts[1]) // 使用 parts[1] 作为 API Key
                .build();
        System.out.println(jsonBody.toString());
        // 发送请求并获取响应
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                // 如果响应码不是 2xx，抛出异常
                System.out.println((char) 27 + "[31m[E]: [HTTP REQ(OPENAI)] Unexpected code " + response);
                state = response.code();
                return;
            }

            // 读取响应内容
            fullResponse.delete(0, fullResponse.length());
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 解析 JSON 响应
                    JSONObject jsonResponse = new JSONObject(line);
                    if (jsonResponse.has("choices")) {
                        // 提取 choices 中的消息内容
                        String content = jsonResponse.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        fullResponse.append(content);
                    }
                }
            } catch (IOException e) {
                state = -2;
                throw new RuntimeException(e);
            }

            // 请求成功
            state = 0;

        } catch (IOException e) {
            state = -2;
            //throw new RuntimeException(e);
        }
    }
    public void reqapi_deepseek() {
        String[] parts = model.split("::");
        String API_URL = "https://api.deepseek.com/chat/completions"; // DeepSeek API 地址

        // 创建 OkHttpClient 实例
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(120, TimeUnit.SECONDS) // 设置读取超时为 120 秒
                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时为 10 秒
                .writeTimeout(10, TimeUnit.SECONDS) // 设置写入超时为 10 秒
                .build();

        // 创建请求体
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", "deepseek-chat"); // 模型名称
        jsonBody.put("stream", false); // 是否流式响应

        // 构造 messages 数组
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", "你是一个总结故事梗概的助手，尽可能简洁的给出用户发给你的下文的故事梗概，并分析文中出现的人物形象（仅主要人物），不要续写！")); // 系统角色
        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", text)); // 用户输入
        jsonBody.put("messages", messages);

        // 其他参数
        jsonBody.put("frequency_penalty", 0);
        jsonBody.put("max_tokens", 2048);
        jsonBody.put("presence_penalty", 0);
        jsonBody.put("response_format", new JSONObject().put("type", "text"));
        jsonBody.put("stop", JSONObject.NULL);
        jsonBody.put("stream_options", JSONObject.NULL);
        jsonBody.put("temperature", 1);
        jsonBody.put("top_p", 1);
        jsonBody.put("tools", JSONObject.NULL);
        jsonBody.put("tool_choice", "none");
        jsonBody.put("logprobs", false);
        jsonBody.put("top_logprobs", JSONObject.NULL);

        // 将 JSON 转为 RequestBody
        RequestBody body = RequestBody.create(
                jsonBody.toString(), MediaType.parse("application/json; charset=utf-8")
        );

        // 构建请求
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + parts[1]) // 使用 parts[1] 作为 API Key
                .build();
        System.out.println(jsonBody.toString());
        // 发送请求并获取响应
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                // 如果响应码不是 2xx，抛出异常
                System.out.println((char) 27 + "[31m[E]: [HTTP REQ(DEEPSEEK)] Unexpected code " + response);
                state = response.code();
                return;
            }

            // 读取响应内容
            fullResponse.delete(0, fullResponse.length());
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 解析 JSON 响应
                    JSONObject jsonResponse = new JSONObject(line);
                    if (jsonResponse.has("choices")) {
                        // 提取 choices 中的消息内容
                        String content = jsonResponse.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        fullResponse.append(content);
                    }
                }
            } catch (IOException e) {
                state = -2;
                throw new RuntimeException(e);
            }

            // 请求成功
            state = 0;

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
