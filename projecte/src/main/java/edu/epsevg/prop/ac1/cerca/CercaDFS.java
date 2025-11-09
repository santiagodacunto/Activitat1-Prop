package edu.epsevg.prop.ac1.cerca;
 
import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.LinkedList; // LinkedList ens serveix com a Pila (Stack)
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class CercaDFS extends Cerca {
    
    // El límit de profunditat que demana l'enunciat
    private static final int MAX_DEPTH = 50;
    
    /**
     * Classe interna per emmagatzemar la informació de cada node de cerca
     */
    private class NodeCerca {
        Mapa estat;
        NodeCerca pare;
        Moviment moviment; // Moviment que va portar a aquest estat
        int profunditat;

        NodeCerca(Mapa estat, NodeCerca pare, Moviment moviment, int profunditat) {
            this.estat = estat;
            this.pare = pare;
            this.moviment = moviment;
            this.profunditat = profunditat;
        }
    }
    
    public CercaDFS(boolean usarLNT) { 
        super(usarLNT);
    }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        
        // Llista de Nodes Oberts (LNO) - Estructura LIFO (Pila/Stack) per DFS
        // Fem servir LinkedList perquè té els mètodes push() i pop()
        LinkedList<NodeCerca> lno = new LinkedList<>();
        
        // Llista de Nodes Tancats (LNT) - Mapa[Estat] -> Profunditat mínima
        HashMap<Mapa, Integer> lnt = new HashMap<>();

        // Creem el node inicial i l'afegim
        NodeCerca nodeInicial = new NodeCerca(inicial, null, null, 0);
        lno.push(nodeInicial); // .push() afegeix a l'inici (com a Pila)
        
        if (usarLNT) {
            lnt.put(inicial, 0);
        }

        rc.updateMemoria(1); // Comptem el node inicial

        while (!lno.isEmpty()) {
            
            // 1. Obtenim el següent node de la Pila
            NodeCerca nodeActual = lno.pop(); // .pop() treu de l'inici (com a Pila)
            rc.incNodesExplorats();

            // 2. Comprovem si és la meta
            if (nodeActual.estat.esMeta()) {
                rc.setCami(reconstruirCami(nodeActual));
                return; // Hem trobat la solució!
            }

            // 3. Comprovem el límit de profunditat
            if (nodeActual.profunditat >= MAX_DEPTH) {
                rc.incNodesTallats(); // Tallem per profunditat
                continue; // No explorem més fons per aquest camí
            }

            // 4. Generem els estats fills (successors)
            List<Moviment> accions = nodeActual.estat.getAccionsPossibles();
            
            for (Moviment accio : accions) {
                Mapa estatFill = nodeActual.estat.mou(accio);
                int profunditatFilla = nodeActual.profunditat + 1;
                NodeCerca nodeFill = new NodeCerca(estatFill, nodeActual, accio, profunditatFilla);
                
                // 5. Control de Cicles
                boolean tallar = false;
                
                if (usarLNT) {
                    // Mirem si està a la LNT i a quina profunditat
                    if (lnt.containsKey(estatFill) && lnt.get(estatFill) <= profunditatFilla) {
                        tallar = true; // Ja l'hem visitat a una profunditat millor o igual
                    }
                } else {
                    // Mirem si està al camí actual (pujant pels pares)
                    if (estaAlCami(nodeActual, estatFill)) {
                        tallar = true;
                    }
                }

                if (tallar) {
                    rc.incNodesTallats();
                    continue; // Descartem aquest fill
                }

                // 6. Afegim el fill a les estructures
                lno.push(nodeFill); // Afegim a l'inici de la llista (Pila)
                if (usarLNT) {
                    lnt.put(estatFill, profunditatFilla);
                }
                
                // Actualitzem l'estadística de memòria
                int memActual = lno.size() + (usarLNT ? lnt.size() : 0);
                rc.updateMemoria(memActual);
            } // fi del for d'accions
        } // fi del while (pila buida)

        // Si sortim del while, la pila (LNO) és buida i no hem trobat solució
    }
    
    /**
     * Mètode auxiliar per comprovar si un estat ja existeix al camí actual
     */
    private boolean estaAlCami(NodeCerca node, Mapa estat) {
        NodeCerca actual = node;
        while (actual != null) {
            if (actual.estat.equals(estat)) {
                return true;
            }
            actual = actual.pare;
        }
        return false;
    }

    /**
     * Mètode auxiliar per reconstruir la llista de moviments
     */
    private List<Moviment> reconstruirCami(NodeCerca nodeFinal) {
        List<Moviment> cami = new ArrayList<>();
        NodeCerca actual = nodeFinal;
        
        while (actual.pare != null) {
            cami.add(actual.moviment);
            actual = actual.pare;
        }
        
        Collections.reverse(cami);
        return cami;
    }
}