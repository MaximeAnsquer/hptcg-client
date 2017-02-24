package hptcg;

import java.util.Hashtable;
import java.util.Map;

public class DeckCollection {

    public static Map<Integer, Card> createHermioneStarterBaseSet(Game game) {
        Map<Integer, Card> deck = new Hashtable<>();
        for (int i = 0; i < 12; i++) {
            deck.put(deck.size(), new CareOfMagicalCreatures(game));
        }
        for (int i = 0; i < 7; i++) {
            deck.put(deck.size(), new Transfiguration(game));
        }
        for (int i = 0; i < 4; i++) {
            deck.put(deck.size(), new CuriousRaven(game));
        }
        for (int i = 0; i < 3; i++) {
            deck.put(deck.size(), new ForestTroll(game));
            deck.put(deck.size(), new ViciousWolf(game));
            deck.put(deck.size(), new Incarcifors(game));
        }
        for (int i = 0; i < 2; i++) {
            deck.put(deck.size(), new Avifors(game));
            deck.put(deck.size(), new Epoximise(game));
            deck.put(deck.size(), new HagridAndTheStranger(game));
            deck.put(deck.size(), new TakeRoot(game));
        }
        return deck;
    }

    public static Map<Integer, Card> createDracoStarterBaseSet(Game game) {
        Map<Integer, Card> deck = new Hashtable<>();
        for (int i = 0; i < 10; i++) {
            deck.put(deck.size(), new CareOfMagicalCreatures(game));
        }
        for (int i = 0; i < 9; i++) {
            deck.put(deck.size(), new Charms(game));
        }
        for (int i = 0; i < 4; i++) {
            deck.put(deck.size(), new MagicalMishap(game));
            deck.put(deck.size(), new Vermillious(game));
        }
        for (int i = 0; i < 3; i++) {
            deck.put(deck.size(), new CuriousRaven(game));
        }
        for (int i = 0; i < 2; i++) {
            deck.put(deck.size(), new Accio(game));
            deck.put(deck.size(), new BoaConstrictor(game));
            deck.put(deck.size(), new HagridAndTheStranger(game));
            deck.put(deck.size(), new Stupefy(game));
            deck.put(deck.size(), new SurlyHound(game));
        }
        return deck;
    }

}
