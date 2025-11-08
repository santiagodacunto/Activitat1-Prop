package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.Queue;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class CercaBFS extends Cerca {
    
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

    public CercaBFS(boolean usarLNT) {
        super(usarLNT);
    }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        
        // Llista de Nodes Oberts (LNO) - Estructura FIFO (Queue) per BFS
        Queue<NodeCerca> lno = new LinkedList<>();
        
        // Llista de Nodes Tancats (LNT) - Mapa[Estat] -> Profunditat mínima
        HashMap<Mapa, Integer> lnt = new HashMap<>();

        // Creem el node inicial i l'afegim
        NodeCerca nodeInicial = new NodeCerca(inicial, null, null, 0);
        lno.add(nodeInicial);
        
        if (usarLNT) {
            lnt.put(inicial, 0);
        }

        rc.updateMemoria(1); // Comptem el node inicial

        while (!lno.isEmpty()) {
            
            // 1. Obtenim el següent node de la cua
            NodeCerca nodeActual = lno.poll();
            rc.incNodesExplorats();

            // 2. Comprovem si és la meta
            if (nodeActual.estat.esMeta()) {
                rc.setCami(reconstruirCami(nodeActual));
                return; // Hem trobat la solució!
            }

            // 3. Generem els estats fills (successors)
            List<Moviment> accions = nodeActual.estat.getAccionsPossibles();
            
            for (Moviment accio : accions) {
                // 'mou' ja crea una còpia del mapa
                Mapa estatFill = nodeActual.estat.mou(accio);
                int profunditatFilla = nodeActual.profunditat + 1;
                NodeCerca nodeFill = new NodeCerca(estatFill, nodeActual, accio, profunditatFilla);
                
                // 4. Control de Cicles
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
                    continue; // Descartem aquest fill i continuem amb la següent acció
                }

                // 5. Afegim el fill a les estructures
                lno.add(nodeFill);
                if (usarLNT) {
                    lnt.put(estatFill, profunditatFilla);
                }
                
                // Actualitzem l'estadística de memòria
                int memActual = lno.size() + (usarLNT ? lnt.size() : 0);
                if (memActual > rc.getMemoriaPic()) {
                    rc.updateMemoria(memActual);
                }
            } // fi del for d'accions
        } // fi del while (cua buida)

        // Si sortim del while, la cua (LNO) és buida i no hem trobat solució
        // rc.setCami(null); // Ja és null per defecte
    }
    
    /**
     * Mètode auxiliar per comprovar si un estat ja existeix al camí actual
     * (per l'opció usarLNT = false)
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
     * des del node final fins a l'inicial.
     */
    private List<Moviment> reconstruirCami(NodeCerca nodeFinal) {
        List<Moviment> cami = new ArrayList<>();
        NodeCerca actual = nodeFinal;
        
        // Anem afegint moviments pujant pels pares fins arribar al node inicial (pare == null)
        while (actual.pare != null) {
            cami.add(actual.moviment);
            actual = actual.pare;
        }
        
        // El camí està en ordre invers (del final a l'inici), el girem
        Collections.reverse(cami);
        return cami;
    }
}