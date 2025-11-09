package edu.epsevg.prop.ac1.cerca.heuristica;

import edu.epsevg.prop.ac1.model.Mapa;
import edu.epsevg.prop.ac1.model.Posicio;
import java.util.List;

/** * Distància de Manhattan a la clau més propera 
 * (si queden per recollir) o a la sortida.
 */
public class HeuristicaBasica implements Heuristica {
    
    @Override
    public int h(Mapa estat) {
        
        // Cas 0: Ja som a la meta, l'heurística és 0.
        if (estat.esMeta()) {
            return 0;
        }

        // Inicialitzem la distància mínima a un valor infinit
        int minDist = Integer.MAX_VALUE;
        
        // Obtenim les posicions de tots els agents
        List<Posicio> agents = estat.getAgents();
        
        // ESTRATÈGIA 1: Mirem si queden claus pendents
        // (Fem servir el mètode nou que hem afegit a Mapa.java)
        List<Posicio> clausPendents = estat.getClausPendents();

        if (!clausPendents.isEmpty()) {
            // SÍ queden claus.
            // Calculem la distància de CADA agent a CADA clau pendent.
            // Ens quedem amb la mínima de totes.
            
            for (Posicio agent : agents) {
                for (Posicio clau : clausPendents) {
                    int dist = manhattan(agent, clau);
                    if (dist < minDist) {
                        minDist = dist;
                    }
                }
            }
        } else {
            // NO queden claus.
            // Calculem la distància de CADA agent a la SORTIDA.
            
            Posicio sortida = estat.getSortidaPosicio();
            for (Posicio agent : agents) {
                int dist = manhattan(agent, sortida);
                if (dist < minDist) {
                    minDist = dist;
                }
            }
        }
        
        return minDist;
    }
    
    /**
     * Mètode privat per calcular la distància de Manhattan.
     * L'enunciat diu que l'heurística ignora les parets.
     */
    private int manhattan(Posicio p1, Posicio p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }
}
