package grossmarkt.maps;

import grossmarkt.application.Lieferant;
import java.util.Collections;
import java.util.HashMap;

public class LieferantMap {
  private HashMap<Integer, Lieferant> lieferantHashMap;

  public HashMap<Integer, Lieferant> getLieferantHashMap() {
    return lieferantHashMap;
  }

  public void addLieferant(Lieferant lieferant){
    int nextFreeKey = lieferantHashMap.isEmpty() ? 0 : Collections.max(lieferantHashMap.keySet()) +1;
    lieferantHashMap.put(nextFreeKey, lieferant);
  }

  public void populateWithDemodata(){
    addLieferant(new Lieferant("Ann", "Geber", "DE", "Stuttgart", "Schulstrasse", "3a", 70174));
    addLieferant(new Lieferant("Jo", "Ghurt", "GB", "London", "Mainstreet", "420", 424242));
    addLieferant(new Lieferant("Knut", "Schfleck", "DE", "Muenchen", "Wilde Maus", "0", 80331));
  }
}
