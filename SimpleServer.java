package chat;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer extends Thread implements ActionListener, WindowListener{
	Frame frame;
	TextArea messageArea;
	TextField inputField;
	ServerSocket s;
	Socket s1;
	DataOutputStream dos;
	DataInputStream dis;
	boolean stop;
	
	public SimpleServer() {
		launchFrame();
		service();
	}
	
	/* UI을 초기화 해주는 메소드 */
	public void launchFrame() {
		frame = new Frame("채팅 프로그램 - Server");
		frame.addWindowListener(this);
		frame.setLocationRelativeTo(null);
		messageArea = new TextArea();
		inputField = new TextField();
		
		frame.setBackground(Color.lightGray);
		messageArea.setEditable(false);
		frame.add(messageArea, BorderLayout.CENTER);
		frame.add(inputField, BorderLayout.SOUTH);
		
		inputField.addActionListener(this);
		frame.setSize(500, 300);
		frame.setVisible(true);
		inputField.requestFocus();
	}
	
	public void service() {
		try {
			messageArea.append("서비스 하기 위해 준비중..\n");
			s = new ServerSocket(5432);
			messageArea.append("클라이언트 접속 대기중..");
			s1 = s.accept();
			messageArea.append("클라이언트가 접속하였습니다. : " + s1.getInetAddress() + "\n");
			dos = new DataOutputStream(s1.getOutputStream());
			dis = new DataInputStream(s1.getInputStream());
			this.start(); //쓰레드 시작
			dos.writeUTF(" 채팅 서버에 접속하신걸 환영합니다.\n");
		}catch(IOException e) {e.printStackTrace();}
	}
	
	public static void main(String[] args) {
		new SimpleServer();
	}
	
	public void actionPerformed(ActionEvent action) {
		try {
			String msg = "서버 : " + inputField.getText() + "\n";
			messageArea.append(msg);
			
			dos.writeUTF(msg);
			inputField.setText("");
			
		}catch(IOException e) {
			messageArea.append(e.toString() + "\n");
		}
	}
	
	public void run() {
		try {
			while(!stop) {
				messageArea.append(dis.readUTF());
			}
			
		}catch(EOFException e) {
			messageArea.append("클라이언트로부터 연결이 끊어졌습니다.\n");
		}catch(IOException e1) { // 생각 해보기
			System.out.println("쓰레드 종료");
		}
	}
	
	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		if(e.getWindow() instanceof Frame) {
			stop = true;
			try {
				if(s1 != null) { // 클라이언트와의 연결이 성공 후에
					dis.close();
					dos.close();
					s1.close();
				}
			}catch(IOException e1) {
				e1.printStackTrace();
			}
		}
		
		e.getWindow().setVisible(false);
		e.getWindow().dispose();
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}
}
