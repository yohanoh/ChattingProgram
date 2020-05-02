package chat;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

class ReadCMessageThread implements Runnable{
	private TextArea messageArea;
	private DataInputStream dis;
	private TextField portField;
	private JButton portBtn;
	
	public ReadCMessageThread(TextArea messageArea, DataInputStream dis, TextField portField, JButton portBtn) {
		this.messageArea = messageArea;
		this.dis = dis;
		this.portField = portField;
		this.portBtn = portBtn;
		
	}
	
	public void run() {
		System.out.println("쓰레드 시작");
		try {
			while(!Thread.currentThread().isInterrupted()) {
				messageArea.append(dis.readUTF());
			}
			
		}catch(EOFException e) {
			messageArea.append("클라이언트로부터 연결이 끊어졌습니다.\n");
			portField.setEditable(true);
			portBtn.setEnabled(true);
		}catch(IOException e1) { // 생각 해보기
			System.out.println("쓰레드 종료");
		}
	}
}

public class SimpleServer implements ActionListener, WindowListener{
	Frame frame;
	TextArea messageArea;
	TextField inputField, portField;
	JButton portBtn;
	JPanel portPanel;
	JLabel portLabel;
	
	ServerSocket s;
	Socket s1;
	DataOutputStream dos;
	DataInputStream dis;
	
	boolean stop;
	int portNum;
	
	Thread t1;
	
	
	public SimpleServer() {
		launchFrame();
	}
	
	/* UI을 초기화 해주는 메소드 */
	public void launchFrame() {
		frame = new Frame("채팅 프로그램 - Server");
		frame.addWindowListener(this);
		frame.setLocationRelativeTo(null);
		frame.setBackground(Color.lightGray);
		
		messageArea = new TextArea();
		messageArea.setEditable(false);
		frame.add(messageArea, BorderLayout.CENTER);
		
		inputField = new TextField();
		frame.add(inputField, BorderLayout.SOUTH);
		
		portPanel = new JPanel();
		portLabel = new JLabel("포트 번호를 입력하세요 (1024이상) : ");
		portField = new TextField();
		portBtn = new JButton("확인");
		frame.add(portPanel, BorderLayout.NORTH);
		portPanel.setLayout(new GridLayout(0, 3, 0, 0));
		portPanel.add(portLabel);
		portPanel.add(portField);
		portPanel.add(portBtn);
		
		portBtn.addActionListener(this);
		inputField.addActionListener(this);
		frame.setSize(700, 500);
		frame.setVisible(true);
		inputField.requestFocus();
	}
	
	public void service() {
		try {
			messageArea.append("서비스 하기 위해 준비중..\n");
			s = new ServerSocket(portNum);
			messageArea.append("클라이언트 접속 대기중..\n");
			s1 = s.accept();
			messageArea.append("클라이언트가 접속하였습니다. : " + s1.getInetAddress() + "\n");
			dos = new DataOutputStream(s1.getOutputStream());
			dis = new DataInputStream(s1.getInputStream());
			
			
			ReadCMessageThread rmThread = new ReadCMessageThread(messageArea, dis, portField, portBtn);
			t1 = new Thread(rmThread);
			t1.start();
			
			dos.writeUTF(" 채팅 서버에 접속하신걸 환영합니다.\n");
		}catch(IOException e) {e.printStackTrace();}
	}
	
	public boolean isOpenPort(int port) {
		boolean result = false;
		try {
			(new Socket("localhost", port)).close();
			result = true;
		}catch(Exception e) {
		}
		
		return result;
	}
	
	public void actionPerformed(ActionEvent action) {
		if(portBtn==action.getSource()) {
			System.out.println("btn");
			portNum = Integer.parseInt(portField.getText());
			if(portNum > 1024) {
				if(isOpenPort(portNum)) {
					JOptionPane.showMessageDialog(null, "중복된 port 번호입니다. 다른 port 번호를 입력하세요.", "중복 port 번호", JOptionPane.WARNING_MESSAGE);
				}else {
					portField.setEditable(false);
					portBtn.setEnabled(false);
					service();
				}
			}else {
				JOptionPane.showMessageDialog(null, "입력이 불가한 port 번호입니다. 다른 port 번호를 입력하세요.", "사용 불허 port 번호", JOptionPane.WARNING_MESSAGE);
			}
			
		}else {
			try {
				String msg = "서버 : " + inputField.getText() + "\n";
				messageArea.append(msg);
				
				dos.writeUTF(msg);
				inputField.setText("");
				
			}catch(IOException e) {
				messageArea.append(e.toString() + "\n");
			}
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
			if(t1 != null) {
				t1.interrupt();
			}

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
	
	public static void main(String[] args) {
		new SimpleServer();
	}
}
