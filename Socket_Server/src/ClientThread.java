import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    MyServerMain myServerMain;
    Socket socket;

    InputStream istream;
    OutputStream ostream;
    BufferedReader receiveRead;
    PrintWriter pwrite;

    String receiveMessage;
    private String nickName;

    public ClientThread(MyServerMain myServerMain, Socket socket) {
        this.myServerMain = myServerMain;
        this.socket = socket;
        System.out.println("IP 주소 "+socket.getInetAddress()+" 님이 입장했습니다.");
    }

    public String getNickName() {
        return nickName;
    }

    @Override
    public void run() {
        try {
        	
            istream = socket.getInputStream();
            receiveRead = new BufferedReader(new InputStreamReader(istream));

            ostream = socket.getOutputStream();
            pwrite = new PrintWriter(ostream, true);

            while ((receiveMessage = receiveRead.readLine()) != null) {
                // 닉네임 등록 메시지는 브로드캐스트하지 않음
                if (receiveMessage.startsWith("/nick ")) {
                    this.nickName = receiveMessage.substring(6);
                    System.out.println(nickName + " 님이 닉네임 등록 완료");
                    continue; // 브로드캐스트 안함
                }
             // 접속자 목록 요청 처리
                if (receiveMessage.equals("/list")) {
                    String users = myServerMain.clientList.stream()
                        .map(ct -> ((ClientThread) ct).getNickName())
                        .filter(n -> !n.equals(this.nickName)) // 자기 자신 제외
                        .reduce((a,b) -> a + "," + b)
                        .orElse("");
                    pwrite.println("/list " + users);
                    continue;
                }


                // 받은 메시지를 모든 클라이언트에 전송
                for (Thread t : myServerMain.clientList) {
                    ClientThread ct = (ClientThread) t;
                    // 자신에게만 보내고 싶으면 nickName 체크 가능
                    if (ct != this) { // 본인 제외
                        ct.pwrite.println(receiveMessage);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("IP 주소 "+socket.getInetAddress()+" 님이 퇴장했습니다.");
            myServerMain.clientList.remove(this);
            System.out.println("총 클라이언트 수 : " + myServerMain.clientList.size() + "명");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
