import com.baidu.aip.speech.AipSpeech;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * @ClassName: AirportASRClient
 * @description:
 * @author: herui03
 * @create: 2019/2/22
 **/
public class AirportASRClient {

    //设置APPID/AK/SK
    public static final String APP_ID = "15608136";
    public static final String API_KEY = "rDUXcgsRGrvgueYs8ba4wiLi";
    public static final String SECRET_KEY = "BvKDDjqgKOfho2f02LERmAkmf1qmBVAR";

    private static final String AUDIO_PATH = "audio";
    private static final String JSON_PATH = "voicearray.json";

    private static final String PASS_WORD = " matches correctly";
    private static final String NOT_PASS_WORD = " matches incorrectly";


    public static void main(String[] args) {
        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理
        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
//        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");
        System.out.println("Hello WOrld");
//        doASR();
    }

    public static void doASR() {
        long startTime = System.currentTimeMillis();
        Logger.getSingleton().setStartTime(startTime);

        SimpleDateFormat format =  new SimpleDateFormat( "HH:mm:ss" );
        String startMessage = "开始语音识别 : " + format.format(startTime);
        System.out.println(startMessage);

        // 初始化一个AipSpeech
        AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        JSONArray jsonArray = new JSONArray(readFile(JSON_PATH));
        int passed = 0;
        int failed = 0;
        Pattern p = Pattern.compile("[a-zA-z]");
        for (int i = 0; i < 399; i++) {
            JSONArray arrayItem = (JSONArray)jsonArray.get(i);
            String content = ((String) arrayItem.get(1))
                    .replaceAll("[\\pP\\pS\\pZ]", "")
                    .toLowerCase();
            String fileName = ((String) arrayItem.get(0));
            String dicName = fileName.split("_")[0] + "_";
            if (p.matcher(content).find()) {
                passed ++;
                Logger.getSingleton().writeSuccessLine(fileName + PASS_WORD);
                continue;
            }

            JSONObject res = client.asr(AUDIO_PATH
                    + File.separator
                    + dicName
                    + File.separator
                    + fileName, "pcm", 16000, null);

            String result = res.getJSONArray("result")
                    .getString(0)
                    .replaceAll("[\\pP\\pS\\pZ]", "")
                    .toLowerCase();

            if (!content.equals(result)) {
                System.out.println(i+ " (" + fileName + ") " + "结果不匹配");
                System.out.println("expect:\t" + content + "\nactual:\t" + result);
                failed ++;
                Logger.getSingleton().writeFailureLine(fileName + NOT_PASS_WORD);
            } else {
                passed ++;
                Logger.getSingleton().writeSuccessLine(fileName + PASS_WORD);
            }
        }

        long endTime = System.currentTimeMillis();
        String resultMessage = "====================================================="
                + "\n比较结束，通过：\t" + passed
                + "\n未通过: \t" + failed
                + "\n耗时：" + (endTime - startTime)/1000 + "s";
        System.out.println(resultMessage);

        Logger.getSingleton().writeBoth(resultMessage);
        Logger.getSingleton().releaseWriter();
    }

    public static String readFile(String filePath) {
        File file = new File(filePath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String lastStr="";
        String tmpString;
        try {
            while ((tmpString = reader.readLine())!= null) {
                lastStr += tmpString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return lastStr;
    }
}
