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
		frame = new Frame("�ϴ��� ä��");
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
			ta.append("���� �ϱ� ���� �غ���..\n");
			s = new ServerSocket(5432);
			ta.append("Ŭ���̾�Ʈ ���� �����..");
			s1 = s.accept();
			ta.append("Ŭ���̾�Ʈ�� �����Ͽ����ϴ�. : " + s1.getInetAddress() + "\n");
			dos = new DataOutputStream(s1.getOutputStream());
			dis = new DataInputStream(s1.getInputStream());
			this.start();
			dos.writeUTF(" ä�� ������ �����ϽŰ� ȯ���մϴ�.");
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
				dos.writeUTF("���� : "+ msg);
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
			ta.append("Ŭ���̾�Ʈ�κ��� ������ ���������ϴ�.\n");
		}catch(IOException e1) {
			e1.printStackTrace();
		}
	}
}
