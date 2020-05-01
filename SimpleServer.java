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
	
	/* UI�� �ʱ�ȭ ���ִ� �޼ҵ� */
	public void launchFrame() {
		frame = new Frame("ä�� ���α׷� - Server");
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
			messageArea.append("���� �ϱ� ���� �غ���..\n");
			s = new ServerSocket(5432);
			messageArea.append("Ŭ���̾�Ʈ ���� �����..");
			s1 = s.accept();
			messageArea.append("Ŭ���̾�Ʈ�� �����Ͽ����ϴ�. : " + s1.getInetAddress() + "\n");
			dos = new DataOutputStream(s1.getOutputStream());
			dis = new DataInputStream(s1.getInputStream());
			this.start(); //������ ����
			dos.writeUTF(" ä�� ������ �����ϽŰ� ȯ���մϴ�.\n");
		}catch(IOException e) {e.printStackTrace();}
	}
	
	public static void main(String[] args) {
		new SimpleServer();
	}
	
	public void actionPerformed(ActionEvent action) {
		try {
			String msg = "���� : " + inputField.getText() + "\n";
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
			messageArea.append("Ŭ���̾�Ʈ�κ��� ������ ���������ϴ�.\n");
		}catch(IOException e1) { // ���� �غ���
			System.out.println("������ ����");
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
				if(s1 != null) { // Ŭ���̾�Ʈ���� ������ ���� �Ŀ�
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
