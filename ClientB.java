import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

class ReceiveFromServer extends Thread
{
InputStream is;
Socket socket;
InputStreamReader isr;
OutputStream os;
OutputStreamWriter osw;
Queue<Integer> points;
String response;
String request;
String splits[];
public ReceiveFromServer()
{
try
{
this.socket=new Socket("localhost",13050);
this.points=new LinkedList<>();
this.start();
}catch(Exception e1)
{
System.out.println(e1.getMessage());
}
}
public Queue<Integer>receivePoints()
{
try
{
is=socket.getInputStream();
isr=new InputStreamReader(is);
StringBuffer sb;
sb=new StringBuffer();
int oneByte;
while(true)
{
oneByte=isr.read();
if(oneByte==-1)break;
if(oneByte=='#')break;
sb.append((char)oneByte);
}
request=sb.toString();
System.out.println("Request Arrived: "+request);
splits=request.split(",");
for(int i=1;i<splits.length;i++)
{
points.add(Integer.parseInt(splits[i]));
}
os=socket.getOutputStream();
osw=new OutputStreamWriter(os);
if(points.isEmpty())
{
response="QueueEmpty#";
}
else
{
response="B#";
}
osw.write(response);
osw.flush();
}catch(Exception e)
{
System.out.println(e.getMessage());
return null;
}
return points;
}
public void closeConnection() 
{
try 
{
if (socket != null) socket.close();
} catch (IOException e) 
{
System.out.println("Error closing socket: " + e.getMessage());
}
}
public void run()
{
ReceiveFromServer rfs=new ReceiveFromServer();
rfs.receivePoints();
}
}


class MakeDrawing extends Canvas
{
private Queue<Integer>points;
private ReceiveFromServer rfs=new ReceiveFromServer();
public MakeDrawing() 
{
setBackground(Color.green);
setForeground(Color.red);
this.points = rfs.receivePoints();
repaint(); 
}
public void paint(Graphics g)
{
if(points==null || points.size()<4)
{
System.out.println("Not enought Space");
return;
}
System.out.println("Points received for drawing: " + points);
if (points.size() < 4) 
{
System.out.println("Not enough points to draw a line. Current queue size: " + points.size());
return;
}
Integer lastX=points.poll();
Integer lastY=points.poll();
Integer x=points.poll();
Integer y=points.poll();
System.out.println("Drawing line from (" + lastX + ", " + lastY + ") to (" + x + ", " + y + ")");
g.drawLine(lastX,lastY,x,y);
}
}
class ClientB extends Frame
{
private MakeDrawing md;
private ReceiveFromServer rfs;
public ClientB()
{
rfs=new ReceiveFromServer();
md=new MakeDrawing();
setLayout(new BorderLayout());
add(md,BorderLayout.CENTER);
setLocation(100,100);
setSize(600,600);
setVisible(true);
}
}
class main
{
public static void main(String gg[])
{
ClientB cb=new ClientB();
}
}