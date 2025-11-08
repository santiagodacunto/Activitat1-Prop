(define (domain locks-keys)
  (:requirements :strips :typing)

  (:types
    agent cell key door
  )

  (:predicates
    (at ?a - agent ?c - cell)            ; El agente ?a está en la celda ?c
    (connected ?c1 - cell ?c2 - cell)    ; La celda ?c1 está conectada con ?c2 (adyacentes)
    (is-wall ?c - cell)                  ; La celda ?c es una pared
    (key-at ?k - key ?c - cell)          ; La llave ?k está en la celda ?c
    (door-at ?d - door ?c - cell)        ; La puerta ?d está en la celda ?c
    (has-key ?k - key)                   ; Los agentes tienen la llave ?k
    (unlocks ?k - key ?d - door)         ; La llave ?k abre la puerta ?d
    (is-exit ?c - cell)                  ; La celda ?c es la salida
  )

  ; Acción 1: Moverse a una celda vacía o a la salida
  (:action move-to-empty-or-exit
    :parameters (?a - agent ?from - cell ?to - cell)
    :precondition (and
      (at ?a ?from)
      (connected ?from ?to)
      (not (is-wall ?to))
      (not (exists (?a2 - agent) (at ?a2 ?to)))         ; No hay otro agente (colisiones)
      (not (exists (?k - key) (key-at ?k ?to)))         ; No hay una llave
      (not (exists (?d - door) (door-at ?d ?to)))       ; No hay una puerta
    )
    :effect (and
      (not (at ?a ?from))
      (at ?a ?to)
    )
  )

  ; Acción 2: Moverse a una celda con llave y cogerla (en un solo paso)
  (:action move-and-pickup-key
    :parameters (?a - agent ?from - cell ?to - cell ?k - key)
    :precondition (and
      (at ?a ?from)
      (connected ?from ?to)
      (key-at ?k ?to)
      (not (exists (?a2 - agent) (at ?a2 ?to)))         ; No hay otro agente
    )
    :effect (and
      (not (at ?a ?from))
      (at ?a ?to)
      (not (key-at ?k ?to))
      (has-key ?k)                                     ; La llave es compartida
    )
  )

  ; Acción 3: Moverse a través de una puerta (si tenemos la llave)
  (:action move-through-door
    :parameters (?a - agent ?from - cell ?to - cell ?d - door ?k - key)
    :precondition (and
      (at ?a ?from)
      (connected ?from ?to)
      (door-at ?d ?to)
      (unlocks ?k ?d)
      (has-key ?k)                                     ; Requisito para cruzar
      (not (exists (?a2 - agent) (at ?a2 ?to)))         ; No hay otro agente
    )
    :effect (and
      (not (at ?a ?from))
      (at ?a ?to)
    )
  )
)