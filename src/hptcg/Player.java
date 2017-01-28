package hptcg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Player {

  private Socket socket;
  private ObjectInputStream objectInputStream;
  private ObjectOutputStream objectOutputStream;
  private boolean active;

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Player(Socket socket) {
    this.socket = socket;
    try {
      this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
      this.objectInputStream = new ObjectInputStream(socket.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void sendObject(Object object) {
    try {
      getObjectOutputStream().writeObject(object);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Socket getSocket() {
    return socket;
  }

  public void setSocket(Socket socket) {
    this.socket = socket;
  }

  public ObjectInputStream getObjectInputStream() {
    return objectInputStream;
  }

  public void setObjectInputStream(ObjectInputStream objectInputStream) {
    this.objectInputStream = objectInputStream;
  }

  public ObjectOutputStream getObjectOutputStream() {
    return objectOutputStream;
  }

  public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
    this.objectOutputStream = objectOutputStream;
  }


}
