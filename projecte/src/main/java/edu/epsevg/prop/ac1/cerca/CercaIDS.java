package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class CercaIDS extends Cerca {
    
    // Límit màxim per si de cas, per evitar bucles infinits si no hi ha solució.
    private static final int MAX_ITERATIONS = 100;

    /**
     * Classe interna per emmagatzemar la informació de cada node de cerca
     */
    private class NodeCerca {
        Mapa estat;
        NodeCerca pare;
        Moviment moviment;
        int profunditat;

        NodeCerca(Mapa estat, NodeCerca pare, Moviment moviment, int profunditat) {
            this.estat = estat;
            this.pare = pare;
            this.moviment = moviment;
            this.profunditat = profunditat;
        }
    }

    public CercaIDS(boolean usarLNT) {
        super(usarLNT);
    }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        
        for (int limit = 0; limit < MAX_ITERATIONS; limit++) {
            
            // Per cada iteració, fem una cerca en profunditat limitada (DFS)
            // Totes les estadístiques (explorats, tallats) s'acumulen al 'rc' principal.
            
            // Buidem la solució anterior (si n'hi havia d'una iteració prèvia)
            rc.setCami(null); 
            
            // Fem la cerca per a aquest límit
            cercaDFSInterna(inicial, rc, limit);
            
            // Si la cerca interna ha trobat un camí, ja l'ha posat a 'rc'.
            if (rc.getCami() != null) {
                return; // Solució trobada i desada a 'rc'.
            }
            
            // Comprovem si hem d'aturar-nos (optimització)
            // Si hem explorat i no hem trobat solució, PERÒ
            // el nombre de nodes explorats no ha augmentat,
            // significa que l'arbre s'ha explorat sencer i no hi ha solució.
            
            // (Això és una mica complicat de fer bé sense un 'hitLimit' flag)
            // Deixem que el bucle for s'acabi si no troba res.
            
        }
    }

    /**
     * Fa una cerca DFS amb un límit de profunditat donat.
     * Modifica el 'rc' directament si troba solució.
     * Acumula estadístiques a 'rc'.
     */
    private void cercaDFSInterna(Mapa inicial, ResultatCerca rc, int limit) {
        
        // Les llistes LNO i LNT són locals per a CADA iteració
        LinkedList<NodeCerca> lno = new LinkedList<>();
        HashMap<Mapa, Integer> lnt = new HashMap<>();

        NodeCerca nodeInicial = new NodeCerca(inicial, null, null, 0);
        lno.push(nodeInicial);
        if (usarLNT) {
            lnt.put(inicial, 0);
        }
        rc.updateMemoria(1); // La memòria pic es mantindrà actualitzada al 'rc'

        while (!lno.isEmpty()) {
            NodeCerca nodeActual = lno.pop();
            rc.incNodesExplorats(); // Acumulem al 'rc' principal

            if (nodeActual.estat.esMeta()) {
                rc.setCami(reconstruirCami(nodeActual));
                return; // Solució trobada!
            }

            // Tallem si arribem al límit d'aquesta iteració
            if (nodeActual.profunditat >= limit) {
                rc.incNodesTallats(); // Acumulem al 'rc' principal
                continue; // Tallem per profunditat
            }

            List<Moviment> accions = nodeActual.estat.getAccionsPossibles();
            Collections.reverse(accions); // Per ordre d'exploració

            for (Moviment accio : accions) {
                Mapa estatFill = nodeActual.estat.mou(accio);
                int profunditatFilla = nodeActual.profunditat + 1;
                NodeCerca nodeFill = new NodeCerca(estatFill, nodeActual, accio, profunditatFilla);
                
                boolean tallar = false;
                if (usarLNT) {
                    if (lnt.containsKey(estatFill) && lnt.get(estatFill) <= profunditatFilla) {
                        tallar = true;
                    }
                } else {
                    if (estaAlCami(nodeActual, estatFill)) {
                        tallar = true;
                    }
                }

                if (tallar) {
                    rc.incNodesTallats(); // Acumulem al 'rc' principal
                    continue;
                }

                lno.push(nodeFill);
                if (usarLNT) {
                    lnt.put(estatFill, profunditatFilla);
                }
                
                int memActual = lno.size() + (usarLNT ? lnt.size() : 0);
                rc.updateMemoria(memActual); // Actualitzem memòria pic
            }
        }
        
        // No s'ha trobat solució en aquest límit
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
