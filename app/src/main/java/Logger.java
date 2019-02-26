import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @ClassName: Logger
 * @description:
 * @author: herui03
 * @create: 2019/2/25
 **/
public class Logger {

    private static final String LOG_DIRECTORY_PATH = "log/";
    private static final String SUCCESS_LOG_FILE_NAME_PREFIX = "success_";
    private static final String FAILURE_LOG_FILE_NAME_PREFIX = "failure_";

    private static final int FAILURE = 0;
    private static final int SUCCESS = 1;

    private static Logger singleton;
    //    private File successFile;
//    private File failureFile;
    private long startTime;
    private FileWriter successWriter;
    private FileWriter failureWriter;


    private Logger() {
        failureWriter = initWriter(FAILURE);
        successWriter =initWriter(SUCCESS);
    }

    private FileWriter initWriter(int type) {
        long startTime = System.currentTimeMillis();
        String prefix = null;
        switch (type) {
            case SUCCESS:
                prefix = SUCCESS_LOG_FILE_NAME_PREFIX;
                break;
            case FAILURE:
                prefix = FAILURE_LOG_FILE_NAME_PREFIX;
                break;
            default:
                prefix = "default";
                break;
        }
        File file = new File(
                "."
                        + File.separator
                        + "log"
                        + File.separator
                        + prefix
                        + new SimpleDateFormat( "HHmmss" )
                        .format(startTime)
                        + ".txt");
        FileWriter writer = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new FileWriter(file);
            writer.write(System.getProperty("line.separator"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer;
    }


    public static Logger getSingleton() {
        if (singleton == null) {
            synchronized (Logger.class) {
                if (singleton == null) {
                    singleton = new Logger();
                }
            }
        }
        return singleton;
    }

    public void writeBoth(String content) {
        Logger.getSingleton().writeSuccessLine(content);
        Logger.getSingleton().writeFailureLine(content);

    }

    public void writeSuccessLine(String content) {
        try {
            successWriter.write(content);
            successWriter.write(System.getProperty("line.separator"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFailureLine(String content) {
        try {
            failureWriter.write(content);
            failureWriter.write(System.getProperty("line.separator"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void releaseWriter() {
        try {
            successWriter.flush();
            successWriter.close();
            failureWriter.flush();
            failureWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            singleton = null;
        }
    }


}
