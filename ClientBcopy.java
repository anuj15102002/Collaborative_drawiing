import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
class MakeDrawing extends Canvas
{
public MakeDrawing()
{
setBackground(Color.green);
setForeground(Color.red);
}
public boolean draw(String points)
{
Graphics g=getGraphics();
Graphics2D g2d=(Graphics2D)g;
String splits[];
splits=points.split(",");
if(splits[2].equals("draw"))
{
g2d.setColor(Color.red);
g2d.setStroke(new BasicStroke(Float.parseFloat(splits[3])));
}
if(splits[2].equals("erase"))
{
g2d.setColor(Color.green);
g2d.setStroke(new BasicStroke(Float.parseFloat(splits[3])));
}
int lastX=Integer.parseInt(splits[4]);
int lastY=Integer.parseInt(splits[5]);
int x=Integer.parseInt(splits[6]);
int y=Integer.parseInt(splits[7]);
System.out.println(lastX+","+lastY+","+x+","+y);
g.drawLine(lastX,lastY,x,y);
if(splits[7].equals("true"))return true;
else return false;
}
}

class ClientBCopy extends Frame
{
public MakeDrawing md;
public ClientBCopy()
{
md=new MakeDrawing();
setLayout(new BorderLayout());
add(md,BorderLayout.CENTER);
setLocation(100,100);
setSize(600,600);
setVisible(true);
}
}


class ReceiveFromServer extends Thread
{
InputStream is;
InputStreamReader isr;
OutputStreamWriter osw;
OutputStream os;
StringBuffer sb;
int oneByte;
String response;
String request;
ClientBCopy cb;
public ReceiveFromServer(ClientBCopy cb)
{
this.cb=cb;
this.start();
}
public String sendRequest(String request)
{
try
{
Socket socket=new Socket("localhost",13050);
os=socket.getOutputStream();
osw=new OutputStreamWriter(os);
osw.write(request);
osw.flush();
is=socket.getInputStream();
isr=new InputStreamReader(is);
sb=new StringBuffer();
while(true)
{
oneByte=isr.read();
if(oneByte=='#')break;
if(oneByte==-1)break;
sb.append((char)oneByte);
}
response=sb.toString();
System.out.println(response);
}catch(Exception e)
{
System.out.println(e);
}
return response;
}
public void run()
{
String req="B#";
while(true)
{
String res;
res=sendRequest(req);
String sps[];
sps=res.split(",");
if(sps[0].equals("True") )
{
if(sps[2].equals("clear"))
{
this.cb.md.repaint();
}
else
{
this.cb.md.draw(res);
}
}
}
}
public static void main(String gg[])
{
ClientBCopy cb=new ClientBCopy();
ReceiveFromServer rf=new ReceiveFromServer(cb);
}
}
