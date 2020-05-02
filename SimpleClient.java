package chat;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
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
import java.net.ConnectException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


class ReadSMessageThread implements Runnable{
	private TextArea messageArea;
	private DataInputStream dis;
	
	public ReadSMessageThread(TextArea messageArea, DataInputStream dis) {
		this.messageArea = messageArea;
		this.dis = dis;
	}
	
	public void run() {
		System.out.println("쓰레드 시작");
		try {
			while(!Thread.currentThread().isInterrupted()) {
				messageArea.append(dis.readUTF());
			}
			
		}catch(EOFException e) {
			messageArea.append("서버로부터 연결이 끊어졌습니다.\n");
		}catch(IOException e1) { // 생각 해보기
			System.out.println("쓰레드 종료");
		}
	}
}

public class SimpleClient implements ActionListener, WindowListener{
	
	Frame frame;
	TextArea messageArea;
	TextField inputField, ipField, idField, portField;
	Dialog dialog;
	Label ipLabel, idLabel, portLabel;
	JButton dialogBtn, portBtn;
	
	Socket s1;
	DataOutputStream dos;
	DataInputStream dis;
	
	boolean stop;
	String host;
	String userID;
	int portNum;
	
	Thread t1;
	
	public SimpleClient() {
		launchFrame();
	}
	
	public void launchFrame() {
		frame = new Frame("채팅 프로그램 - Client");
		frame.addWindowListener(this);
		
		messageArea = new TextArea();
		inputField = new TextField();
		frame.add(messageArea, BorderLayout.CENTER);
		frame.add(inputField, BorderLayout.SOUTH);
		messageArea.setEditable(false);
		inputField.addActionListener(this);
		frame.setBackground(Color.lightGray);
		frame.setSize(700, 500);
		frame.setVisible(true);
		inputField.requestFocus();
		
		//dialog 설정
		dialog = new Dialog(frame, "서버 IP/port 및 클라이언트 ID", true);
		dialog.addWindowListener(this);
		dialog.setLayout(new GridLayout(1, 3, 50, 0));
		
		JPanel labelPanel = new JPanel();
		dialog.add(labelPanel);
		labelPanel.setLayout(new GridLayout(0, 1, 0, 10));
		
		ipLabel = new Label("접속할 서버 IP 주소를 입력하세요");
		portLabel = new Label("서버 port 번호를 입력하세요");
		idLabel = new Label("ID를 입력하세요");
		
		labelPanel.add(ipLabel);
		labelPanel.add(portLabel);
		labelPanel.add(idLabel);
		
		
		JPanel fieldPanel = new JPanel();
		dialog.add(fieldPanel);
		fieldPanel.setLayout(new GridLayout(0, 1, 0, 10));
		
		ipField = new TextField(15);
		portField = new TextField(15);
		idField = new TextField(15);
		fieldPanel.add(ipField);
		fieldPanel.add(portField);
		fieldPanel.add(idField);
		
		dialogBtn = new JButton("확인");
		dialog.add(dialogBtn);
		
		dialogBtn.addActionListener(this);
		dialog.pack(); // 구성 요소를 크기에 맞게 조절
		dialog.setVisible(true);
		ipField.requestFocus();
		
	}
	
	public void service() throws IOException{
		s1 = new Socket(host, portNum);
		dis = new DataInputStream(s1.getInputStream());
		dos = new DataOutputStream(s1.getOutputStream());
		messageArea.append("접속완료.. \n");
		
		ReadSMessageThread rmThread = new ReadSMessageThread(messageArea, dis);
		t1 = new Thread(rmThread);
		t1.start();

	}
	
	public void actionPerformed(ActionEvent action) {
		if(inputField==action.getSource()) {
			try {
				String msg = userID + " : " + inputField.getText() + "\n";
				messageArea.append(msg);
				
				dos.writeUTF(msg);
				inputField.setText("");
				
			}catch(IOException e) {
				messageArea.append(e.toString() + "\n");
			}
			
		}else if(dialogBtn == action.getSource()) {
			host = ipField.getText().trim();
			portNum = Integer.parseInt(portField.getText());
			userID = idField.getText();
			
			if(userID == null || userID.trim().length() ==0) {
				JOptionPane.showMessageDialog(null, "ID을 입력해주세요", "ID 입력 확인", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			if(host.equals("")) host = "localhost";
			
			try {
				service();
			}catch(ConnectException e) {
				JOptionPane.showMessageDialog(null, "port 번호를 확인해주세요", "port 번호 확인", JOptionPane.WARNING_MESSAGE);
				return;
			}catch(IOException e2) {
				e2.printStackTrace();
			}
			
			dialog.dispose();
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
				dis.close();
				dos.close();
				s1.close();
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
		new SimpleClient();
	}
}
