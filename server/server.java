import java.io.InputStream;
import java.io.OutputStream;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;


public class server extends MIDlet implements CommandListener {
    String myURL ="btspp://localhost:"+new UUID(0x7598490)+";name=rfcommtest;authorize=true";
    sThread sendmessage;
    Display dis;
    Form form;
    Command Exit,Send;
    StreamConnection con;
    StreamConnectionNotifier notifier;
    InputStream in;
    OutputStream out;
    TextField message;
    LocalDevice localdevice;
    RemoteDevice rd;
    String name = "Client";
    
  
public server(){
    message = new TextField("Message","",100,TextField.ANY);
    Exit = new Command("Exit",Command.EXIT,1);
    Send = new Command("Send",Command.OK,1);
    form = new Form("Server");
    form.addCommand(Exit);
    
    form.setCommandListener(this);
}
    public void startApp() {
        dis = Display.getDisplay(this);
        dis.setCurrent(form);
        //form.append(myURL+"\n");
        try{
            localdevice = LocalDevice.getLocalDevice();
            
            notifier = (StreamConnectionNotifier)Connector.open(myURL);
            
          
         
          con = notifier.acceptAndOpen();
        
          
         
        
          rd = RemoteDevice.getRemoteDevice(con);
          name = rd.getFriendlyName(false);
          form.append(message);
          form.addCommand(Send);
          in = con.openInputStream();
          out = con.openOutputStream();
          sendmessage = new sThread(out,this);
          rThread receivemessage = new rThread(in,form,message,name,this);
          receivemessage.start();
          sendmessage.start();
          
          
          
            
            
        }catch(Exception e){ 
            form.append("Error:"+e);
            try{destroyApp(false);}catch(Exception w){} 
        }
       
        
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
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
        if(c.getLabel().equals("Exit")){
            try{
            in.close();
            out.close();
            destroyApp(true);
            }catch(Exception e){
                try{destroyApp(false);}catch(Exception w){} 
            }
        }
    }
}
class sThread extends Thread{
    
    OutputStream outs;
    String msg;
    server s;
    sThread(OutputStream out,server s)
    {
       
        outs = out;
        this.s = s;
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
                s.destroyApp(false);
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
    String n;
    server s;
    rThread(InputStream in,Form form,TextField message,String name,server s){
        this.mes = message;
        ins = in;
        forms = form;
        this.n = name;
        this.s = s;
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
                  hismessage+=(char)q;
                    
              
                   
               
                    
               }
          if(!hismessage.equals("")){
          forms.delete(forms.size()-1)  ;     
          forms.append(n+">>"+hismessage+"\n");
          forms.append(mes);
          } 
           
            
            
             }catch(Exception e){
                 s.destroyApp(false);
             }
        }
    }
    
}
