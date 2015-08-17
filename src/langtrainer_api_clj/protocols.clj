(ns langtrainer-api-clj.protocols)

(defprotocol ClosuresContainer
  (define-closures [this context] "Add closures to the record"))

(defprotocol Model
  "A database model protocol"
  (define-relations [this models] "Add relations to model record"))

;; No-op implementation if one is not defined.
(extend-protocol ClosuresContainer
  java.lang.Object
  (define-closures [this models]
    this))

(extend-protocol Model
  java.lang.Object
  (define-relations [this models]
    this))
