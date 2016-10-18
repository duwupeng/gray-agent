package pn.eric.operations.client;

import io.socket.client.Socket;
import pn.eric.operations.util.JavaShellUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by eric on 16/10/18.
 */
public class Tailer implements Runnable{
    private static Process process;
    private static InputStream inputStream;
    private static BufferedReader reader;
    Socket socket;

    static {
        try{
            process = Runtime.getRuntime().exec("tail -f .record");
            inputStream = process.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public Tailer(){
    }
    public Tailer(Socket socket){
        this.socket=socket;
    }
    @Override
    public void run() {
        String line;
        try {
            while((line = reader.readLine()) != null) {
                // 将实时日志通过WebSocket发送给客户端，给每一行添加一个HTML换行
//                socket.emit("res", line);
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
//        JavaShellUtil.executeShell("mkdir ~/agentLog");
//        JavaShellUtil.executeShell("cd ~/agentLog;touch nohup.out");
        new Thread(new Tailer()).start();
    }
}
