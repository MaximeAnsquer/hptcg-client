package hptcg;

import javax.swing.*;
import java.io.Serializable;

public abstract class Card implements ICard, Serializable {

  protected int cost;
  protected Edition edition;
  protected ImageIcon image;
  protected String name;
  protected String text;
  protected Type type;

  public String toString() {
    return name;
  }

}
