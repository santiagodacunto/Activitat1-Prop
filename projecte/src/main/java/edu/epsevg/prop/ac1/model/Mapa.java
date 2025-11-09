package edu.epsevg.prop.ac1.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Representa l'estat del mapa: grid, posicions agents (indexades per id 1..n),
 * i bitmask de claus.
 *
 * Codifiquem:
 * - PARET = -1
 * - ESPAI = 0
 * - SORTIDA = -2
 * - claus: ascii 'a'..'z' (valors positius > 0)
 * - portes: ascii 'A'..'Z' (valors positius > 0)
 */
public class Mapa {
    private final int n;
    private final int m;
    private final int[][] grid; // conservem caràcters ordinals o codis
    private final List<Posicio> agents; // agents indexats a partir de 1 (index 0 -> agent 1)
    private int clausMask;
    private Posicio sortida;
    
    /**
     * Definicions dels valors del grid: PARET
     */
    public static final int PARET = -1;
    /**
     * Definicions dels valors del grid: ESPAI
     */    
    public static final int ESPAI = 0;
    /**
     * Definicions dels valors del grid: SORTIDA
     */    
    public static final int SORTIDA = -2;

    /**
     * Constructor a partir d'un arxiu
     */
    public Mapa(Path fitxer) throws IOException {
        List<String> lines = Files.readAllLines(fitxer);
        this.n = lines.size();
        this.m = lines.get(0).length();
        this.grid = new int[n][m];
        this.agents = new ArrayList<>();
        this.clausMask = 0;
        
        sortida = null;
        
        for (int i = 0; i < n; i++) {
            String row = lines.get(i);
            for (int j = 0; j < m; j++) {
                char c = row.charAt(j);
                switch (c) {
                    case '#': grid[i][j] = PARET; break;
                    case ' ': grid[i][j] = ESPAI; break;
                    case '@': grid[i][j] = SORTIDA; sortida = new Posicio(i,j); break;
                    default:
                        if (Character.isDigit(c)) {
                            // posem l'agent, però __NO__ es situa a la graella
                            agents.add(new Posicio(i, j));
                            grid[i][j] = ESPAI;
                        } else if (Character.isLowerCase(c)) {
                            grid[i][j] = c; // desem directament la lletra
                        } else if (Character.isUpperCase(c)) {
                            grid[i][j] = c; // desem directament la lletra
                        } else {
                            grid[i][j] = ESPAI;
                        }
                }
            }
        }
        if(sortida==null) throw new RuntimeException("Sortida no definida.");
        if(agents.size()==0) throw new RuntimeException("Agents no definits.");
    }

    /** * Constructor còpia PÚBLIC (LENT)
     * Fa una "Deep copy" del mapa (duplica en memòria i copia tots els valors d'un a l'altre) 
     * Es manté per compatibilitat, però 'mou' ja no l'utilitza.
     */
    public Mapa(Mapa other) {
        this.n = other.n;
        this.m = other.m;
        this.grid = new int[n][m];
        for (int i = 0; i < n; i++) System.arraycopy(other.grid[i], 0, this.grid[i], 0, m);
        
        this.agents = new ArrayList<>();
        for (Posicio p : other.agents) this.agents.add(new Posicio(p.x, p.y));
        this.clausMask = other.clausMask;
        this.sortida = other.sortida;
    }

    /** * Constructor còpia PRIVAT (Optimitzat)
     * S'usa per 'mou'. Si 'deepCopyGrid' és false, comparteix la referència del grid (RÀPID).
     * Si 'deepCopyGrid' és true, fa una còpia profunda del grid (LENT, només quan agafem clau).
     */
    private Mapa(Mapa other, boolean deepCopyGrid) {
        this.n = other.n;
        this.m = other.m;
        this.sortida = other.sortida;
        this.clausMask = other.clausMask;
        
        // Agents sempre es copia (deep) pq almenys 1 es mourà
        this.agents = new ArrayList<>();
        for (Posicio p : other.agents) this.agents.add(new Posicio(p.x, p.y));
        
        if (deepCopyGrid) {
            // Còpia profunda (lenta) -> S'agafarà una clau
            this.grid = new int[n][m];
            for (int i = 0; i < n; i++) System.arraycopy(other.grid[i], 0, this.grid[i], 0, m);
        } else {
            // Còpia per referència (ràpida) -> No s'agafa clau
            this.grid = other.grid;
        }
    }


    /**
     * Número de columnes
     * @return el nombre de columnes
     */
    public int getN() { return n; }
    
    /**
     * Número de files
     * @return el nombre de files
     */
    public int getM() { return m; }
    
    /**
     * @return Retorna la llista immutable de la posició dels agents
     */
    public List<Posicio> getAgents() { return Collections.unmodifiableList(agents); }
    
    /**
     * @return la màscara binària en format int de les claus. Cada clau és un bit, començant per la a (bit menys significant),b,c...
     * P.ex. Si hi ha 3 claus, a, b i c, i tenim agafada la b i la c, la màscara val 6 (110 en binari)
     * cba
     * 110 
     */
    public int getClausMask() { return clausMask; }

    /**
     * Permet saber si una posició conté la sortida
     * @return true si la posició és la sortida, false altrament
     */
    public boolean esSortida(Posicio p) {
        return getCell(p) == SORTIDA;
    }

    /**
     * @return el valor de la cella (veure constants PARET, ESPAI, SORTIDA)
     */
    public int getCell(Posicio p) {
        if (p.x < 0 || p.x >= n || p.y < 0 || p.y >= m) return PARET;
        return grid[p.x][p.y];
    }

    /**
     * Indicar que una clau ha estat recollida
     */
    private void setClauRecollida(char key) {
        int idx = key - 'a';
        clausMask |= (1 << idx);
    }

    /**
     * Permet saber si una clau ha estat recollida
     * @param key la clau que volem preguntar
     * @return true si la tenim
     */
    public boolean teClau(char key) {
        int idx = key - 'a';
        return (clausMask & (1 << idx)) != 0;
    }

    /**
     * Permet saber si podem obrir una clau determinada
     * @param door la porta que volem obrir (caràcter majúscules)
     * @return true si podem obrir-la
     */
    public boolean portaObrible(char door) {
        char key = Character.toLowerCase(door);
        return teClau(key);
    }

    /** * Aplica el moviment SOBRE UNA CÒPIA (no altera el mapa actual)
     * VERSIÓ OPTIMITZADA: Fa servir el constructor privat.
     * @return  la nova instància amb el moviment ja fet.
     */
    public Mapa mou(Moviment acc) {
        // --- 1. VALIDACIONS (sobre 'this', l'estat actual) ---
        int aid = acc.getAgentId();
        if (aid < 1 || aid > agents.size()) throw new IllegalArgumentException("Agent id invalid");
        
        Posicio actual = agents.get(aid - 1);
        Posicio dest = actual.translate(acc.getDireccio());

        int cell = getCell(dest);
        if (cell == PARET) throw new IllegalArgumentException("Moviment cap a mur");
        if (Character.isUpperCase(cell)) {
            // porta
            if (!portaObrible((char) cell)) throw new IllegalArgumentException("Porta tancada");
        }
        // no permetre col·lisions
        for (int i = 0; i < agents.size(); i++) {
            if (i == aid-1) continue;
            Posicio p = agents.get(i);
            if (p.equals(dest)) throw new IllegalArgumentException("Colisio amb altre agent");
        }
        
        // --- 2. COMPROVAR SI CALDRÀ MODIFICAR EL GRID ---
        boolean recolliraClau = false;
        char key = 0;
        if (Character.isLowerCase(cell)) {
            key = (char) cell;
            if (!teClau(key)) {
                recolliraClau = true;
            }
        }
        
        // --- 3. CREAR LA CÒPIA (OPTIMITZADA) ---
        // Si 'recolliraClau' és true, necessitem una còpia profunda del grid (pq el modificarem)
        // Si 'recolliraClau' és false, compartim la referència del grid (ràpid)
        Mapa nou = new Mapa(this, recolliraClau);

        // --- 4. APLICAR CANVIS A LA CÒPIA 'nou' ---
        
        // Sempre movem l'agent
        nou.agents.set(aid - 1, dest);
        
        // Si havíem de recollir clau, ho fem ara sobre 'nou'
        if (recolliraClau) {
            nou.setClauRecollida(key);
            // Com que 'nou' té una còpia única del grid, podem modificar-la
            nou.grid[dest.x][dest.y] = ESPAI;
        }
        
        return nou;
    }

    /** * Obtenir els moviments possibles des de l'estat actual
     * @return la llista de moviments possibles des de l'estat actual:
     * - per cada agent (1..k) i cada direcció valida (que no sigui mur, si és una porta ha de ser obrible i sense col·lisió amb d'altres agents)
     * - indica recullClau=true si el destí té una clau que encara no s'ha recollit
     */
    public List<Moviment> getAccionsPossibles() {
        List<Moviment> res = new ArrayList<>();
        
        // Iterem per cada agent (recorda que agentId es 1-based)
        for (int i = 0; i < agents.size(); i++) {
            int agentId = i + 1;
            Posicio posActual = agents.get(i);

            // Iterem per cada direcció possible
            for (Direccio d : Direccio.values()) {
                Posicio posDesti = posActual.translate(d);
                int cellDesti = getCell(posDesti); // Aquest mètode ja gestiona els límits del mapa

                // --- INICI DE LA VALIDACIÓ ---
                
                // 1. Comprovar Paret
                if (cellDesti == PARET) {
                    continue; // Moviment il·legal, passem a la següent direcció
                }

                // 2. Comprovar Porta Tancada
                if (Character.isUpperCase(cellDesti)) {
                    if (!portaObrible((char) cellDesti)) {
                        continue; // Porta tancada, moviment il·legal
                    }
                }
                
                // 3. Comprovar Col·lisions amb altres agents
                boolean colisio = false;
                for (int j = 0; j < agents.size(); j++) {
                    if (i == j) continue; // No comprovem amb nosaltres mateixos
                    
                    if (agents.get(j).equals(posDesti)) {
                        colisio = true; // El destí està ocupat per l'agent 'j'
                        break;
                    }
                }
                if (colisio) {
                    continue; // Moviment il·legal
                }
                
                // --- FI DE LA VALIDACIÓ ---
                // Si hem arribat aquí, el moviment és VÀLID.

                // Mirem si al destí hi ha una clau que no tenim
                boolean recullClau = false;
                if (Character.isLowerCase(cellDesti) && !teClau((char) cellDesti)) {
                    recullClau = true;
                }

                // Afegim el moviment vàlid a la llista
                res.add(new Moviment(agentId, d, recullClau));
            }
        }
        return res;
    }

    /** * Permet saber si algú ha arribat a la sortida
     * @return true si algun agent ha arribat a la sortida 
     */
    public boolean esMeta() {
        for (Posicio p : agents) if (esSortida(p)) return true;
        return false;
    }

    // =================================================================
    // VERSIÓ OPTIMITZADA DE EQUALS I HASHCODE
    // =================================================================
    
    @Override
    public boolean equals(Object o) {
        // Comprovació estàndard d'equals
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        // Comparem els camps que defineixen l'estat
        Mapa mapa = (Mapa) o;
        
        // L'estat es defineix NOMÉS per la posició dels agents i les claus recollides.
        // El 'grid' és redundant, ja que els seus canvis (claus que desapareixen)
        // són un resultat directe i determinista del 'clausMask'.
        // Comprovar el 'grid' (amb deepEquals) és el que feia el codi lent.
        return clausMask == mapa.clausMask &&
               agents.equals(mapa.agents);
    }

    @Override
    public int hashCode() {  
        // Ha de ser consistent amb 'equals' (només fer servir els mateixos camps).
        // Fem servir 'Objects.hash' que és eficient i gestiona nuls.
        return Objects.hash(agents, clausMask);
    }

    // =================================================================
    // FI DE LA OPTIMITZACIÓ
    // =================================================================


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Agents:");
        for (int i = 0; i < agents.size(); i++) sb.append(" ").append(i+1).append(agents.get(i));
        sb.append(" clausMask=").append(Integer.toBinaryString(clausMask));
        return sb.toString();
    }

    /** * @return la posició de sortida del mapa
     */
    public Posicio getSortidaPosicio() {
        return sortida;
    }

    
    //===================================================================
    // Aquí van les vostres ampliacions (nous mètodes d'utilitat)
    //===================================================================
    
    //@TODO: (opcionalment) el que cregueu convenient per ampliar la classe.

    /** * Mètode d'utilitat per l'heurística.
     * @return Una llista de Posicions de totes les claus que encara no s'han recollit.
     */
    public List<Posicio> getClausPendents() {
        List<Posicio> claus = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                // Accedim directament al grid privat, pq estem dins de Mapa.java
                int cell = grid[i][j]; 
                
                // Si la cel·la és una lletra minúscula, és una clau
                if (Character.isLowerCase(cell)) {
                    // Mirem si és una clau que NO tenim
                    if (!teClau((char) cell)) {
                        claus.add(new Posicio(i, j));
                    }
                }
            }
        }
        return claus;
    }

    /**
     * MÈTODE D'AJUDA PER L'HEURÍSTICA AVANÇADA (PRE-CÀLCUL)
     * Fa una cerca BFS des d'un punt 'inici' i retorna un grid on
     * cada cel·la té la distància real (amb parets) des de 'inici'.
     * @param inici La posició des d'on calcular les distàncies.
     * @return Un grid int[n][m] amb les distàncies. -1 si és inaccessible.
     */
    public int[][] getMapaDistanciesReals(Posicio inici) {
        int[][] distMap = new int[n][m];
        for (int i = 0; i < n; i++) {
            java.util.Arrays.fill(distMap[i], -1); // -1 = No visitat / Inaccessible
        }

        java.util.Queue<Posicio> queue = new java.util.LinkedList<>();
        
        queue.add(inici);
        distMap[inici.x][inici.y] = 0;

        while (!queue.isEmpty()) {
            Posicio actual = queue.poll();

            for (Direccio d : Direccio.values()) {
                Posicio desti = actual.translate(d);
                int cell = getCell(desti); // Aquest mètode ja gestiona parets i límits

                // Si és un lloc vàlid (no paret) I no l'hem visitat
                if (cell != PARET && distMap[desti.x][desti.y] == -1) {

                    distMap[desti.x][desti.y] = distMap[actual.x][actual.y] + 1;
                    queue.add(desti);
                }
            }
        }
        return distMap;
    }
}