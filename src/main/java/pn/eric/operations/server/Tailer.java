package pn.eric.operations.server;

import com.corundumstudio.socketio.SocketIOServer;
import io.socket.client.Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by duwupeng on 16/10/18.
 */
public class Tailer implements Runnable{
    private static Process process;
    private static InputStream inputStream;
    private static BufferedReader reader;
    SocketIOServer server;

    static {
        try{
            process = Runtime.getRuntime().exec("tail -f agent.log");
            inputStream = process.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public Tailer(){
    }
    public Tailer(SocketIOServer server){
        this.server=server;
    }
    @Override
    public void run() {
        String line;
        try {
            while((line = reader.readLine()) != null) {
                // 将实时日志通过WebSocket发送给客户端，给每一行添加一个HTML换行
//                socket.emit("res", line);
                System.out.println(line);
                server.getRoomOperations("webReg").sendEvent("res",line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                reader.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        new Thread(new Tailer()).start();
    }
}
