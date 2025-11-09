package edu.epsevg.prop.ac1.cerca.heuristica;

import edu.epsevg.prop.ac1.model.Mapa;
import edu.epsevg.prop.ac1.model.Posicio;
import java.util.List;
import java.util.HashMap;

/**
 * Heuristica avançada V3: Pre-calcula les distàncies REALS (amb BFS)
 * per tenir una heurística 'perfecta' (ignorant agents).
 */
public class HeuristicaAvancada implements Heuristica {
    
    // Mapes de distàncies pre-calculades
    private HashMap<Character, int[][]> mapesDistClaus;
    private int[][] mapaDistSortida;
    private List<Character> llistaClaus;

    /**
     * CONSTRUCTOR: Aquí fem tot el pre-càlcul (es crida 1 sol cop)
     */
    public HeuristicaAvancada(Mapa mapaInicial) {
        this.mapesDistClaus = new HashMap<>();
        this.llistaClaus = new java.util.ArrayList<>();
        
        // 1. Pre-calculem el mapa de distàncies des de la SORTIDA
        this.mapaDistSortida = mapaInicial.getMapaDistanciesReals(mapaInicial.getSortidaPosicio());
        
        // 2. Pre-calculem el mapa de distàncies des de CADA CLAU
        for (int i = 0; i < mapaInicial.getN(); i++) {
            for (int j = 0; j < mapaInicial.getM(); j++) {
                int cell = mapaInicial.getCell(new Posicio(i,j));
                if (Character.isLowerCase(cell)) {
                    char nomClau = (char) cell;
                    Posicio posClau = new Posicio(i, j);
                    
                    this.llistaClaus.add(nomClau);
                    this.mapesDistClaus.put(nomClau, mapaInicial.getMapaDistanciesReals(posClau));
                }
            }
        }
    }

    @Override
    public int h(Mapa estat) {
        
        if (estat.esMeta()) return 0;

        List<Posicio> agents = estat.getAgents();
        
        // Obtenim les claus que encara queden al mapa
        List<Posicio> clausPendentsPos = estat.getClausPendents();
        
        if (clausPendentsPos.isEmpty()) {
            // ===============================================
            // Cas 1: NO QUEDEN CLAUS
            // =l'heurística és la distància REAL (pre-calculada) a la sortida
            // ===============================================
            int minDistSortida = Integer.MAX_VALUE;
            for (Posicio agent : agents) {
                int dist = mapaDistSortida[agent.x][agent.y];
                if (dist != -1 && dist < minDistSortida) {
                    minDistSortida = dist;
                }
            }
            return (minDistSortida == Integer.MAX_VALUE) ? 0 : minDistSortida; // 0 si és inaccessible?
            
        } else {
            // ===============================================
            // Cas 2: SÍ QUEDEN CLAUS (La lògica V2, però amb dades REALS)
            // ===============================================
            // h = min( dist_REAL(Agent, K) + dist_REAL(K, Sortida) )
            
            int h_final = Integer.MAX_VALUE;

            // Iterem per CADA clau pendent
            for (Posicio clauPos : clausPendentsPos) {
                
                int cellClau = estat.getCell(clauPos); // Obtenim el char de la clau
                if (!Character.isLowerCase(cellClau)) continue; // Ja no és una clau?
                
                char nomClau = (char) cellClau;
                int[][] mapDistAquestaClau = mapesDistClaus.get(nomClau);
                
                // 1. Trobem la distància REAL de l'agent més proper a AQUESTA clau
                int minDistAgentAquestaClau = Integer.MAX_VALUE;
                for (Posicio agent : agents) {
                    int dist = mapDistAquestaClau[agent.x][agent.y];
                    if (dist != -1 && dist < minDistAgentAquestaClau) {
                        minDistAgentAquestaClau = dist;
                    }
                }
                if (minDistAgentAquestaClau == Integer.MAX_VALUE) continue; // Clau inaccessible

                // 2. Trobem la distància REAL d'AQUESTA clau a la sortida
                int distClauSortida = mapaDistSortida[clauPos.x][clauPos.y];
                if (distClauSortida == -1) continue; // Sortida inaccessible des d'aquesta clau
                
                // 3. El cost total estimat per la "ruta" d'aquesta clau
                int costTotalRutaClau = minDistAgentAquestaClau + distClauSortida;
                
                // 4. Mirem si és la millor ruta
                h_final = Math.min(h_final, costTotalRutaClau);
            }
            
            return (h_final == Integer.MAX_VALUE) ? 0 : h_final;
        }
    }
    
    // El mètode Manhattan ja no el necessitem!
    // private int manhattan(Posicio p1, Posicio p2) { ... }
}
