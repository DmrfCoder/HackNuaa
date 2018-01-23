import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.awt.*;
import java.net.*;
import java.util.Enumeration;
import javax.swing.*;


public class RemoteControlServer extends JFrame {
    private static int port = 888;
    ServerThread serverthread;
    String message = null;
    String[] messages = null;
    String type = null;
    String info = null;
    private static double mx;
    private static double my;
    JLabel jlshowport;

    public void CreateJFream(String title) {
        JFrame jf = new JFrame(title);
        Container container = jf.getContentPane();
        container.setLayout(new GridLayout(2, 2));

        JLabel jlip = new JLabel("IP:");
        JLabel jlshowip = null;
        try {
            jlshowip = new JLabel(getLANAddressOnWindows());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        JLabel jlport = new JLabel("Port:");
        jlshowport = new JLabel(getPortOnWindows());
        //        jl.setHorizontalAlignment(JLabel.CENTER);
        //jlport.setBounds(10,40,30,20);
        container.add(jlip);
        container.add(jlshowip);
        container.add(jlport);
        container.add(jlshowport);

        jf.setVisible(true);
        jf.setSize(300, 150);


        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public String getLANAddressOnWindows() throws SocketException {
        Enumeration e = NetworkInterface.getNetworkInterfaces();

        String ip[];
        ip = new String[10];
        int k = 0;
        while (e.hasMoreElements()) {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements()) {


                InetAddress i = (InetAddress) ee.nextElement();

                ip[k++] = i.getHostAddress();

            }
        }
        ;
        return ip[1];
    }

    public String getPortOnWindows() {
        serverthread = new ServerThread();
        serverthread.start();
        return String.valueOf(port);
    }

    public static void main(String[] args) {
        RemoteControlServer jf = new RemoteControlServer();
        jf.CreateJFream("");
    }


    public class ServerThread extends Thread {
        public void run() {
            try {
                DatagramSocket socket;
                while (true) {
                    try {
                        socket = new DatagramSocket(port);
                        break;
                    } catch (Exception e) {
                        port++;

                        continue;
                    }
                }
                byte data[] = new byte[1024];
                //创建一个空的DatagramPacket对象
                DatagramPacket packet = new DatagramPacket(data, data.length);
                //使用receive方法接收客户端所发送的数据
                port = socket.getLocalPort();
                jlshowport.setText(socket.getLocalPort() + "");
                System.out.println("开启端口监听" + socket.getLocalPort());
                while (true) {
                    socket.receive(packet);
                    message = new String(packet.getData(), packet.getOffset(), packet.getLength());
                    System.out.println("message--->" + message);
                    messages = message.split(":");
                    if (message.length() >= 2) {
                        type = messages[0];
                        info = messages[1];
                        if (type.equals("mouse"))
                            MouseMove(info);
                        if (type.equals("leftButton"))
                            LeftButton(info);
                        if (type.equals("rightButton"))
                            RightButton(info);
                        if (type.equals("mousewheel"))
                            MouseWheel(info);
                        if (type.equals("keyboard"))
                            KeyBoard(info);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void MouseMove(String info) {
            String args[] = info.split(",");
            String x = args[0];
            String y = args[1];
            float px = Float.valueOf(x);
            float py = Float.valueOf(y);

            PointerInfo pinfo = MouseInfo.getPointerInfo();
            java.awt.Point p = pinfo.getLocation();
            mx = p.getX();
            my = p.getY();
            java.awt.Robot robot;
            try {
                robot = new Robot();

                robot.mouseMove((int) mx + (int) px, (int) my + (int) py);
            } catch (AWTException e) {
                e.printStackTrace();
            }

        }

        public void LeftButton(String info) throws AWTException {
            java.awt.Robot robot = new Robot();
            if (info.equals("down"))
                robot.mousePress(InputEvent.BUTTON1_MASK);
            else if (info.equals("release"))
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
            else if (info.equals("up"))
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
            else if (info.equals("click")) {
                robot.mousePress(InputEvent.BUTTON1_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
            }
        }

        public void RightButton(String info) throws AWTException {
            java.awt.Robot robot = new Robot();
            if (info.equals("down"))
                robot.mousePress(InputEvent.BUTTON3_MASK);
            else if (info.equals("release"))
                robot.mouseRelease(InputEvent.BUTTON3_MASK);
            else if (info.equals("up"))
                robot.mouseRelease(InputEvent.BUTTON3_MASK);
        }

        public void MouseWheel(String info) throws AWTException {
            java.awt.Robot robot = new Robot();
            float num = Float.valueOf(info);
            if (num > 0)
                robot.mouseWheel(1);
            else
                robot.mouseWheel(-1);
        }

        public void KeyBoard(String info) throws AWTException {
            String args[] = info.split(",");
            String type = null;
            String cont = null;
            String keystate = null;
            java.awt.Robot robot = new Robot();
            if (args.length == 2) {
                type = args[0];
                cont = args[1];
            }
            if (args.length == 3) {
                type = args[0];
                cont = args[1];
                keystate = args[2];
            }


            if (type.equals("message")) {
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                cb.setContents(new StringSelection(cont), null);//调用粘贴板


                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                robot.keyRelease(KeyEvent.VK_V);
            } else if (type.equals("key")) {
                if (cont.equals("BackSpace")) {
                    if (keystate.equals("click")) {
                        robot.keyPress(KeyEvent.VK_BACK_SPACE);
                        robot.keyRelease(KeyEvent.VK_BACK_SPACE);
                    }
                }
                if (cont.equals("Enter")) {
                    if (keystate.equals("click")) {
                        robot.keyPress(KeyEvent.VK_ENTER);
                        robot.keyRelease(KeyEvent.VK_ENTER);
                    }
                }
                if (cont.equals("Up")) {
                    if (keystate.equals("click")) {
                        robot.keyPress(KeyEvent.VK_UP);
                        robot.keyRelease(KeyEvent.VK_UP);
                    }

                    if (keystate.equals("down"))
                        robot.keyPress(KeyEvent.VK_UP);
                    if (keystate.equals("up"))
                        robot.keyRelease(KeyEvent.VK_UP);
                }
                if (cont.equals("Down")) {
                    if (keystate.equals("click")) {
                        robot.keyPress(KeyEvent.VK_DOWN);
                        robot.keyRelease(KeyEvent.VK_DOWN);
                    }
                    if (keystate.equals("down"))
                        robot.keyPress(KeyEvent.VK_DOWN);
                    if (keystate.equals("up"))
                        robot.keyRelease(KeyEvent.VK_DOWN);
                }
                if (cont.equals("Left")) {
                    if (keystate.equals("down"))
                        robot.keyPress(KeyEvent.VK_LEFT);
                    if (keystate.equals("up"))
                        robot.keyRelease(KeyEvent.VK_LEFT);
                }
                if (cont.equals("Right")) {
                    if (keystate.equals("down"))
                        robot.keyPress(KeyEvent.VK_RIGHT);
                    if (keystate.equals("up"))
                        robot.keyRelease(KeyEvent.VK_RIGHT);
                }
                if (cont.equals("W")) {
                    if (keystate.equals("down"))
                        robot.keyPress(KeyEvent.VK_W);
                    if (keystate.equals("up"))
                        robot.keyRelease(KeyEvent.VK_W);
                }
                if (cont.equals("S")) {
                    if (keystate.equals("down"))
                        robot.keyPress(KeyEvent.VK_S);
                    if (keystate.equals("up"))
                        robot.keyRelease(KeyEvent.VK_S);
                }
                if (cont.equals("A")) {
                    if (keystate.equals("down"))
                        robot.keyPress(KeyEvent.VK_A);
                    if (keystate.equals("up"))
                        robot.keyRelease(KeyEvent.VK_A);
                }
                if (cont.equals("S")) {
                    if (keystate.equals("down"))
                        robot.keyPress(KeyEvent.VK_S);
                    if (keystate.equals("up"))
                        robot.keyRelease(KeyEvent.VK_S);
                }

                if (cont.equals("Ctrl")) {
                    if (keystate.equals("down"))
                        robot.keyPress(KeyEvent.VK_CONTROL);
                    if (keystate.equals("up"))
                        robot.keyRelease(KeyEvent.VK_CONTROL);
                    if (keystate.equals("click")) {
                        robot.keyPress(KeyEvent.VK_CONTROL);
                        robot.keyRelease(KeyEvent.VK_CONTROL);
                    }
                }

                if (cont.equals("Z")) {
                    if (keystate.equals("down"))
                        robot.keyPress(KeyEvent.VK_Z);
                    if (keystate.equals("up"))
                        robot.keyRelease(KeyEvent.VK_Z);
                    if (keystate.equals("click")) {
                        robot.keyPress(KeyEvent.VK_Z);
                        robot.keyRelease(KeyEvent.VK_Z);
                    }
                }

                if (cont.equals("Space")) {
                    if (keystate.equals("down"))
                        robot.keyPress(KeyEvent.VK_SPACE);
                    if (keystate.equals("up"))
                        robot.keyRelease(KeyEvent.VK_SPACE);
                    if (keystate.equals("click")) {
                        robot.keyPress(KeyEvent.VK_SPACE);
                        robot.keyRelease(KeyEvent.VK_SPACE);
                    }
                }


            } else if (type.equals("dosmessage")) {
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                cb.setContents(new StringSelection(cont), null);//调用粘贴板


                robot.mousePress(InputEvent.BUTTON3_MASK);
                robot.mouseRelease(InputEvent.BUTTON3_MASK);
                robot.keyPress(KeyEvent.VK_P);
                robot.keyRelease(KeyEvent.VK_P);
            }
        }

    }
}

