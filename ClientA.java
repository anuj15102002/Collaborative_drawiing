import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
class SendToServer 
{
OutputStream os;
OutputStreamWriter osw;
InputStream is;
InputStreamReader isr;
String request;
String response;
StringBuffer sb;
Socket socket;
int oneByte;
public void sendDrawRequest(float a,int lastX,int lastY,int x,int y)
{
String request="A,draw,"+a+","+lastX+","+lastY+","+x+","+y+"#";
sendRequest(request);
}
public void sendEraseRequest(float a,int lastX,int lastY,int x,int y)
{
String request="A,erase,"+a+","+lastX+","+lastY+","+x+","+y+"#";
sendRequest(request);
}
public void sendClearRequest()
{
String request="A,clear#";
sendRequest(request);
}
public void sendRequest(String request)
{
try
{
socket=new Socket("localhost",13050);
os=socket.getOutputStream();
osw=new OutputStreamWriter(os);
osw.write(request);
osw.flush();
System.out.println("Request Send: "+request);
is=socket.getInputStream();
isr=new InputStreamReader(is);
sb=new StringBuffer();
while(true)
{
oneByte=isr.read();
if(oneByte==-1)break;
if(oneByte=='#')break;
sb.append((char)oneByte);
}
response=sb.toString();
System.out.println("Response Arrived: "+response);
socket.close();
}catch(Exception e1)
{
System.out.println(e1);
}
}
}
class MyDrawing extends Canvas 
{
private int lastX,lastY;
public static Button clearAll,erase;
private boolean isErasing=false;
boolean isClearAllClicked,isEraseClicked;
SendToServer sendToServer=new SendToServer();
private float bs,es;
public MyDrawing()
{
setBackground(Color.green);
setForeground(Color.red);

clearAll=new Button("ClearAll");
clearAll.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent ae)
{
isClearAllClicked=true;
repaint(30);
sendToServer.sendClearRequest();
isClearAllClicked=false;
}
});

}
public boolean mouseDown(Event e,int x,int y)
{
lastX=x;
lastY=y;
return true;
}
public boolean mouseDrag(Event e,int x,int y)
{
if(Client.bSize.getText().isEmpty())
{
bs=1.0F;
}
else
{
bs=Float.parseFloat(Client.bSize.getText());
}
if(Client.eSize.getText().isEmpty())
{
es=1.0F;
}
else
{
es=Float.parseFloat(Client.eSize.getText());
}
boolean c=isClearAllClicked;
Graphics g=getGraphics();
Graphics2D g2d=(Graphics2D) g;
SendToServer ss;
ss=new SendToServer();
if(isErasing)
{
g2d.setColor(Color.green);
g2d.setStroke(new BasicStroke(es));
g2d.drawLine(lastX,lastY,x,y);
ss.sendEraseRequest(es,lastX,lastY,x,y);

int diameter = (int) es;
g2d.setColor(new Color(0,153,0)); 
g2d.setStroke(new BasicStroke(2));
g2d.drawOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
try
{
Thread.sleep(50);
}catch(InterruptedException ie)
{
System.out.println(ie);
}

g2d.setColor(Color.green);
g2d.drawOval(x - diameter / 2, y - diameter / 2, diameter, diameter);

}
if(!isErasing)
{
g2d.setColor(Color.red);
g2d.setStroke(new BasicStroke(bs));
g2d.drawLine(lastX,lastY,x,y);
ss.sendDrawRequest(bs,lastX,lastY,x,y);

}

lastX=x;
lastY=y;
return true;
}
public void setErasing(boolean isErasing) {
this.isErasing = isErasing;
}
}
/*class Erase extends Canvas
{
private int a,b;
public Erase()
{
setBackground(Color.green);
setForeground(Color.green);
}

public boolean mouseDown(Event e,int x,int y)
{
a=x;
b=y;
return true;
}
public boolean mouseDrag(Event e,int x,int y)
{
Graphics g=getGraphics();
g.setColor(isErasing ? getBackground() : getForeground());
g.drawLine(a,b,x,y);
a=x;
b=y;
return true;
}
class Scene extends Canvas
{
public Scene()
{
setBackground(Color.green);
}
}
}*/
class Client extends Frame
{
private MyDrawing md;
private Scene sce;
private Button erase,brush;
public Erase era;
private Label brushSize,eraserSize;
public static TextField bSize,eSize;
public Client()
{
brushSize=new Label("Brush Size");
bSize=new TextField(3);
eraserSize=new Label("Erase Size");
eSize=new TextField(3);

md=new MyDrawing();
//MyDrawing.clearAll=new Button("Clear");
sce=new Scene();
brush=new Button("Brush");
brush.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent ae)
{
System.out.println("brush");
md.setErasing(false);
}
});
erase=new Button("Erase");
erase.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent ae)
{
System.out.println("erase");
md.setErasing(true);
}
});
Panel p=new Panel();
p.setLayout(new GridLayout(5,3));
p.add(new Label("     "));
p.add(erase);
p.add(new Label("     "));
p.add(eraserSize);
p.add(eSize);
p.add(new Label("     "));
p.add(new Label("     "));
p.add(brush);
p.add(new Label("     "));
p.add(brushSize);
p.add(bSize);
p.add(new Label("     "));
p.add(new Label("     "));
p.add(MyDrawing.clearAll);
p.add(new Label("     "));
setLayout(new BorderLayout());
add(md,BorderLayout.CENTER);
add(p,BorderLayout.WEST);
setLocation(100,100);
setSize(600,600);
setVisible(true);
}
}
class psp
{
public static void main(String gg[])
{
Client c=new Client();
}
}