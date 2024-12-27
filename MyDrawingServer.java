import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
class ServerException extends Exception
{
public ServerException(String message)
{
super(message);
}
}
class RequestProcessor extends Thread
{
static Queue<String> responsePoints=new LinkedList<>();;
private Socket socket;
Queue<Integer> points;
public RequestProcessor(Socket socket)
{
this.socket=socket;
this.points=new LinkedList<>();
this.start();
}
public void run()
{
try
{

InputStream is;
InputStreamReader isr;
OutputStreamWriter osw;
OutputStream os;
String request;
String response;
int oneByte;
StringBuffer sb;
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
request=sb.toString();
String splits[];
splits=request.split(",");
String command=splits[0];
System.out.println("Request Arrived: "+request);
if(command.equals("A"))
{
if(splits[1].equals("draw"))
{
responsePoints.add(request);
response="drawing";
}
else if(splits[1].equals("erase"))
{
responsePoints.add(request);
response="erasing";
}
else
{
responsePoints.add(request);
response="True#";
}
}
else
{
if(!responsePoints.isEmpty())
{
response="True,"+responsePoints.remove()+"#";
}
else 
{
response="False#";
}
}
System.out.println("Response Send: "+response);
os=socket.getOutputStream();
osw=new OutputStreamWriter(os);
osw.write(response);
osw.flush();
this.socket.close();
}catch(Exception e1)
{
System.out.println(e1);
}
}
public Queue<Integer> getPoints()
{
return points;
}

}
class MultiThreadedServer
{
private ServerSocket serverSocket;
private int portNumber;
public MultiThreadedServer(int portNumber)
{
this.portNumber=portNumber;
try
{
serverSocket=new ServerSocket(this.portNumber);
}catch(Exception e)
{
System.out.println(e);
}
}
public void startServer()
{
Socket socket;
System.out.println("Server is ready and listening on port: "+this.portNumber);
while(true)
{
try
{
socket=this.serverSocket.accept();
RequestProcessor rp=new RequestProcessor(socket);
}catch(Exception e)
{
System.out.println(e);
}
}
}
public static void main(String args[])
{
if(args.length!=1)
{
System.out.println("usage[java MultiThreadedServer port_number]");
return;
}
try
{
int portNumber=Integer.parseInt(args[0]);
if(portNumber<1 || portNumber>65535)
{
throw new ServerException("PortNumber should be between 1 and 65535");
}
MultiThreadedServer mts;
mts=new MultiThreadedServer(portNumber);
mts.startServer();
}catch(ServerException se)
{
System.out.println(se.getMessage());
}
}
}