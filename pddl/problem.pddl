(define (problem mapa-a)
  (:domain locks-keys)

  (:objects
    ; agentes (números 1 y 2 del mapa)
    a1 a2 - agent

    ; celdas transitables (coordenadas fila/col)
    r2c2 r2c3 r2c4 r2c5 r2c6
    r3c2 r3c3 r3c4 r3c5 r3c7
    r4c2 r4c3 r4c4 r4c5 r4c6 r4c7 - cell

    ; llaves y puertas
    ka - key
    dA - door
  )

  (:init
    ; posiciones iniciales
    (at a2 r2c3)   ; '2' en el mapa
    (at a1 r4c5)   ; '1' en el mapa

    ; meta (la celda '@' es la salida)
    (goal-cell r3c7)

    ; llave y puerta
    (key-at ka r3c2)   ; 'a'
    (door-at dA r4c7)  ; 'A'
    (locked dA)
    (opens ka dA)

    ; adyacencias (4-dir, simétricas)
    (adj r2c2 r2c3) (adj r2c3 r2c2)
    (adj r2c2 r3c2) (adj r3c2 r2c2)

    (adj r2c3 r2c4) (adj r2c4 r2c3)
    (adj r2c3 r3c3) (adj r3c3 r2c3)

    (adj r2c4 r2c5) (adj r2c5 r2c4)
    (adj r2c4 r3c4) (adj r3c4 r2c4)

    (adj r2c5 r2c6) (adj r2c6 r2c5)
    (adj r2c5 r3c5) (adj r3c5 r2c5)

    (adj r3c2 r3c3) (adj r3c3 r3c2)
    (adj r3c2 r4c2) (adj r4c2 r3c2)

    (adj r3c3 r3c4) (adj r3c4 r3c3)
    (adj r3c3 r4c3) (adj r4c3 r3c3)

    (adj r3c4 r3c5) (adj r3c5 r3c4)
    (adj r3c4 r4c4) (adj r4c4 r3c4)

    (adj r3c5 r4c5) (adj r4c5 r3c5)

    (adj r4c2 r4c3) (adj r4c3 r4c2)
    (adj r4c3 r4c4) (adj r4c4 r4c3)
    (adj r4c4 r4c5) (adj r4c5 r4c4)
    (adj r4c5 r4c6) (adj r4c6 r4c5)

    ; zona de la puerta y la salida
    (adj r4c6 r4c7) (adj r4c7 r4c6)
    (adj r4c7 r3c7) (adj r3c7 r4c7)
  )

  ; “cualquiera de los agentes llega a la salida”
  (:goal (exists (?a - agent ?g - cell) (and (at ?a ?g) (goal-cell ?g))))
)
