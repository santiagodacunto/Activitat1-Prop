package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.cerca.heuristica.Heuristica;
import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.util.PriorityQueue; // La nostra LNO serà una Cua de Prioritat
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class CercaAStar extends Cerca {

    private final Heuristica heur;

    /**
     * Classe interna per als nodes de cerca.
     * La fem 'Comparable' perquè la PriorityQueue sàpiga com ordenar-la.
     */
    private class NodeCerca implements Comparable<NodeCerca> {
        Mapa estat;
        NodeCerca pare;
        Moviment moviment;
        
        int g; // Cost real (profunditat)
        int h; // Cost estimat (heurística)
        int f; // Cost total (f = g + h)

        NodeCerca(Mapa estat, NodeCerca pare, Moviment moviment, int g, int h) {
            this.estat = estat;
            this.pare = pare;
            this.moviment = moviment;
            this.g = g;
            this.h = h;
            this.f = this.g + this.h;
        }

        @Override
        public int compareTo(NodeCerca other) {
            // El mètode 'compareTo' defineix l'ordre.
            // Volem que la PriorityQueue ens doni el node amb la 'f' MÉS BAIXA.
            return Integer.compare(this.f, other.f);
        }
    }

    public CercaAStar(boolean usarLNT, Heuristica heur) {
        super(usarLNT);
        this.heur = heur; // L'heurística (Basica o Avancada) que farem servir
    }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        
        // LNO - Llista de Nodes Oberts (PriorityQueue)
        PriorityQueue<NodeCerca> lno = new PriorityQueue<>();
        
        // LNT - Llista de Nodes Tancats
        // En A*, la LNT ha de guardar el cost 'g' mínim trobat per a cada estat.
        HashMap<Mapa, Integer> lnt = new HashMap<>();

        // Creem el node inicial
        int hInicial = heur.h(inicial);
        NodeCerca nodeInicial = new NodeCerca(inicial, null, null, 0, hInicial);
        
        lno.add(nodeInicial);
        if (usarLNT) {
            lnt.put(inicial, 0); // Cost (g) per arribar a l'inici és 0
        }

        rc.updateMemoria(1);

        while (!lno.isEmpty()) {
            
            // 1. Obtenim el node més prometedor (f més baixa)
            NodeCerca nodeActual = lno.poll();
            rc.incNodesExplorats();

            // 2. Comprovem si és la meta
            if (nodeActual.estat.esMeta()) {
                rc.setCami(reconstruirCami(nodeActual));
                return; // Solució trobada!
            }

            // (Optimització A*): Si traiem un node de la LNO que ja està
            // a la LNT amb un cost 'g' inferior, l'ignorem.
            // (El nostre control de cicles de sota ja gestiona això).

            // 3. Generem els estats fills
            List<Moviment> accions = nodeActual.estat.getAccionsPossibles();
            
            for (Moviment accio : accions) {
                Mapa estatFill = nodeActual.estat.mou(accio);
                
                // El cost per arribar al fill és el cost del pare + 1
                int gFill = nodeActual.g + 1;
                
                // 4. Control de Cicles (LNT)
                boolean tallar = false;
                if (usarLNT) {
                    // Mirem si ja hem visitat aquest estat (estatFill)
                    // i si ho vam fer per un camí MÉS CURT (amb una 'g' més baixa).
                    if (lnt.containsKey(estatFill) && lnt.get(estatFill) <= gFill) {
                        tallar = true; // Sí, ja tenim un camí millor cap a 'estatFill'
                    }
                } else {
                    if (estaAlCami(nodeActual, estatFill)) {
                        tallar = true;
                    }
                }

                if (tallar) {
                    rc.incNodesTallats();
                    continue; // Descartem aquest fill
                }

                // 5. Afegim el fill a les estructures
                int hFill = heur.h(estatFill); // Calculem l'heurística del fill
                NodeCerca nodeFill = new NodeCerca(estatFill, nodeActual, accio, gFill, hFill);
                
                lno.add(nodeFill);
                if (usarLNT) {
                    lnt.put(estatFill, gFill); // Guardem el nou cost 'g' (el millor fins ara)
                }
                
                int memActual = lno.size() + (usarLNT ? lnt.size() : 0);
                rc.updateMemoria(memActual);
            }
        }
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
