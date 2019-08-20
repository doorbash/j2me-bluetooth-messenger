
import java.io.InputStream;
import java.io.OutputStream;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;


public class c extends MIDlet implements CommandListener,DiscoveryListener{
    
    
    Display dis;
    Form form,first;
    Command Exit,Send,Start;
    LocalDevice localdevice;
    DiscoveryAgent discoveryagent;
    StreamConnection con;
    InputStream in;
    OutputStream out;
    TextField message,name;
    sThread sendmessage;
    boolean lets_start = false;
    boolean server_found = false;
    boolean service_found = false;
public c(){
   first = new Form("Milson-Messenger");
   name = new TextField("Enter Server Name:","",30,TextField.ANY);
   Start = new Command("Start",Command.OK,1);
   first.append(name);
   first.addCommand(Start);
   Exit = new Command("Exit",Command.EXIT,1);
   first.addCommand(Exit);
   first.setCommandListener(this);
    try{
        
    try{
        localdevice = LocalDevice.getLocalDevice();
        discoveryagent = localdevice.getDiscoveryAgent();
    }catch(Exception e){
        form.append("Error:"+e+"\n");
       try{destroyApp(false);}catch(Exception a){}
    }
   
            
    message = new TextField("Message","",100,TextField.ANY);
    
    form = new Form("Client");
    
    Send = new Command("Send",Command.OK,1);
    form.addCommand(Exit);
    
    form.setCommandListener(this);
    }catch(Exception t){
        form.append("Error"+t+"\n");
       try{destroyApp(false);}catch(Exception a){}
    }
    
}
    public void startApp() throws MIDletStateChangeException {
        try{
        dis = Display.getDisplay(this);
        
        dis.setCurrent(first);
        while(true){
            if(lets_start == true){
        try{discoveryagent.startInquiry(DiscoveryAgent.GIAC,this);}catch(Exception e){}
        break;
            }else{
                Thread.currentThread().sleep(100);
            }
        }
        }catch(Exception h){
          form.append("Error:"+h+"\n");
          try{destroyApp(false);}catch(Exception a){}
        }
    }

    public void pauseApp() {}
       
    

    public void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        notifyDestroyed();
    }

    public void commandAction(Command c, Displayable d) {
        if(c.getLabel().equals("Send")){
            if(!message.getString().equals("")){
            sendmessage.send("");   
            sendmessage.send(message.getString()+"\n");
            form.delete(form.size()-1);
            form.append(localdevice.getFriendlyName()+">>"+message.getString()+"\n");
            form.append(message);
            
            message.setString("");
            }
        }
        if(c.getLabel().equals("Start")){
            
            dis.setCurrent(form);
            lets_start = true;
        }
        //agar exit entekhab shod barname khateme miabad
       if(c.getLabel().equals("Exit")){
           
       try{
            in.close();
            out.close();
           try{destroyApp(false);}catch(Exception a){}
          }catch(Exception e){}
            
     try{destroyApp(false);}catch(Exception a){}
       
       }
       
       
    }

    public void deviceDiscovered(RemoteDevice rd, DeviceClass arg1) {
       
     
       try{
           if(rd.getFriendlyName(false).equals(name.getString())){
            server_found = true;
            }
            
       }catch(Exception e){
           form.append("Error:"+e+"\n");
           //*************************************************
       }
       try{
       if(rd.getFriendlyName(false).equals(name.getString())){
          myThread t = new myThread(discoveryagent,rd,this);  
          t.start(); 
            
       } 
       }catch(Exception e){
           form.append("Error:"+e+"\n");
         try{destroyApp(false);}catch(Exception a){}
       }
      
            
        
       
        
    }

    public void servicesDiscovered(int i, ServiceRecord[] records) {
        service_found = true;
        for(int k=0;k<records.length;k++){
                   String URLConnection  = records[k].getConnectionURL(ServiceRecord.AUTHENTICATE_ENCRYPT, false);

                   if(URLConnection.startsWith("btspp")){
               
                  // form.append(URLConnection+"\n");
           
                try{
                    con = (StreamConnection) Connector.open(URLConnection);
                    form.append(message);
                    form.addCommand(Send);
                    in = con.openInputStream();
                    out = con.openOutputStream();
                    sendmessage = new sThread(out,this,form);
                    rThread receivemessage = new rThread(in,form,message,name,this);
                    receivemessage.start();
                    sendmessage.start();
            
            
            
            
            
               }catch(Exception e){
                   form.append("Error:"+e+"\n");
                  try{destroyApp(false);}catch(Exception a){}
               }
               
           break; 
        }
            
        }
        
    }

    public void serviceSearchCompleted(int arg0, int arg1) {
      if(service_found==false){
          Alert service_not_found = new Alert("Error","Cant find server service.Aborting...",null,AlertType.ERROR);
          service_not_found.setTimeout(Alert.FOREVER);
          dis.setCurrent(service_not_found,form);
          try{
              destroyApp(false);
          }catch(Exception e){}
      }
    }

    public void inquiryCompleted(int arg0) {
        if(server_found==false){
            Alert a = new Alert("Milson","Server not found.Aborting...",null,AlertType.ERROR);
            a.setTimeout(Alert.FOREVER);
            dis.setCurrent(a,form);
            try{destroyApp(false);}catch(Exception b){}
        }
    }
}
class myThread extends Thread{
    DiscoveryAgent myDiscoveryAgent;
    RemoteDevice rd;
    DiscoveryListener g;
    myThread(DiscoveryAgent f,RemoteDevice d,DiscoveryListener m){
        myDiscoveryAgent = f;
        rd =d;
        g =m;
    }
    public void run(){
        UUID[] uuid = new UUID[1];
       uuid[0] = new UUID(0x7598490);
             
       try{   
      myDiscoveryAgent.searchServices(null, uuid, rd, g);
       }catch(Exception r){
           
       }
      
    }
}
class sThread extends Thread{
    
    OutputStream outs;
    String msg;
    c c;
    Form f;
    sThread(OutputStream out,c c,Form f)
    {
     
        outs = out;
        this.c = c;
        this.f =f;
    }
    public void send(String msg){
        this.msg = msg;
          
    }
    public void run()
    {
        
        while(true){
            try{
              
            
            Thread.currentThread().sleep(100);  
            if(msg!=null){
            if(!msg.equals("")){    
            outs.write(msg.getBytes());
            outs.flush();
}
}
}catch(Exception e){
    f.append("Error:"+e+"\n");
       try{c.destroyApp(false);}catch(Exception a){}
            
}
send("");
}
}
}
class rThread extends Thread{
InputStream ins;
Form forms;
String hismessage="";
TextField mes;
TextField name;
c c;
rThread(InputStream in,Form form,TextField message,TextField name,c c){
this.mes = message;
ins = in;
forms = form;
this.name = name;
this.c = c;
}
public void run()
{
while(true)
{
hismessage="";
int q ;
try{
while((q=ins.read())!=-1){
if((char)q=='\n'){
break;
}
hismessage+=""+(char)q;
}
if(!hismessage.equals("")){
forms.delete(forms.size()-1)  ;  
forms.append(name.getString()+">>"+hismessage+"\n");
forms.append(mes);
} 
}catch(Exception e){
forms.append("Error:"+e+"\n");
try{c.destroyApp(false);}catch(Exception a){}
   }
  }
 }  
}