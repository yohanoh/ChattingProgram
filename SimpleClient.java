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
		System.out.println("������ ����");
		try {
			while(!Thread.currentThread().isInterrupted()) {
				messageArea.append(dis.readUTF());
			}
			
		}catch(EOFException e) {
			messageArea.append("�����κ��� ������ ���������ϴ�.\n");
		}catch(IOException e1) { // ���� �غ���
			System.out.println("������ ����");
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
		frame = new Frame("ä�� ���α׷� - Client");
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
		
		//dialog ����
		dialog = new Dialog(frame, "���� IP/port �� Ŭ���̾�Ʈ ID", true);
		dialog.addWindowListener(this);
		dialog.setLayout(new GridLayout(1, 3, 50, 0));
		
		JPanel labelPanel = new JPanel();
		dialog.add(labelPanel);
		labelPanel.setLayout(new GridLayout(0, 1, 0, 10));
		
		ipLabel = new Label("������ ���� IP �ּҸ� �Է��ϼ���");
		portLabel = new Label("���� port ��ȣ�� �Է��ϼ���");
		idLabel = new Label("ID�� �Է��ϼ���");
		
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
		
		dialogBtn = new JButton("Ȯ��");
		dialog.add(dialogBtn);
		
		dialogBtn.addActionListener(this);
		dialog.pack(); // ���� ��Ҹ� ũ�⿡ �°� ����
		dialog.setVisible(true);
		ipField.requestFocus();
		
	}
	
	public void service() throws IOException{
		s1 = new Socket(host, portNum);
		dis = new DataInputStream(s1.getInputStream());
		dos = new DataOutputStream(s1.getOutputStream());
		messageArea.append("���ӿϷ�.. \n");
		
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
				JOptionPane.showMessageDialog(null, "ID�� �Է����ּ���", "ID �Է� Ȯ��", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			if(host.equals("")) host = "localhost";
			
			try {
				service();
			}catch(ConnectException e) {
				JOptionPane.showMessageDialog(null, "port ��ȣ�� Ȯ�����ּ���", "port ��ȣ Ȯ��", JOptionPane.WARNING_MESSAGE);
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
