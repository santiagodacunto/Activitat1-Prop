(define (problem map-a)
  (:domain locks-keys)

  (:objects
    ; Agentes [cite: 43]
    a1 a2 - agent
    
    ; Llaves y puertas [cite: 43]
    k_a - key
    d_A - door

    ; Celdas (R=Fila, C=Columna)
    c1_1 c1_2 c1_3 c1_4 c1_5 c1_6 c1_7 c1_8 - cell
    c2_1 c2_2 c2_3 c2_4 c2_5 c2_6 c2_7 c2_8 - cell
    c3_1 c3_2 c3_3 c3_4 c3_5 c3_6 c3_7 c3_8 - cell
    c4_1 c4_2 c4_3 c4_4 c4_5 c4_6 c4_7 c4_8 - cell
    c5_1 c5_2 c5_3 c5_4 c5_5 c5_6 c5_7 c5_8 - cell
  )

  (:init
    ; Relación llave-puerta [cite: 24]
    (unlocks k_a d_A)

    ; Posición inicial agentes
    (at a1 c4_5) ; Agente 1 en (4,5)
    (at a2 c2_2) ; Agente 2 en (2,2)

    ; Posición llave 'a'
    (key-at k_a c3_2) ; Llave 'a' en (3,2)

    ; Posición puerta 'A'
    (door-at d_A c4_7) ; Puerta 'A' en (4,7)

    ; Posición salida '@'
    (is-exit c3_7) ; Salida en (3,7)

    ; Definición de las paredes '#' [cite: 43]
    (is-wall c1_1) (is-wall c1_2) (is-wall c1_3) (is-wall c1_4) (is-wall c1_5) (is-wall c1_6) (is-wall c1_7) (is-wall c1_8)
    (is-wall c2_1) (is-wall c2_3) (is-wall c2_4) (is-wall c2_8)
    (is-wall c3_1) (is-wall c3_6) (is-wall c3_8)
    (is-wall c4_1) (is-wall c4_8)
    (is-wall c5_1) (is-wall c5_2) (is-wall c5_3) (is-wall c5_4) (is-wall c5_5) (is-wall c5_6) (is-wall c5_7) (is-wall c5_8)

    ; Definición de conexiones (horizontal)
    (connected c1_1 c1_2) (connected c1_2 c1_1) (connected c1_2 c1_3) (connected c1_3 c1_2) (connected c1_3 c1_4) (connected c1_4 c1_3) (connected c1_4 c1_5) (connected c1_5 c1_4) (connected c1_5 c1_6) (connected c1_6 c1_5) (connected c1_6 c1_7) (connected c1_7 c1_6) (connected c1_7 c1_8) (connected c1_8 c1_7)
    (connected c2_1 c2_2) (connected c2_2 c2_1) (connected c2_2 c2_3) (connected c2_3 c2_2) (connected c2_3 c2_4) (connected c2_4 c2_3) (connected c2_4 c2_5) (connected c2_5 c2_4) (connected c2_5 c2_6) (connected c2_6 c2_5) (connected c2_6 c2_7) (connected c2_7 c2_6) (connected c2_7 c2_8) (connected c2_8 c2_7)
    (connected c3_1 c3_2) (connected c3_2 c3_1) (connected c3_2 c3_3) (connected c3_3 c3_2) (connected c3_3 c3_4) (connected c3_4 c3_3) (connected c3_4 c3_5) (connected c3_5 c3_4) (connected c3_5 c3_6) (connected c3_6 c3_5) (connected c3_6 c3_7) (connected c3_7 c3_6) (connected c3_7 c3_8) (connected c3_8 c3_7)
    (connected c4_1 c4_2) (connected c4_2 c4_1) (connected c4_2 c4_3) (connected c4_3 c4_2) (connected c4_3 c4_4) (connected c4_4 c4_3) (connected c4_4 c4_5) (connected c4_5 c4_4) (connected c4_5 c4_6) (connected c4_6 c4_5) (connected c4_6 c4_7) (connected c4_7 c4_6) (connected c4_7 c4_8) (connected c4_8 c4_7)
    (connected c5_1 c5_2) (connected c5_2 c5_1) (connected c5_2 c5_3) (connected c5_3 c5_2) (connected c5_3 c5_4) (connected c5_4 c5_3) (connected c5_4 c5_5) (connected c5_5 c5_4) (connected c5_5 c5_6) (connected c5_6 c5_5) (connected c5_6 c5_7) (connected c5_7 c5_6) (connected c5_7 c5_8) (connected c5_8 c5_7)

    ; Definición de conexiones (vertical)
    (connected c1_1 c2_1) (connected c2_1 c1_1) (connected c2_1 c3_1) (connected c3_1 c2_1) (connected c3_1 c4_1) (connected c4_1 c3_1) (connected c4_1 c5_1) (connected c5_1 c4_1)
    (connected c1_2 c2_2) (connected c2_2 c1_2) (connected c2_2 c3_2) (connected c3_2 c2_2) (connected c3_2 c4_2) (connected c4_2 c3_2) (connected c4_2 c5_2) (connected c5_2 c4_2)
    (connected c1_3 c2_3) (connected c2_3 c1_3) (connected c2_3 c3_3) (connected c3_3 c2_3) (connected c3_3 c4_3) (connected c4_3 c3_3) (connected c4_3 c5_3) (connected c5_3 c4_3)
    (connected c1_4 c2_4) (connected c2_4 c1_4) (connected c2_4 c3_4) (connected c3_4 c2_4) (connected c3_4 c4_4) (connected c4_4 c3_4) (connected c4_4 c5_4) (connected c5_4 c4_4)
    (connected c1_5 c2_5) (connected c2_5 c1_5) (connected c2_5 c3_5) (connected c3_5 c2_5) (connected c3_5 c4_5) (connected c4_5 c3_5) (connected c4_5 c5_5) (connected c5_5 c4_5)
    (connected c1_6 c2_6) (connected c2_6 c1_6) (connected c2_6 c3_6) (connected c3_6 c2_6) (connected c3_6 c4_6) (connected c4_6 c3_6) (connected c4_6 c5_6) (connected c5_6 c4_6)
    (connected c1_7 c2_7) (connected c2_7 c1_7) (connected c2_7 c3_7) (connected c3_7 c2_7) (connected c3_7 c4_7) (connected c4_7 c3_7) (connected c4_7 c5_7) (connected c5_7 c4_7)
    (connected c1_8 c2_8) (connected c2_8 c1_8) (connected c2_8 c3_8) (connected c3_8 c2_8) (connected c3_8 c4_8) (connected c4_8 c3_8) (connected c4_8 c5_8) (connected c5_8 c4_8)
  )

  (:goal
    ; El objetivo es que *cualquier* agente llegue a la salida [cite: 34]
    (or
      (at a1 c3_7)
      (at a2 c3_7)
    )
  )
)