(define (domain locks-keys)
  (:requirements :strips :typing :adl)
  (:types agent cell key door)

  (:predicates
    (at ?a - agent ?c - cell)          ; el agente ?a está en la celda ?c
    (adj ?x - cell ?y - cell)          ; celdas adyacentes (movimiento permitido)
    (goal-cell ?c - cell)              ; celda de salida
    (key-at ?k - key ?c - cell)        ; la llave ?k está en la celda ?c
    (have ?k - key)                    ; alguna agente ya tiene la llave ?k
    (door-at ?d - door ?c - cell)      ; en la celda ?c hay una puerta ?d
    (locked ?d - door)                 ; puerta cerrada
    (open ?d - door)                   ; puerta abierta
    (opens ?k - key ?d - door)         ; la llave ?k abre la puerta ?d
  )

  (:action move
    :parameters (?a - agent ?from - cell ?to - cell)
    :precondition (and
      (at ?a ?from) (adj ?from ?to)
      (or
        (not (exists (?d - door) (door-at ?d ?to)))          ; no hay puerta
        (exists (?d - door) (and (door-at ?d ?to) (open ?d))) ; o la puerta está abierta
      )
    )
    :effect (and (not (at ?a ?from)) (at ?a ?to))
  )

  (:action pick-key
    :parameters (?a - agent ?k - key ?c - cell)
    :precondition (and (at ?a ?c) (key-at ?k ?c))
    :effect (and (have ?k) (not (key-at ?k ?c)))
  )

  (:action open-door
    :parameters (?a - agent ?k - key ?d - door ?c - cell)
    :precondition (and (at ?a ?c) (door-at ?d ?c) (locked ?d) (have ?k) (opens ?k ?d))
    :effect (and (open ?d) (not (locked ?d)))
  )
)
