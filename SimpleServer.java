package chat;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer extends Thread implements ActionListener{
	Frame frame;
	TextArea ta;
	TextField tf;
	ServerSocket s;
	Socket s1;
	DataOutputStream dos;
	DataInputStream dis;
	boolean stop;
	
	public SimpleServer() {
		launchFrame();
		service();
	}
	
	public void launchFrame() {
		frame = new Frame("일대일 채팅");
		ta = new TextArea();
		tf = new TextField();
		frame.setBackground(Color.lightGray);
		ta.setEditable(false);
		frame.add(ta, BorderLayout.CENTER);
		frame.add(tf, BorderLayout.SOUTH);
		tf.addActionListener(this);
		frame.setSize(500, 300);
		frame.setVisible(true);
		tf.requestFocus();
	}
	
	public void service() {
		try {
			ta.append("서비스 하기 위해 준비중..\n");
			s = new ServerSocket(5432);
			ta.append("클라이언트 접속 대기중..");
			s1 = s.accept();
			ta.append("클라이언트가 접속하였습니다. : " + s1.getInetAddress() + "\n");
			dos = new DataOutputStream(s1.getOutputStream());
			dis = new DataInputStream(s1.getInputStream());
			this.start();
			dos.writeUTF(" 채팅 서버에 접속하신걸 환영합니다.");
		}catch(IOException e) {e.printStackTrace();}
	}
	
	public static void main(String[] args) {
		new SimpleServer();
	}
	
	public void actionPerformed(ActionEvent action) {
		try {
			String msg = tf.getText();
			ta.append(msg + "\n");
			if(msg.equals("exit")) {
				ta.append("bye");
				stop = true;
				dos.close();
				s1.close();
				System.exit(0);
			}else {
				dos.writeUTF("서버 : "+ msg);
				tf.setText("");
			}
		}catch(IOException e) {
			ta.append(e.toString() + "\n");
		}
	}
	
	public void run() {
		try {
			while(!stop) {
				ta.append(dis.readUTF() + "\n");
			}
			dis.close();
			s1.close();
		}catch(EOFException e) {
			ta.append("클라이언트로부터 연결이 끊어졌습니다.\n");
		}catch(IOException e1) {
			e1.printStackTrace();
		}
	}
}
