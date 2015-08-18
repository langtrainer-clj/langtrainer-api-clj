(ns langtrainer-api-clj.protocols)

(defprotocol HasRelations
  "A database model protocol"
  (define-relations [this models]))

(defprotocol Model
  "A database model protocol"
  (initialize [this models]))

;; No-op implementation if one is not defined.
(extend-protocol HasRelations
  java.lang.Object
  (define-relations [this models]
    this))

(extend-protocol Model
  java.lang.Object
  (initialize [this models]
    (define-relations this models)))
