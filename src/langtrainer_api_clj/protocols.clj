(ns langtrainer-api-clj.protocols)

(defprotocol Model
  "A database model protocol"
  (define-closures [this data] "Add closures to model record"))
