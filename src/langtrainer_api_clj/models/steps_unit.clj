(ns langtrainer-api-clj.models.steps-unit
  (:use [korma.core]))

(defn new-steps-unit-model [db]
  {:entity (-> (create-entity (name "steps_units"))
               (entity-fields :id))})
