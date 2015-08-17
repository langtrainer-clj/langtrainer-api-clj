(ns langtrainer-api-clj.models.unit
  (:use [korma.core]))

(defn new-unit-model [db]
  {:entity (-> (create-entity (name "units"))
               (entity-fields :id :slug))})

(defn all [{units :entity} scopes]
  (apply select* (conj units scopes)))

(defn published []
   '(where {:published true}))
